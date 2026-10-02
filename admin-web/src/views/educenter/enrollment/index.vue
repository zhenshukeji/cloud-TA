<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入学员姓名" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit/>
      </el-form-item>
      <el-form-item prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入学员手机号" clearable @keyup.enter.native="handleQuery" maxlength="11" show-word-limit/>
      </el-form-item>
      <el-form-item prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入报读课程" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit/>
      </el-form-item>
      <el-form-item prop="className">
        <el-input v-model="queryParams.className" placeholder="请输入所在班级" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="recordList" stripe>
      <el-table-column label="学员姓名" align="center" prop="studentName" />
      <el-table-column label="手机号" align="center" prop="phone" />
      <el-table-column label="报读课程" align="center" prop="courseName" />
      <el-table-column label="所在班级" align="center" prop="className" />
      <el-table-column label="购买数量" align="center" prop="total">
        <template slot-scope="{row}">
          {{ row.total }}课次
        </template>
      </el-table-column>
      <el-table-column label="消课数量" align="center" prop="number">
        <template slot-scope="{row}">
          {{ row.number }}课次
        </template>
      </el-table-column>
      <el-table-column label="剩余数量" align="center" prop="remainNumber">
        <template slot-scope="{row}">
          {{ row.remainNumber }}课次
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listEnrollment } from '@/api/educenter/enrollment'

export default {
  name: 'Enrollment',
  data() {
    return {
      activeIndex: 0,
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 报读情况表格数据
      recordList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseName: undefined,
        className: undefined,
        phone: undefined,
        studentName: undefined
      }
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/educenter/enrollment') {
          this.queryParams.pageNum = 1
          this.queryParams.pageSize = 10
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    /** 查询报读情况列表 */
    getList() {
      let query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading = true
      listEnrollment(query).then((response) => {
        this.recordList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        mecId: undefined,
        classId: undefined,
        courseId: undefined,
        realNumber: undefined,
        shouldNumber: undefined,
        classStartTime: undefined,
        classEndTime: undefined,
        isDelete: undefined,
        createTime: undefined,
        createBy: undefined,
        updateTime: undefined,
        updateBy: undefined
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
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams
      this.$confirm('是否确认导出所有上课记录数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(function () {
          return exportRecord(queryParams)
        })
        .then((response) => {
          this.download(response.msg)
        })
        .catch(function () {})
    }
  }
}
</script>
 <style lang="scss"  scoped>
::v-deep .el-table .el-table__header-wrapper th {
  background-color: #b7cbee;
}
::v-deep .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #e8f1ff;
}
</style>