<template>
  <div>
    <a-tag color="blue" style="position:absolute;left: 25%;cursor: pointer" @click="toPage('/')">首页</a-tag>
    <br><br>
    <h1>用户列表</h1>
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
  export default {
    name: "",
    data() {
      return {
        users: [],
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
          Role: "ROLE_ADMIN",
          showName: "test",
          loginName: "test"
        }],
        loading: false
      }
    },
    methods: {
      toPage(path) {
        this.$router.push(path)
      }
    },
    mounted() {
      this.loading=true
      this.tableDatas=[]
      this.$axios.get(this.axiosBase + "/users").then(res => {
        this.loading=false
        if (res.data.status == 400) {
          if(res.data.data=="认证错误"){
            this.$cookies.remove("infosecAuth")
            this.$notification['error']({
              message: "登录信息已失效，请重新登录",
              description: res.data.data,
              duration: 4.5,
              placement: 'topRight'
            })
            this.$router.push("/login")
          }
          else if(res.data.data=="没有相关权限"){
            this.$notification['error']({
              message: "没有相关权限！",
              description: res.data.data,
              duration: 4.5,
              placement: 'topRight'
            })
            this.$router.push("/")
          }
        } else if (res.data.status == 200) {
          this.users = res.data.data
          for (let i = 0; i < this.users.length; i++) {
            let temp = {
              key: i,
              loginName: this.users[i].username,
              showName: this.users[i].showname,
              Role: this.users[i].rolecode
            }
            this.tableDatas.push(temp)
          }
        }
      })
    }
  }
</script>

<style scoped>
  .table {
    position: relative;
    margin: 0 auto;
  }
</style>
