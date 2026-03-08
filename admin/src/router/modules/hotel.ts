const Layout = () => import("@/layout/index.vue");

export default {
  path: "/hotel",
  name: "HotelManagement",
  component: Layout,
  redirect: "/hotel/list",
  meta: {
    icon: "ep/office-building",
    title: "酒店管理",
    rank: 2
  },
  children: [
    {
      path: "/hotel/list",
      name: "HotelList",
      component: () => import("@/views/hotel/list.vue"),
      meta: {
        title: "酒店列表"
      }
    }
  ]
} satisfies RouteConfigsTable;
