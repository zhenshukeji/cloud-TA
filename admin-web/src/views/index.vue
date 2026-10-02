<template>
  <div class="community-dashboard app-container">
    <!-- 基础教务概览：全部数据来自社区版真实存活的接口（见 OS010 页面→API 决策矩阵） -->
    <div class="panel-title">基础数据</div>
    <el-row :gutter="16" class="stat-row">
      <el-col v-for="(item, index) in statCards" :key="index" :xs="12" :sm="12" :lg="6">
        <el-card v-loading="loading" shadow="hover" class="stat-card">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
          <div class="stat-unit">{{ item.unit }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mid-row">
      <el-col :xs="24" :sm="24" :lg="15">
        <el-card shadow="never" class="block-card">
          <div slot="header" class="block-header">快捷入口</div>
          <div class="entry-box">
            <div
              v-for="(item, index) in entryData"
              :key="index"
              class="entry-item"
              @click="jump(item)"
            >
              <svg-icon :icon-class="item.icon" class="entry-icon" />
              <span class="entry-text">{{ item.text }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="9">
        <el-card shadow="never" class="block-card">
          <div slot="header" class="block-header">机构与校区</div>
          <div v-if="campusList.length === 0" class="empty-tip">暂无校区信息</div>
          <div v-for="item in campusList" :key="item.id" class="campus-item">
            <div class="campus-line">
              <span class="campus-key">校区名称</span>
              <span class="campus-val">{{ item.name || '-' }}</span>
            </div>
            <div class="campus-line">
              <span class="campus-key">校区负责人</span>
              <span class="campus-val">{{ item.mgrName || '未设置' }}</span>
            </div>
            <div class="campus-line">
              <span class="campus-key">联系方式</span>
              <span class="campus-val">{{ item.mgrContactNumber || '未设置' }}</span>
            </div>
            <div class="campus-line">
              <span class="campus-key">详细地址</span>
              <span class="campus-val">{{ item.address || '未设置' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="notice-card">
      <div class="notice-title">口径说明</div>
      <ul class="notice-list">
        <li>本系统为<b>单机构、单校区</b>版本，不提供平台入驻、多机构与总部经营能力。</li>
        <li>课程与报读金额单位统一为<b>人民币元</b>，仅作为<b>线下收款登记</b>；系统不提供在线支付与资金收付能力。</li>
        <li>学员课时以<b>课次</b>登记，点名与扣课在同一事务内完成，重复提交不会重复扣课。</li>
      </ul>
    </el-card>
  </div>
</template>

<script>
import { listStudent } from '@/api/educenter/student'
import { listClass } from '@/api/educenter/class'
import { listCourse } from '@/api/educenter/course'
import { listTeacher } from '@/api/educenter/teacher'
import { getCampusList } from '@/api/mechanism/region'

export default {
  name: 'Index',
  data() {
    return {
      loading: false,
      campusList: [],
      counts: {
        student: 0,
        class: 0,
        course: 0,
        teacher: 0
      },
      entryData: [
        { icon: 'people', text: '学员管理', path: '/educenter/student' },
        { icon: 'peoples', text: '班级信息', path: '/educenter/class' },
        { icon: 'book', text: '课程管理', path: '/educenter/course' },
        { icon: 'build', text: '课表管理', path: '/educenter/curriculum' },
        { icon: 'checkbox', text: '上课记录', path: '/educenter/record' },
        { icon: 'dict', text: '报读登记', path: '/educenter/enrollment' },
        { icon: 'info', text: '教师管理', path: '/educenter/teacher' },
        { icon: 'chart', text: '统计报表', path: '/statistics/class' }
      ]
    }
  },
  computed: {
    statCards() {
      return [
        { label: '在读学员', unit: '人', value: this.counts.student },
        { label: '班级', unit: '个', value: this.counts['class'] },
        { label: '课程', unit: '门', value: this.counts.course },
        { label: '教师', unit: '位', value: this.counts.teacher }
      ]
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    // 概览数据：逐个取保留页面同源的列表总数，任一接口失败不影响其它卡片
    loadData() {
      this.loading = true
      const tasks = [
        [listStudent, 'student'],
        [listClass, 'class'],
        [listCourse, 'course'],
        [listTeacher, 'teacher']
      ]
      const query = { pageNum: 1, pageSize: 1 }
      Promise.all(
        tasks.map((pair) =>
          pair[0]({ pageNum: query.pageNum, pageSize: query.pageSize })
            .then((res) => (res && res.total !== undefined ? res.total : 0))
            .catch(() => 0)
        )
      ).then((values) => {
        tasks.forEach((pair, i) => {
          this.counts[pair[1]] = values[i] || 0
        })
        this.loading = false
      })
      getCampusList({ pageNum: 1, pageSize: 10 })
        .then((res) => {
          this.campusList = (res && res.rows) || []
        })
        .catch(() => {
          this.campusList = []
        })
    },
    jump(item) {
      this.$router.push({ path: item.path }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.community-dashboard {
  .panel-title {
    font-size: 15px;
    font-weight: 600;
    color: #4a4a4a;
    margin: 4px 0 12px;
  }
  .stat-row {
    margin-bottom: 16px;
  }
  .stat-card {
    position: relative;
    min-height: 96px;
    .stat-value {
      font-size: 30px;
      font-weight: 600;
      color: #384f43;
      line-height: 1.2;
    }
    .stat-label {
      font-size: 13px;
      color: #888;
      margin-top: 6px;
    }
    .stat-unit {
      position: absolute;
      right: 16px;
      bottom: 14px;
      font-size: 12px;
      color: #c0c4cc;
    }
  }
  .mid-row {
    .block-card {
      min-height: 232px;
      margin-bottom: 16px;
    }
  }
  .block-header {
    font-weight: 600;
    color: #4a4a4a;
  }
  .entry-box {
    display: flex;
    flex-wrap: wrap;
    .entry-item {
      width: 25%;
      display: flex;
      align-items: center;
      padding: 14px 0;
      cursor: pointer;
      color: #5a6b62;
      &:hover {
        color: #384f43;
      }
      .entry-icon {
        width: 20px;
        height: 20px;
        margin-right: 8px;
      }
      .entry-text {
        font-size: 13px;
      }
    }
  }
  .campus-item {
    padding: 6px 0 10px;
    border-bottom: 1px dashed #ebeef5;
    &:last-child {
      border-bottom: none;
    }
    .campus-line {
      display: flex;
      font-size: 13px;
      line-height: 26px;
      .campus-key {
        width: 90px;
        color: #9a9a9a;
        flex: none;
      }
      .campus-val {
        color: #4a4a4a;
        word-break: break-all;
      }
    }
  }
  .empty-tip {
    font-size: 13px;
    color: #9a9a9a;
    padding: 8px 0;
  }
  .notice-card {
    margin-top: 4px;
    .notice-title {
      font-weight: 600;
      color: #4a4a4a;
      margin-bottom: 8px;
    }
    .notice-list {
      margin: 0;
      padding-left: 18px;
      font-size: 13px;
      color: #6d6d6d;
      line-height: 26px;
    }
  }
}
</style>
