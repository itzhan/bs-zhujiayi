const Layout = () => import("@/layout/index.vue");

export default {
  path: "/financial",
  name: "FinancialManagement",
  component: Layout,
  redirect: "/financial/payment",
  meta: {
    icon: "ep/wallet",
    title: "财务管理",
    rank: 5
  },
  children: [
    {
      path: "/financial/payment",
      name: "PaymentList",
      component: () => import("@/views/payment/list.vue"),
      meta: {
        title: "支付记录"
      }
    },
    {
      path: "/financial/coupon",
      name: "CouponList",
      component: () => import("@/views/coupon/list.vue"),
      meta: {
        title: "优惠券管理"
      }
    }
  ]
} satisfies RouteConfigsTable;
