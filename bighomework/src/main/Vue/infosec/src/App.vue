<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>

import index from "./components/index";
import login from "./components/login";

export default {
  name: 'App',
  components: {
    index: index,
    login:login
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
