<template>
  <div>
    <!-- 图片上传组件辅助 -->
    <el-upload class="avatar-uploader quill-img" :action="uploadImgUrl" name="file" :headers="headers" :show-file-list="false" :on-success="quillImgSuccess" :on-error="uploadError" :before-upload="quillImgBefore" accept='.jpg,.jpeg,.png,.gif'></el-upload>

    <!-- 上传视频文件 -->
    <el-upload :headers="headers" :show-file-list="false" name="file" :on-success="handleFileSuccess" :before-upload="handleFileBeforeUpload" :action="uploadImgUrl" class="uploadFile" accept='.mp4,ogg,webm'>
    </el-upload>

    <el-upload :headers="headers" :show-file-list="false" name="file" :on-success="handleFileSuccess2" :before-upload="handleFileBeforeUpload2" :action="uploadImgUrl" class="uploadFile2" accept='.mp3,ogg,webm'>
    </el-upload>

    <!-- 富文本组件 -->
    <quill-editor class="editor" v-loading='loading' element-loading-text="拼命上传中" element-loading-spinner="el-icon-loading" v-model="content" ref="quillEditor" :options="editorOption" @blur="onEditorBlur($event)" @focus="onEditorFocus($event)" @change="onEditorChange($event)"></quill-editor>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import { quillEditor } from 'vue-quill-editor'
import { Quill } from 'vue-quill-editor'
let fontSizeStyle = Quill.import('attributors/style/size')
fontSizeStyle.whitelist = [
  '10px',
  '12px',
  false,
  '16px',
  '18px',
  '20px',
  '24px',
  '26px',
  '32px',
  '48px'
]
Quill.register(fontSizeStyle, true)
// 这里引入修改过的video模块并注册
import { Video, Audio } from '@/assets/quill/video'
// 默认选择是16
Quill.register(Video, true)
Quill.register(Audio, true)

