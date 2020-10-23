package org.hum.wiretiger.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.hum.wiretiger.console.common.codec.IContentCodec;
import org.hum.wiretiger.console.common.codec.impl.CodecFactory;
import org.hum.wiretiger.provider.WiretigerBuilder;
import org.hum.wiretiger.proxy.mock.CatchRequest;
import org.hum.wiretiger.proxy.mock.CatchResponse;
import org.hum.wiretiger.proxy.mock.Mock;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;

import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WiretigerServerRun {
	
	public static void main(String[] args) throws Exception {
		WiretigerBuilder wtBuilder = new WiretigerBuilder();
		wtBuilder.parseHttps(false);
		wtBuilder.proxyPort(52007).threads(400);
		wtBuilder.consoleHttpPort(8080).consoleWsPort(52996);
		wtBuilder.pipeHistory(10).sessionHistory(200);
		wtBuilder.addMock(
				// DEMO1：将「wiretiger.com」重定向到「localhost:8080」，等效于配置host:   wiretiger.com    127.0.0.1:8080
				mockDemo1(), 
				// DEMO2：修改了百度首页的Logo，打开https://www.baidu.com，会发现首页Logo重定向到360So
//				mockDemo2(), 
				// DEMO3：修改了百度首页的Logo，读取本地GoogleLogo文件，首页Logo变为Google
				mockDemo3(), 
				// DEMO4：拦截所有响应，对响应打标记
				mockDemo4(), 
				// DEMO5：根据Request，重新Mock Response
				// mockDemo5(),
				// DEMO6：对百度首页注入一段JS代码（根据请求拦截响应报文，并追加一段代码）
				mockDemo6()
				);
		wtBuilder.webRoot(WiretigerServerRun.class.getResource("/webroot").getFile());
		wtBuilder.webXmlPath(WiretigerServerRun.class.getResource("/webroot/WEB-INF/web.xml").getFile());
		
		wtBuilder.build().start();
	}

	private static Mock mockDemo6() {
		return new CatchRequest().eval(request -> {
			if (request.headers() == null || request.headers().get("Host") == null) {
				return false;
			}
			return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) && "/".equals(request.uri());
		}).rebuildResponse(response -> {
			// 注入的JS代码
			String json = "<script type='text/javascript'>alert('Wiretiger say hello');</script>";
			byte[] readBytes = HttpMessageUtil.readBytes(response.content());
			// 因为响应头是gzip进行压缩，因此无法直接将ASCII串追加到内容末尾，需要先将原响应报文解压，在将JS追加到末尾
			IContentCodec contentCodec = CodecFactory.create("gzip");
			String outBody = "";
			try {
				byte[] decompress = contentCodec.decompress(readBytes);
				outBody = new String(decompress) + json;
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 解压后为了省事，就不再进行压缩
			response.content().retain().clear().writeBytes(outBody.getBytes());
			response.headers().remove("Content-Encoding").set("Content-Length", outBody.getBytes().length);
			return response;
		}).mock();
	}

	private static Mock mockDemo4() {
		return new CatchResponse().eval(response -> {
			return true;
		}).rebuildResponse(response -> {
			response.headers().set("signby", "hudaming");
			return response;
		}).mock();
	}

	private static Mock mockDemo3() {
		return new CatchRequest().eval(request -> {
			if (request.headers() == null || request.headers().get("Host") == null) {
				return false;
			}
			return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) &&
					("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri()));
		}).rebuildResponse(response -> {
			System.out.println("mock google logo");
			byte[] googleLogo = readFile("/mock/google.png");
			response.content().clear().writeBytes(googleLogo);
			response.headers().set("Content-Type", "image/gif")
							  .set("Content-Length", googleLogo.length);
			return response;
		}).mock();
	}
	
	private static byte[] readFile(String file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(WiretigerServerRun.class.getResource(file).getFile()));
			byte[] bytes = new byte[fileInputStream.available()];
			fileInputStream.read(bytes);
			fileInputStream.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Mock mockDemo2() {
		return new CatchRequest().eval(request -> {
			if (request.headers() == null || request.headers().get("Host") == null) {
				return false;
			}
			return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) &&
					("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri())); 
		}).rebuildRequest(request -> {
			log.info("hit baidu_logo");
			request.headers().set("Host", "p.ssl.qhimg.com:443");
			request.setUri("/t012cdb572f41b93733.png");
			return request;
		}).rebuildResponse(response -> {
			response.headers().add("wiretiger_mock", "redirect to 360_search");
			return response;
		}).mock();
	}

	private static Mock mockDemo1() {
		// 将wiretiger.com重定向到localhost:8080
		return new CatchRequest().eval(request -> {
			if (request.headers() == null || request.headers().get("Host") == null) {
				return false;
			}
			return "wiretiger.com".equals(request.headers().get("Host").split(":")[0]);
		}).rebuildRequest(request -> {
			request.headers().set("Host", "localhost:8080");
			return request;
		}).mock();
	}
}
