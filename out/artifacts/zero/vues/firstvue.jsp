<%@ taglib prefix="v-on" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="../vue.js"></script>
</head>
<body>
<div id="app">
    {{message}}
</div>
<div id="app-2">
    <span v-bind:title="message">鼠标悬停几秒钟查看此处动态绑定的提示信息！</span>
</div>
<div id="app-3">
    <p v-if="seen">现在你看到我了</p>
</div>
<div id="app-4">
    <ol>
        <li v-for="todo in todos">
            {{ todo.text }}
        </li>
    </ol>
</div>
<div id="app-5">
    <p>{{message}}</p>
    <button v-on:click="reserve">逆转消息</button>
</div>
<div id="app-6">
    <p>{{message}}</p>
    <input v-model="message">
</div>
<ol>
    <!-- 创建一个 todo-item 组件的实例 -->
    <todo-item></todo-item>
</ol>

<div id="app-7">
    <ol>
        <todo-item v-for="item in groList" v-bind:todo="item" v-bind:kry="item.id"></todo-item>
    </ol>
</div>

<script type="text/javascript">

    // 定义名为 todo-item 的新组件
    Vue.component('todo-item',{
        props: ['todo'],
        template: '<li>{{todo.text}}</li>'
    })
    var app7 = new Vue({
        el: '#app-7',
        data: {
            groList: [
                {id: 0, text: '吃蔬菜'},
                {id: 1, text: '吃水果'},
                {id: 2, text: '吃维生素'}
            ]
        }
    })

    var app6 = new Vue({
        el: '#app-6',
        data: {
            message: 'Hell Vue!'
        }
    })
    var app5 = new Vue({
        el: '#app-5',
        data: {
            message: 'Hello Vue!'
        },
        methods: {
            reserve : function () {
                this.message = this.message.split('').reverse().join('')
            }
        }
    })
    var app = new Vue({
        el: '#app',
        data: {
            message: 'Hello vue!'
        }
    })
    var app2 = new Vue({
        el: '#app-2',
        data:{
            message: '页面加载于' + new Date().toLocaleString()
        }
    })
    var app3 = new Vue({
        el: '#app-3',
        data: {
            seen: true
        }
    })
    var app4 = new Vue({
        el: '#app-4',
        data: {
            todos: [
                {text: "java"},
                {text: "php"},
                {text: "vue"}
            ]
        }
    })
</script>
</body>
</html>
