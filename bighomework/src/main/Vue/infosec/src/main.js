// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import routes from './router'
import axios from "axios";
import VueRouter from 'vue-router'
import VueCookies from 'vue-cookies'
import 'ant-design-vue/dist/antd.css';
import {Button,Tag,Input,Form,Icon,Table,notification} from 'ant-design-vue';
axios.defaults.withCredentials = true
Vue.prototype.$notification=notification
Vue.config.productionTip = false
Vue.prototype.$axios = axios;
Vue.use(Icon)
Vue.use(Form)
Vue.use(Table)
Vue.use(Input)
Vue.use(Button)
Vue.use(Tag)
Vue.use(VueRouter)
Vue.use(VueCookies)
/* eslint-disable no-new */
Vue.$cookies.config('7d')
const router = new VueRouter({
  mode: 'history',
  base:'/infosec/',
  routes: routes
})
var filterPath=['/login','/']
router.beforeEach((to, from, next) => {
  if (filterPath.indexOf(to.path)!=-1) {
    next();
  } else {
    let token = Vue.$cookies.get('infosecAuth');
    if (token == null || token === '') {
      next('/login');
    } else {
      next();
    }
  }
});
new Vue({
  el: '#app',
  router,
  components: {App},
  template: '<App/>'
})
