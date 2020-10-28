<template>
  <div>
    <el-card shadow="never" class="card">
      <div slot="header" class="clearfix">
        <span>学校信息</span>
      </div>
      <div id="charts_one" style="width: 100%; min-height: 300px">
        <el-button
          type="danger"
          size="small"
          icon="el-icon-finished"
          :disabled="selectionButtonState"
          @click="showSelection"
          >{{ selectionButtonTitle }}</el-button
        >
        <el-table
          :data="schoolData"
          max-height="350"
          @selection-change="selection"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column
            type="index"
            label="序号"
            width="50"
          ></el-table-column>
          <el-table-column property="value" label="学校名"></el-table-column>
        </el-table>
        <el-pagination
          @size-change="pageSizeChange"
          @current-change="currentChange"
          :current-page="pageData.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageData.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageData.pageTotal"
          style="margin-top: 10px"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>
<script>
export default {
  data() {
    return {
      selectionButtonTitle: "未选择数据",
      selectionButtonState: true,
      selectionData: [],
      SchoolForm: {
        value: "",
      },
      schoolData: [],
      pageData: {
        pageSize: 10,
        currentPage: 1,
        pageTotal: 200,
      },
    };
  },
  methods: {
    showSelection() {
      this.$message.success("已打印选择数据成功，请打开检查查看");
      console.log(this.selectionData);
    },
    pageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
      this.getSchoolData();
    },
    currentChange(clickPage) {
      this.pageData.currentPage = clickPage;
      this.getSchoolData();
    },
    getSchoolData() {
      this.$axios
        .get(
          "users/schools/" + this.pageData.currentPage + "/" + this.pageData.pageSize
        )
        .then(({ data }) => {
          this.schoolData = data.queryResult.data;
          this.pageData.pageTotal = data.queryResult.total;
        });
    },
  },
  mounted() {
    this.getSchoolData();
  },
};
</script>