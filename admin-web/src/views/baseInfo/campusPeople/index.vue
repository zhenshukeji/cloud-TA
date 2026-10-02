<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入登录账号" clearable @keyup.enter.native="handleQuery" maxlength="14" show-word-limit />
      </el-form-item>
      <el-form-item prop="roleId">
        <el-select v-model="queryParams.roleId" placeholder="请选择岗位" clearable>
          <el-option v-for="(dict,index) in roleData" :key="dict.id" :label="dict.roleName" :value="dict.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加校区人员</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="campus_personnelList">
      <el-table-column label="人员姓名" align="center" prop="name" />
      <el-table-column label="登录账号" align="center" prop="phone" />
      <el-table-column label="联系方式" align="center" prop="contactNumber" />
      <el-table-column label="岗位" align="center" prop="roleNames" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="入职时间" align="center" prop="joinTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.joinTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <!-- <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button> -->
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row)">删除</el-button>
          <el-button size="mini" type="text" @click="handleReset(scope.row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加/修改校区人员 -->
    <el-dialog :title="title" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="left">
        <el-form-item label="姓名:" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item label="岗位:" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择岗位">
            <el-option v-for="(dict,index) in roleData" :key="dict.id" :label="dict.roleName" :value="dict.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="登录账号:" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入登录账号" maxlength="14" show-word-limit />
          <div style="fontSize:14px">账号由4-14位英文和数字组合，首位为字母</div>
        </el-form-item>
        <el-form-item label="联系方式:" prop="contactNumber">
          <el-input v-model="form.contactNumber" placeholder="请输入联系方式" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="入职时间：" prop="joinTime">
          <el-date-picker clearable v-model="form.joinTime" type="date" value-format="yyyy-MM-dd" placeholder="请选择入职日期" :disabled="edit" style="width: 200px">
          </el-date-picker>
        </el-form-item>
        <el-form-item v-if="add" label="初始密码:" prop="password">
          <el-input v-model="form.password" placeholder="请输入初始密码" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item v-if="add" label="教师:" prop="isTeacher">
          <el-checkbox v-model="form.isTeacher" :true-label="1" :false-label="0">同步新增一个教师到教师管理</el-checkbox>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" style="textAlign:center">
        <el-button @click="cancel" style="margin-right:50px;width:100px">取 消</el-button>
        <el-button type="primary" @click="submitForm" style="width:100px">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查看 -->
    <!-- <el-dialog title="查看校区人员" :visible.sync="detailVisible" width="500px">
      <el-divider />
      <el-form ref="form" :model="form" label-width="100px" label-position="left">
        <el-form-item label="人员姓名:">
          {{ form.name }}
        </el-form-item>
        <el-form-item label="岗位:">
          <span v-for="(item,index) in form.roleNameArr" :key="item">
            {{ item }}
            <span v-if="index < form.roleNameArr.length - 1">、</span>
          </span>
        </el-form-item>
        <el-form-item label="手机号:" prop="phone">
          {{ form.phone }}
        </el-form-item>
        <el-form-item label="入职时间：" prop="joinTime">
          {{ form.joinTime }}
        </el-form-item>
        <el-form-item label="创建时间：" prop="joinTime">
          {{ form.joinTime }}
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" style="textAlign:center">
        <el-button type="primary" @click="detailVisible = false">确 定</el-button>
      </div>
    </el-dialog> -->

    <!-- 重置密码 -->
    <el-dialog title="重置密码" :visible.sync="resetVisible" width="500px">
      <el-divider />
      <span style="fontSize:16px;color:#999999">
        <svg-icon icon-class="warn" style="color:#FF9E5D;margin-right:10px" />
        重新设置"{{ name }}"的密码
      </span>
      <div style="fontSize:16px;margin-top:20px">
        新密码：
        <el-input v-model="newPassword" placeholder="请输入新密码" style="width:50%" />
      </div>

      <div slot="footer" class="dialog-footer" style="textAlign:center">
        <el-button type="primary" @click="resetPassword">确 认</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {
  listCampus_personnel,
  getCampus_personnel,
  updateCampus_personnel,
  delCampus_personnel,
  addCampus_personnel,
  exportCampus_personnel,
  getRoleList,
  resetPassword
} from '@/api/baseInfo/campusPeople'
import { tel } from '@/utils/validate.js'
export default {
  name: 'campusPeople',
  data() {
    var validatePhone = (rule, value, callback) => {
      if (value === '' || value === undefined || value === null) {
        callback(new Error('联系方式不能为空'))
      } else if (tel(value)) {
        callback(new Error('请输入正确的手机号码'))
      } else {
        callback()
      }
    }
    var validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('密码不能为空'))
      } else if (value.length <= 6) {
        callback(new Error('密码必须大于6个字符'))
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
      detailVisible: false,
      id: '',
      newPassword: '',
      name: '',
      resetVisible: false,
      //弹窗显示
      dialogVisible: false,
      // 岗位数组
      roleData: [],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 校区人员表格数据
      campus_personnelList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      edit: false,
      add: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        campusName: undefined,
        roleId: undefined,
        phone: undefined
      },

      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          {
            required: true,
            message: '姓名不能为空',
            trigger: 'blur'
          }
        ],
        roleIds: [
          {
            required: true,
            message: '请选择岗位',
            trigger: ['blur', 'change']
          }
        ],
        phone: [
          {
            required: true,
            validator: validateUser,
            trigger: ['blur', 'change']
          }
        ],
        contactNumber: [
          {
            required: true,
            validator: validatePhone,
            trigger: 'change'
          }
        ],
        joinTime: [
          {
            required: true,
            message: '请选择入职日期',
            trigger: 'change'
          }
        ],
        password: [
          {
            required: true,
            validator: validatePassword,
            trigger: 'blur'
          }
        ]
      }
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/baseInfo/campusPeople') {
          this.queryParams.pageNum = 1
          this.queryParams.pageSize = 10
          this.getList()
          this.getRoleList()
        }
      },
      immediate: true
    }
  },
  methods: {
    /** 查询校区人员列表 */
    getList() {
      let query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading = true
      listCampus_personnel(query).then((response) => {
        this.campus_personnelList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getRoleList() {
      getRoleList().then((res) => {
        if (res.code === 200) {
          this.roleData = Array.from(
            new Set(
              res.data.map((item) => ({
                id: item.roleId,
                roleName: item.roleName
              }))
            )
          )
          console.log('岗位data', this.roleData)
        }
      })
    },
    // 新增表单的取消按钮
    cancel() {
      this.add = false
      this.edit = false
      this.dialogVisible = false
    },
    // 表单重置
    reset() {
      this.form = {
        name: undefined,
        roleIds: undefined,
        phone: undefined,
        contactNumber: undefined,
        joinTime: undefined,
        password: undefined,
        isTeacher: undefined
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
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.form.roleIds = []
      this.add = true
      this.edit = false
      this.dialogVisible = true
      this.title = '添加校区人员'
    },
    // 查看
    handleView(row) {
      this.reset()
      const id = row.id || this.ids
      getCampus_personnel(id).then((response) => {
        this.form = response.data
        this.form.roleNameArr = []
        this.form.roleIds.forEach((item) => {
          this.form.roleNameArr.push(
            this.roleData.find((item2) => {
              return item2.id == item
            }).roleName
          )
        })
        console.log(this.form.roleNameArr)
        this.detailVisible = true
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getCampus_personnel(id).then((response) => {
        this.form = response.data
        this.edit = true
        this.add = false
        this.dialogVisible = true
        this.title = '修改校区人员'
      })
    },

    /** 提交按钮 */
    submitForm: function () {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.click) {
            this.click = false
            if (this.form.id != undefined) {
              updateCampus_personnel(this.form)
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
              addCampus_personnel(this.form)
                .then((response) => {
                  if (response.code === 200) {
                    this.msgSuccess('新增成功')
                    this.add = false
                    this.dialogVisible = false
                    this.getList()
                  } else {
                    this.msgError(response.msg)
                  }
                })
                .finally(() => {
                  this.click = true
                })
            }
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$confirm('登录人员删除后需要重新设置，是否确认删除？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function () {
          return delCampus_personnel(ids)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function () {})
    },
    //  打开重设密码的弹窗
    handleReset(row) {
      this.resetVisible = true
      this.name = row.name
      // this.campusName = row.campusName
      this.id = row.id
    },
    // 重置密码
    resetPassword() {
      if (!this.newPassword || this.newPassword.length < 6) {
        this.msgError('密码必须大于6个字符')
        return
      }
      resetPassword({
        id: this.id,
        pwd: this.newPassword
      }).then((res) => {
        if (res.code === 200) {
          this.resetVisible = false
          this.msgSuccess('密码重设成功')
        }
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有校区人员数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function () {
          return exportCampus_personnel(queryParams)
        })
        .then((response) => {
          this.download(response.msg)
        })
        .catch(function () {})
    }
  }
}
</script>