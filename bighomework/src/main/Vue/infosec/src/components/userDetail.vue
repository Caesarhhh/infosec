<template>
  <div>
    <a-tag color="blue" style="position:absolute;left: 25%;cursor: pointer" @click="toPage('/')">首页</a-tag>
    <br>
    <br>
    <h1>用户信息</h1>
    <br>
    <a-table :loading="loading" style="width: 50%;margin:0 auto;" :columns="columns" :data-source="tableDatas" bordered>
      <a slot="name" slot-scope="loginName">{{ loginName }}</a>
      <span slot="customTitle"><a-icon type="user-o"/> loginName</span>
      <span slot="role" slot-scope="Role">
        <a-tag :color="Role=='ROLE_ADMIN'?'orange':'blue'">{{ Role }}</a-tag>
      </span>
    </a-table>
  </div>
</template>

<script>
  import Vue from "vue";

  export default {
    name: "",
    data() {
      return {
        user: {},
        axiosBase: "https://www.caelog.top:8023",
        columns: [
        {
          key: "loginName",
          dataIndex: 'loginName',
          slots: {title: 'customTitle'},
          scopedSlots: {customRender: 'name'}
        },
        {
          title: "showName",
          dataIndex: 'showName',
          key: 'showName',
        },
        {
          title: "Role",
          dataIndex: 'Role',
          key: 'Role',
          scopedSlots: {customRender: 'role'}
        }
      ],
        tableDatas: [{
          key: "1",
          Role: "test",
          showName: "test",
          loginName: "test"
        }],
        loading:false
      }
    },
    methods: {
      toPage(path) {
        this.$router.push(path)
      }
    },
    mounted() {
      let user = Vue.$cookies.get("infosecAuth")
      this.tableDatas=[]
      this.loading=true
      this.$axios.get(this.axiosBase + "/getUserByName?name=" + user.name).then(res => {
        this.loading=false
        if (res.data.status == 400) {
          if (res.data.data == "认证错误") {
            this.$cookies.remove("infosecAuth")
            this.$notification['error']({
              message: "登录信息已失效，请重新登录",
              description: res.data.data,
              duration: 4.5,
              placement: 'topRight'
            })
            this.$router.push("/login")
          } else if (res.data.data == "没有相关权限") {
            this.$notification['error']({
              message: "没有相关权限！",
              description: res.data.data,
              duration: 4.5,
              placement: 'topRight'
            })
            this.$router.push("/")
          }
        } else if (res.data.status == 200) {
          this.user = res.data.data
          let temp = {
            key:"1",
            loginName: this.user.username,
            showName: this.user.showname,
            Role: this.user.rolecode
          }
          this.tableDatas.push(temp)
        }
      })
    }
  }
</script>

<style scoped>

</style>
