<template>
  <div id="login">
    <div id="form_space">
      <div align="center">
        <h1>{{ login_title }}</h1>
      </div>
      <div style="padding: 20px">
        <el-form ref="form" :model="user">
          <el-form-item>
            <el-input
              v-model="user.username"
              prefix-icon="el-icon-user"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="user.password"
              prefix-icon="el-icon-lock"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          </el-form-item>
          <el-form-item align="center">
            <el-button
              type="primary"
              @click="login()"
              style="display: block; width: 100%"
              >登录</el-button
            >
          </el-form-item>
          <el-form-item>
            <el-link type="danger" style="float: left" @click="to('/register')"
              >注册</el-link
            >
            <el-link
              type="primary"
              style="float: right"
              @click="to('/forget/password')"
              >找回密码？</el-link
            >
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>
<script>
import { hexMd5 } from "./api/md5";
export default {
  data() {
    return {
      login_title: "后台管理登录",
      user: {
        username: "",
        password: "",
      },
      rememberMe: false,
    };
  },
  methods: {
    login() {
      let loginUser = {
        username: this.user.username,
        password: hexMd5(this.user.password),
        rememberMe: this.rememberMe
      };
      this.$axios.post("auth/login/1/1", loginUser).then(() => {
        this.$router.push("/");
      });
    },
    to(link) {
      this.$router.push(link);
    },
    setting_web() {
      this.color_form = {
        aside_background_color: "#000000",
        aside_text_color: "#ffffff",
        aside_icon_color: "#ffffff",
        aside_active_text_color: "#67C23A",
        aside_title: "EuiAdmin",
        aside_small_title: "Eui",
        aside_width: 200,
        aside_small_width: 65,
        head_icon_color: "#606266",
        head_background_color: "#fff",
        head_text_color: "#606266",
        head_active_text_color: "#303133",
        head_height: 60,
        main_background_color: "#f2f6fc",
      };
      this.$cookies.set("setting", JSON.stringify(this.color_form));
    },
  },
  mounted() {
    if (this.$cookies.get("setting") == null) {
      this.setting_web();
    }
  },
};
</script>
<style scoped>
h1 {
  color: #606266;
}
p {
  color: #606266;
}
#login {
  min-height: 100vh;
}
#form_space {
  border-radius: 10px;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  height: 400px;
  width: 400px;
}
</style>