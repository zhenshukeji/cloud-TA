<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import '@/assets/quill/quill.core.css'
import '@/assets/quill/quill.snow.css'
import '@/assets/quill/quill.bubble.css'
export default {
  name: 'App'
}
</script>
<style lang="scss">
.el-dialog__body {
  padding-top: 0;
}
.el-divider--horizontal {
  margin-top: 0;
  margin-bottom: 20px;
}
.el-table .el-table__header-wrapper th {
  background-color: #b7cbee !important;
}
.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #e8f1ff !important;
}
.el-pagination {
  // margin-top: 30px;
  text-align: center;
}
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
    border-top: 2px solid #4f93fe;
    color: #4f93fe !important;
  }
}
.dialog-footer {
  text-align: center;
}
// .el-form {
//   padding: 0 20px !important;
// }
</style>