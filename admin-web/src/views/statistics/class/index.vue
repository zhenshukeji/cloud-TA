<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item prop="className">
        <el-input v-model="queryParams.className" placeholder="请输入班级名称" clearable size="small" @keyup.enter.native="handleQuery" maxlength="16" show-word-limit/>
      </el-form-item>
      <el-form-item prop="bishopTeacherName">
        <el-input v-model="queryParams.bishopTeacherName" placeholder="请输入主教老师名称" clearable size="small" @keyup.enter.native="handleQuery" maxlength="10" show-word-limit/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="recordList" stripe>
      <el-table-column label="班级名称" align="center" prop="className" />
      <el-table-column label="上课时间" align="center" prop="classTimes">
        <template slot-scope="{row}">
          <div v-for="(item,index) in row.classTimes" :key="index">
            {{ item }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="主教老师" align="center" prop="bishopTeacherName" />
      <el-table-column label="在读学员数" align="center" prop="existStudents" />
      <el-table-column label="班级容量" align="center" prop="sumStudents">
        <template slot-scope="{row}">
          {{ row.existStudents }} / {{ row.sumStudents }}
        </template>
      </el-table-column>
      <el-table-column label="点名实到人数" align="center" prop="actuallyNum" />

      <el-table-column label="迟到人数" align="center" prop="lateNum" />

      <el-table-column label="缺课人数" align="center" prop="notArrived" />
      <el-table-column label="请假人数" align="center" prop="leaveNum" />
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { getClassList } from '@/api/statistics/class'

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
        className: undefined,
        bishopTeacherName: undefined
      }
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/statistics/class') {
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
      let query = this.queryParams
      for (var key in query) {
        if (
          query[key] === undefined ||
          query[key] === null ||
          query[key] === ''
        ) {
          delete query[key]
        }
      }
      this.loading = true
      getClassList(query).then((response) => {
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