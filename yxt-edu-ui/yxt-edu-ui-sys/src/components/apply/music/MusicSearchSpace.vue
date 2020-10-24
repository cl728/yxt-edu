<template>
  <div>
    <el-input
      v-model="music_search_value"
      placeholder="请输入搜索信息"
      @change="search_music"
      size="small"
      style="width: 30%"
    ></el-input>
    <el-button
      type="danger"
      size="small"
      @click="search_music()"
      :icon="button_title == '搜索' ? 'el-icon-search' : 'el-icon-loading'"
      :disabled="button_title != '搜索'"
      >{{ button_title }}</el-button
    >
    <el-table :data="music_data.abslist" style="width: 100%">
      <el-table-column label="歌曲名称">
        <template slot-scope="scope">
          <div style="float: left">
            <i class="el-icon-headset" style="color: #409eff" />
            <span v-html="scope.row.NAME" style="margin-left: 20px"></span>
          </div>
          <div style="float: right; cursor: pointer">
            <i
              class="el-icon-video-play"
              style="color: #f56c6c"
              @click="play_music(scope.$index)"
            />
            &emsp;
            <i
              class="el-icon-plus"
              style="color: #409eff"
              @click="add_list(scope.$index)"
            />
            &emsp;
            <i class="el-icon-download" @click="download_music(scope.$index)" />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="歌手" width="150">
        <template slot-scope="scope">
          <el-avatar :src="scope.row.hts_MVPIC" size="small" v-show="scope.row.hts_MVPIC!=''"></el-avatar>
          <span v-html="scope.row.ARTIST"></span>
        </template>
      </el-table-column>
      <el-table-column label="专辑" width="200">
        <template slot-scope="scope">
          <div v-show="scope.row.ALBUM != ''">
            《
            <span v-html="scope.row.ALBUM"></span>
            》
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="PLAYCNT" label="播放次数" width="80">
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="page_size_change"
      @current-change="current_change"
      :current-page="page_data.current_page"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="page_data.page_size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="page_data.page_total"
      style="margin-top: 10px"
    ></el-pagination>
  </div>
</template>
<script>
export default {
  data() {
    return {
      button_title: "搜索",
      music_search_value: "",
      page_data: {
        page_size: 10,
        current_page: 1,
        page_total: 0,
      },
      music_data: [],
      msic_play_data: {
        title: " ",
        artist: " ",
        url: "",
        pic: "",
        lrc: "[00:00.00]EuiAdmin音乐播放系统\n[00:01.00]暂无歌词",
      },
      music_list: [],
    };
  },
  methods: {
    page_size_change(page_size) {
      this.page_data.page_size = page_size;
      this.search_music();
      this.$message.success("每页显示" + page_size + "条数据");
    },
    current_change(click_page) {
      this.page_data.current_page = click_page;
      this.$message.success("正在展示第" + click_page + "页数据");
      this.search_music();
    },
    search_music() {
      this.button_title = "搜索中";
      this.$axios({
        method: "post",
        url: "/music/search",
        data: this.$qs.stringify({
          search_value: this.music_search_value,
          page_size: this.page_data.page_size,
          current_page: this.page_data.current_page,
        }),
      }).then((response) => {
        var json = eval("(" + response.data + ")");
        this.music_data = json;
        this.page_data.page_total = parseInt(json.TOTAL);
        // console.log(this.music_data);
        // console.log(json);
        this.button_title = "搜索";
      });
    },
    play_music(index) {
      this.$axios({
        method: "post",
        url: "/music/play",
        data: this.$qs.stringify({
          music_id: this.music_data.abslist[index].MUSICRID,
        }),
      }).then((response) => {
        let play_data = {
          title: this.music_data.abslist[index].NAME,
          artist: this.music_data.abslist[index].ARTIST,
          url: response.data,
          pic: this.music_data.abslist[index].hts_MVPIC,
          lrc: "[00:00.00]lrc here\n[00:01.00]aplayer",
        };
        this.$emit("play_data", play_data);
      });
    },
    add_list(index) {
      this.$axios({
        method: "post",
        url: "/music/play",
        data: this.$qs.stringify({
          music_id: this.music_data.abslist[index].MUSICRID,
        }),
      }).then((response) => {
        let play_data = {
          title: this.music_data.abslist[index].NAME,
          artist: this.music_data.abslist[index].ARTIST,
          url: response.data,
          pic: this.music_data.abslist[index].hts_MVPIC,
          lrc: "[00:00.00]lrc here\n[00:01.00]aplayer",
        };
        this.music_list.splice(this.music_list.length, 0, play_data);
        this.$emit("play_list", this.music_list);
      });
    },
    download_music(index) {
      this.$axios({
        method: "post",
        url: "/music/play",
        data: this.$qs.stringify({
          music_id: this.music_data.abslist[index].MUSICRID,
        }),
      }).then((response) => {
        this.$message.success("即将为您打开下载链接，如不执行下载请用迅雷下载");
        setTimeout(() => {
          window.open(response.data);
        }, 1500);
      });
    },
  },
};
</script>