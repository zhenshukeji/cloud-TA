<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入人员名称" clearable size="small" @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入登录账号" clearable size="small" @keyup.enter.native="handleQuery" maxlength="14" show-word-limit />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd">新增人员</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="classList" stripe>
      <el-table-column label="人员名称" align="center" prop="name" />
      <el-table-column label="登录账号" align="center" prop="phone" />
      <el-table-column label="联系方式" align="center" prop="contactNumber" />
      <!-- <el-table-column label="所属校区" align="center" prop="campusConnectionList" /> -->
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(row.id)">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(row)">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleReset(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑人员对话框 -->
    <ope-people :show="opeVisible" :isAdd="isAdd" :info="opeInfo" @close="opeVisible = false" @submitForm="submitForm" />

    <el-dialog title="重置密码" :visible.sync="dialogVisible2" width="30%">
      <el-divider />
      <span style="fontSize:18px">
        <svg-icon icon-class="warn" style="color:#FF9E5D" />重新设置机构工作人员"{{ name }}"密码
      </span>
      <div style="fontSize:18px;margin-top:20px">
        新密码：
        <el-input v-model="newPassword" placeholder="请输入新密码" style="width:50%" maxlength="16" show-word-limit />
      </div>

      <div slot="footer" class="dialog-footer" style="textAlign:center">
        <el-button type="primary" @click="resetPassword">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listMechanismPeople,
  addMechanismPeople,
  getCampusList,
  getPeopleInfo,
  delMechanismPeople,
  resetPassword,
  editMechanismPeople
} from '@/api/mechanism/people'
import { tel, idCard } from '@/utils/validate.js'
import opePeople from './components/opePeople'
export default {
  filters: {
    filterState(state) {
      const stateArr = ['已结业', '未结业']
      return stateArr[state]
    }
  },
  components: {
    opePeople
  },
  data() {
    const validatePhone = (rule, value, callback) => {
      if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入手机号码'))
      } else if (tel(value)) {
        callback(new Error('请输入正确的手机号码'))
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
      if (!value || value.length < 0) {
        callback(new Error('至少选择一个校区'))
      } else {
        callback()
      }
    }
    return {
      // 提交表单的防重点
      click: true,
      notUse: false,
      opeVisible: false,
      opeInfo: {},
      isAdd: false,
      name: '',
      newPassword: '',
      // 选中的id
      ids: '',
      dialogVisible: false,
      dialogVisible2: false,
      multipleSelection: [],
      chooseData: [],
      campusData: [],
      radio: '1',
      title2: '',
      showChoose: false,
      week_list: [],
      activeIndex: 0,
      // 遮罩层
      loading: true,
      loading2: true,
      // 总条数
      total: 0,
      total2: 0,
      // 班级表格数据
      classList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      edit: false,
      add: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        phone: undefined
      },
      queryParams2: {
        pageNum: 1,
        pageSize: 5
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: '人员姓名不能为空', trigger: 'blur' }
        ],
        phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
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
    $route: {
      handler: function (route) {
        if (route.path === '/mechanism/people') {
          this.queryParams.pageNum = 1
          this.queryParams.pageSize = 10
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    /** 查询班级列表 */
    getList() {
      this.loading = true
      listMechanismPeople(this.queryParams)
        .then((response) => {
          this.classList = response.rows
          this.total = response.total
          this.notUse = false
        })
        .catch(() => {
          this.notUse = true
        })
        .finally(() => {
          this.loading = false
        })
    },
    // getList2() {
    //   this.loading2 = true
    //   getCampusList(this.queryParams2).then((response) => {
    //     this.campusData = response.rows
    //     console.log('校区数据', this.campusData)
    //     this.total2 = response.total
    //     this.loading2 = false
    //   })
    // },
    // 表单重置
    reset() {
      this.form = {
        name: undefined,
        phone: undefined,
        password: undefined,
        campusConnectionList: []
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(val) {
      console.log(val)
      this.ids = val.map((item) => item.id)
      this.form.campusConnectionList = []
      val.forEach((item) => {
        var data = {}
        data.campusId = item.id
        data.campusName = item.name
        this.form.campusConnectionList.push(data)
      })

      console.log('多选数据', this.form.campusConnectionList)
    },

    /** 新增按钮操作 */
    handleAdd() {
      if (this.notUse) {
        return false
      }
      this.isAdd = true
      this.opeVisible = true
    },
    /** 修改按钮操作 */
    handleUpdate(id) {
      getPeopleInfo(id).then((response) => {
        this.isAdd = false
        this.opeInfo = response.data
        this.opeVisible = true
        // this.form = response.data
        // console.log('选中的校区', this.form.campusConnectionList)
        // if (this.form.campusConnectionList.length > 0) {
        //   this.form.campusConnectionList.forEach((item, index) => {
        //     var flagIndex = this.campusData.findIndex((item2) => {
        //       return item2.id === item.campusId
        //     })
        //     if (flagIndex != -1) {
        //       this.$refs.multipleTable.toggleRowSelection(
        //         this.campusData[flagIndex],
        //         true
        //       )
        //     }
        //   })
        // }
      })
    },
    //打开重新设置密码界面
    handleReset(row) {
      this.name = row.name
      this.ids = row.id
      this.newPassword = ''
      this.dialogVisible2 = true
    },
    // 确认重新设置密码
    resetPassword() {
      if (!this.newPassword || this.newPassword.length < 6) {
        this.msgError('密码必须大于6个字符')
        return
      }
      resetPassword({
        id: this.ids,
        pwd: this.newPassword
      }).then((res) => {
        if (res.code === 200) {
          this.msgSuccess('重置成功')
          this.dialogVisible2 = false
        }
      })
    },
    /** 提交按钮 */
    submitForm: function (data) {
      if (this.click) {
        this.click = false
        if (data.id != undefined) {
          editMechanismPeople(data)
            .then((response) => {
              if (response.code === 200) {
                this.msgSuccess('修改成功')
                this.getList()
                this.opeVisible = false
              } else {
                this.msgError(response.msg)
              }
            })
            .finally(() => {
              this.click = true
            })
        } else {
          addMechanismPeople(data)
            .then((response) => {
              if (response.code === 200) {
                this.msgSuccess('新增成功')
                this.getList()
                this.opeVisible = false
              } else {
                this.msgError(response.msg)
              }
            })
            .finally(() => {
              this.click = true
            })
        }
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id
      this.$confirm(
        '是否确认删除人员名称为"' + row.name + '"的数据项?',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        delMechanismPeople(ids).then((res) => {
          if (res.code === 200) {
            this.msgSuccess('删除成功')
            this.getList()
          }
        })
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.tip {
  font-size: 12px;
  color: #716d6d;
  padding: 0 10px 10px;
}
</style>