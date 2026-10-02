<template>
  <div class="upload-page">
    <el-dialog v-loading="loading" title="导入学员" :visible.sync="show" :before-close="handleClose">
      <el-divider />
      <p>请按照我们的标准模板上传学员信息 </p>
      <p @click="downloadFile" style="color: #1890ff;cursor:pointer">请点此下载模板</p>
      <el-upload drag :action="action" :headers="headers" :show-file-list="false" :on-success="handleSuccess" :before-upload="beforeMediaUpload">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'

export default {
  components: {},
  props: {
    show: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: true,
      href: process.env.VUE_APP_BASE_API + '/example.xlsx',
      action:
        process.env.VUE_APP_BASE_API + '/educenter/student/importStudents',
      headers: {
        Authorization: getToken()
      }
    }
  },
  methods: {
    downloadFile() {
      // 社区版不依赖外部 CDN，模板由后端静态资源提供（/example.xlsx）
      window.location.href = this.href
    },
    // 上传前的限制
    beforeMediaUpload(file) {
      this.loading = true
      const limitSize = file.size / 1024 / 1024 < 1
      // var isExcel =
      //   file.type === 'image/jpeg' ||
      //   file.type === 'image/png' ||
      //   file.type === 'image/jpg' ||
      //   file.type === 'image/gif'
      // if (!isPic) {
      //   this.$message({
      //     message: '请上传类型的文件',
      //     type: 'error',
      //     duration: 2000
      //   })
      // }
      if (!limitSize) {
        this.loading = false
        this.$message({
          message: '上传的文件大小不能超过1M',
          type: 'error',
          duration: 2000
        })
      }
      return limitSize
    },
    handleSuccess(res) {
      this.loading = false
      if (res.code === 200) {
        this.msgSuccess('上传成功')
        this.$emit('handleSuccess')
      } else {
        this.msgError(res.msg)
      }
    },
    // 关闭关联弹窗
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>
<style lang='scss' scoped>
.upload-page {
  text-align: center;
}
::v-deep .el-dialog__header {
  text-align: left;
}
</style>