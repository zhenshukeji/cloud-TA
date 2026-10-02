<template>
  <div class="login">
    <!-- <img src="@/assets/image/loginImg.png" alt="" class="login_img" /> -->
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" v-if="login == 0">
      <!-- 社区版（OS011）：移除角色填充按钮（单机构单校区，且平台管理员属于商业版语义） -->
      <h3 class="title">云朵助教后台管理系统</h3>
      <el-form-item prop="username" class="input_box">
        <el-input v-model="loginForm.username" type="text" maxlength="14" autocomplete="off" placeholder="请输入登录账号/注册手机号码"
          class="input_sel" @keyup.enter.native="handleLogin" />
        <i class="el-icon-user-solid input-icon"></i>
      </el-form-item>
      <el-form-item prop="password" class="input_box">
        <el-input v-model="loginForm.password" type="password" maxlength="16" autocomplete="off" placeholder="请输入密码"
          class="input_sel" @keyup.enter.native="handleLogin" />
        <i class="el-icon-unlock input-icon"></i>
      </el-form-item>
      <div class="reg-box">
        <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
        <!-- 社区版（OS011）：忘记密码依赖短信验证码、注册是平台入驻入口，两者均已移除 -->
      </div>
      <el-form-item style="width: 100%; margin-top: 58px">
        <el-button :loading="loading" size="medium" class="btn" type="primary" style="width: 100%" @click="handleLogin">
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span></span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from '@/api/login'
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import { isManualLogout, clearManualLogout } from '@/utils/auth'

export default {
  name: 'Login',

  data() {
    var validatePassword = (rule, value, callback) => {
      if (!value || value.length <= 6) {
        callback(new Error('密码必须大于6个字符'))
      } else {
        callback()
      }
    }
    var checkPwd = (rule, value, callback) => {
      if (value != this.getpwdForm.passWord) {
        callback(new Error('两次密码输入不一致'))
      } else {
        callback()
      }
    }
    return {
      // 社区版（OS011）：只保留登陆页（0），找回密码两步（1/2）随短信链路移除
      click: true,
      login: 0,
      codeUrl: '',
      cookiePassword: '',
      // 登陆
      loginForm: {
        username: '',
        password: '',
        rememberMe: false,
        code: '',
        uuid: ''
      },
      loginRules: {
        username: [
          {
            required: true,
            trigger: 'blur',
            message: '登录账号/手机号码不能为空'
          }
        ],
        password: [
          {
            required: true,
            trigger: 'blur',
            message: '密码不能为空'
          }
        ],
        code: [
          {
            required: true,
            trigger: 'change',
            message: '验证码不能为空'
          }
        ]
      },
      loading: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    // this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then((res) => {
        this.codeUrl = 'data:image/gif;base64,' + res.img
        // this.loginForm.uuid = res.uuid
      })
    },
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password:
          password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set('username', this.loginForm.username, {
              expires: 30
            })
            Cookies.set('password', encrypt(this.loginForm.password), {
              expires: 30
            })
            Cookies.set('rememberMe', this.loginForm.rememberMe, {
              expires: 30
            })
          } else {
            Cookies.remove('username')
            Cookies.remove('password')
            Cookies.remove('rememberMe')
          }
          this.$store
            .dispatch('Login', this.loginForm)
            .then(() => {
              // 手动退出登录后再登录：忽略遗留的 redirect 地址，直接进入当前账号的对应首页
              const manual = isManualLogout()
              clearManualLogout()
              this.$router.push({ path: manual ? '/' : this.redirect || '/' })
            })
            .catch(() => {
              this.loading = false
              // this.getCode()
            })
        }
      })
    },
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login {
  position: relative;

  .login_img {
    position: absolute;
    top: 343px;
    left: 90px;
  }

  // display: flex;
  // justify-content: center;
  // align-items: center;
  height: 100%;
  position: relative;
  background-image: url('../assets/image/login_bg.jpg');
  background-size: cover;
}

.login-form {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  line-height: 20px;
  border-radius: 20px;
  font-size: 14px;
  text-align: center;
  background-color: #ffffff;
  // box-shadow: 0px 5px 18px 3px rgba(79, 147, 254, 0.3);
  // font-family: Roboto;
  border: 1px solid rgba(255, 255, 255, 100);
  padding: 36px 52px 64px;

  .setCode {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 35px;

    span {
      color: #fe774f;
      border-bottom: 1px solid #fe774f;
      cursor: pointer;
    }
  }

  .backLogin {
    cursor: pointer;
    width: 71px;
    height: 20px;
    color: #777;
    font-size: 14px;
    position: absolute;
    top: 0;
    left: 0;
  }

  .input_box {
    position: relative;

    ::v-deep .input_sel {
      .el-input__inner {
        width: 385px;
        height: 60px;
        box-sizing: border-box;
        // padding: 20px 20px 20px 60px;
        padding-left: 60px;
        line-height: 20px;
        border-radius: 100px;
        border: 1px solid rgba(220, 220, 220, 100);
        display: flex;
        align-items: center;
      }
    }

    .input-icon {
      font-size: 24px;
      color: rgba(79, 147, 254, 100);
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      left: 17px;
    }
  }

  .title {
    color: rgba(16, 16, 16, 100);
    font-size: 24px;
    text-align: center;
    margin: 36px 0 51px;
  }

  .reg-box {
    display: flex;
    justify-content: space-between;

    .text {
      color: #4f93fe;
      font-size: 14px;
      cursor: pointer;
    }

    .reg {
      margin-left: 16px;
    }
  }

  .btn {
    cursor: pointer;
    height: 61px;
    // line-height: 61px;
    border-radius: 100px;
    background-color: rgba(79, 147, 254, 100);
    color: rgba(255, 255, 255, 100);
    font-size: 18px;
    // font-weight: 600;
    text-align: center;
  }
}

::v-deep .input_box {
  .el-form-item__error {
    left: 60px;
  }
}

// .login-form {
//   border-radius: 6px;
//   background: #ffffff;
//   width: 400px;
//   padding: 25px 25px 5px 25px;
//   .el-input {
//     height: 38px;
//     input {
//       height: 38px;
//     }
//   }
//   .input-icon {
//     height: 39px;
//     width: 14px;
//     margin-left: 2px;
//   }
// }
// .login-tip {
//   font-size: 13px;
//   text-align: center;
//   color: #bfbfbf;
// }
// .login-code {
//   width: 33%;
//   height: 38px;
//   float: right;
//   img {
//     cursor: pointer;
//     vertical-align: middle;
//   }
// }
// .el-login-footer {
//   height: 40px;
//   line-height: 40px;
//   position: fixed;
//   bottom: 0;
//   width: 100%;
//   text-align: center;
//   color: #fff;
//   font-family: Arial;
//   font-size: 12px;
//   letter-spacing: 1px;
// }
</style>
