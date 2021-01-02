<template>
  <div>
    <el-row :gutter="20">
      <el-col v-for="(car, index) in webData" :key="index" :span="6">
        <el-card shadow="never">
          <div slot="header" class="clearfix">
            <span>
              <i :class="car.titleIcon" />
              {{ car.title }}
            </span>
          </div>
          <div>
            <h1 style="font-size: 150%; color: #909399">{{ car.vistNum }}</h1>
            <br />
            <p style="float: left; color: #909399">{{ car.bottomTitle }}</p>
            <p style="float: right; color: #909399">
              {{ car.vistAllNum }}
              <i :class="car.vistAllIcon" />
            </p>
          </div>
          <br />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  data() {
    return {
      webData: [
        {
          titleIcon: "el-icon-view",
          title: "访问",
          cycleBackColor: "#409EFF",
          bottomTitle: "访问总量",
          vistNum: Math.ceil(Math.random() * 100000),
          vistAllNum: Math.ceil(Math.random() * 100),
          vistAllIcon: "el-icon-trophy",
        },
        {
          titleIcon: "el-icon-download",
          title: "下载",
          cycleBackColor: "#67C23A",
          bottomTitle: "下载总量",
          vistNum: Math.ceil(Math.random() * 100000),
          vistAllNum: Math.ceil(Math.random() * 100),
          vistAllIcon: "el-icon-download",
        },
        {
          titleIcon: "el-icon-user",
          title: "用户",
          cycleBackColor: "#E6A23C",
          bottomTitle: "总用户",
          vistNum: Math.ceil(Math.random() * 100000),
          vistAllNum: Math.ceil(Math.random() * 100),
          vistAllIcon: "el-icon-data-line",
        },
      ],
    };
  },
  created() {
    this.$axios.get("auth/webDataCount").then(({ data }) => {
      if (data.success) {
        let webDataCount = data.queryResult.data[0];
        this.webData[0].vistAllNum = webDataCount.totalVisitCount;
        this.webData[0].vistNum = webDataCount.dateVisitCount;
        this.webData[1].vistAllNum = webDataCount.totalDownloadCount;
        this.webData[1].vistNum = webDataCount.dateDownloadCount;
        this.webData[2].vistAllNum = webDataCount.totalRegisterCount;
        this.webData[2].vistNum = webDataCount.dateRegisterCount;
      }
    });
  },
};
</script>
<style scoped>
#cycle {
  width: 30px;
  height: 25px;
  float: right;
  border-radius: 3px;
  color: white;
  padding-left: 10px;
}
</style>