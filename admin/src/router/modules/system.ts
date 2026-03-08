const Layout = () => import("@/layout/index.vue");

export default {
  path: "/system",
  name: "SystemManagement",
  component: Layout,
  redirect: "/system/user",
  meta: {
    icon: "ep/setting",
    title: "系统管理",
    rank: 8
  },
  children: [
    {
      path: "/system/user",
      name: "UserManagement",
      component: () => import("@/views/system/user.vue"),
      meta: {
        title: "用户管理",
        icon: "ep/user"
      }
    },
    {
      path: "/system/audit-log",
      name: "AuditLog",
      component: () => import("@/views/system/audit-log.vue"),
      meta: {
        title: "审计日志",
        icon: "ep/document-copy"
      }
    }
  ]
} satisfies RouteConfigsTable;
