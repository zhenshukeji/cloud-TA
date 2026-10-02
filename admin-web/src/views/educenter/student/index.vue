<template>
  <div class="app-container">
    <div v-show="!show">
      <div class="type-list">
        <div class="type-item" :class="{active:activeIndex === 0}" @click="changeType(0)">在读学员</div>
        <div class="type-item" :class="{active:activeIndex === 1}" @click="changeType(1)">回收站</div>
      </div>
      <!-- 搜索 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
        <el-form-item prop="studentName">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item prop="studentsPhone">
          <el-input v-model="queryParams.studentsPhone" placeholder="请输入学生手机号码" clearable @keyup.enter.native="handleQuery" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button v-show="activeIndex === 0" type="primary" icon="el-icon-plus" @click="handleAdd">新增学员</el-button>
          <el-button v-show="activeIndex === 0" icon="el-icon-download" @click="handleExport">导出课时账户</el-button>
          <el-button v-show="activeIndex === 0" type="primary" @click="loadVisible = true" style="background:#01B55C;border:1px solid #01B55C">已有学员批量导入</el-button>
        </el-form-item>
      </el-form>
      <!-- 表格 -->
      <el-table v-loading="loading" :data="studentList" stripe>
        <el-table-column label="学员姓名" align="center" prop="studentName" />
        <el-table-column label="性别" align="center" prop="sex">
          <template slot-scope="{row}">
            <span v-if="row.sex == 0">男</span>
            <span v-if="row.sex == 1">女</span>
            <span v-if="row.sex == 2">未知</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" align="center" prop="studentsPhone" />
        <el-table-column label="报名时间" align="center" prop="createTime" />
        <el-table-column v-if="activeIndex === 0" label="所在班级" align="center" prop="classes" key="1" />
        <el-table-column label="学校" align="center" prop="schoolName" />
        <el-table-column label="年级" align="center" prop="grade" />
        <el-table-column label="年龄" align="center" prop="age" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="{row}">
            <div v-show="activeIndex === 0">
              <el-button size="mini" type="text" @click="handleUpdate(row)">编辑</el-button>
              <el-button size="mini" type="text" @click="handleDetail(row)">详情</el-button>
              <el-button size="mini" type="text" @click="handleAccount(row)">账户信息</el-button>
              <el-button size="mini" type="text" @click="handleDelete(row)">删除</el-button>
            </div>
            <div v-show="activeIndex === 1">
              <el-button size="mini" type="text" icon="el-icon-tickets" @click="(handleRecover(row.id))">恢复</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

      <!-- 查看学生详情 -->
      <el-dialog title="学员信息详情" :visible.sync="showDetail" width="700px" class="trainee-details">
        <el-divider />
        <el-form ref="form" :model="form" label-width="120px" label-position="left">
          <div class="content-box">
            <div class="left">
              <el-form-item label="学员姓名:" prop="studentName">
                {{ form.studentName }}
              </el-form-item>
              <el-form-item label="学员手机号:" prop="studentsPhone">
                {{ form.studentsPhone }}
              </el-form-item>
              <el-form-item label="性别:">
                <span v-if="form.sex == 0">男</span>
                <span v-if="form.sex == 1">女</span>
                <span v-if="form.sex == 2">未知</span>
              </el-form-item>
              <el-form-item label="所在班级:">
                {{ form.classes }}
              </el-form-item>
              <el-form-item label="年级:" prop="grade">
                {{ form.grade }}
              </el-form-item>
              <el-form-item label="学校/幼儿园:" prop="schoolName">
                {{ form.schoolName }}
              </el-form-item>
              <el-form-item label="年龄:" prop="age">
                {{ form.age }}
              </el-form-item>
              <el-form-item label="地址:" prop="address">
                {{ form.address }}
              </el-form-item>
              <!-- <el-form-item label="微信头像:" prop="headImg">
                <img v-if="form.headImg" :src="form.headImg">
              </el-form-item>
              <el-form-item label="微信昵称:" prop="wechatName">
                {{ form.wechatName }}
              </el-form-item> -->
            </div>
            <div class="right">
              <div class="title">
                <span class="desc">相关联系人</span>
              </div>
              <el-form-item label="姓名:" prop="parentsName">
                {{ form.parentsName }}
              </el-form-item>
              <el-form-item label="手机号:" prop="parentsPhone">
                {{ form.parentsPhone }}
              </el-form-item>
              <el-form-item label="关系:" prop="associated">
                {{ options[form.associated] }}
              </el-form-item>
            </div>
          </div>
        </el-form>
      </el-dialog>
    </div>
    <!-- 账户信息 -->
    <accountInfo :infoVisible="infoVisible" :infoData="infoData" @close="infoVisible = false" />
    <!-- 新增/修改学员信息 -->
    <operate-student :show="show" :info="info" :isCreate="isCreate" @close="show = false" @submit="submitForm" />
    <!-- 上传文件组件 -->
    <upload :show="loadVisible" @close="loadVisible = false" @handleSuccess="handleSuccess" />
  </div>
</template>

