## 可编程式网关
简单说就是你可以对自己浏览器的所有请求和响应进行拦截，并通过Java代码方式进行重制。
基于编程式网关，你可以：

**根据Request重制Request**    
Demo1:例如将「wiretiger.com」重定向到「localhost:8080」，等效于配置浏览器级别的host:   wiretiger.com    127.0.0.1:8080  

```java
// 将wiretiger.com重定向到localhost:8080
proxy.add(new CatchRequest().eval(request -> {
    // 判断请求域名是wiretigert.com
    return "wiretiger.com".equals(request.host());
}).rebuildRequest(request -> {
    // 如果命中Request，则将请求实际转发到localhost:8080
    return request.header("Host", "localhost:8080");
}).mock());
```

**根据Request重制Response**    
Demo1:拦截百度首页Logo，读取本地GoogleLogo文件，首页Logo变为Google
![](https://github.com/hudaming1/wiretiger/blob/master/Show.png)
```java
proxy.add(new CatchRequest().eval(request -> {
    // 如果域名是baidu，访问的图片是百度的Logo（第一个图片是PC上的，后两个路径是移动端的Logo）
    return "www.baidu.com".equals(request.host()) &&("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri()));
}).rebuildResponse(response -> {
    // 如果命中请求，则读取本地Google的Logo并对响应进行替换
    byte[] googleLogo = readFile("/mock/google.png");
    return response.body(googleLogo).header("Content-Type", "image/gif");
}).mock());
```

Demo2:对百度首页注入一段JS代码（根据请求拦截响应报文，并追加一段代码）
![](https://github.com/hudaming1/wiretiger/blob/master/Show2.png)
```java
proxy.add(new CatchRequest().eval(request -> {
    // 如果访问的是百度首页
    return "www.baidu.com".equals(request.host()) && "/".equals(request.uri());
}).rebuildResponse(response -> {
    // 如果命中百度首页，则将以下JS代码追加到网页HTML的末尾，通过查看浏览器网页源代码也会发现在末尾处多了一段JS
    // 注入的JS代码
    String json = "<!--add by wiretigher--><script type='text/javascript'>alert('Wiretiger say hello');</script>";
    // 因为响应头是gzip进行压缩，因此无法直接将ASCII串追加到内容末尾，需要先将原响应报文解压，在将JS追加到末尾
    String outBody = new String(CodecFactory.create("gzip").decompress(response.body())) + json;
    // 解压后为了省事，就不再进行压缩
    return response.removeHeader("Content-Encoding").body(outBody.getBytes());
}).mock());
```
**根据Response重制Response**  
暂时No Case...

## Quick Start
1.git clone https://github.com/hudaming1/wiretiger.git   
2.启动 WiretigerServerRun.java （默认端口52007）   
3.访问localhost:8080进入控制台    
4.点击控制台单「Download Cert」按钮下载并安装CA（如果需要卸载，在证书库中搜索Wiretiger删除即可）   
🌟Mac系统导入后，还需要手动将CA进行授信。    
5.访问HTTPS网页，当控制台显示出HTTPS请求时，即可对HTTP请求响应进行重制   

#### 其他
