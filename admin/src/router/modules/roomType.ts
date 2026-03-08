const Layout = () => import("@/layout/index.vue");

export default {
  path: "/room-type",
  name: "RoomTypeManagement",
  component: Layout,
  redirect: "/room-type/list",
  meta: {
    icon: "ep/house",
    title: "房型管理",
    rank: 3
  },
  children: [
    {
      path: "/room-type/list",
      name: "RoomTypeList",
      component: () => import("@/views/room-type/list.vue"),
      meta: {
        title: "房型列表"
      }
    }
  ]
} satisfies RouteConfigsTable;
