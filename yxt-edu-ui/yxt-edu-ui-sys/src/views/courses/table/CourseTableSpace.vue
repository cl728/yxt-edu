<template>
  <div>
    <el-card shadow="never" class="card">
      <div slot="header" class="clearfix">
        <span>课程信息</span>
      </div>
      <div id="charts_one" style="width: 100%; min-height: 300px">
        <el-form
          :inline="true"
          :model="pageData.queryPageRequest"
          class="demo-form-inline"
        >
          <el-form-item>
            <el-select
              v-model="pageData.queryPageRequest.schoolYear"
              placeholder="选择学年"
            >
              <el-option label="不限" value="all"></el-option>
              <el-option
                v-for="(schoolYear, index) in schoolYears"
                :key="index"
                :label="schoolYear"
                :value="schoolYear"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="pageData.queryPageRequest.semester"
              placeholder="选择学期"
            >
              <el-option label="不限" value="all"></el-option>
              <el-option label="第一学期" value="第一学期"></el-option>
              <el-option label="第二学期" value="第二学期"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="pageData.queryPageRequest.courseName"
              placeholder="课程名称"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input-number
              v-model="pageData.queryPageRequest.minStudentCount"
              controls-position="right"
              placeholder="最少加课人数"
              :min="0"
              :max="pageData.queryPageRequest.maxStudentCount - 1"
            ></el-input-number>
            &nbsp;-&nbsp;
            <el-input-number
              v-model="pageData.queryPageRequest.maxStudentCount"
              controls-position="right"
              placeholder="最多加课人数"
              :min="pageData.queryPageRequest.minStudentCount + 1"
            ></el-input-number>
          </el-form-item>
        </el-form>
        <el-table :data="courseData" max-height="528" style="width: 100%">
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
                @confirm="deleteCourse(scope.row.id)"
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
        queryPageRequest: {},
      },
      schoolYears: [
        "2017-2018",
        "2018-2019",
        "2019-2020",
        "2020-2021",
        "2021-2022",
      ],
    };
  },
  methods: {
    deleteCourse(courseId) {
      this.$axios
        .delete("/courses/admin/id/" + courseId)
        .then(({ data }) => {
          if (data.success) {
            this.$message.success(data.message);
            this.getCourseData();
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
            this.pageData.pageSize +
            "?" +
            this.$qs.stringify(this.pageData.queryPageRequest)
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
  watch: {
    pageData: {
      deep: true,
      handler() {
        this.getCourseData();
      },
    },
  },
};
</script>