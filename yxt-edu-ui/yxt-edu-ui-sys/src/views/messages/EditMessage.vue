<template>
  <el-container id="aside">
    <vAsideSpace />
    <el-container>
      <el-header
        :height="head_height"
        :style="{ backgroundColor: head_background_color }"
      >
        <vHeadSpace />
      </el-header>
      <el-main id="main" :style="{ backgroundColor: main_background_color }">
        <el-card shadow="never" style="min-height: 70vh">
          <div slot="header" class="clearfix">
            <span>编辑通知</span>
          </div>
          <el-form :model="messageForm" :rules="messageRule">
            <el-form-item prop="title">
              <el-input
                placeholder="请输入公告标题"
                v-model="messageForm.title"
                clearable
              >
              </el-input>
            </el-form-item>
            <el-form-item>
              <MessageEditor v-model="messageForm.content" />
            </el-form-item>
            <el-form-item>
              <el-radio-group v-model="messageForm.receiverType" disabled>
                <el-radio :label="messageForm.receiverType" border>{{
                  messageForm.receiverType === 0 ? "全部用户" : "单一用户"
                }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-show="messageForm.receiverType === 1">
              <el-input
                v-model="messageForm.receiverId"
                placeholder="请输入接收者id"
                disabled
              ></el-input>
            </el-form-item>
            <el-form-item>
              <div id="Message-button" style="float: right; padding-top: 10px">
                <el-button type="primary" plain @click="goback()">
                  取消
                </el-button>
                <el-button type="primary" @click="editMessage()">
                  确定
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
export default {
  components: {
    MessageEditor: (resolve) => {
      require(["./editor/MessageEditor"], resolve);
    },
  },
  data() {
    return {
      head_background_color: this.$cookies.get("setting").head_background_color,
      head_height: this.$cookies.get("setting").head_height + "px",
      main_background_color: this.$cookies.get("setting").main_background_color,
      messageForm: {},
      messageRule: {
        title: [{ required: true, message: "请输入通知标题", trigger: "blur" }],
      },
    };
  },
  methods: {
    editMessage() {
      this.$axios
        .put(
          "message/admin/adminId/" +
            3 +
            "/messageId/" +
            this.$route.params.messageId,
          this.messageForm
        ) // 管理员id暂时写死
        .then(({ data }) => {
          if (data.success) {
            this.$message.success(data.message);
            this.$router.push({ path: "/messages" });
          } else {
            this.$message.error(data.message);
          }
        })
        .catch(() => {
          this.$message.error("服务器繁忙，请稍候再试！");
        });
    },
    getMessageData() {
      this.$axios
        .get("/message/info/messageId/" + this.$route.params.messageId)
        .then(({ data }) => {
          if (data.success) {
            this.messageForm = data.queryResult.data[0];
          } else {
            this.$message.error(data.message);
          }
        })
        .catch(() => {
          this.$message.error("服务器繁忙，请稍候再试一次！");
        });
    },
    goback() {
      this.$router.push({ path: "/messages" });
    },
  },
  mounted() {
    this.getMessageData();
  },
};
</script>
<style scoped>
#aside {
  height: 100vh;
}
#main {
  height: 100vh;
  overflow: auto;
}
/*滚动条的宽度*/
::-webkit-scrollbar {
  width: 9px;
  height: 9px;
}

/*外层轨道。可以用display:none让其不显示，也可以添加背景图片，颜色改变显示效果*/
::-webkit-scrollbar-track {
  width: 6px;
  background-color: #f2f6fc;
  -webkit-border-radius: 2em;
  -moz-border-radius: 2em;
  border-radius: 2em;
}

/*滚动条的设置*/

::-webkit-scrollbar-thumb {
  background-color: #f56c6c;
  background-clip: padding-box;
  min-height: 1px;
  -webkit-border-radius: 2em;
  -moz-border-radius: 2em;
  border-radius: 2em;
}
/*滚动条移上去的背景*/

::-webkit-scrollbar-thumb:hover {
  background-color: #fff;
}
</style>