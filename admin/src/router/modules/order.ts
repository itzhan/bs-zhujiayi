const Layout = () => import("@/layout/index.vue");

export default {
  path: "/order",
  name: "OrderManagement",
  component: Layout,
  redirect: "/order/list",
  meta: {
    icon: "ep/document",
    title: "订单管理",
    rank: 4
  },
  children: [
    {
      path: "/order/list",
      name: "OrderList",
      component: () => import("@/views/order/list.vue"),
      meta: {
        title: "订单列表"
      }
    }
  ]
} satisfies RouteConfigsTable;
