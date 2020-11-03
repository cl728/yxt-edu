<template>
  <div>
    <el-card shadow="never" class="card">
      <div slot="header" class="clearfix">
        <span>角色信息</span>
      </div>
      <div id="charts_one" style="width: 100%; min-height: 300px">
        <el-table :data="roleData" max-height="350" style="width: 100%">
          <el-table-column
            type="index"
            label="序号"
            width="50"
          ></el-table-column>
          <el-table-column property="rname" label="角色名称"></el-table-column>
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
      roleData: [],
      pageData: {
        pageSize: 10,
        currentPage: 1,
        pageTotal: 200,
      },
    };
  },
  methods: {
    pageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
      this.getRoleData();
    },
    currentChange(clickPage) {
      this.pageData.currentPage = clickPage;
      this.getRoleData();
    },
    getRoleData() {
      this.$axios
        .get(
          "users/roles/page/" +
            this.pageData.currentPage +
            "/" +
            this.pageData.pageSize
        )
        .then(({ data }) => {
          this.roleData = data.queryResult.data;
          this.pageData.pageTotal = data.queryResult.total;
        });
    },
  },
  mounted() {
    this.getRoleData();
  },
};
</script>