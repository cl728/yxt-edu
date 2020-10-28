<template>
  <div>
    <el-card shadow="never" class="card">
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
        <el-button
          type="danger"
          size="small"
          icon="el-icon-finished"
          :disabled="selectionButtonState"
          @click="showSelection"
          >{{ selectionButtonTitle }}</el-button
        >
        <el-table
          :data="userData"
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
          <el-table-column property="username" label="用户名"></el-table-column>
          <el-table-column property="email" label="用户邮箱"></el-table-column>
          <el-table-column property="phone" label="用户电话"></el-table-column>
          <el-table-column property="age" label="用户年龄"></el-table-column>
          <el-table-column label="用户性别">
            <template slot-scope="scope">
              <el-tag
                size="medium"
                :type="scope.row.gender === '男' ? 'danger' : 'success'"
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
              <el-button
                size="mini"
                type="success"
                @click="editorUser(scope.$index, scope.row)"
              >
                <i class="el-icon-edit-outline" />
              </el-button>
              <el-popconfirm
                confirmButtonText="确认"
                cancelButtonText="取消"
                confirmButtonType="danger"
                cancelButtonType="success"
                @onConfirm="deleteUser(scope.$index, scope.row)"
                title="确认要注销此用户吗？"
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisideState">
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
        <el-form-item label="用户年龄">
          <el-input-number
            v-model="userForm.age"
            :min="1"
            :max="200"
            label="描述文字"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="用户性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户角色">
          <el-radio-group v-model="userForm.role.rname">
            <el-radio label="管理员">管理员</el-radio>
            <el-radio label="老师/助教">老师/助教</el-radio>
            <el-radio label="学生">学生</el-radio>
          </el-radio-group>
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
export default {
  data() {
    return {
      dialogVisideState: false,
      passwordInputState: false,
      selectionButtonTitle: "未选择数据",
      selectionButtonState: true,
      selectionData: [],
      dialogTitle: "",
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
      },
    };
  },
  methods: {
    add(key) {
      switch (key) {
        case "dialog":
          this.userForm = {
            username: "",
            password: "",
            email: "",
            phone: "",
            age: "",
            gender: "",
            role: {
              rname: "",
            },
          };
          this.dialogVisideState = true;
          this.passwordInputState = true;
          this.dialogTitle = "新增用户";
          break;
        case "add":
          this.$message.success("新增成功");
          setTimeout(() => {
            this.dialogVisideState = false;
            this.passwordInputState = false;
          }, 1500);
          break;
      }
    },
    editorUser(index) {
      this.userForm = this.userData[index];
      this.dialogTitle = "修改（" + this.userData[index].username + "）";
      this.dialogVisideState = true;
      this.passwordInputState = false;
    },
    editor() {
      this.$message.success("修改成功");
      setTimeout(() => {
        this.dialogVisideState = false;
      }, 1000);
    },
    selection(selectData) {
      if (selectData.length > 0 && this.userData.length != selectData.length) {
        this.selectionButtonTitle = "已选择" + selectData.length;
        this.selectionButtonState = false;
        this.selectionData = selectData;
      } else if (this.userData.length == selectData.length) {
        this.selectionButtonTitle = "已全选";
        this.selectionButtonState = false;
        this.selectionData = selectData;
      } else {
        this.selectionButtonTitle = "未选择数据";
        this.selectionButtonState = true;
      }
    },
    deleteUser(index) {
      this.$message.warning("删除" + this.userData[index].username + "成功");
      setTimeout(() => {
        this.userData.splice(index, 1);
      }, 1500);
    },
    showSelection() {
      console.log(this.selectionData);
    },
    pageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
      this.getUserData();
    },
    currentChange(clickPage) {
      this.pageData.currentPage = clickPage;
      this.getUserData();
    },
    getUserData() {
      this.$axios
        .get(
          "users/" + this.pageData.currentPage + "/" + this.pageData.pageSize
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
};
</script>