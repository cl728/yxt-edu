<template>
  <div>
    <el-card shadow="never" class="card">
      <!-- 搜索框 -->
      <div slot="header" class="clearfix">
       <span>{{play_title}}</span>
      </div>
      <div>
        <MusicSearchSpace @play_data="play" @play_list="play_list"/>
      </div>
      <div id="aplayer">
        <!-- 播放器 -->
        <aplayer
          autoplay
          :music="msic_data"
          :list="music_list"
          :showlrc="true"
          v-if="paly_state"
        >
        </aplayer>
      </div>
    </el-card>
  </div>
</template>
<script>
import aplayer from "vue-aplayer";
export default {
  components: {
    aplayer,
    MusicSearchSpace:resolve=>{require(['@/components/apply/music/MusicSearchSpace'],resolve)},
  },
  data() {
    return {
      play_title:'音乐播放器',
      paly_state: true,
      msic_data: {
        title: " ",
        artist: " ",
        url: "",
        pic: "",
        lrc: "[00:00.00]EuiAdmin音乐播放系统\n[00:01.00]暂无歌词",
      },
      music_list: [],
      page_data: {
        page_size: 10,
        current_page: 1,
        page_total: 0,
      },
    };
  },
  methods: {
    play(play_data){
      this.paly_state=false
      this.msic_data=play_data
      setTimeout(()=>{
        this.paly_state=true
      },100)
    },
    play_list(play_list){
      this.music_list=play_list
      console.log(play_list)
    }
  },
};
</script>
<style scoped>
#aplayer{
  position:absolute;
  right:25px;
  top:70px;
  width: 500px;
}
#music_search_list {
  cursor: pointer;
  height: 40vh;
  overflow: auto;
}
/*滚动条的宽度*/
::-webkit-scrollbar {
  width: 9px;
  height: 9px;
}

/*外层轨道。可以用display:none让其不显示，也可以添加背景图片，颜色改变显示效果*/
::-webkit-scrollbar-track {
  width: 6px;
  background-color: #f2f6fc;
  -webkit-border-radius: 2em;
  -moz-border-radius: 2em;
  border-radius: 2em;
}

/*滚动条的设置*/

::-webkit-scrollbar-thumb {
  background-color: #409eff;
  background-clip: padding-box;
  min-height: 1px;
  -webkit-border-radius: 2em;
  -moz-border-radius: 2em;
  border-radius: 2em;
}
/*滚动条移上去的背景*/

::-webkit-scrollbar-thumb:hover {
  background-color: #fff;
}
</style>