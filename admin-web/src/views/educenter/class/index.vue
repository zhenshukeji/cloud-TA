<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item prop="className">
        <el-input v-model="queryParams.className" placeholder="请输入班级名称" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item prop="classState">
        <el-select v-model="queryParams.classState" clearable placeholder="请选择班级状态">
          <el-option label="正常" value="0">
          </el-option>
          <el-option label="已废弃" value="1">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item prop="bishopTeacherName">
        <el-input v-model="queryParams.bishopTeacherName" placeholder="请输入主教老师" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item prop="helpTeacherName">
        <el-input v-model="queryParams.helpTeacherName" placeholder="请输入辅助老师" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增班级</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table v-loading="loading" :data="classList" stripe>
      <el-table-column label="班级名称" align="center" prop="className" />
      <el-table-column label="关联课程" align="center" prop="courseName" />
      <el-table-column label="主教老师" align="center" prop="bishopTeacherName" />
      <el-table-column label="辅助老师" align="center" prop="helpTeacherName" />
      <el-table-column label="上课时间" align="center">
        <template slot-scope="{row}">
          <div v-for="(item,index) in row.classTimes" :key="index">
            {{ item }}
          </div>
        </template>
      </el-table-column>/
      <el-table-column label="人员上限" align="center" width="100px">
        <template slot-scope="{row}">
          {{row.existStudents }} / {{ row.sumStudents }}
        </template>
      </el-table-column>
      <el-table-column label="已排课次" align="center" prop="hasClass">
        <template slot-scope="{row}">
          {{ row.hasClass }} 次
        </template>
      </el-table-column>
      <el-table-column label="班级状态" align="center" prop="classState" width="100px">
        <template slot-scope="scope">
          {{ scope.row.classState | filterState }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-if="row.classState === 0" slot-scope="{row}">
          <el-button size="mini" type="text" @click="handleUpdate(row)">编辑</el-button>
          <el-button size="mini" type="text" @click="handleManager(row)">学员管理</el-button>
          <el-button size="mini" type="text" @click="handleDelete(row)">废弃</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑班级对话框 -->
    <el-dialog :title="title" :visible.sync="dialogVisible" width="800px" :close-on-click-modal="false">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称" style="width:400px" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item label="班级类型" prop="classType">
          <!-- 社区版只有常规班级；体验班级链路已按指引 §3.2 移除 -->
          <el-select v-model="form.classType" placeholder="请选择班级类型" style="width:400px" disabled>
            <el-option label="常规班级" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联课程" prop="courseName">
          <template v-if="edit">
            <el-input v-model="form.courseName" :disabled="true" placeholder="请输入关联课程" style="width:400px" />
          </template>
          <template v-else>
            <el-button style="backgroundColor:#426FB3;color:#ffffff" @click="handleCourse">请选择关联课程</el-button>
            <span v-if="form.courseName">已选择：{{ form.courseName }}</span>
          </template>
        </el-form-item>
        <el-form-item label="班级人数" prop="sumStudents">
          <el-input v-model="form.sumStudents" placeholder="请输入班级人数" style="width:400px" maxlength="10" show-word-limit />
        </el-form-item>

        <el-form-item label="主教老师" prop="bishopTeacherName">
          <el-button style="backgroundColor:#426FB3;color:#ffffff" @click="handleTeacher(1)">请选择主教老师</el-button>
          <span v-if="form.bishopTeacherName">已选择：{{ form.bishopTeacherName }}</span>
        </el-form-item>
        <el-form-item label="上课时间" prop="classTimeList">
          <el-checkbox-group v-model="week_list" @change="changeWeekDay">
            <el-checkbox v-for="(item,index) in timeList" :key="item.value" :label="index">{{ item.value }}</el-checkbox>
          </el-checkbox-group>
          <div v-for="entry in filteredTimeList" :key="entry.item.value" class="time-item">
            <span>{{ entry.item.value }}：</span>
            <el-time-picker is-range v-model="entry.item.time" :clearable="false" range-separator="至" :start-placeholder="entry.item.value" :end-placeholder="entry.item.value" value-format="HH:mm" @change="(val) => changeTime(val, entry.index + 1)"></el-time-picker>
          </div>
        </el-form-item>
        <el-form-item label="辅助老师" prop="helpTeacherName">
          <el-button style="backgroundColor:#426FB3;color:#ffffff" @click="handleTeacher(2)">请选择辅助老师</el-button>
          <span v-if="form.helpTeacherName">已选择：{{ form.helpTeacherName }}</span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 关联人员 -->
    <related :show="teacherVisible" :relatedCourseData="relatedCourseData" @confirmCelated="confirmCelated" @close="teacherVisible =false,dialogVisible = true" />

    <!-- 学员管理 -->
    <student-manager :show="studentVisible" :classId="classId" @close="closeStudent" />
  </div>
</template>

<script>
import {
  listClass,
  getClass,
  updateClass,
  delClass,
  addClass,
  exportClass,
  getCourseList,
  getTeacherList,
  getStudentList,
  getAddStudentList
} from '@/api/educenter/class'
import Related from './components/Related'
import studentManager from './components/studentManager'
export default {
  name: 'Class',
  components: {
    Related,
    studentManager
  },
  filters: {
    filterState(state) {
      const stateArr = ['正常', '已废弃']
      return stateArr[state]
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/educenter/class') {
          this.queryParams.pageNum = 1
          this.queryParams.pageSize = 10
          this.getList()
        }
      },
      immediate: true
    }
  },
  data() {
    var checkTime = (rule, value, callback) => {
      if (this.form.classTimeList.length <= 0) {
        callback(new Error('请选择上课时间'))
      } else {
        callback()
      }
    }
    var checkCourseName = (rule, value, callback) => {
      if (!this.form.courseName) {
        callback(new Error('关联课程不能为空'))
      } else {
        callback()
      }
    }
    var checkBishopTeacherName = (rule, value, callback) => {
      if (!this.form.bishopTeacherName) {
        callback(new Error('主教老师不能为空'))
      } else {
        callback()
      }
    }
    var checkHelpTeacherName = (rule, value, callback) => {
      if (this.form.helpTeacherName == this.form.bishopTeacherName) {
        callback(new Error('主教老师、辅助老师不能为同一人'))
      } else {
        callback()
      }
    }
    // 验证为正整数
    var checkNumber = (rule, value, callback) => {
      var reg = /^[0-9]*[1-9][0-9]*$/
      if (!value && value != 0) {
        return callback(new Error('不能为空'))
      } else {
        if (!reg.test(value)) {
          callback(new Error('请输入正整数'))
        } else {
          if (value < 0) {
            callback(new Error('输入的值必须大于0'))
          } else {
            callback()
          }
        }
      }
    }
    return {
      // 包含老师id、
      relatedCourseData: {},
      // 学生ID
      studentId: -1,
      // 班级ID
      classId: -1,
      addStudentVisible: false,
      click: true,
      studentVisible: false,
      dialogVisible: false,
      isHelper: false,
      // 学员管理的数据
      studentData: [],
      // 新增学员管理的数据
      addStudentData: [],
      // 弹出关联教师还是课程
      relatedCourse: false,
      // 选择的课程名字
      courseName: '',
      // 选了哪个课程
      chooseIndex: 0,
      // 选择老师或课程数据
      chooseData: [],
      teacherVisible: false,
      timeList: [
        {
          value: '周一',
          time: ['07:00', '22:00']
        },
        {
          value: '周二',
          time: ['07:00', '22:00']
        },
        {
          value: '周三',
          time: ['07:00', '22:00']
        },
        {
          value: '周四',
          time: ['07:00', '22:00']
        },
        {
          value: '周五',
          time: ['07:00', '22:00']
        },
        {
          value: '周六',
          time: ['07:00', '22:00']
        },
        {
          value: '周日',
          time: ['07:00', '22:00']
        }
      ],
      week_list: [],
      // 遮罩层
      loading: true,
      loading2: false,
      loading3: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
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
        className: undefined,
        classState: undefined,
        courseName: undefined,
        bishopTeacherName: undefined,
        helpTeacherName: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        hasClass: [
          { required: true, message: '已授课时不能为空', trigger: 'blur' }
        ],
        haveClass: [
          { required: true, message: '已排课次不能为空', trigger: 'blur' }
        ],
        bishopTeacherName: [
          { required: true, validator: checkBishopTeacherName, trigger: 'blur' }
        ],
        helpTeacherName: [{ validator: checkHelpTeacherName, trigger: 'blur' }],
        sumStudents: [
          { required: true, validator: checkNumber, trigger: 'blur' }
        ],
        className: [
          { required: true, message: '班级名称不能为空', trigger: 'blur' }
        ],
        courseName: [
          { required: true, validator: checkCourseName, trigger: 'blur' }
        ],
        classType: [
          { required: true, message: '请选择班级类型', trigger: 'change' }
        ],
        classTimeList: [
          { required: true, validator: checkTime, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    // 已勾选星期对应的上课时间项（预过滤，避免同元素混用 v-for 与 v-if）
    filteredTimeList() {
      var list = []
      this.timeList.forEach((item, index) => {
        if (this.week_list.indexOf(index) > -1) {
          list.push({ item: item, index: index })
        }
      })
      return list
    }
  },
  methods: {
    /** 查询班级列表（社区版只有常规班级） */
    getList() {
      let query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading = true
      listClass(query).then((response) => {
        this.classList = response.rows
        this.total = response.total
        this.loading = false
      })
    },

    // 打开关联课程
    handleCourse() {
      //关闭新增/修改弹窗的同时打开关联弹窗
      this.dialogVisible = false
      this.teacherVisible = true
      this.relatedCourseData.relatedCourse = true
      this.relatedCourseData.isExperience = this.form.classType
    },
    // 打开关联老师
    handleTeacher(index) {
      this.relatedCourseData.relatedCourse = false
      this.relatedCourseData.chooseId = -1
      console.log('关联教师')
      // 1为主教老师
      if (index === 1) {
        this.isHelper = false
        this.relatedCourseData.chooseId = this.form.bishopTeacherId
      } else {
        this.isHelper = true
        this.relatedCourseData.chooseId = this.form.helpTeacherId
      }
      this.dialogVisible = false
      this.teacherVisible = true
      // this.relatedCourse = false
    },
    // 关闭学员管理
    closeStudent() {
      this.studentVisible = false
      this.resetQuery()
    },
    // 新增表单的取消按钮
    cancel() {
      this.dialogVisible = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        hasClass: undefined,
        haveClass: undefined,
        haveTime: undefined,
        helpTeacherName: undefined,
        bishopTeacherName: undefined,
        sumStudents: undefined,
        className: undefined,
        courseName: undefined,
        // 社区版只有常规班级（后端 ClassEnum.ORDINARY=1）
        classType: 1,
        classState: undefined
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 确认关联的课程名或教师名
    confirmCelated(data) {
      console.log(data)
      // 关联课程
      if (this.relatedCourseData.relatedCourse) {
        this.form.courseName = data.courseName
        this.form.classCourseId = data.id
        this.$refs.form.validateField('courseName')
      } else {
        // 关联老师
        if (this.isHelper) {
          this.form.helpTeacherName = data.name
          this.form.helpTeacherId = data.id
          this.$refs.form.validateField('helpTeacherName')
        } else {
          this.form.bishopTeacherName = data.name
          this.form.bishopTeacherId = data.id
          this.$refs.form.validateField('bishopTeacherName')
        }
      }
      this.teacherVisible = false
      this.dialogVisible = true
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.dialogVisible = true
      this.reset()
      this.add = true
      this.edit = false
      this.title = '添加班级'
      this.form.classTimeList = []
      this.week_list = []
      this.timeList.forEach((item, index) => {
        item.time = ['07:00', '22:00']
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getClass(id).then((response) => {
        this.week_list = []
        this.dialogVisible = true
        this.form = response.data
        this.add = false
        this.edit = true
        this.timeList.forEach((item, index) => {
          item.time = ['07:00', '22:00']
        })
        this.form.classTimeList.forEach((item, index) => {
          this.week_list.push(item.weekDay - 1)
          this.$set(this.timeList[item.weekDay - 1], 'time', [
            item.clsStartTime,
            item.clsEndTime
          ])
        })
        console.log('选中的', this.week_list)
        this.title = '编辑班级'
      })
    },
    // 修改上课时间
    changeTime(val, num) {
      var flagIndex = this.form.classTimeList.findIndex((item) => {
        return item.weekDay === num
      })
      console.log('匹配的', flagIndex)
      if (flagIndex != -1) {
        this.form.classTimeList[flagIndex].clsStartTime = val[0]
        this.form.classTimeList[flagIndex].clsEndTime = val[1]
      }
      console.log('数据', this.form.classTimeList)
    },
    // 选中星期几上课
    changeWeekDay(val) {
      console.log(val)
      this.form.classTimeList = []
      val.forEach((item, index) => {
        // 定义数据格式
        let data = {
          clsStartTime: this.timeList[item]['time'][0],
          clsEndTime: this.timeList[item]['time'][1],
          weekDay: item + 1
        }
        this.form.classTimeList.push(data)
        this.$refs.form.validateField('classTimeList')
      })
    },

    // 学员管理
    handleManager(row) {
      this.classId = row.id
      this.studentVisible = true
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.click) {
            this.click = false
            if (this.form.id != undefined) {
              updateClass(this.form)
                .then((response) => {
                  if (response.code === 200) {
                    this.msgSuccess('修改成功')
                    this.edit = false
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
            } else {
              addClass(this.form)
                .then((response) => {
                  if (response.code === 200) {
                    this.msgSuccess('新增成功')
                    this.edit = false
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
      const h = this.$createElement
      this.$confirm(
        h('p', null, [
          h(
            'span',
            null,
            `一旦作废无法恢复,请确认是否作废班级名称为"${row.className}"的班级`
          ),
          h(
            'p',
            { style: 'font-size: 12px;color: red' },
            '注：已有学员的班级不可废弃，已授课的班级不可废弃'
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
          return delClass(ids)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('作废成功')
        })
        .catch(function () {})
    }
  }
}
</script>
<style lang='scss' scoped>
.time-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.time-item span {
  white-space: nowrap;
}
::v-deep .el-radio__label {
  display: none;
}
</style>