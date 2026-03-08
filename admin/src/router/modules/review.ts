const Layout = () => import("@/layout/index.vue");

export default {
  path: "/review",
  name: "ReviewManagement",
  component: Layout,
  redirect: "/review/list",
  meta: {
    icon: "ep/chat-dot-round",
    title: "评价管理",
    rank: 6
  },
  children: [
    {
      path: "/review/list",
      name: "ReviewList",
      component: () => import("@/views/review/list.vue"),
      meta: {
        title: "评价列表"
      }
    }
  ]
} satisfies RouteConfigsTable;
