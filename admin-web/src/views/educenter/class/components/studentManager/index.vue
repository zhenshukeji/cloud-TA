<template>
  <div class="studentManange-page">
    <el-dialog title="学员管理" :visible.sync="show" width="50%" :before-close="handleClose" :close-on-click-modal="false">
      <el-divider />
      <el-input v-model="queryParams.studentName" clearable placeholder="请输入学员姓名" style="width:200px" maxlength="10" show-word-limit />
      <el-button type="primary" @click="handleQuery">搜索</el-button>
      <el-button type="primary" style="background:#4FB03C;border:none" @click="openClassList">
        +从其他班级导入
      </el-button>
      <el-button type="primary" style="background:#FF7413;border:none" @click="openAddStudent">
        +新增学员
      </el-button>
      <!-- 列表 -->
      <el-table ref="multipleTable" v-loading="loading" :data="studentData" stripe size="small" style="margin-top:30px" @selection-change="handleStudentChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="学员姓名" align="center" prop="studentName" />
        <el-table-column label="联系手机号" align="center" prop="studentsPhone" />
        <el-table-column label="总课次" align="center" prop="totalNumber" />
        <el-table-column label="剩余课次" align="center" prop="remainingNumber" />
        <el-table-column label="报名时间" align="center" prop="registerTime" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template>
            <!-- 社区版（OS010）：不含家校通消息发送，该入口随 /homeschool/* 一并移除 -->
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getStudentList" />
      <div style="textAlign:center">
        <el-button type="danger" style="width:100px;margin-top:20px" @click="removeStudent">移出</el-button>
      </div>

      <!-- 选择学员 -->
      <el-dialog title="选择学员" :visible.sync="addStudentVisible" width="50%" append-to-body>
        <el-divider />
        <el-input v-model="queryParams2.studentName" clearable placeholder="请输入学员名称" style="width:200px" maxlength="10" show-word-limit />
        <el-button type="primary" style="margin-bottom:30px" @click="handleQuery2">搜索</el-button>
        <el-table v-loading="loading2" :data="addStudentData" stripe size="small" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column label="学员姓名" align="center" prop="studentName" />
          <el-table-column label="联系手机号" align="center" prop="studentPhone" />
          <el-table-column label="总课次" align="center" prop="totalNumber" />
          <el-table-column label="剩余课次" align="center" prop="remainingNumber" />
        </el-table>
        <pagination v-show="total2 > 0" :total="total2" :page.sync="queryParams2.pageNum" :limit.sync="queryParams2.pageSize" @pagination="getAddStudentList" />
        <div style="display:flex;justify-content:center;margin-top:60px">
          <el-button type="primary" @click="confirm">
            确定
          </el-button>
        </div>
      </el-dialog>

      <!-- 从班级导入学员 -->
      <el-dialog title="选择班级" :visible.sync="classVisible" append-to-body>
        <el-divider />
        <el-input v-model="queryParams3.className" clearable placeholder="请输入班级名称" style="width:200px" maxlength="10" show-word-limit />
        <el-button type="primary" style="margin-bottom:30px" @click="handleQuery3">搜索</el-button>
        <el-table v-loading="loading3" :data="classList" stripe size="small">
          <el-table-column align="center" width="100">
            <template slot-scope="{$index}">
              <el-radio v-model="chooseClass" :label="$index">
                &nbsp;
              </el-radio>
            </template>
          </el-table-column>
          <el-table-column label="班级名称" align="center" prop="className" />
          <el-table-column label="学员数量" align="center" prop="existStudents" />
        </el-table>
        <pagination v-show="total3 > 0" :total="total3" :page.sync="queryParams3.pageNum" :limit.sync="queryParams3.pageSize" @pagination="getExtraClass" />
        <div style="display:flex;justify-content:center;margin-top:40px">
          <el-button type="primary" @click="exportStudent">
            确定
          </el-button>
        </div>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
import {
  getStudentList,
  getAddStudentList,
  AddStudent,
  removeStudent,
  getExtraClass,
  exportStudent
} from '@/api/educenter/class'
export default {
  components: {},
  props: {
    show: {
      type: Boolean,
      default: false
    },
    classId: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      exportClick: true,
      chooseClass: -1,
      classVisible: false,
      ids: [],
      click: true,
      multipleSelection: [],
      addStudentVisible: false,
      loading: false,
      loading2: false,
      loading3: false,
      studentData: [],
      addStudentData: [],
      classList: [],
      total: 0,
      total2: 0,
      total3: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        id: '',
        studentName: undefined
      },
      // 新增学员的翻页
      queryParams2: {
        pageNum: 1,
        pageSize: 5,
        id: '',
        studentName: undefined
      },
      // 导入班级的翻页
      queryParams3: {
        pageNum: 1,
        pageSize: 5,
        id: '',
        className: ''
      }
    }
  },
  watch: {
    show(val) {
      if (val) {
        this.queryParams.pageNum = 1
        this.queryParams.pageSize = 5
        this.queryParams.id = this.classId
        this.getStudentList()
        this.queryParams3.id = this.classId
        this.ids = []
        this.chooseClass = -1
      }
    }
  },
  methods: {
    // 获取班级学生列表
    getStudentList() {
      for (var key in this.queryParams) {
        if (
          this.queryParams[key] === '' ||
          this.queryParams[key] === undefined
        ) {
          delete this.queryParams[key]
        }
      }
      this.loading = true
      getStudentList(this.queryParams).then((response) => {
        this.studentData = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 获取可新增学生列表
    getAddStudentList() {
      for (var key in this.queryParams2) {
        if (
          this.queryParams2[key] === '' ||
          this.queryParams2[key] === undefined
        ) {
          delete this.queryParams2[key]
        }
      }
      getAddStudentList(this.queryParams2).then((response) => {
        this.addStudentData = response.rows
        this.total2 = response.total
        this.loading2 = false
      })
    },

    // 搜索按钮
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getStudentList()
    },
    // 社区版（OS010）：家校通消息发送随 /homeschool/* 一并移除，此处不再跳转
    // 新增学生的搜索
    handleQuery2() {
      this.queryParams2.pageNum = 1
      this.getAddStudentList()
    },
    // 其他班级列表
    handleQuery3() {
      this.queryParams3.pageNum = 1
      this.getExtraClass()
    },
    // 新增学员
    openAddStudent() {
      this.queryParams2.id = this.classId
      this.queryParams2.pageSize = 5
      this.queryParams2.pageNum = 1
      this.queryParams2.studentName = undefined
      this.multipleSelection = []
      this.addStudentVisible = true
      this.getAddStudentList()
    },

    // 多选新增学生
    handleSelectionChange(val) {
      this.multipleSelection = val.map((item) => item.studentId)
    },
    // 多选 移出学生
    handleStudentChange(val) {
      this.ids = val.map((item) => {
        return item.id
      })
    },
    // 关闭弹窗
    handleClose() {
      this.$emit('close')
    },
    // 移出的学生
    removeStudent(id) {
      if (this.ids.length === 0) {
        this.msgError('请选择需要移出的学生')
        return false
      }
      removeStudent({
        studentIds: this.ids,
        classId: this.classId
      }).then((res) => {
        if (res.code === 200) {
          this.msgSuccess('移出成功')
          this.ids = []
          this.getStudentList()
        } else {
          this.msgError('移出失败')
        }
      })
    },

    // 新增学生的确认
    confirm() {
      if (this.multipleSelection.length > 0) {
        if (this.click) {
          this.click = false
          AddStudent({
            stuIds: this.multipleSelection,
            id: this.classId
          })
            .then((res) => {
              if (res.code === 200) {
                this.addStudentVisible = false
                this.queryParams.pageNum = 1
                this.msgSuccess('新增成功')
                this.getStudentList()
              }
            })
            .finally(() => {
              this.click = true
            })
        }
      } else {
        this.msgError('请选择学员')
      }
    },
    // 打开弹窗 获取班级列表
    openClassList() {
      this.chooseClass = -1
      this.getExtraClass()
    },
    // 获取其他班级列表
    getExtraClass() {
      const query = { ...this.queryParams3 }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading3 = true
      getExtraClass(this.queryParams3)
        .then((res) => {
          if (res.code === 200) {
            this.classList = res.rows
            this.total3 = res.total
            this.classVisible = true
          }
        })
        .finally(() => {
          this.loading3 = false
        })
    },
    // 导入学生
    exportStudent() {
      // 如果未选择班级
      if (this.chooseClass === -1) {
        this.msgError('请选择班级')
        return
      }
      if (this.exportClick) {
        this.exportClick = false
        exportStudent({
          importClassId: this.classList[this.chooseClass].id,
          importedClassId: this.queryParams3.id
        })
          .then((res) => {
            if (res.code === 200) {
              this.msgSuccess(res.msg)
              this.classVisible = false
              this.getStudentList()
            }
          })
          .finally(() => {
            this.exportClick = true
          })
      }
    }
  }
}
</script>

