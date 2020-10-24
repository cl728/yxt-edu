// 引入自写全局组件(后台)
// import AsideSpace from "@/components/system/AsideSpace";//富文本编辑器组件
// import HeadSpace from "@/components/system/HeadSpace";//富文本编辑器组件
// import EditorSpace from "@/components/module/EditorSpace";//富文本编辑器组件
// 注册全局组件
// export default {
//     AsideSpace,
//     HeadSpace,
//     EditorSpace,
// }
export default {
    AsideSpace:resolve=>{require(['@/components/system/AsideSpace'],resolve)},
    HeadSpace:resolve=>{require(['@/components/system/HeadSpace'],resolve)},
    EditorSpace:resolve=>{require(['@/components/module/EditorSpace'],resolve)},
}