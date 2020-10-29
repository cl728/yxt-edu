<template>
  <div>
    <el-card shadow="never" class="card">
      <div slot="header" class="clearfix">
        <span>角色信息</span>
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
          :data="roleData"
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
          <el-table-column property="rname" label="角色名称"></el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="success"
                @click="editorRole(scope.$index, scope.row)"
              >
                <i class="el-icon-edit-outline" />
              </el-button>
              <el-popconfirm
                confirmButtonText="确认"
                cancelButtonText="取消"
                confirmButtonType="danger"
                cancelButtonType="success"
                @onConfirm="deleteRole(scope.$index, scope.row)"
                title="确认要删除此角色吗？"
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
      <el-form ref="form" :model="RoleForm" label-width="80px">
        <el-form-item label="角色名">
          <el-input
            v-model="RoleForm.rname"
            placeholder="请输入角色名"
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
export default {
  data() {
    return {
      dialogVisideState: false,
      passwordInputState: false,
      selectionButtonTitle: "未选择数据",
      selectionButtonState: true,
      selectionData: [],
      dialogTitle: "",
      RoleForm: {
        rname: "",
        password: "",
        email: "",
        phone: "",
        age: "",
        gender: "",
        role: {
          rname: "",
        },
      },
      roleData: [],
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
          this.dialogVisideState = true;
          this.passwordInputState = true;
          this.dialogTitle = "新增角色";
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
    editorRole(index) {
      this.RoleForm = this.roleData[index];
      this.dialogTitle = "修改（" + this.roleData[index].rname + "）";
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
      if (selectData.length > 0 && this.roleData.length != selectData.length) {
        this.selectionButtonTitle = "已选择" + selectData.length;
        this.selectionButtonState = false;
        this.selectionData = selectData;
      } else if (this.roleData.length == selectData.length) {
        this.selectionButtonTitle = "已全选";
        this.selectionButtonState = false;
        this.selectionData = selectData;
      } else {
        this.selectionButtonTitle = "未选择数据";
        this.selectionButtonState = true;
      }
    },
    deleteRole(index) {
      this.$message.warning("删除" + this.roleData[index].rname + "成功");
      setTimeout(() => {
        this.roleData.splice(index, 1);
      }, 1500);
    },
    showSelection() {
      this.$message.success("已打印选择数据成功，请打开检查查看");
      console.log(this.selectionData);
    },
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