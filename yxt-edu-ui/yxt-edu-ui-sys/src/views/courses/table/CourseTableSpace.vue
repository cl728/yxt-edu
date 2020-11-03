<template>
  <div>
    <el-card shadow="never" class="card">
      <div slot="header" class="clearfix">
        <span>课程信息</span>
      </div>
      <div id="charts_one" style="width: 100%; min-height: 300px">
        <el-table
          :data="courseData"
          max-height="528"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column
            type="index"
            label="序号"
            width="50"
          ></el-table-column>
          <el-table-column
            property="teacher.realName"
            label="创建者"
          ></el-table-column>
          <el-table-column property="cname" label="课程名称"></el-table-column>
          <el-table-column property="clazz" label="课程班级"></el-table-column>
          <el-table-column
            property="schoolYear"
            label="课程学年"
          ></el-table-column>
          <el-table-column
            property="semester"
            label="课程学期"
          ></el-table-column>
          <el-table-column property="ccode" label="加课码"></el-table-column>
          <el-table-column property="scount" label="加课人数"></el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-popconfirm
                confirmButtonText="确认删除"
                cancelButtonText="取消"
                confirmButtonType="danger"
                cancelButtonType="success"
                @onConfirm="deleteCourse(scope.$index, scope.row)"
                title="确认要删除该课程吗？"
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
      dialogTitle: "",
      courseForm: {
        cname: "",
        clazz: "",
        schoolYear: "",
        semester: "",
        ccode: "",
        scount: "",
        teacher: {
          realName: "",
        },
      },
      courseData: [],
      pageData: {
        pageSize: 10,
        currentPage: 1,
        pageTotal: 200,
      },
    };
  },
  methods: {
    deleteCourse() {},
    pageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
      this.getCourseData();
    },
    currentChange(clickPage) {
      this.pageData.currentPage = clickPage;
      this.getCourseData();
    },
    getCourseData() {
      this.$axios
        .get(
          "/courses/page/" +
            this.pageData.currentPage +
            "/" +
            this.pageData.pageSize
        )
        .then(({ data }) => {
          this.courseData = data.queryResult.data;
          this.pageData.pageTotal = data.queryResult.total;
        });
    },
  },
  mounted() {
    this.getCourseData();
  },
};
</script>