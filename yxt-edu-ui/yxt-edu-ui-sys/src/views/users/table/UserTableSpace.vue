<template>
  <div>
    <el-card shadow="never" class="card" body-style="{height: '400px'}">
      <div slot="header" class="clearfix">
        <span>用户信息</span>
      </div>
      <div id="charts_one" style="width: 100%; min-height: 300px">
        <el-form
          :inline="true"
          :model="pageData.queryPageRequest"
          class="demo-form-inline"
        >
          <el-form-item>
            <el-select
              v-model="pageData.queryPageRequest.roleName"
              placeholder="用户角色"
            >
              <el-option label="全部" value="all"></el-option>
              <el-option
                v-for="(role, index) in roleList"
                :key="index"
                :label="role.roleName"
                :value="role.roleName"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="pageData.queryPageRequest.gender"
              placeholder="用户性别"
            >
              <el-option label="全部" value="all"></el-option>
              <el-option label="男" value="男"></el-option>
              <el-option label="女" value="女"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="pageData.queryPageRequest.username"
              placeholder="用户名"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="pageData.queryPageRequest.realName"
              placeholder="用户姓名"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="pageData.queryPageRequest.email"
              placeholder="用户邮箱"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model.lazy="pageData.queryPageRequest.phone"
              placeholder="用户电话"
            ></el-input>
          </el-form-item>
        </el-form>
        <el-table :data="userData" max-height="528" style="width: 100%">
          <el-table-column
            type="index"
            label="序号"
            width="50"
          ></el-table-column>
          <el-table-column property="username" label="用户名"></el-table-column>
          <el-table-column property="email" label="用户邮箱"></el-table-column>
          <el-table-column property="phone" label="用户电话"></el-table-column>
          <el-table-column property="age" label="用户年龄"></el-table-column>
          <el-table-column label="用户性别">
            <template slot-scope="scope">
              <el-tag
                size="medium"
                :type="scope.row.gender === '女' ? 'danger' : 'success'"
                >{{ scope.row.gender }}</el-tag
              >
            </template>
          </el-table-column>
          <el-table-column label="用户角色">
            <template slot-scope="scope">
              <el-tag
                size="medium"
                :type="scope.row.role.rname == '管理员' ? 'danger' : 'success'"
                effect="dark"
                >{{ scope.row.role.rname }}</el-tag
              >
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-popconfirm
                confirmButtonText="确认"
                cancelButtonText="取消"
                confirmButtonType="danger"
                cancelButtonType="success"
                @confirm="deleteUser(scope.row.id)"
                title="确认要注销此用户吗？"
              >
                <el-button size="mini" slot="reference" type="danger" :disabled="scope.row.role.id === 1">
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
      userForm: {
        username: "",
        password: "",
        email: "",
        phone: "",
        age: "",
        gender: "",
        role: {
          rname: "",
        },
      },
      userData: [],
      pageData: {
        pageSize: 10,
        currentPage: 1,
        pageTotal: 200,
        queryPageRequest: {
          username: "",
        },
      },
      roleList: [
        { roleId: 1, roleName: "管理员" },
        { roleId: 2, roleName: "老师/助教" },
        { roleId: 3, roleName: "学生" },
      ],
    };
  },
  methods: {
    deleteUser(userId) {
      this.$axios
        .delete("/users/admin/id/" + userId)
        .then(({ data }) => {
          if (data.success) {
            this.$message.success(data.message);
            this.getUserData();
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
    getUserData() {
      this.$axios
        .get(
          "users/page/" +
            this.pageData.currentPage +
            "/" +
            this.pageData.pageSize +
            "?" +
            this.$qs.stringify(this.pageData.queryPageRequest)
        )
        .then(({ data }) => {
          this.userData = data.queryResult.data;
          this.pageData.pageTotal = data.queryResult.total;
        });
    },
  },
  mounted() {
    this.getUserData();
  },
  watch: {
    pageData: {
      deep: true,
      handler() {
        this.getUserData();
      },
    },
  },
};
</script>