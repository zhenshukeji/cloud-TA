<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入校区名称" size="small" maxlength="16" show-word-limit @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item prop="mgrName">
        <el-input v-model="queryParams.mgrName" placeholder="请选择校区负责人" clearable size="small" maxlength="16" show-word-limit @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item prop="createByName">
        <el-input v-model="queryParams.createByName" placeholder="请输入创建人名称" clearable size="small" maxlength="16" show-word-limit @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <!-- 社区版（OS010）：不提供「新增校区」。单机构单校区（指引 §4.2），
             且后端 POST /base/campus 已在 OS009 移除。 -->
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="regionList" stripe>
      <el-table-column label="校区名称" align="center" prop="name" />
      <el-table-column label="校区负责人" align="center" prop="mgrName" />
      <el-table-column label="登录账号" align="center" prop="mgrPhone" />
      <el-table-column label="联系方式" align="center" prop="mgrContactNumber" />
      <el-table-column label="所在城市" align="center" prop="city" />
      <el-table-column label="所在省份" align="center" prop="province" />
      <el-table-column label="详细地址" align="center" prop="address" min-width="150px" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="创建人" align="center" prop="createByName" />
      <el-table-column label="操作" align="center" min-width="150px">
        <template slot-scope="{row}">
          <el-button size="mini" type="text" @click="entryCampus(row.id)">进入校区</el-button>
          <el-button size="mini" type="text" @click="handleUpdate(row.id)">编辑</el-button>
          <el-button size="mini" type="text" @click="handleReset(row)">重置密码</el-button>
          <el-button size="mini" type="text" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑班级对话框 -->
    <el-dialog :title="title" :visible.sync="dialogVisible" width="40%" append-to-body :close-on-click-modal="false">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="130px" label-position="left">
        <el-form-item label="校区名称:" prop="name">
          <el-input v-model="form.name" placeholder="请输入校区名称" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item label="负责人姓名:" prop="mgrName">
          <el-input v-model="form.mgrName" placeholder="请输入负责人姓名" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item label="负责人联系方式:" prop="mgrContactNumber">
          <el-input v-model="form.mgrContactNumber" placeholder="请输入负责人联系方式" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="负责人登录账号:" prop="mgrPhone">
          <el-input v-model="form.mgrPhone" placeholder="请输入负责人登录账号" maxlength="14" show-word-limit />
          <div style="fontSize:14px">账号由4-14位英文和数字组合，首位为字母</div>
        </el-form-item>
        <el-form-item label="所在城市:" prop="province">
          <el-select v-model="form.province" placeholder="省级地区" @change="choseProvince">
            <el-option v-for="item in province" :key="item.id" :label="item.value" :value="item.value" />
          </el-select>
          <el-select v-model="form.city" placeholder="市级地区">
            <el-option v-for="item in shi1" :key="item.id" :label="item.value" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细地址:" prop="address">
          <el-input v-model="form.address" type="textarea" :rows="5" placeholder="请输入详细地址，包括省市区，例如广西省南宁市西乡塘区明秀东路18号" maxlength="85" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" style="textAlign:center">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <!-- <el-button @click="cancel">取 消</el-button> -->
      </div>
    </el-dialog>

    <el-dialog title="重置密码" :visible.sync="dialogVisible2" width="30%">
      <el-divider />
      <span style="fontSize:18px">
        <svg-icon icon-class="warn" style="color:#FF9E5D" />重新设置校区负责人"{{ name }}"密码
      </span>
      <div style="fontSize:18px;margin-top:20px">
        新密码：
        <el-input v-model="newPassword" placeholder="请输入新密码" style="width:50%" maxlength="16" show-word-limit />
      </div>

      <div slot="footer" class="dialog-footer" style="textAlign:center">
        <el-button type="primary" @click="resetPassword">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 社区版不含附近机构/课程等基于经纬度的平台能力，故不再做经纬度校验提示 -->
  </div>
</template>

<script>
import {
  getCampusList,
  getCampusInfo,
  editCampusInfo,
  resetPassword,
  delCampus,
  entryCampus
} from '@/api/mechanism/region'
import { tel } from '@/utils/validate.js'
import mapJson from './map.json'

