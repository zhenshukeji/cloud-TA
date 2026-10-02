<template>
  <div class="realted-page">
    <el-dialog :title="title" :visible.sync="show" width="700px" :close-on-click-modal='false' :before-close="handleClose">
      <el-divider />
      <!-- 关联课程 -->
      <el-input v-if="relatedCourse" v-model="queryParams.courseName" clearable placeholder="请输入课程名称" style="width:200px" @change="change" maxlength="16" show-word-limit/>
      <!-- 关联教师 -->
      <el-input v-else v-model="queryParams.name" clearable placeholder="请输入教师名称" style="width:200px" maxlength="10" show-word-limit/>

      <el-button type="primary" style="margin-bottom:30px" @click="handleQuery">搜索</el-button>
      <!-- 课程 -->
      <div v-if="relatedCourse">
        <el-table v-loading="loading" :data="chooseData" stripe size="small" row-key='0'>
          <el-table-column align="center" width="100">
            <template slot-scope="{$index}">
              <el-radio v-model="chooseIndex" :label="$index">
              </el-radio>
            </template>
          </el-table-column>
          <el-table-column label="课程名称" prop="courseName" align="center" key="3"/>
          <el-table-column label="报名金额（元）" prop="amountCny" align="center" key="4">
            <template slot-scope="{row}">
              {{ row.amountCny }}元
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-if="!relatedCourse">
        <el-table v-loading="loading" :data="chooseData" stripe size="small" row-key='1'>
          <el-table-column align="center" width="100">
            <template slot-scope="{$index}">
              <el-radio v-model="chooseIndex" :label="$index">
              </el-radio>
            </template>
          </el-table-column>
          <el-table-column label="老师姓名" prop="name" key="0"/>
          <el-table-column label="当前班级数" prop="taughtNum" key="1"/>
          <el-table-column label="当前学员总量" prop="taughtStuNum" key="2">
            <template slot-scope="{row}">
              <span v-if="row.taughtStuNum">
                {{ row.taughtStuNum }}人
              </span>
              <span v-else>0</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <pagination v-show="total> 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getRelatedList" />
      <div style="display:flex;justify-content:center;margin-top:60px">
        <el-button type="primary" @click="confirmCelated">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCourseList, getTeacherList } from '@/api/educenter/class'
export default {
  props: {
    show: {
      type: Boolean,
      default: false
    },
    relatedCourseData: {
      type: Object,
      default: () => ({})
    }
  },
  watch: {
    show(val) {
      if (val) {
        console.log('数据来源', this.relatedCourseData)
        this.chooseIndex = ''
        this.queryParams.pageNum = 1
        this.queryParams.pageSize = 5
        this.queryParams.courseName = undefined
        this.queryParams.name = undefined
        this.queryParams.isExperience = undefined
        if (this.relatedCourseData.relatedCourse) {
          this.relatedCourse = true
        } else {
          this.relatedCourse = false
        }
        this.getRelatedList()
      }
    }
  },
  data() {
    return {
      relatedCourse: true,
      chooseIndex: -1,
      chooseData: [],
      title: '',
      loading: true,
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        courseName: undefined,
        id: undefined,
        name: undefined,
        isExperience: undefined
      }
    }
  },
  methods: {
    // 获取数据
    getRelatedList() {
      for (var key in this.queryParams) {
        if (
          this.queryParams[key] === '' ||
          this.queryParams[key] === undefined
        ) {
          delete this.queryParams[key]
        }
      }
      this.loading = true
      if (this.relatedCourse) {
        this.queryParams.isExperience = this.relatedCourseData.isExperience
        getCourseList(this.queryParams).then((response) => {
          this.chooseData = response.rows
          this.total = response.total
          this.loading = false
          this.title = '选择课程'
          this.chooseIndex = this.chooseData.findIndex((item) => {
            return item.id == this.relatedCourseData.chooseId
          })
        })
      } else {
        getTeacherList(this.queryParams).then((response) => {
          this.chooseData = response.rows
          console.log('数据', this.chooseData)
          this.total = response.total
          this.loading = false
          this.title = '选择老师'
          this.chooseIndex = this.chooseData.findIndex((item) => {
            return item.id == this.relatedCourseData.chooseId
          })
        })
      }
    },
    change(val) {
      console.log(val)
    },
    // 确认按钮
    confirmCelated() {
      console.log(this.queryParams)
      console.log('调用了')
      if (this.chooseIndex === -1) {
        this.msgError('请选择')
        return false
      }
      this.$emit('confirmCelated', this.chooseData[this.chooseIndex])
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getRelatedList()
    },
    // 关闭关联弹窗
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>

