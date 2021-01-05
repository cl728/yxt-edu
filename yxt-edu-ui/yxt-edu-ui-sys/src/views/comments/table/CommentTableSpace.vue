<template>
  <div>
    <el-card shadow="never" class="card" body-style="{height: '400px'}">
      <div slot="header" class="clearfix">
        <span>评论信息</span>
      </div>
      <div id="charts_one" style="width: 100%; min-height: 300px">
        <el-table :data="commentData" max-height="528" style="width: 100%">
          <el-table-column
            type="index"
            label="序号"
            width="50"
            align="center"
          ></el-table-column>
          <el-table-column property="user.username" label="发布者" align="center"></el-table-column>
          <el-table-column property="content" label="评论内容" align="center"></el-table-column>
          <el-table-column property="createTime" label="评论时间" align="center"></el-table-column>
          <el-table-column label="评论类型" align="center">
            <template slot-scope="scope">
              <el-tag
                size="medium"
                :type="scope.row.parentComment === null ? 'danger' : 'success'"
                >{{ scope.row.parentComment === null ? '发布' : '回复' }}</el-tag
              >
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template slot-scope="scope">
              <el-popconfirm
                confirmButtonText="确认"
                cancelButtonText="取消"
                confirmButtonType="danger"
                cancelButtonType="success"
                @confirm="deleteComment(scope.row.id)"
                title="确认要删除此评论吗？"
              >
                <el-button
                  size="mini"
                  slot="reference"
                  type="danger"
                >
                  <i class="el-icon-delete" />
                </el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
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
      commentData: [],
      pageData: {
        pageSize: 10,
        currentPage: 1,
        pageTotal: 200,
        queryPageRequest: {
          username: "",
        },
      },
    };
  },
  methods: {
    deleteComment(commentId) {
      this.$axios
        .delete("/comment/admin/id/" + commentId)
        .then(({ data }) => {
          if (data.success) {
            this.$message.success(data.message);
            this.getCommentData();
          } else {
            this.$message.error(data.message);
          }
        })
        .catch(() => {
          this.$message.error("服务器繁忙，请稍候再试一次！");
        });
    },
    pageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
    },
    currentChange(clickPage) {
      this.pageData.currentPage = clickPage;
    },
    getCommentData() {
      this.$axios
        .get(
          "comment/page/" +
            this.pageData.currentPage +
            "/" +
            this.pageData.pageSize
        )
        .then(({ data }) => {
          this.commentData = data.queryResult.data;
          this.pageData.pageTotal = data.queryResult.total;
        });
    },
  },
  mounted() {
    this.getCommentData();
  },
  watch: {
    pageData: {
      deep: true,
      handler() {
        this.getCommentData();
      },
    },
  },
};
</script>