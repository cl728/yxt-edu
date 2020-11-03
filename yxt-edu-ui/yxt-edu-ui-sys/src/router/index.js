import Vue from 'vue'
import VueRouter from 'vue-router'
import axios from 'axios'
import { Message } from 'element-ui'


Vue.use(VueRouter)

const routes = [{
  path: '/login',
  name: 'login',
  component: resolve => require(['@/views/login/Login.vue'], resolve),
  meta: {
    title: '登录',
    loginState: false,
  },
},
{
  path: '/register',
  name: 'register',
  component: resolve => require(['@/views/login/Register.vue'], resolve),
  meta: {
    title: '注册',
    loginState: false,
  },
},
{
  path: '/forget/password',
  name: 'forget_password',
  component: resolve => require(['@/views/login/ForgetPassword.vue'], resolve),
  meta: {
    title: '找回密码',
    loginState: false,
  },
},
{
  path: '/home',
  name: 'home',
  component: resolve => require(['@/views/home/Home.vue'], resolve),
  meta: {
    title: '主页',
    loginState: true,
  },
},
{
  path: '/',
  name: 'home',
  component: resolve => require(['@/views/home/Home.vue'], resolve),
  meta: {
    title: '主页',
    loginState: true,
  },
},
{
  path: '/users',
  name: 'users',
  component: resolve => require(['@/views/users/Users.vue'], resolve),
  meta: {
    title: '用户管理',
    loginState: true,
  },
},
{
  path: '/users/roles',
  name: 'roles',
  component: resolve => require(['@/views/users/Roles.vue'], resolve),
  meta: {
    title: '角色管理',
    loginState: true,
  },
},
{
  path: '/users/schools',
  name: 'schools',
  component: resolve => require(['@/views/users/Schools.vue'], resolve),
  meta: {
    title: '学校管理',
    loginState: true,
  },
},
{
  path: '/courses',
  name: 'courses',
  component: resolve => require(['@/views/courses/Courses.vue'], resolve),
  meta: {
    title: '课程管理',
    loginState: true,
  },
},
{
  path: '/module/editor',
  name: 'editor',
  component: resolve => require(['@/views/module/Editor.vue'], resolve),
  meta: {
    title: '富文本编辑器',
    loginState: true,
  },
},
{
  path: '/module/superform',
  name: 'superform',
  component: resolve => require(['@/views/module/SuperForm.vue'], resolve),
  meta: {
    title: '超级表单',
    loginState: true,
  },
},
{
  path: '/module/upload/image',
  name: 'image',
  component: resolve => require(['@/views/module/UploadImage.vue'], resolve),
  meta: {
    title: '图片上传',
    loginState: true,
  },
},
{
  path: '/module/upload/file',
  name: 'file',
  component: resolve => require(['@/views/module/UploadFile.vue'], resolve),
  meta: {
    title: '文件上传',
    loginState: true,
  },
},
{
  path: '/module/animate',
  name: 'animate',
  component: resolve => require(['@/views/module/Animate.vue'], resolve),
  meta: {
    title: 'Eui动画',
    loginState: true,
  },
},
{
  path: '/module/table',
  name: 'table',
  component: resolve => require(['@/views/module/Table.vue'], resolve),
  meta: {
    title: '虚拟用户表格',
    loginState: true,
  },
},
{
  path: '/module/table/excel',
  name: 'TableExcel',
  component: resolve => require(['@/views/module/TableExcel.vue'], resolve),
  meta: {
    title: '支持Excel表格',
    loginState: true,
  },
},
// Echarts表格
{
  path: '/echarts/bar',
  name: 'echarts_bar',
  component: resolve => require(['@/views/echarts/EchartsBar.vue'], resolve),
  meta: {
    title: '柱状图',
    loginState: true,
  },
},
{
  path: '/echarts/line',
  name: 'echarts_line',
  component: resolve => require(['@/views/echarts/EchartsLine.vue'], resolve),
  meta: {
    title: '折线图',
    loginState: true,
  },
},
{
  path: '/echarts/map',
  name: 'echarts_map',
  component: resolve => require(['@/views/echarts/EchartsMap.vue'], resolve),
  meta: {
    title: '地图',
    loginState: true,
  },
},
// 常用页面
{
  path: '/page/message/list',
  name: 'message_list',
  component: resolve => require(['@/views/page/MessageList.vue'], resolve),
  meta: {
    title: '留言',
    loginState: true,
  },
},
{
  path: '/page/not/found',
  name: 'not_found',
  component: resolve => require(['@/views/page/NotFound.vue'], resolve),
  meta: {
    title: '页面不存在',
    loginState: false,
  },
},
{
  path: '/page/warning',
  name: 'warning',
  component: resolve => require(['@/views/page/Warning.vue'], resolve),
  meta: {
    title: '系统错误',
    loginState: false,
  },
},
// 应用
{
  path: '/apply/article',
  name: 'apply_article',
  component: resolve => require(['@/views/apply/article/Article.vue'], resolve),
  meta: {
    title: '文章撰写系统-文章',
    loginState: false,
  },
},
{
  path: '/apply/article/label',
  name: 'apply_article_label',
  component: resolve => require(['@/views/apply/article/ArticleLabel.vue'], resolve),
  meta: {
    title: '文章撰写系统-类别',
    loginState: false,
  },
},
{
  path: '/apply/article/reply',
  name: 'article_reply',
  component: resolve => require(['@/views/apply/article/ArticleReply.vue'], resolve),
  meta: {
    title: '文章评论回复',
    loginState: false,
  },
},
// 应用-商品列表
{
  path: '/apply/goods',
  name: 'article_reply',
  component: resolve => require(['@/views/apply/goods/Goods.vue'], resolve),
  meta: {
    title: '文章评论回复',
    loginState: false,
  },
},
{
  path: '/apply/goods/release',
  name: 'article_reply',
  component: resolve => require(['@/views/apply/goods/ReleaseGoods.vue'], resolve),
  meta: {
    title: '文章评论回复',
    loginState: false,
  },
},
// API管理
{
  path: '/apply/api',
  name: 'article_reply',
  component: resolve => require(['@/views/apply/api/Api.vue'], resolve),
  meta: {
    title: 'Api列表',
    loginState: false,
  },
},
{
  path: '/apply/api/editor',
  name: 'article_reply',
  component: resolve => require(['@/views/apply/api/EidtorApi.vue'], resolve),
  meta: {
    title: '新增修改Api',
    loginState: false,
  },
},
// 邮箱
{
  path: '/apply/email',
  name: 'email',
  component: resolve => require(['@/views/apply/email/Email.vue'], resolve),
  meta: {
    title: '邮件管理',
    loginState: false,
  },
},
// 音乐
{
  path: '/apply/music',
  name: 'article_music',
  component: resolve => require(['@/views/apply/music/Music.vue'], resolve),
  meta: {
    title: '音乐播放器',
    loginState: false,
  },
},
// 设置
{
  path: '/setting/web',
  name: 'setting_seb',
  component: resolve => require(['@/views/setting/WebSetting.vue'], resolve),
  meta: {
    title: '系统设置',
    loginState: true,
  },
},
{
  path: '/setting/email',
  name: 'setting_email',
  component: resolve => require(['@/views/setting/EmailSetting.vue'], resolve),
  meta: {
    title: '邮箱设置',
    loginState: true,
  },
},
{
  path: '/setting/user',
  name: 'setting_user',
  component: resolve => require(['@/views/setting/UserSetting.vue'], resolve),
  meta: {
    title: '个人设置',
    loginState: true,
  },
},
{
  path: '/setting/password',
  name: 'setting_password',
  component: resolve => require(['@/views/setting/PasswordSetting.vue'], resolve),
  meta: {
    title: '密码修改',
    loginState: true,
  },
},
{
  path: '/download',
  name: 'setting_password',
  component: resolve => require(['@/views/Download.vue'], resolve),
  meta: {
    title: '获取Eui',
    loginState: true,
  },
},
{
  path: '/api',
  name: 'api',
  component: resolve => require(['@/views/api/Api.vue'], resolve),
  meta: {
    title: 'Api接口',
    loginState: true,
  },
},
]

const router = new VueRouter({
  routes
})

router.beforeEach((to, from, next) => {
  axios.get("/auth/verify/1").then(({ data }) => {
    let isAuthorized = data.success
    let needLogin = to.meta.loginState // 是否需要登录
    if (!needLogin) {
      next()
      document.title = to.meta.title
    } else if (needLogin && isAuthorized) {
      next()
      document.title = to.meta.title
    } else {
      Message.error("您尚未登录或未具备权限！")
      if (to.path === '/login') { // 多一个判断避免 next() 死循环
        next()
      } else {
        next({
          path: '/login'
        })
      }
    }
    console.log(to.meta.isAuthorized)
  })

})

export default router