export default {
  filters: {
    filterState(state) {
      const stateArr = ['已结业', '未结业']
      return stateArr[state]
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
      click: true,
      notUse: false,
      ids: '',
      name: '',
      newPassword: '',
      province: [],
      shi1: [],
      dialogVisible: false,
      dialogVisible2: false,
      multipleSelection: [],
      chooseData: [],
      campusData: [],
      radio: '1',
      title2: '',
      showChoose: false,
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 校区表格数据
      regionList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      edit: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        mgrName: undefined,
        createByName: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: '校区名称不能为空', trigger: 'blur' }
        ],
        mgrContactNumber: [
          { required: true, validator: validatePhone, trigger: 'change' }
        ],
        mgrName: [
          { required: true, message: '负责人姓名不能为空', trigger: 'blur' }
        ],
        mgrPhone: [
          { required: true, validator: validateUser, trigger: 'change' }
        ],
        province: [
          { required: true, message: '所在城市不能为空', trigger: 'change' }
        ],
        city: [
          { required: true, message: '所在城市不能为空', trigger: 'change' }
        ],
        address: {
          required: true,
          message: '详细地址不能为空',
          trigger: 'change'
        }
      }
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        if (route.path === '/mechanism/region') {
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
      // 去除参数
      for (var key in this.queryParams) {
        if (
          this.queryParams[key] === '' ||
          this.queryParams[key] === undefined
        ) {
          delete this.queryParams[key]
        }
      }
      this.loading = true
      getCampusList(this.queryParams)
        .then((response) => {
          this.regionList = response.rows
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
    // 进入校区
    entryCampus(ids) {
      entryCampus(ids).then((res) => {
        if (res.code === 200) {
          this.$store.dispatch('clearRole')
          location.reload()
        }
      })
    },
    // 表单重置
    reset() {
      this.form = {
        hasClass: undefined,
        haveClass: undefined,
        bishopTeacherId: undefined,
        helpTeacherId: undefined,
        haveTime: undefined,
        sumStudent: undefined,
        className: undefined,
        classCourseId: undefined,
        classType: undefined,
        classState: undefined,
        createTime: undefined,
        createByName: undefined,
        mgrContactNumber: undefined
      }
      this.getCityData()
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
    /** 修改按钮操作 */
    handleUpdate(id) {
      this.reset()
      getCampusInfo(id).then((response) => {
        this.form = response.data
        this.edit = true
        this.title = '编辑校区'
        this.dialogVisible = true
        // 回填省份后重新加载城市下拉，否则编辑时城市下拉为空
        if (this.form.province) {
          const p = this.province.find(x => x.value === this.form.province)
          if (p) this.shi1 = p.children
        }
      })
    },

    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.click) {
            this.click = false
            if (this.form.id !== undefined) {
              editCampusInfo(this.form)
                .then((response) => {
                  if (response.code === 200) {
                    this.msgSuccess('修改成功')
                    this.edit = false
                    this.dialogVisible = false
                    this.getList()
                  } else {
                    this.msgError(response.msg)
                  }
                })
                .finally(() => {
                  this.click = true
                })
            } else {
              // 社区版（OS010）：不支持新建校区（单机构单校区，指引 §4.2）
              this.msgError('社区版不支持新建校区')
              this.click = true
            }
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id
      this.$confirm(
        '是否确认删除负责人姓名为"' + row.mgrName + '"的数据项?',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        delCampus(ids).then((res) => {
          if (res.code === 200) {
            this.msgSuccess('删除成功')
            this.getList()
          }
        })
      })
    },
    // 获取省份
    getCityData: function() {
      var that = this
      var data = mapJson
      that.province = []
      that.city = []
      that.block = []
      // 省市区数据分类
      for (var item in data) {
        if (item.match(/0000$/)) {
          // 省
          that.province.push({ id: item, value: data[item], children: [] })
        } else if (item.match(/00$/)) {
          // 市
          that.city.push({ id: item, value: data[item], children: [] })
        } else {
          // 区
          that.block.push({ id: item, value: data[item] })
        }
      }
      // 分类市级
      for (var index in that.province) {
        for (var index1 in that.city) {
          if (
            that.province[index].id.slice(0, 2) ===
            that.city[index1].id.slice(0, 2)
          ) {
            that.province[index].children.push(that.city[index1])
          }
        }
      }
      // 如果省份存在 需要获取城市名单
      if (this.form.province) {
        this.choseProvince(this.form.province)
      }
    },
    // 获取城市
    choseProvince: function(e) {
      for (var index2 in this.province) {
        if (e === this.province[index2].value) {
          this.shi1 = this.province[index2].children
          this.form.city = this.province[index2].children[0].value
        }
      }
    },
    // 打开重新设置密码界面
    handleReset(row) {
      this.name = row.mgrName
      this.ids = row.id
      this.newPassword = ''
      this.dialogVisible2 = true
    },
    // 确认重新设置密码
    resetPassword() {
      if (!this.newPassword || this.newPassword.length <= 6) {
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
