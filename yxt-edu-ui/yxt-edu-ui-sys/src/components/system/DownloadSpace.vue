<template>
  <div>
    <el-card shadow="never" class="card">
      <div slot="header" class="clearfix">
        <span>获取EuiAdmin源码</span>
      </div>
      <div id="back">
        <row>
          <el-col :span="6" v-for="(eui, index) in eui_data" :key="index">
            <el-card shadow="never" class="card">
              <div slot="header" class="clearfix">
                <span>{{ eui.title }}</span>
              </div>
              <div>
                <p style="color: #606266">{{ eui.explain }}</p>
                <div style="margin-top: 30px; text-align: center">
                  <el-button
                    :type="eui.button_type"
                    @click="download(index)"
                    icon="el-icon-download"
                    >{{ eui.button_title }}</el-button
                  >
                </div>
              </div>
              <p v-show="file_link != ''" style="color: #606266">
                如系统未能自动打开链接，请手动<a
                  :href="file_link"
                  target="_blank"
                  rel="noopener noreferrer"
                  >点击此处</a
                >
              </p>
            </el-card>
          </el-col>
        </row>
      </div>
    </el-card>
  </div>
</template>
<script>
export default {
  data() {
    return {
      file_link: "",
      web_link: "http://api.euiadmin.com",
      eui_data: [
        {
          title: "EuiAdmin0.1",
          explain:
            "EuiAdmin基于Vue+Element的免费开源后台模板。通过使用EuiAdmin你可以很简单的搭建一个成型的后台，可以为你提供简单的学习环境。",
          download_key: "euiadmin",
          button_title: "获取EuiAdmin源码",
          button_type: "success",
        },
      ],
    };
  },
  methods: {
    download(index) {
      this.$axios({
        method: "post",
        url: "/file/download",
        data: this.$qs.stringify({
          file_key: this.eui_data[index].download_key,
        }),
      }).then((response) => {
        this.$message.success("获取文件成功，即将执行下载");
        this.file_link = this.web_link + response.data;
        setTimeout(() => {
          window.open(this.file_link);
        }, 1500);
      });
    },
  },
};
</script>
<style scoped>
#back {
  min-height: 50vh;
  color: #606266;
}
</style>