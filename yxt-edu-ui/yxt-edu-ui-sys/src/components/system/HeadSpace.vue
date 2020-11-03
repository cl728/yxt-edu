<template>
  <div>
    <div style="float: left">
      <i
        @click="change_aside_state"
        :class="aside_state == true ? 'el-icon-s-fold' : 'el-icon-s-unfold'"
        :style="{
          color: head.head_icon_color,
          padding: '20px',
          cursor: 'pointer',
        }"
      />
    </div>
    <el-menu
      class="el-menu-demo"
      mode="horizontal"
      :background-color="head.head_background_color"
      :text-color="head.head_text_color"
      :active-text-color="head.head_active_text_color"
      :style="{ float: 'right' }"
    >
      <el-menu-item index="3" @click="message()">
        <i class="el-icon-bell" :style="{ color: head.head_icon_color }" />
        <el-badge is-dot class="item"></el-badge>
      </el-menu-item>
      <el-menu-item index="1">
        <ColorSettingSpace />
      </el-menu-item>
      <el-menu-item index="2" @click="change_full_screen">
        <template slot="title">
          <i
            class="el-icon-full-screen"
            :style="{ color: head.head_icon_color }"
          />
        </template>
      </el-menu-item>
      <el-submenu index="4">
        <template slot="title">
          {{ user.user_pet_name }}
          <el-avatar :src="user.user_avatar_url"></el-avatar>
        </template>
        <el-menu-item index="2-1">个人中心</el-menu-item>
        <el-menu-item index="2-2">修改密码</el-menu-item>
        <el-menu-item index="2-3" @click="logout()">退出</el-menu-item>
      </el-submenu>
      <el-menu-item index="10">
        <HeadSettingSpace />
      </el-menu-item>
    </el-menu>
  </div>
</template>
<script>
import Pass from "@/plugins/Pass.js";
import HeadSettingSpace from "@/components/system/HeadSettingSpace";
import ColorSettingSpace from "@/components/system/ColorSettingSpace";
export default {
  components: {
    HeadSettingSpace,
    ColorSettingSpace,
  },
  data() {
    return {
      search: "",
      user: {
        user_pet_name: "admin",
        user_avatar_url:
          "http://www.pava.run/group1/M00/00/00/rBAABV9oD7yAZtXEAAAnvyJPq-0710.jpg",
      },
      head: {
        head_icon_color: this.$cookies.get("setting").head_icon_color,
        head_background_color: this.$cookies.get("setting")
          .head_background_color,
        head_text_color: this.$cookies.get("setting").head_text_color,
        head_active_text_color: this.$cookies.get("setting")
          .head_active_text_color,
      },
      aside_state: false,
      fullscreen: false, //全屏专题
    };
  },
  methods: {
    change_aside_state() {
      this.aside_state = !this.aside_state;
      Pass.$emit("aside_state", this.aside_state);
    },
    message() {
      this.$router.push("/page/message/list");
    },
    change_full_screen() {
      //全屏切换函数
      let element = document.documentElement;
      if (this.fullscreen) {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        } else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.msExitFullscreen) {
          document.msExitFullscreen();
        }
      } else {
        if (element.requestFullscreen) {
          element.requestFullscreen();
        } else if (element.webkitRequestFullScreen) {
          element.webkitRequestFullScreen();
        } else if (element.mozRequestFullScreen) {
          element.mozRequestFullScreen();
        } else if (element.msRequestFullscreen) {
          // IE11
          element.msRequestFullscreen();
        }
      }
      this.fullscreen = !this.fullscreen; //判断全屏状态
    },
    logout() {
      this.$axios
        .delete("auth/logout/1")
        .then(({ data }) => {
          if (data.success) {
            this.$router.push("/login");
          } else {
            this.$message.error(data.message);
          }
        })
        .catch(() => {
          this.$message.error("服务器繁忙，请稍候再试！");
        });
    },
  },
};
</script>
<style scoped>
.el-menu {
  /* 去除右侧边框线 */
  border-bottom: 0 !important;
}
</style>