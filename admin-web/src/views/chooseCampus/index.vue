<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">云朵助教后台管理系统</h3>
      <div v-for="(item,index) in userList" :key="index" class="campus-item" :class="{active:index === activeIndex}" @click="chooseCampus(index)">
        {{ item.campusName }}
      </div>
      <el-form-item style="width:100%;">
        <div style="display:flex;justify-content:space-between">
          <el-button size="medium" @click="backLogin">
            <span>返回</span>
          </el-button>
          <el-button :loading="loading" size="medium" type="primary" @click="handleLogin">
            <span v-if="!loading">确 认</span>
          </el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  name: 'Login',
  computed: {
    ...mapState({
      userList: (state) => state.user.user.mecPersonnel.campusConnectionList,
      addRoute: (state) => state.permission.addRoutes
    })
  },
  data() {
    return {
      activeIndex: -1,
      codeUrl: '',
      cookiePassword: '',
      loginForm: {
        username: '',
        password: '',
        rememberMe: false,
        code: '',
        uuid: ''
      },
      loginRules: {},
      loading: false
    }
  },
  created() {
    console.log(this.userList)
    // this.getCode()
    // this.getCookie()
  },
  methods: {
    chooseCampus(index) {
      this.activeIndex = index
    },
    backLogin() {
      this.$store.dispatch('clearRole')
      this.$router.replace({
        path: '/login'
      })
      // this.$router.back()
    },
    handleLogin() {
      if (this.activeIndex === -1) {
        this.$message({
          message: '请选择校区',
          type: 'error'
        })
        return
      }
      this.$store
        .dispatch('CampusLogin', {
          campusId: this.userList[this.activeIndex].campusId
        })
        .then(() => {
          this.$store.dispatch('GenerateRoutes').then((accessRoutes) => {
            this.$router.addRoutes(accessRoutes)
            if (
              this.addRoute &&
              this.addRoute.length > 0 &&
              this.addRoute[0].children &&
              this.addRoute[0].children.length > 0
            ) {
              const parentPath = this.addRoute[0].path
              const childPath = this.addRoute[0].children[0].path
              let path = ''
              if (parentPath === '/') {
                path = parentPath
              } else {
                path = parentPath + '/' + childPath
              }
              this.$router.push({ path })
            } else {
              this.$router.push({ path: '/404' })
            }
          })
        })
    }
  }
}
</script>

<style  lang="scss" scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url('~@/assets/image/login_bg.jpg');
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.campus-item {
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #dcdcdc;
  // border-radius: 100px;
  padding: 10px;
  font-size: 16px;
  color: black;
  margin-bottom: 20px;
}
// .campus-item:hover {
//   background: red;
// }
.active {
  background: #426fb3 !important;
  color: #ffffff;
}
</style>
