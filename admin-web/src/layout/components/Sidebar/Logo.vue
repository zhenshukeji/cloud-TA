<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}">
    <transition name="sidebarLogoFade">
      <div v-if="collapse" key="collapse" class="sidebar-logo-link" @click="goHome">
        <!-- <img v-if="logo" :src="logo" class="sidebar-logo"> -->
        <h1 class="sidebar-title">云朵助教</h1>
      </div>
      <div v-else key="expand" class="sidebar-logo-link" @click="goHome">
        <!-- <img v-if="logo" :src="logo" class="sidebar-logo"> -->
        <p class="title_box">云朵助教</p>
        <h1 class="sidebar-title">{{ title }} </h1>
      </div>
    </transition>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/logo.png'
import { mapState } from 'vuex'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      title: 'LEARNING TO LEARN',
      logo: logoImg
    }
  },
  computed: {
    ...mapState({
      addRoute: (state) => state.permission.addRoutes
    })
  },
  methods: {
    goHome() {
      const parentPath = this.addRoute[0].path
      const childPath = this.addRoute[0].children[0].path
      let path = ''
      if (parentPath === '/') {
        path = parentPath
      } else {
        path = parentPath + '/' + childPath
      }
      this.$router.push({
        path
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  // height: 50px;
  // line-height: 50px;
  background: #fff;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    cursor: pointer;
    & .sidebar-logo {
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 12px;
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #FF7441;
      font-weight: 600;
      line-height: 30px;
      font-size: 14px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }

    .title_box {
      font-size: 38px;
      line-height: 45px;
      margin: 0;
      padding: 10px 0 0;
      color: #FF7441;
      font-weight: bold;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}
</style>
