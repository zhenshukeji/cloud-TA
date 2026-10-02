<template>
  <div class="app-container">
    <div class="desc">
      学员考勤统计表
    </div>
    <div class="report_table">
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
import { getReportList } from '@/api/statistics/attendance'
// 引入 ECharts 主模块
var echarts = require('echarts/lib/echarts')
// 引入柱状图
require('echarts/lib/chart/bar')
// 引入折线图
require('echarts/lib/chart/line')
// 引入散点图
require('echarts/lib/chart/scatter')
// 引入象形图
require('echarts/lib/chart/pictorialBar')
// 引入提示框和标题组件
require('echarts/lib/component/tooltip')
require('echarts/lib/component/title')
require('echarts/lib/component/legend')
export default {
  components: {},
  data() {
    return {
      idArr: ['actually', 'leave', 'late', 'notArrived'],
      courseList: [],
      // 请假
      leaveArr: [],
      leaveUrl: '',
      // 迟到
      lateArr: [],
      lateUrl: '',
      // 实到
      actuallyArr: [],
      actuallyUrl: '',
      // 未到
      notArrivedArr: [],
      notArrivedUrl: ''
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/statistics/attendance') {
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    getList() {
      getReportList().then((res) => {
        if (res.code === 200) {
          const reportData = res.data
          reportData.forEach((item) => {
            this.courseList.push(item.courseName)
            this.leaveArr.push(item.leaveNum)
            this.lateArr.push(item.lateNum)
            this.actuallyArr.push(item.actuallyNum)
            this.notArrivedArr.push(item.notArrived)
          })
          this.lineReportDraw()
          this.pointReportDraw()
          this.elephantReportDraw()
          this.barReportDraw()
        }
      })
    },
    // 折线图 课程出勤数
    lineReportDraw() {
      var myChart = echarts.init(document.getElementById('actually'))
      myChart.on('finished', () => {
        this.actuallyUrl = myChart.getDataURL({
          type: 'png',
          backgroundColor: '#FFF',
          pixelRatio: 2
        })
      })
      // 绘制图表
      myChart.setOption({
        title: {
          text: '课程出勤数'
        },
        animationDuration: 500,
        legend: {
          data: ['出勤数']
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
            name: '出勤数',
            type: 'line',
            data: this.actuallyArr
          }
        ]
      })
    },
    // 象形图 课程旷课数
    elephantReportDraw() {
      var myChart = echarts.init(document.getElementById('notArrived'))
      myChart.on('finished', () => {
        this.notArrivedUrl = myChart.getDataURL({
          type: 'png',
          backgroundColor: 'white',
          pixelRatio: 2
        })
      })
      // 绘制图表
      myChart.setOption({
        title: {
          text: '课程旷课数'
        },
        animationDuration: 500,
        legend: {
          data: ['旷课数']
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
            name: '旷课数',
            type: 'pictorialBar',
            data: this.notArrivedArr
          }
        ]
      })
    },
    // 散点图 课程请假数
    pointReportDraw() {
      var myChart = echarts.init(document.getElementById('leave'))
      myChart.on('finished', () => {
        this.leaveUrl = myChart.getDataURL({
          type: 'png',
          backgroundColor: 'white',
          pixelRatio: 2
        })
      })
      // 绘制图表
      myChart.setOption({
        title: {
          text: '课程请假数'
        },
        animationDuration: 500,
        legend: {
          data: ['请假数']
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
            name: '请假数',
            type: 'scatter',
            data: this.leaveArr
          }
        ]
      })
    },
    // 柱状图 课程迟到数
    barReportDraw() {
      var myChart = echarts.init(document.getElementById('late'))
      myChart.on('finished', () => {
        this.lateUrl = myChart.getDataURL({
          type: 'png',
          backgroundColor: 'white',
          pixelRatio: 2
        })
      })
      // 绘制图表
      myChart.setOption({
        title: {
          text: '课程迟到数'
        },
        animationDuration: 500,
        legend: {
          data: ['迟到数']
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
            name: '迟到数',
            type: 'bar',
            data: this.lateArr
          }
        ]
      })
    },
    exportTable(index) {
      let url = ''
      let name = ''
      switch (index) {
        case 0:
          url = this.actuallyUrl
          name = '课程出勤人数'
          break
        case 1:
          url = this.notArrivedUrl
          name = '课程旷课人数'
          break
        case 2:
          url = this.leaveUrl
          name = '课程请假人数'
          break
        case 3:
          url = this.lateUrl
          name = '课程迟到人数'
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

<style lang='scss' scoped>
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
