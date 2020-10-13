# Wiretiger

#### 个基于B/S版的抓包软件，支持HTTPS抓包
1.启动后，HTTP代理挂 localhost:52007  
2.另一个没有代理的浏览器，访问localhost:8080进入控制台

#### 基于编程式的微型网关
将浏览器挂好代理后，可以通过Java代码方式对所有HTTP/HTTPS请求和响应进行拦截并重写。

例如：我就将浏览器访问百度首页的Logo换成了Google
![](https://github.com/hudaming1/wiretiger/blob/master/Show.png)