// 工具栏配置
const toolbarOptions = [
  ['bold', 'italic', 'underline', 'strike'], // 加粗 斜体 下划线 删除线
  ['blockquote', 'code-block'], // 引用  代码块
  [{ list: 'ordered' }, { list: 'bullet' }], // 有序、无序列表
  [{ indent: '-1' }, { indent: '+1' }], // 缩进
  [
    {
      size: fontSizeStyle.whitelist
    }
  ], // 字体大小
  [{ header: [1, 2, 3, 4, 5, 6, false] }], // 标题
  [{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色
  [{ align: [] }], // 对齐方式
  ['clean'], // 清除文本格式
  ['link', 'image', 'upload', 'audio'] // 链接、图片、视频、音频
]
export default {
  props: {
    /* 编辑器的内容 */
    value: {
      type: String,
      default: ''
    },
    /* 图片大小 */
    maxSize: {
      type: Number,
      default: 4000 //kb
    }
  },
  components: { quillEditor },
  data() {
    return {
      loading: false,
      content: this.value,
      editorOption: {
        placeholder: '',
        theme: 'snow', // or 'bubble'
        placeholder: '请输入内容',
        modules: {
          toolbar: {
            container: toolbarOptions,
            handlers: {
              image: function (value) {
                if (value) {
                  // 触发input框选择图片文件
                  document.querySelector('.quill-img input').click()
                } else {
                  this.quill.format('image', false)
                }
              },
              upload: (value) => {
                if (value) {
                  document.querySelector('.uploadFile input').click()
                } else {
                  this.quill.format('video', false)
                }
              },
              audio: (value) => {
                if (value) {
                  document.querySelector('.uploadFile2 input').click()
                } else {
                  this.quill.format('audio', false)
                }
              }
            }
          }
        }
      },
      uploadImgUrl: process.env.VUE_APP_BASE_API + '/common/upload', // 上传的图片服务器地址
      headers: {
        Authorization: 'Bearer ' + getToken()
      }
    }
  },
  watch: {
    value: function () {
      this.content = this.value
    }
  },
  methods: {
    onEditorBlur() {
      this.$emit('blur', this.content)
      //失去焦点事件
    },
    onEditorFocus() {
      //获得焦点事件
    },
    onEditorChange() {
      //内容改变事件
      this.$emit('input', this.content)
    },

    // 富文本图片上传前
    quillImgBefore(file) {
      this.loading = true
      const isPic = [
        'image/jpeg',
        'image/png',
        'image/jpg',
        'image/gif'
      ].includes(file.type)
      const isLt10M = file.size / 1024 / 1024 < 2

      if (!isPic) {
        this.$message.error('请上传规定的图片格式(png/jpg/jpeg/gif)')
        this.loading = false
      }
      if (!isLt10M) {
        this.$message.error(`上传图片大小不能超过2MB!`)
        this.loading = false
      }
      return isPic && isLt10M
    },
    handleFileBeforeUpload(file) {
      this.loading = true
      // 视频
      const isVideo = ['video/mp4', 'video/ogg', 'video/webm'].includes(
        file.type
      )
      // 视频限制500M
      const limitSize = file.size / 1024 / 1024 < 500
      if (!isVideo) {
        this.$message.error('请上传规定的视频格式(mp4/ogg/webm)')
        this.loading = false
      }
      if (!limitSize) {
        this.$message.error(`上传视频大小不能超过500MB!`)
        this.loading = false
      }

      return isVideo && limitSize
    },
    handleFileBeforeUpload2(file) {
      this.loading = true
      // 音频
      const isAudio = ['audio/mp3', 'audio/ogg', 'audio/mpeg'].includes(
        file.type
      )
      // 音频限制500M
      const limitSize = file.size / 1024 / 1024 < 500
      if (!isAudio) {
        this.$message.error('请上传规定的音频格式(mp3/ogg/webm)')
        this.loading = false
      }
      if (!limitSize) {
        this.$message.error(`上传音频大小不能超过500MB!`)
        this.loading = false
      }
      return isAudio && limitSize
      // let fileType = file.type
      // if (
      //   file.type === 'audio/mp3' ||
      //   file.type === 'audio/ogg' ||
      //   file.type === 'audio/mpeg'
      // ) {
      //   return true
      // } else {
      //   this.$message.error('请插入音频类型文件')
      //   return false
      // }
    },
    quillImgSuccess(res, file) {
      this.$nextTick(() => {
        this.loading = false
        // res为图片服务器返回的数据
        // 获取富文本组件实例
        if (this.$refs.quillEditor.quill) {
          let quill = this.$refs.quillEditor.quill
          // 如果上传成功
          if (res.code == 200) {
            // 获取光标所在位置
            let length = quill.getSelection().index
            // 插入图片  res.url为服务器返回的图片地址
            quill.insertEmbed(length, 'image', res.url)
            // 调整光标到最后
            quill.setSelection(length + 1)
          } else {
            this.$message.error('图片插入失败')
          }
        }
      })
    },
    // 文件
    handleFileSuccess(res, file) {
      this.$nextTick(() => {
        this.loading = false
        if (this.$refs.quillEditor.quill) {
          if (res.code == 200) {
            // 插入链接
            let quill = this.$refs.quillEditor.quill
            console.log(quill)
            let length = quill.getSelection().index
            quill.insertEmbed(length, 'video', res.url)
            quill.setSelection(length + 1)
          } else {
            this.$message.error('视频插入失败')
          }
        }
      })
    },
    handleFileSuccess2(res, file) {
      this.$nextTick(() => {
        this.loading = false
        if (this.$refs.quillEditor.quill) {
          if (res.code === 200) {
            console.log(res.url)
            // 插入链接
            let quill = this.$refs.quillEditor.quill
            let length = quill.getSelection().index
            quill.insertEmbed(length, 'audio', res.url)
            quill.setSelection(length + 1)
          } else {
            this.$message.error('音频插入失败')
          }
        }
      })
    },
    // 富文本图片上传失败
    uploadError() {
      // loading动画消失
      this.$message.error('图片插入失败')
    }
  }
}
</script> 

<style>
.editor {
  line-height: normal !important;
}
.quill-img {
  display: none;
}
.ql-editor {
  min-height: 192px;
  max-height: 400px;
}
.ql-snow .ql-tooltip[data-mode='link']::before {
  content: '请输入链接地址:';
}
.ql-snow .ql-tooltip.ql-editing a.ql-action::after {
  border-right: 0px;
  content: '保存';
  padding-right: 0px;
}
.ql-toolbar.ql-snow + .ql-container.ql-snow {
  min-height: 192px;
}
.ql-snow .ql-tooltip[data-mode='video']::before {
  content: '请输入视频地址:';
}

.ql-snow .ql-picker.ql-size .ql-picker-label::before,
.ql-snow .ql-picker.ql-size .ql-picker-item::before {
  content: '14px';
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value='small']::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value='small']::before {
  content: '10px';
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value='large']::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value='large']::before {
  content: '18px';
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value='huge']::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value='huge']::before {
  content: '32px';
}

.ql-snow .ql-picker.ql-header .ql-picker-label::before,
.ql-snow .ql-picker.ql-header .ql-picker-item::before {
  content: '文本';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='1']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='1']::before {
  content: '标题1';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='2']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='2']::before {
  content: '标题2';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='3']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='3']::before {
  content: '标题3';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='4']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='4']::before {
  content: '标题4';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='5']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='5']::before {
  content: '标题5';
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value='6']::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value='6']::before {
  content: '标题6';
}

.ql-snow .ql-picker.ql-font .ql-picker-label::before,
.ql-snow .ql-picker.ql-font .ql-picker-item::before {
  content: '标准字体';
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value='serif']::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value='serif']::before {
  content: '衬线字体';
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value='monospace']::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value='monospace']::before {
  content: '等宽字体';
}
</style>
<style lang="scss" scoped>
// 去除默认样式
::v-deep .uploadImage,
::v-deep .uploadFile,
.uploadFile2 {
  width: 0;
  height: 0;
  display: none;
}
</style>
<style>
.ql-editor .ql-video {
  width: 500px !important;
}
.ql-snow.ql-toolbar .ql-upload {
  width: 14px;
  height: 24px;
  background-size: 100%;
  margin-left: 10px;
  background-position: center center;
  background-repeat: no-repeat;
  background-image: url('../../assets/image/video.png');
}
.ql-snow.ql-toolbar .ql-audio {
  width: 14px;
  height: 24px;
  background-size: 100%;
  margin-left: 10px;
  background-position: center center;
  background-repeat: no-repeat;
  background-image: url('../../assets/image/audio.png');
}
</style>