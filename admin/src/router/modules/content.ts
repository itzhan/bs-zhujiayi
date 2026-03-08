const Layout = () => import("@/layout/index.vue");

export default {
  path: "/content",
  name: "ContentManagement",
  component: Layout,
  redirect: "/content/announcement",
  meta: {
    icon: "ep/bell",
    title: "内容管理",
    rank: 7
  },
  children: [
    {
      path: "/content/announcement",
      name: "AnnouncementList",
      component: () => import("@/views/announcement/list.vue"),
      meta: {
        title: "公告管理"
      }
    }
  ]
} satisfies RouteConfigsTable;
