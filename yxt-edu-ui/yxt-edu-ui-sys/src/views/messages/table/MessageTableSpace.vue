<template>
  <div>
    <el-card shadow="never" class="card" body-style="{height: '400px'}">
      <!-- 条件查询表单 -->
      <el-form
        :inline="true"
        :model="pageData.queryPageRequest"
        class="demo-form-inline"
      >
        <el-form-item>
          <el-select
            v-model="pageData.queryPageRequest.receiverType"
            placeholder="接收者类型"
          >
            <el-option label="全部" value="all"></el-option>
            <el-option label="全部用户" value="全部用户"></el-option>
            <el-option label="单一用户" value="单一用户"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="pageData.queryPageRequest.status"
            placeholder="通知状态"
          >
            <el-option label="全部" value="all"></el-option>
            <el-option label="已拉取" value="已拉取"></el-option>
            <el-option label="未拉取" value="未拉取"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="pageData.queryPageRequest.title"
            placeholder="通知标题"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="header" class="clearfix">
        <span>通知信息</span>
        <el-button
          type="success"
          size="small"
          icon="el-icon-plus"
          style="float: right"
          @click="input()"
          >发布</el-button
        >
      </div>
      <div id="charts_one" style="width: 100%; min-height: 300px">
        <el-table :data="messageData" max-height="528" style="width: 100%">
          <el-table-column
            type="index"
            label="序号"
            width="50"
            align="center"
          ></el-table-column>
          <el-table-column label="发布者" align="center">
            <template slot-scope="scope">
              {{ scope.row.publisher.username }}
            </template>
          </el-table-column>
          <el-table-column label="接收者类型" align="center">
            <template slot-scope="scope">
              {{ scope.row.receiverType === 0 ? "全部用户" : "单一用户" }}
            </template>
          </el-table-column>
          <el-table-column label="接收者" align="center">
            <template slot-scope="scope">
              {{
                scope.row.receiverType === 0
                  ? "全部用户"
                  : scope.row.receiver.username
              }}
            </template>
          </el-table-column>
          <el-table-column property="title" label="通知标题" align="center"></el-table-column>
          <el-table-column label="通知状态">
            <template slot-scope="scope">
              <el-tag
                size="medium"
                :type="scope.row.status ? 'success' : 'danger'"
                >{{ scope.row.status ? "已拉取" : "未拉取" }}</el-tag
              >
            </template>
          </el-table-column>
          <el-table-column
            property="publishTime"
            label="发布时间"
            align="center"
          ></el-table-column>
          <el-table-column
            property="updateTime"
            label="更新时间"
            align="center"
          ></el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="success"
                @click="edit(scope.row.id)"
              >
                <i class="el-icon-edit-outline" />
              </el-button>
              <el-popconfirm
                confirmButtonText="确认"
                cancelButtonText="取消"
                confirmButtonType="danger"
                cancelButtonType="success"
                @confirm="deleteMessage(scope.row.id)"
                title="确认要删除此通知吗？"
              >
                <el-button size="mini" slot="reference" type="danger">
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
      messageData: [],
      pageData: {
        pageSize: 10,
        currentPage: 1,
        pageTotal: 200,
        queryPageRequest: {},
      },
    };
  },
  methods: {
    input() {
      this.$router.push({ path: "/messages/input"})
    },
    deleteMessage(messageId) {
      this.$axios
        .delete("/message/admin/messageId/" + messageId)
        .then(({ data }) => {
          if (data.success) {
            this.$message.success(data.message);
            this.getMessageData();
          } else {
            this.$message.error(data.message);
          }
        })
        .catch(() => {
          this.$message.error("服务器繁忙，请稍候再试一次！");
        });
    },
    edit(messageId) {
      this.$router.push({
        path: '/messages/edit/' + messageId
      })
    },
    pageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
    },
    currentChange(clickPage) {
      this.pageData.currentPage = clickPage;
    },
    getMessageData() {
      this.$axios
        .get(
          "message/page/" +
            this.pageData.currentPage +
            "/" +
            this.pageData.pageSize +
            "?" +
            this.$qs.stringify(this.pageData.queryPageRequest)
        )
        .then(({ data }) => {
          this.messageData = data.queryResult.data;
          this.pageData.pageTotal = data.queryResult.total;
        });
    },
  },
  mounted() {
    this.getMessageData();
  },
  watch: {
    pageData: {
      deep: true,
      handler() {
        this.getMessageData();
      },
    },
  },
};
</script>