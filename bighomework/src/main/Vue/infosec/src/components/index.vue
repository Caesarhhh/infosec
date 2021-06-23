<template>
  <div>
    <!--  <h1>信息系统安全-课程设计</h1>-->
    <a-tag color="green" v-if="ifLogin" style="text-align: left;position: absolute; left: 20%;">目前已登陆账户为：{{user.name}}</a-tag>
    <a-tag color="red" v-if="!ifLogin" style="text-align: left;position: absolute; left: 20%;">账号未登陆</a-tag>
    <br>
    <br>
    <a-button class="button" @click="toPage('/login')">登陆</a-button>
    <a-button class="button" @click="toPage('/userList')">用户列表</a-button>
    <a-button class="button" @click="toPage('/userDetail')">用户信息</a-button>
    <a-button v-if="ifLogin" class="button" @click="logout()">退出</a-button>
  </div>
</template>

<script>
  //https://segmentfault.com/a/1190000038330859
  import Vue from "vue";

  export default {
    name: "",
    data(){
      return{
        ifLogin:false,
        user:"",
        axiosBase:"https://www.caelog.top:8023"
      }
    },
    mounted() {
      let token = Vue.$cookies.get('infosecAuth');
      if (token == null || token === '') {
        this.ifLogin=false
      } else {
        this.ifLogin=true
        this.user=token
      }
    },
    methods: {
      toPage(path) {
        this.$router.push(path)
      },
      logout(){
        this.$axios({
          method: "get",
          url: this.axiosBase+"/logoutuser"
        }).then(res=>{
          if(res.data.status==200){
            this.$cookies.remove("infosecAuth")
            this.ifLogin=false
            this.$notification['success']({
              message: "账号已注销！",
              description: res.data.data,
              duration: 4.5,
              placement: 'topRight'
            })
          }
        })
      }
    }
  }
</script>

<style scoped>
  .button {
    display: block;
    margin: 0 auto;
    margin-top: 10px;
    width:30%;
  }
</style>
