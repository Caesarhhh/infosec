<template>
  <div>
    <h1>Infosec login</h1>
    <a-form
      id="components-form-demo-normal-login"
      :form="form"
      class="login-form"
      style="width: 40%;margin:0 auto;"
    >
      <a-form-item>
        <a-input
          v-decorator="[
          'userName',
          { rules: [{ required: true, message: 'Please input your username!' }] },
        ]"
          placeholder="Username"
          v-model="userInput"
        >
          <a-icon slot="prefix" type="user" style="color: rgba(0,0,0,.25)" />
        </a-input>
      </a-form-item>
      <a-form-item>
        <a-input
          v-decorator="[
          'password',
          { rules: [{ required: true, message: 'Please input your Password!' }] },
        ]"
          type="password"
          placeholder="Password"
          v-model="passInput"
        >
          <a-icon slot="prefix" type="lock" style="color: rgba(0,0,0,.25)" />
        </a-input>
      </a-form-item>
      <a-form-item>
        <a-button style="width: 50%" @click="login" type="primary" html-type="submit" class="login-form-button">
          Log in
        </a-button>
      </a-form-item>
      <a-form-item>
        <a-button style="width: 50%" @click="loginbyP12" type="primary" html-type="submit" class="login-form-button">
          Log in by cert
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
  export default {
    name: "",
    data() {
      return {
        userInput: "",
        passInput: "",
        axiosBase:"https://www.caelog.top:8023"
      }
    },
    mounted() {
      // this.$axios.get("https://www.caelog.top:8023/hello").then(res=>{
      //   console.log(res)
      // })
    },
    methods:{
      login(){
        let params={
          "username":this.userInput,
          "password":this.passInput
        }
        this.$axios({
          method: "get",
          url: this.axiosBase+"/login",
          params: params
        }).then(res => {
          if(res.data.status==200){
            this.$cookies.set("infosecAuth",res.data.data)
            this.$notification['success']({
              message: "登陆成功！",
              description: res.data.data.name,
              duration: 4.5,
              placement: 'topRight'
            })
            this.$router.push("/")
          }
          else{
            this.$notification['error']({
              message: "用户名或密码错误！",
              description: res.data.data,
              duration: 4.5,
              placement: 'topRight'
            })
          }
        })
      },
      loginbyP12(){
        this.$axios({
          method: "get",
          url: this.axiosBase+"/loginbyP12"
        }).then(res => {
          if(res.data.status==200){
            this.$cookies.set("infosecAuth",res.data.data)
            this.$notification['success']({
              message: "登陆成功！",
              description: res.data.data.name,
              duration: 4.5,
              placement: 'topRight'
            })
            this.$router.push("/")
          }
          else{
            this.$notification['error']({
              message: "证书不存在或证书错误！",
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
  .inputBox {
    position: relative;
    left: 50%;
    margin-top: 12px;
    margin-left: -200px;
    height: 50px;
    width: 250px;
  }

  .inputLabel {
    position: absolute;
    left: 50px;
  }

  .inputInput {
    position: absolute;
  }

  .button {
    cursor: pointer;
    border-style: solid;
    border-width: thin;
    width: 100px;
    position: relative;
    left:50%;
    margin-left: -50px;
    margin-top : 20px;
    border-radius: 10px;
  }
</style>