<script>
import {
  listStudent,
  getStudent,
  updateStudent,
  delStudent,
  addStudent,
  listRecycleStudent,
  recoverStudent,
  exportStudentAccount
} from '@/api/educenter/student'
import operateStudent from './components/operateStudent'
import upload from './components/upload'
import operateClassTime from './components/operateClassTime'
import accountInfo from './components/accountInfo'
import { getToken } from '@/utils/auth'
export default {
  name: 'Student',
  components: {
    operateStudent,
    upload,
    operateClassTime,
    accountInfo
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/educenter/student') {
          this.activeIndex = 0
          this.queryParams.pageNum = 1
          this.queryParams.pageSize = 10
          this.getList()
        }
      },
      immediate: true
    }
  },
  data() {
    var checkClassHour = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('课次不能为空'))
      } else {
        if (!Number.isInteger(parseFloat(value))) {
          callback(new Error('请输入正整数'))
        } else {
          if (value <= 0) {
            callback(new Error('输入的值必须大于0'))
          } else {
            callback()
          }
        }
      }
    }
    return {
      click: true,
      info: {},
      options: ['父亲', '母亲', '爷爷', '奶奶', '外公', '外婆', '其他'],
      infoData: {},
      classData: {
        addHour: '',
        result: '',
        classHour: '',
        className: ''
      },
      isAdd: true,
      // 上传弹窗
      loadVisible: false,
      showLesson: false,
      // 查看详情
      showDetail: false,
      //上传图片的请求地址拼接
      action:
        process.env.VUE_APP_BASE_API + '/educenter/student/importStudents',
      show: false,
      // 创建学员
      activeIndex: 0,
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 学生表格数据
      studentList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      edit: false,
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentName: undefined,
        studentsPhone: undefined
      },
      // 表单参数
      form: {},
      headers: {
        Authorization: getToken()
      },
      isCreate: false,
      infoVisible: false,
      id: ''
    }
  },
  methods: {
    /** 查询学生列表 */
    getList() {
      let query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      console.log('列表查询参数', query)
      this.loading = true
      if (this.activeIndex === 0) {
        listStudent(query).then((response) => {
          this.studentList = response.rows
          this.total = response.total
          this.loading = false
        })
      } else {
        // 回收站
        listRecycleStudent(query).then((response) => {
          this.studentList = response.rows
          this.total = response.total
          this.loading = false
        })
      }
    },
    //打开账户信息
    handleAccount(row) {
      this.id = row.id
      this.infoData.id = row.id
      this.infoVisible = true
    },
    // 切换表格
    changeType(index) {
      this.activeIndex = index
      this.queryParams.pageNum = 1
      this.resetQuery()
    },

    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        studentName: undefined,
        studentsPhone: undefined,
        school: undefined,
        grade: undefined,
        age: undefined,
        openid: undefined,
        wechatName: undefined,
        address: undefined,
        associated: undefined,
        parentsName: undefined,
        img: undefined,
        parentsPhone: undefined,
        idCard: undefined,
        isDelete: undefined,
        isOrdinary: undefined,
        isHistory: undefined,
        createdTime: undefined,
        schoolName: undefined
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

    /** 新增按钮操作 */
    handleExport() {
      exportStudentAccount(this.queryParams ? this.queryParams : {}).then(res => {
        if (res.code === 200 && res.data) {
          this.download(res.data)
        } else {
          this.$message.error((res && res.msg) || '导出失败')
        }
      }).catch(() => {
        this.$message.error('导出失败')
      })
    },
    handleAdd() {
      this.reset()
      this.show = true
      this.isCreate = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.isCreate = false
      this.form.id = row.id
      getStudent(row.id).then((res) => {
        if (res.code === 200) {
          this.form = res.data
          this.info = res.data
          this.show = true
        }
      })
    },
    // 查看详情
    handleDetail(row) {
      const id = row.id || this.ids
      getStudent(id).then((response) => {
        this.form = response.data
        this.showDetail = true
      })
    },
    // 恢复学员
    handleRecover(id) {
      this.$confirm('确认恢复该学员？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        recoverStudent(id).then((response) => {
          if (response.code === 200) {
            console.log('返回200')
            this.msgSuccess('恢复成功')
            this.getList()
          }
        })
      })
    },
    /** 确认提交 */
    submitForm(data) {
      if (this.click) {
        this.click = false
        if (data.id) {
          updateStudent(data)
            .then((response) => {
              if (response.code === 200) {
                this.msgSuccess('修改成功')
                this.show = false
                this.getList()
              } else {
                this.msgError(response.msg)
              }
            })
            .finally(() => {
              this.click = true
            })
        } else {
          addStudent(data)
            .then((response) => {
              if (response.code === 200) {
                this.msgSuccess('新增成功')
                this.show = false
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
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$confirm(
        '是否确认删除学生名为"' + row.studentName + '"的数据项?',
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(function () {
          return delStudent(ids)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function () {})
    },
    // 上传成功重新刷新
    handleSuccess() {
      this.handleQuery()
      this.loadVisible = false
    }
  }
}
</script>
<style lang='scss' scoped>
.content-box {
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
  .title {
    margin-top: 10px;
    margin-bottom: 10px;
    .desc {
      padding: 5px;
      font-size: 16px;
      color: #303133;
      border-top: 1px solid #bbbbbb;
    }
  }
}
.flex {
  display: flex;
  justify-content: space-around;
  align-items: center;
  color: #ff6206;
  font-size: 16px;
}
.trainee-details {
  .el-form-item {
    margin-bottom: 0;
  }
  img {
    width: 60px;
    height: 60px;
  }
}
.trainee-account {
  .money {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30px;
  }
}
.add-class-popup {
  .addClass-form {
    display: flex;
    flex-direction: column;
    .el-form-item {
      margin-bottom: 20px;
    }
  }
  .btn-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
.pay-kc {
  position: absolute;
  top: 120px;
  right: 10px;
  z-index: 5;
}
::v-deep .el-pagination {
  text-align: center;
}
</style>