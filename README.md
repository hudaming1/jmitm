# Wiretiger

#### 个基于B/S版的抓包软件，支持HTTPS抓包
1.启动后，HTTP代理挂 localhost:52007  
2.另一个没有代理的浏览器，访问localhost:8080进入控制台

#### 基于编程式的微型网关
将浏览器挂好代理后，可以通过Java代码方式对所有HTTP/HTTPS请求和响应进行拦截并重写。
基于编程式网关，你可以：
1.重制Request：例如将「wiretiger.com」重定向到「localhost:8080」，等效于配置浏览器级别的host:   wiretiger.com    127.0.0.1:8080
2.重制Request：例如浏览器访问百度首页Logo，重定向到360搜索Logo
3.重制Response：拦截百度首页Logo，读取本地GoogleLogo文件，首页Logo变为Google
![](https://github.com/hudaming1/wiretiger/blob/master/Show.png)
4.重制Response：对百度首页注入一段JS代码（根据请求拦截响应报文，并追加一段代码）
