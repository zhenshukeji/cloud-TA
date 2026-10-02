<template>
  <div class="realted-page">
    <el-dialog title="关联人员" :visible.sync="show" width="700px" :close-on-click-modal='false' :before-close="handleClose">
      <el-divider />
      <div class="type-list">
        <div class="type-item" :class="{active:activeIndex === 0}" @click="changeType(0)">校区人员</div>
        <div class="type-item" :class="{active:activeIndex === 1}" @click="changeType(1)">机构人员</div>
      </div>
      <el-input v-model="queryParams.name" placeholder="请输入人员名称" style="width:200px" maxlength="10" show-word-limit/>
      <el-button type="primary" style="margin-bottom:30px" @click="handleQuery">搜索</el-button>
      <el-table v-loading="loading" :data="chooseData" stripe size="small">
        <el-table-column align="center" prop="noticeTitle" width="50">
          <template slot-scope="{$index}">
            <el-radio v-model="chooseIndex" :label="$index">
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column label="姓名" prop="name" />
        <el-table-column label="手机号" prop="phone" />
      </el-table>
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
import { getCampusPerson, getMechanismPerson } from '@/api/educenter/teacher'
export default {
  props: {
    show: {
      type: Boolean,
      default: false
    },
    chooseId: {
      type: Number,
      default: 0
    }
  },
  watch: {
    show(val) {
      if (val) {
        this.queryParams.pageNum = 1
        this.queryParams.pageSize = 5
        this.activeIndex = 0
        this.chooseIndex = -1
        this.chooseData = []
        this.getRelatedList()
      }
    }
  },
  data() {
    return {
      activeIndex: 0,
      chooseIndex: 0,
      chooseData: [],
      title: '',
      loading: true,
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        courseName: undefined,
        name: undefined
      }
    }
  },
  methods: {
    // 获取数据
    getRelatedList() {
      let query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading = true
      if (this.activeIndex === 0) {
        getCampusPerson(query).then((res) => {
          if (res.code === 200) {
            this.chooseData = res.rows
            this.loading = false
            this.total = res.total
          }
          this.chooseIndex = this.chooseData.findIndex((item) => {
            return item.id == this.chooseId
          })
        })
      } else {
        getMechanismPerson(query).then((res) => {
          if (res.code === 200) {
            this.chooseData = res.rows
            this.loading = false
            this.total = res.total
          }
          this.chooseIndex = this.chooseData.findIndex((item) => {
            return item.id == this.chooseId
          })
        })
      }
    },
    changeType(index) {
      this.queryParams.pageSize = 5
      this.queryParams.pageNum = 1
      this.activeIndex = index
      this.chooseData = []
      this.getRelatedList()
    },
    // 确认按钮
    confirmCelated() {
      if (this.chooseIndex != -1) {
        const data = {
          ...this.chooseData[this.chooseIndex],
          ...{ personType: this.activeIndex === 0 ? 1 : 0 }
        }
        this.$emit('confirmCelated', data)
      } else {
        this.msgError('请选择关联人员')
      }
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
<style  lang='scss' scoped>
::v-deep .el-radio__label {
  display: none;
}
</style>
