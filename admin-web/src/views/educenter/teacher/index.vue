<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入教师名称 " clearable @keyup.enter.native="handleQuery" maxlength="10" show-word-limit />
      </el-form-item>
      <el-form-item prop="isDeparture">
        <el-select v-model="queryParams.isDeparture" clearable placeholder="请选择是否在职">
          <el-option label="在职" value="0">
          </el-option>
          <el-option label="离职" value="1">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增教师</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格数据 -->
    <el-table v-loading="loading" :data="teacherList" stripe>
      <el-table-column label="教师姓名" align="center" prop="name" />
      <el-table-column label="手机号" align="center" prop="phone" />
      <el-table-column label="已授班级数" align="center" prop="taughtNum" />
      <el-table-column label="班级名称" align="center" prop="bishopClasses" />
      <el-table-column label="性别" align="center" prop="sex">
        <template slot-scope="{row}">
          {{ row.sex | sexFilter }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="isDeparture">
        <template slot-scope="{row}">
          {{ row.isDeparture | statusFilter }}
        </template>
      </el-table-column>
      <el-table-column label="入职时间 " align="center" prop="inductionTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.inductionTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="离职时间 " align="center" prop="departureTime" width="180">
        <template slot-scope="{row}">
          <span v-show="row.isDeparture != 0">{{ parseTime(row.departureTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">

        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改教师信息 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="left">
        <el-form-item label="教师姓名:" prop="name">
          <el-input v-model="form.name" placeholder="请输入教师姓名" maxlength="10" show-word-limit />
        </el-form-item>
        <el-form-item label="手机号码:" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="入职时间:" prop="inductionTime">
          <el-date-picker clearable size="small" v-model="form.inductionTime" type="date" value-format="yyyy-MM-dd" placeholder="选择入职时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="性别:" prop="sex">
          <el-select v-model="form.sex" placeholder="请选择性别">
            <el-option label="男" value="0" />
            <el-option label="女" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联人员:" prop="associationPersonName">
          <el-button style="backgroundColor:#426FB3;color:#ffffff;margin-right:10px" @click="chooseCampusPeason">请选择关联登录人员</el-button>
          <span v-if="form.associationPersonName">已选择：{{ form.associationPersonName }}</span>
        </el-form-item>
        <!-- 限制只有在编辑的时候才会选择在职离职 -->
        <el-form-item v-if="!isAdd" label="状态:" prop="isDeparture">
          <el-select v-model="form.isDeparture" placeholder="请选择是否在职">
            <el-option label="在职" :value="0" />
            <el-option label="离职" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isAdd && form.isDeparture === 1" label="离职时间:" prop="departureTime">
          <el-date-picker clearable size="small" v-model="form.departureTime" type="date" value-format="yyyy-MM-dd" placeholder="请输入离职时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel" style="margin-right:50px;width:100px">取 消</el-button>
        <el-button type="primary" @click="submitForm" style="width:100px">保 存</el-button>
      </div>
    </el-dialog>

    <!-- 教师详情 -->
    <el-dialog title="教师详情" :visible.sync="showDetail" width="30%" append-to-body>
      <el-form ref="form2" :model="form" label-width="120px" label-position="left">
        <el-divider />
        <el-form-item label="教师姓名:" prop="name">
          {{ form.name }}
        </el-form-item>
        <el-form-item label="手机号码:" prop="phone">
          {{ form.phone }}
        </el-form-item>
        <el-form-item label="状态:" prop="isDeparture">
          {{ form.isDeparture | statusFilter }}
        </el-form-item>
        <el-form-item label="入职时间:" prop="inductionTime">
          {{ form.inductionTime }}
        </el-form-item>
        <el-form-item v-if="form.isDeparture === 1" label="离职时间:" prop="departureTime">
          {{ form.departureTime }}
        </el-form-item>
        <el-form-item label="主教班级:" prop="bishopClasses">
          {{ form.bishopClasses }}
        </el-form-item>
        <el-form-item label="辅助班级:" prop="assistantClasses">
          {{ form.assistantClasses }}
        </el-form-item>
        <el-form-item label="关联人员:" prop="associationPersonName">
          {{ form.associationPersonName }}
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- 选择关联人员 -->
    <related :show="showChoose" :chooseId="chooseId" @close="showChoose = false" @confirmCelated="confirmCelated" />
  </div>
</template>

<script>
import {
  listTeacher,
  getTeacher,
  updateTeacher,
  delTeacher,
  addTeacher,
  exportTeacher,
  getCampusPerson
} from '@/api/educenter/teacher'
import Related from './components/Related'
import { tel } from '@/utils/validate.js'
export default {
  name: 'Teacher',
  components: {
    Related
  },
  filters: {
    sexFilter(sex) {
      var sexArr = ['男', '女']
      return sexArr[sex]
    },
    statusFilter(status) {
      var statusArr = ['在职', '离职']
      return statusArr[status]
    }
  },
  data() {
    var checkTime = (rule, value, callback) => {
      var compareDate = (d1, d2) => {
        let date1 = new Date(Date.parse(d1))
        let date2 = new Date(Date.parse(d2))
        return date1 >= date2
      }
      if (compareDate(this.form.inductionTime, value)) {
        callback(new Error('离职时间必须大于等于入职时间'))
      } else if (!value) {
        callback(new Error('请选择离职时间'))
      } else {
        callback()
      }
    }
    var checkName = (rule, value, callback) => {
      if (
        this.form.associationPersonName === '' ||
        this.form.associationPersonName === undefined ||
        this.form.associationPersonName === null
      ) {
        callback(new Error('请选择关联人员'))
      } else {
        callback()
      }
    }
    var validatePhone = (rule, value, callback) => {
      if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入手机号码'))
      } else if (tel(value)) {
        callback(new Error('请输入正确的手机号码'))
      } else {
        callback()
      }
    }
    return {
      // 新增还是修改
      isAdd: true,
      // 防重点
      click: true,
      chooseId: 0,
      showDetail: false,
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
      // 老师 老师表格数据
      teacherList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      edit: false,
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        isDeparture: undefined
      },

      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          {
            required: true,
            message: '老师名称不能为空',
            trigger: 'blur'
          }
        ],
        phone: [
          {
            required: true,
            validator: validatePhone,
            trigger: 'blur'
          }
        ],
        // associationPersonName: [
        //   {
        //     required: true,
        //     validator: checkName,
        //     trigger: 'change'
        //   }
        // ],
        // sex: [
        //   {
        //     required: true,
        //     message: '请选择性别',
        //     trigger: 'change'
        //   }
        // ],
        isDeparture: [
          {
            required: true,
            message: '请选择是否在职',
            trigger: 'change'
          }
        ],
        inductionTime: [
          {
            required: true,
            message: '入职时间不能为空',
            trigger: 'blur'
          }
        ],
        departureTime: [
          { validator: checkTime, trigger: ['change', 'blur'], required: true }
        ]
      },
      showChoose: false,
      loading2: false,
      chooseData: [],
      radio: ''
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/educenter/teacher') {
          this.queryParams.pageNum = 1
          this.queryParams.pageSize = 10
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    /** 查询老师 老师列表 */
    getList() {
      let query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading = true
      listTeacher(query).then((response) => {
        this.teacherList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 选中校区人员
    chooseCampusPeason() {
      this.showChoose = true
      console.log(this.form.associationPersonName)
      if (this.form.associationPersonName) {
        console.log(this.form.associationPerson)
        this.chooseId = this.form.associationPerson
      }
    },
    // 新增表单的取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        phone: undefined,
        sex: undefined,
        isDeparture: undefined,
        inductionTime: undefined,
        departureTime: undefined,
        personType: undefined
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
    // 确认关联的课程名或教师名
    confirmCelated(data) {
      this.form.associationPerson = data.id
      // console.log(this.form.associationPerson)
      this.form.associationPersonName = data.name
      this.form.personType = data.personType
      console.log('form', this.form)
      this.showChoose = false
      this.$refs.form.validateField('helpTeacherName')
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.isAdd = true
      this.chooseId = -1
      this.title = '新增教师'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.isAdd = false
      this.chooseId = row.campusPerson
      const id = row.id || this.ids
      getTeacher(id).then((res) => {
        if (res.code === 200) {
          this.form = res.data
          this.form
          this.open = true
          this.title = '编辑教师'
        } else {
          this.msgError(res.msg)
        }
      })
    },
    // 教师详情
    handleDetail(row) {
      this.reset()
      const id = row.id || this.ids
      getTeacher(id).then((response) => {
        this.form = response.data
        this.showDetail = true
      })
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.click) {
            this.click = false
            if (this.form.id != undefined) {
              updateTeacher(this.form)
                .then((response) => {
                  if (response.code === 200) {
                    this.click = true
                    this.msgSuccess('修改成功')
                    this.edit = false
                    this.open = false
                    this.getList()
                  } else {
                    this.msgError(response.msg)
                  }
                })
                .finally(() => {
                  this.click = true
                })
            } else {
              addTeacher(this.form)
                .then((response) => {
                  if (response.code === 200) {
                    this.msgSuccess('新增成功')
                    this.open = false
                    this.click = true
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
      const h = this.$createElement
      this.$confirm(
        h('p', null, [
          h('span', null, `是否确认删除教师为"${row.name}"的教师?`),
          h(
            'p',
            { style: 'font-size: 12px;color: #EDCF8E' },
            '注：已有班级的教师需更换授课老师后才能删除哦~'
          )
        ]),
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(function () {
          return delTeacher(row.id)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function () {})
    }
  }
}
</script>