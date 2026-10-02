<template>
  <div>
    <el-dialog :title="title" :visible.sync="show" width="800px" :before-close="close" :close-on-click-modal="false">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="left">
        <el-form-item label="人员姓名:" prop="name">
          <el-input v-model="form.name" placeholder="请输入人员姓名" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item label="联系方式:" prop="contactNumber">
          <el-input v-model="form.contactNumber" placeholder="请输入联系方式" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="登录账号:" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入登录账号" maxlength="14" show-word-limit />
          <div style="fontSize:14px">账号由4-14位英文和数字组合，首位为字母</div>
        </el-form-item>
        <el-form-item v-if="isAdd" label="初始密码:" prop="password">
          <el-input v-model="form.password" placeholder="请输入初始密码" maxlength="16" show-word-limit />
        </el-form-item>
        <div class="tip">
          注：这里配置所属校区后，机构工作人员会出现在对应校区的工作人员里。其在校区里的权限，还需要对应校区的负责人进行分配。
        </div>
        <el-form-item label="所属校区:" prop="campusConnectionList">
          <el-table ref="multipleTable" :data="campusData" border>
            <!-- <el-table-column type="selection" width="55" align="center">
            </el-table-column> -->
            <el-table-column align="center" width="55">
              <template slot-scope="{row}">
                <el-checkbox v-model="row.checked" @change="changeChecked(row)" />
              </template>
            </el-table-column>
            <el-table-column prop="name" label="校区名称" align="center" />
          </el-table>
        </el-form-item>
        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">保 存</el-button>
        <!-- <el-button @click="cancel">取 消</el-button> -->
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getCampusList
} from '@/api/mechanism/people'
import { tel } from '@/utils/validate.js'
export default {
  props: {
    show: {
      type: Boolean,
      default: false
    },
    isAdd: {
      type: Boolean,
      default: false
    },
    info: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    const validatePhone = (rule, value, callback) => {
      if (value === '' || value === undefined || value === null) {
        callback(new Error('联系方式不能为空'))
      } else if (tel(value)) {
        callback(new Error('请输入正确的联系方式'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (!value || value.length <= 6) {
        callback(new Error('密码必须大于6个字符'))
      } else {
        callback()
      }
    }
    const validateMul = (rule, value, callback) => {
      if ((this.form.campusConnectionList || []).length <= 0) {
        callback(new Error('至少选择一个校区'))
      } else {
        callback()
      }
    }
    var validateUser = (rule, value, callback) => {
      var reg = /^[a-zA-Z][A-Za-z0-9]{3,13}$/
      if (!value) {
        callback(new Error('登录账号不能为空'))
      } else if (!reg.test(value)) {
        callback(new Error('账号格式不正确'))
      } else {
        callback()
      }
    }
    return {
      selectList: [],
      multipleSelection: [],
      ids: [],
      chooseId: [],
      title: '',
      loading: true,
      queryParams: {
        pageNum: 1,
        pageSize: 5
      },
      campusData: [],
      total: 0,
      form: {},
      rules: {
        name: [
          { required: true, message: '人员姓名不能为空', trigger: 'blur' }
        ],
        contactNumber: [
          {
            required: true,
            validator: validatePhone,
            trigger: ['change', 'blur']
          }
        ],
        phone: [
          {
            required: true,
            validator: validateUser,
            trigger: ['change', 'blur']
          }
        ],
        password: [
          { required: true, validator: validatePassword, trigger: 'blur' }
        ],
        campusConnectionList: [
          { required: true, validator: validateMul, trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    show(val) {
      if (val) {
        this.reset()
        if (this.isAdd) {
          this.title = '新建机构工作人员'
          this.selectList = []
        } else {
          this.form = { ...this.info }
          this.title = '编辑机构工作人员'
          if ((this.form.campusConnectionList || []).length > 0) {
            this.selectList = []
            this.form.campusConnectionList.forEach((item) => {
              this.selectList.push({
                campusId: item.campusId,
                campusName: item.campusName
              })
            })
          }
        }
        this.chooseId = []
        this.queryParams.pageNum = 1
        this.queryParams.pageSize = 5
        this.getList(true)
      }
    }
  },
  methods: {
    getList() {
      this.loading = true
      getCampusList(this.queryParams).then((response) => {
        this.campusData = response.rows
        this.total = response.total
        var chooseId = this.selectList.map((item) => item.campusId)
        this.campusData.forEach((item) => {
          if (chooseId.includes(item.id)) {
            this.$set(item, 'checked', true)
          } else {
            this.$set(item, 'checked', false)
          }
        })

        console.log(this.selectList)
        this.loading = false
      })
    },
    // 选中
    changeChecked(row) {
      if (row.checked) {
        this.selectList.push({
          campusId: row.id,
          campusName: row.name
        })
      } else {
        var flagIndex = this.selectList.findIndex((item) => {
          return (row.id === item.campusId)
        })
        if (flagIndex !== -1) {
          this.selectList.splice(flagIndex, 1)
        }
      }
      console.log(this.selectList)
    },
    // 重设
    reset() {
      this.form = {
        name: undefined,
        contactNumber: undefined,
        phone: undefined,
        password: undefined,
        campusConnectionList: []
      }
      this.resetForm('form')
    },
    // 关闭
    close() {
      this.$emit('close')
    },
    // 提交表单
    submitForm() {
      this.form.campusConnectionList = this.selectList
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$emit('submitForm', this.form)
        }
      })
    }
  }
}
</script>
