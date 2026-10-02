<template>
  <div v-show="show" class="addStudent-page">
    <el-form ref="form" :model="form" :rules="rules" label-width="150px" label-position="left">
      <div class="student-box">
        <div class="left">
          <el-form-item label="学生姓名:" prop="studentName">
            <el-input v-model="form.studentName" placeholder="请输入学生姓名" maxlength="16" show-word-limit/>
          </el-form-item>
          <el-form-item label="年级:" prop="grade">
            <el-input v-model="form.grade" placeholder="请输入年级" maxlength="10" show-word-limit/>
          </el-form-item>
          <el-form-item label="年龄:" prop="age">
            <el-input v-model="form.age" placeholder="请输入年龄" maxlength="3" show-word-limit/>
          </el-form-item>
          <el-form-item label="住址:" prop="address">
            <el-input v-model="form.address" placeholder="请输入住址" maxlength="25" show-word-limit/>
          </el-form-item>
        </div>
        <div class="right">
          <el-form-item label="学生/家长手机号码:" prop="studentsPhone">
            <el-input v-model="form.studentsPhone" placeholder="请输入学生/家长手机号码" maxlength="11" show-word-limit/>
          </el-form-item>
          <el-form-item label="性别:" prop="sex">
            <el-select v-model="form.sex" placeholder="请选择性别">
              <el-option label="男" :value="0">
              </el-option>
              <el-option label="女" :value="1">
              </el-option>
              <el-option label="未知" :value="2">
              </el-option>
            </el-select>
          </el-form-item>
          <!-- <el-form-item label="身份证号:" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item> -->
          <el-form-item label="学校/幼儿园:" prop="schoolName">
            <el-input v-model="form.schoolName" placeholder="请输入学生学校/幼儿园" maxlength="16" show-word-limit/>
          </el-form-item>
        </div>
      </div>
      <p class="desc">-相关联系人</p>
      <div class="contact-box">
        <div class="left">
          <el-form-item label="关系:" prop="associated">
            <el-select v-model="form.associated" clearable placeholder="请选择与学生的关系">
              <el-option v-for="(item,index) in options" :key="item" :label="item" :value="index">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="家长手机号码:" prop="parentsPhone">
            <el-input v-model="form.parentsPhone" placeholder="请输入家长手机号码" maxlength="11" show-word-limit/>
          </el-form-item>
        </div>
        <div class="right">
          <el-form-item label="家长姓名:" prop="parentsName">
            <el-input v-model="form.parentsName" placeholder="请输入家长姓名" maxlength="16" show-word-limit/>
          </el-form-item>
        </div>
      </div>
    </el-form>
    <div style="display:flex;justify-content:space-around">
      <el-button @click="cancel" style="width:100px">取 消</el-button>
      <el-button type="primary" @click="submitForm" style="width:100px">保 存</el-button>
    </div>
  </div>
</template>

<script>
import { tel, idCard } from '@/utils/validate.js'
export default {
  props: {
    info: {
      type: Object,
      default: () => ({})
    },
    show: {
      type: Boolean,
      default: false
    },
    isCreate: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    show(val) {
      if (val) {
        this.reset()
        if (!this.isCreate) {
          this.form = { ...this.info }
          console.log(this.form)
        }
      }
    }
  },
  data() {
     var checkAge = (rule, value, callback) => {
      if( value != 0 && value){   
       var reg = /^[0-9]*[1-9][0-9]*$/
        if (!reg.test(value)) {
          callback(new Error('请输入正整数'))
        } else {
          if (value < 0) {
            callback(new Error('输入的值必须大于0'))
          } else {
            callback()
          }
        }
      }else{
         callback()
      }
    }
    var validatePhone = (rule, value, callback) => {
      if (value === '' || value === undefined || value === null) {
        callback(new Error('请输入手机号码'))
      } else if (tel(value)) {
        callback(new Error('请输入正确的手机号码'))
      } else {
        callback()
      }
    }
    var validatePhone2 = (rule, value, callback) => {
      if (value && tel(value)) {
        callback(new Error('请输入正确的手机号码'))
      } else {
        callback()
      }
    }
    var validateIdCard = (rule, value, callback) => {
      if (value && idCard(value)) {
        callback(new Error('请输入正确的身份证号'))
      } else {
        callback()
      }
    }
    return {
      form: {},
      // 表单校验
      rules: {
        age: [
          {
            validator: checkAge,
            trigger: 'change'
          }
        ],
        studentName: [
          {
            required: true,
            message: '学生姓名不能为空',
            trigger: ['change', 'blur']
          }
        ],
        studentsPhone: [
          {
            validator: validatePhone,
            trigger: ['change', 'blur'],
            required: true
          }
        ],
        parentsPhone: [
          { validator: validatePhone2, trigger: ['change', 'blur'] }
        ],
        idCard: [{ validator: validateIdCard, trigger: ['change', 'blur'] }]
      },
      options: ['父亲', '母亲', '爷爷', '奶奶', '外公', '外婆', '其他']
    }
  },
  mounted() {},
  methods: {
    submitForm() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$emit('submit', this.form)
        }
      })
    },
    //
    reset() {
      this.form = {
        studentName: undefined,
        grade: undefined,
        age: undefined,
        address: undefined,
        studentsPhone: undefined,
        sex: '',
        idCard: undefined,
        schoolName: undefined,
        associated: undefined,
        parentsPhone: undefined,
        parentsName: undefined
      }
      this.resetForm('form')
    },
    cancel() {
      this.$emit('close')
    }
  }
}
</script>

<style lang='scss' scoped>
.addStudent-page {
  padding: 20px;
  width: 1000px;
  ::v-deep .el-form-item {
    margin-bottom: 25px;
  }
  .desc {
    color: #4f93fe;
    font-size: 20px;
  }
  .student-box {
    display: flex;
    justify-content: space-between;
  }
  .contact-box {
    display: flex;
    justify-content: space-between;
  }
  .left {
    width: 400px;
  }
  .right {
    width: 400px;
  }
}
</style>
