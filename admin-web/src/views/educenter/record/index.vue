<template>
  <div class="app-container">
    <div class="type-list">
      <div class="type-item" :class="{active:activeIndex === 0}" @click="changeType(0)">上课记录</div>
      <div class="type-item" :class="{active:activeIndex === 1}" @click="changeType(1)">请假申请</div>
    </div>
    <template v-if="activeIndex === 0">
      <classRecord :activeIndex="activeIndex" />
    </template>
    <template v-if="activeIndex === 1">
      <valication :activeIndex="activeIndex" />
    </template>
  </div>
</template>

<script>
import valication from './componenst/vocation'
import classRecord from './componenst/classRecord'
export default {
  name: 'Record',
  filters: {
    typeFilter(type) {
      var typeArr = ['到课', '迟到', '请假', '未到', '提前请假']
      return typeArr[type]
    }
  },
  components: {
    valication,
    classRecord
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/educenter/record') {
          if (route.query.flag) {
            this.activeIndex = 1
          } else {
            this.activeIndex = 0
          }
        } else {
          this.activeIndex = -1
        }
      },
      immediate: true
    }
  },
  mounted() {},
  data() {
    return {
      activeIndex: 0
    }
  },
  methods: {
    // 切换表格
    changeType(index) {
      this.activeIndex = index
    }
  }
}
</script>
<style lang="scss" scoped>
.type-list {
  display: flex;
  .type-item {
    padding: 0 10px;
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    border-top: 2px solid #8c8c8c;
    margin-right: 10px;
    cursor: pointer;
  }
  .active {
    transition: border-top 0.5s;
    border-top: 2px solid #4f93fe;
  }
}
</style>