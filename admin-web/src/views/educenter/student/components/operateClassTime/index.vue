<template>
  <div>
    <el-dialog :title="lessonTitle" :visible.sync="showLesson" width="30%" :before-close="handleClose">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" label-position="left">
        <el-form-item label="课程名称:">
          {{ classData.courseName }}
        </el-form-item>
        <el-form-item label="剩余课次:">
          {{ classData.remainingNumber }}
        </el-form-item>
        <template v-if="isAdd">
          <el-form-item label="增加课次:" prop="num">
            <el-input v-model="form.num" placeholder="请输入增加课次" maxlength="10" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="增加原因:" prop="result">
            <el-input v-model="form.result" placeholder="请输入增加原因" maxlength="40" show-word-limit></el-input>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="减少课次:" prop="num">
            <el-input v-model="form.num" placeholder="请输入减少课次" maxlength="10" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="减少原因:" prop="result">
            <el-input v-model="form.result" placeholder="请输入减少原因" maxlength="40" show-word-limit></el-input>
          </el-form-item>
        </template>
        <div style="display:flex;align-items:center;justify-content:space-around">
          <span style="color:#ff6206">
            <svg-icon icon-class="warn" /> 课次为敏感数据，请谨慎操作！
          </span>
          <el-button @click="operateClassHour" style="width:140px;color: #ffffff;background-color: #FF6206;border-color: #FF6206;">
            确定
          </el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    showLesson: {
      type: Boolean,
      default: false
    },
    isAdd: {
      type: Boolean,
      default: false
    },
    classData: {
      type: Object,
      default: () => ({})
    }
  },
  watch: {
    showLesson: {
      handler() {
        console.log('触发')
        if (this.isAdd) {
          this.lessonTitle = '增加课次'
        } else {
          this.lessonTitle = '减少课次'
        }
        this.$nextTick(() => {
          this.$refs['form'].resetFields()
        })
      }
    }
  },
  data() {
    var checkClassHour = (rule, value, callback) => {
      console.log('测试')
      if (!value) {
        return callback(new Error('不能为空'))
      } else {
        if (!Number.isInteger(parseFloat(value))) {
          callback(new Error('请输入正整数'))
        } else {
          if (value <= 0) {
            callback(new Error('输入的值必须大于0'))
          } else {
            callback()
          }
        }
      }
    }
    return {
      lessonTitle: '',
      form: {
        num: '',
        result: ''
      },
      // 表单校验
      rules: {
        num: [
          {
            required: true,
            validator: checkClassHour,
            trigger: 'blur'
          }
        ],
        result: [
          {
            required: true,
            message: '原因不能为空',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    operateClassHour() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$emit('operateClassHour', {
            num: this.form.num,
            reason: this.form.result,
            courseTimeId: this.classData.id,
            changeType: this.isAdd ? 0 : 1
          })
        }
      })
    },
    // 关闭关联弹窗
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>

