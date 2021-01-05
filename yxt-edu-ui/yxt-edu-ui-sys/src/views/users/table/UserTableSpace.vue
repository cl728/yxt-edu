<template>
  <div>
    <el-card shadow="never" class="card" body-style="{height: '400px'}">
      <div slot="header" class="clearfix">
        <span>用户信息</span>
        <el-button
          type="success"
          size="small"
          icon="el-icon-plus"
          style="float: right"
          @click="add('dialog')"
          >新增</el-button
        >
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
            align="center"
          ></el-table-column>
          <el-table-column property="username" label="用户名" align="center"></el-table-column>
          <el-table-column property="email" label="用户邮箱" align="center"></el-table-column>
          <el-table-column property="phone" label="用户电话" align="center"></el-table-column>
          <el-table-column property="age" label="用户年龄" align="center"></el-table-column>
          <el-table-column label="用户性别" align="center">
            <template slot-scope="scope">
              <el-tag
                size="medium"
                :type="scope.row.gender === '女' ? 'danger' : 'success'"
                >{{ scope.row.gender }}</el-tag
              >
            </template>
          </el-table-column>
          <el-table-column label="用户角色" align="center">
            <template slot-scope="scope">
              <el-tag
                size="medium"
                :type="scope.row.role.rname == '管理员' ? 'danger' : 'success'"
                effect="dark"
                >{{ scope.row.role.rname }}</el-tag
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
                @confirm="deleteUser(scope.row.id)"
                title="确认要注销此用户吗？"
              >
                <el-button
                  size="mini"
                  slot="reference"
                  type="danger"
                  :disabled="scope.row.role.id === 1 || scope.row.role.id === 4 && user.role.id !== 1"
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
    <el-dialog title="新增用户" :visible.sync="dialogVisideState">
      <el-form ref="form" :model="userForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户密码" v-show="passwordInputState">
          <el-input
            v-model="userForm.password"
            show-password
            placeholder="请输入用户密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户邮箱">
          <el-input
            v-model="userForm.email"
            placeholder="请输入用户邮箱"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户电话">
          <el-input
            v-model="userForm.phone"
            placeholder="请输入用户电话"
          ></el-input>
        </el-form-item>
        <el-form-item label="用户性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户类别">
          <el-radio-group v-model="userForm.roleId">
            <el-radio
              v-for="role in availableRoleList"
              :value="role.roleId"
              :label="role.roleId"
              :key="role.roleId"
              :disabled="user.role.id !== 1 && role.roleName === '管理员'"
            >
              {{ role.roleName }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户学校" v-show="userForm.roleId === 2 || userForm.roleId === 3">
          <el-autocomplete
            v-model="userForm.school"
            :fetch-suggestions="querySearch"
            placeholder="请选择学校"
          >
          </el-autocomplete>
        </el-form-item>
        <el-form-item label="学工号" v-show="userForm.roleId === 2 || userForm.roleId === 3">
          <el-input
            v-model="userForm.teSno"
            placeholder="请输入用户学工号"
          ></el-input>
        </el-form-item>
        <div align="center">
          <el-button
            type="danger"
            size="small"
            @click="editor"
            v-show="!passwordInputState"
            >确认修改</el-button
          >
          <el-button
            type="success"
            size="small"
            @click="add('add')"
            v-show="passwordInputState"
            >确认新增</el-button
          >
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { hexMd5 } from "../../login/api/md5";
export default {
  data() {
    return {
      user: {
        role: {},
      },
      userForm: {},
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
        { roleId: 1, roleName: "超级管理员" },
        { roleId: 4, roleName: "管理员" },
        { roleId: 2, roleName: "老师/助教" },
        { roleId: 3, roleName: "学生" },
      ],
      availableRoleList: [
        { roleId: 4, roleName: "管理员" },
        { roleId: 2, roleName: "老师/助教" },
        { roleId: 3, roleName: "学生" },
      ],
      dialogVisideState: false,
      dialogTitle: "",
      passwordInputState: false,
      schools: [],
    };
  },
  methods: {
    add(key) {
      switch (key) {
        case "dialog":
          this.dialogVisideState = true;
          this.passwordInputState = true;
          break;
        case "add":
          this.userForm.password = hexMd5(this.userForm.password)
          this.$axios
            .post("users/admin", this.userForm)
            .then(({ data }) => {
              if (data.success) {
                this.$message.success(data.message);
              } else {
                this.$message.error(data.message);
              }
              this.dialogVisideState = false;
              this.passwordInputState = false;
            })
            .catch(() => {
              this.$message.error("服务器繁忙，请稍候再试！");
            });
          break;
      }
    },
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
    querySearch(queryString, cb) {
      let schools = this.schools;
      let results = queryString
        ? schools.filter(this.createFilter(queryString))
        : schools;
      // 调用 callback 返回学校列表的数据
      cb(results);
    },
    createFilter(queryString) {
      return (school) => {
        return (
          school.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0
        );
      };
    },
    loadSchools() {
      // 调用后台接口获取全国高校列表
      this.$axios.get("users/schools").then(({ data }) => {
        this.schools = data.queryResult.data;
      });
    },
    getAdminData() {
      this.$axios.get("auth/verify/1").then(({ data }) => {
        if (data.success) {
          let user = data.queryResult.data[0];
          this.$axios.get("users/info/id/" + user.id).then(({ data }) => {
            if (data.success) {
              this.user = data.queryResult.data[0];
            }
          });
        }
      });
    },
  },
  mounted() {
    this.getUserData();
    // 加载学校列表
    this.loadSchools();
    this.getAdminData();
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