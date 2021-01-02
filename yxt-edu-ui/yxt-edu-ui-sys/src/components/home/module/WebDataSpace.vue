<template>
  <el-card shadow="never" class="card">
    <div slot="header" class="clearfix">
      <span>网站数据</span>
    </div>
    <div>
      <el-row :gutter="20">
        <el-col :span="16">
          <div id="myChart" style="width: 100%; height: 400px"></div>
        </el-col>
        <el-col :span="8">
          <div
            class="grid-content bg-purple"
            id="labelVist"
            style="width: 100%; height: 400px"
          ></div>
        </el-col>
      </el-row>
    </div>
  </el-card>
</template>

<script>
import "echarts/theme/macarons.js";
export default {
  data() {
    return {
      perHourViewCount: [
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
        Math.ceil(Math.random() * 1000),
      ],
      userTypeCount: [
        { value: Math.ceil(Math.random() * 1000), name: "超级管理员" },
        { value: Math.ceil(Math.random() * 1000), name: "管理员" },
        { value: Math.ceil(Math.random() * 1000), name: "教师" },
        { value: Math.ceil(Math.random() * 1000), name: "学生" },
      ],
    };
  },
  methods: {
    drawLine() {
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(
        document.getElementById("myChart"),
        "macarons"
      );
      // 绘制图表
      myChart.setOption({
        title: {
          text: "网站访问数据",
          subtext: "http://www.yixuetang.com",
        },
        tooltip: {
          trigger: "axis",
        },
        legend: {
          data: ["访问量"],
        },
        toolbox: {
          show: true,
          feature: {
            magicType: { show: true, type: ["line", "bar"] },
            dataView: { show: true, readOnly: false },
            restore: { show: true },
            saveAsImage: { show: true },
          },
        },
        calculable: true,
        xAxis: [
          {
            type: "category",
            data: [
              "0:00",
              "1:00",
              "2:00",
              "3:00",
              "4:00",
              "5:00",
              "6:00",
              "7:00",
              "8:00",
              "9:00",
              "10:00",
              "11:00",
              "12:00",
              "13:00",
              "14:00",
              "15:00",
              "16:00",
              "17:00",
              "18:00",
              "19:00",
              "20:00",
              "21:00",
              "22:00",
              "23:00",
            ],
          },
        ],
        yAxis: [
          {
            type: "value",
          },
        ],
        series: [
          {
            name: "访问量",
            type: "bar",
            data: this.perHourViewCount,
            markPoint: {
              data: [
                { type: "max", name: "最大值" },
                { type: "min", name: "最小值" },
              ],
            },
            markLine: {
              data: [{ type: "average", name: "平均值" }],
            },
          },
        ],
      });
    },
    labelVist() {
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(
        document.getElementById("labelVist"),
        "macarons"
      );
      // 绘制图表
      myChart.setOption({
        title: {
          text: "网站用户类别",
          subtext: "yxt",
          left: "center",
        },
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b} : {c} ({d}%)",
        },
        legend: {
          orient: "vertical",
          left: "left",
          data: ["超级管理员", "管理员", "老师/助教", "学生"],
        },
        series: [
          {
            name: "用户数量",
            type: "pie",
            radius: "55%",
            center: ["50%", "60%"],
            data: this.userTypeCount,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
      });
    },
    getPerHourViewCount() {
      this.$axios.get("auth/perHourViewCount").then(({ data }) => {
        if (data.success) {
          this.perHourViewCount = data.queryResult.data;
          this.drawLine();
        }
      });
    },
    getUserTypeCount() {
      this.$axios.get("users/userTypeCount").then(({ data }) => {
        this.userTypeCount = data.queryResult.data;
        this.labelVist();
      });
    },
  },
  mounted() {
    this.getPerHourViewCount();
    this.getUserTypeCount();
  },
};
</script>
<style scoped>
.card {
  min-height: 50vh;
}
</style>