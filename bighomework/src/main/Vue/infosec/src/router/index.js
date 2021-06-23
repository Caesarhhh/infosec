import login from "../components/login";
import index from "../components/index";
import userDetail from "../components/userDetail";
import userList from "../components/userList";
const routes=[
  {
    path:'/login',component:login
  },
  {
    path:'/',component: index
  },
  {
    path: '/userDetail',component: userDetail
  },
  {
    path: '/userList',component: userList
  }
]

export default routes
