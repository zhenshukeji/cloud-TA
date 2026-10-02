<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item prop="className">
        <el-input v-model="queryParams.className" placeholder="请输入班级名称" clearable size="small" @keyup.enter.native="handleQuery" maxlength="16" show-word-limit/>
      </el-form-item>
      <el-form-item prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable size="small" @keyup.enter.native="handleQuery" maxlength="16" show-word-limit/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="recordList" stripe>
      <el-table-column label="学员名称" align="center" prop="studentName" />
      <el-table-column label="所在班级" align="center" prop="classNames" />
      <el-table-column label="课程名称" align="center" prop="courseNames" />
      <el-table-column label="操作" align="center">
        <template slot-scope="{row}">
          <el-button type="text" @click="handleDetail(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    <span class="detail">
      查看详情
    </span>
    <span v-show="show" class="name">
      学员名称：{{ studentName }}
    </span>
    <div v-show="show" class="report_table">
      <div v-for="(item,index) in idArr" :key="item" class="table_box">
        <div class="table_item" :id="item"></div>
        <el-button @click="exportTable(index)" type="primary" class="table_btn">
          导出
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getTimeList, getTimeDetail } from '@/api/statistics/classtime'
// 引入 ECharts 主模块
var echarts = require('echarts/lib/echarts')
// 引入柱状图
require('echarts/lib/chart/bar')
// 引入折线图
require('echarts/lib/chart/line')
// 引入提示框和标题组件
require('echarts/lib/component/tooltip')
require('echarts/lib/component/title')
require('echarts/lib/component/legend')
export default {
  name: 'Enrollment',
  data() {
    return {
      show: false,
      idArr: ['used', 'reamin'],
      courseList: [],
      // 已消课次
      usedArr: [],
      usedUrl: '',
      // 剩余课次
      remainArr: [],
      remainUrl: '',
      studentName: '',
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 课次表格数据
      recordList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        className: undefined,
        courseName: undefined
      }
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/statistics/classTime') {
          this.queryParams.pageNum = 1
          this.queryParams.pageSize = 5
          this.show = false
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    /** 查询报读情况列表 */
    getList() {
      for (var key in this.queryParams) {
        if (
          this.queryParams[key] === undefined ||
          this.queryParams[key] === null ||
          this.queryParams[key] === ''
        ) {
          delete this.queryParams[key]
        }
      }
      this.loading = true
      getTimeList(this.queryParams).then((response) => {
        this.recordList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 查看详情
    handleDetail(row) {
      this.usedArr = []
      this.remainArr = []
      const { studentId } = row
      this.courseList = row.classNames.split(',')
      this.studentName = row.studentName
      getTimeDetail({
        studentId
      }).then((res) => {
        const timeData = res.data
        timeData.forEach((item) => {
          const usedNum = item.totalNumber - item.remainingNumber
          this.usedArr.push(usedNum)
          this.remainArr.push(item.remainingNumber)
        })
        this.lineReportDraw()
        this.barReportDraw()
        this.show = true
      })
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
    // 柱状图 已消课次
    barReportDraw() {
      var myChart = echarts.init(document.getElementById('used'))
      myChart.on('finished', () => {
        this.usedUrl = myChart.getDataURL({
          type: 'png',
          backgroundColor: 'white',
          pixelRatio: 2
        })
      })
      // 绘制图表
      myChart.setOption({
        title: {
          text: '已消课次'
        },
        animationDuration: 500,
        legend: {
          data: ['课次']
        },
        tooltip: {},
        xAxis: {
          data: this.courseList,
          axisLabel: {
            interval: 0
          }
        },
        yAxis: {},
        series: [
          {
            name: '课次',
            type: 'bar',
            data: this.usedArr
          }
        ]
      })
    },
    // 折线图 剩余课次
    lineReportDraw() {
      var myChart = echarts.init(document.getElementById('reamin'))
      myChart.on('finished', () => {
        this.remainUrl = myChart.getDataURL({
          type: 'png',
          backgroundColor: '#FFF',
          pixelRatio: 2
        })
      })
      // 绘制图表
      myChart.setOption({
        title: {
          text: '剩余课次'
        },
        animationDuration: 500,
        legend: {
          data: ['课次']
        },
        tooltip: {},
        xAxis: {
          data: this.courseList,
          axisLabel: {
            interval: 0
          }
        },
        yAxis: {},
        series: [
          {
            name: '课次',
            type: 'line',
            data: this.remainArr
          }
        ]
      })
    },
    exportTable(index) {
      let url = ''
      let name = ''
      switch (index) {
        case 0:
          url = this.usedUrl
          name = '已消课次'
          break
        case 1:
          url = this.remainUrl
          name = '剩余课次'
          break
      }
      if (url) {
        var href = document.createElement('a')
        href.setAttribute('href', url)
        href.setAttribute('download', `${name}.png`)
        href.click()
      } else {
        this.msgError('图片生成中')
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.detail {
  color: #101010;
  border-top: 1px solid #bbbbbb;
  padding: 5px;
  margin-top: 30px;
  display: inline-block;
}
.name {
  color: #ff6206;
  font-size: 14px;
  margin-left: 10px;
}
.report_table {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  .table_box {
    display: flex;
    margin-bottom: 30px;
    .table_item {
      width: 600px;
      height: 400px;
    }
    .table_btn {
      width: 100px;
      height: 40px;
      box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.4);
    }
  }
}
</style>