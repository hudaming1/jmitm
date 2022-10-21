package org.hum.jmitm.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.hum.jmitm.console.common.codec.impl.CodecFactory;
import org.hum.jmitm.provider.JmitmBuilder;
import org.hum.jmitm.proxy.mock.CatchRequest;
import org.hum.jmitm.proxy.mock.CatchResponse;
import org.hum.jmitm.proxy.mock.Mock;
import org.hum.jmitm.proxy.mock.wiredog.HttpResponse;

import com.alibaba.fastjson.JSON;

import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JmitmServerRun {
	
	public static void main(String[] args) throws Exception {
		JmitmBuilder wtBuilder = new JmitmBuilder();
		wtBuilder.parseHttps(true);
		wtBuilder.proxyPort(8888).threads(100);
		wtBuilder.consoleHttpPort(8080).consoleWsPort(52996);
		wtBuilder.pipeHistory(10).sessionHistory(200);
		wtBuilder.addMock(
				// DEMO1：将「wiredog.com」重定向到「localhost:8080」，等效于配置host:   wiredog.com    127.0.0.1:8080
				mockDemo1(), 
				// DEMO2：修改了百度首页的Logo，读取本地GoogleLogo文件，首页Logo变为Google
				mockDemo2(), 
				// DEMO3：拦截所有响应，对响应打标记
				mockDemo3(),
				// DEMO4：对百度首页注入一段JS代码（根据请求拦截响应报文，并追加一段代码）
				mockDemo4(),
				// 
				mockRMGSSchedule()
				);
		
		wtBuilder.build().start();
	}

	private static Mock mockDemo4() {
		return new CatchRequest().eval(request -> {
			// PC版首页注入
			String uri = request.uri().indexOf("?") > 0 ? request.uri().substring(0, request.uri().indexOf("?")) : request.uri();
			return ("www.baidu.com".equals(request.host()) && "/".equals(request.uri())
					||
					// 移动版首页注入
					("m.baidu.com".equals(request.host()) && "/".equals(uri)));
		}).rebuildResponse(response -> {
			log.info("inject js...");
			// 注入的JS代码
			String json = "<!--add by wiretigher--><script type='text/javascript'>alert('wiredog say hello');</script>";
			String outBody = "";
			try {
				// 因为响应头是gzip进行压缩，因此无法直接将ASCII串追加到内容末尾，需要先将原响应报文解压，在将JS追加到末尾
				outBody = new String(CodecFactory.create("gzip").decompress(response.body())) + json;
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 解压后为了省事，就不再进行压缩
			return response.removeHeader("Content-Encoding").body(outBody.getBytes());
		}).mock();
	}
	
	private static Mock mockDemo3() {
		return new CatchResponse().eval(response -> {
			return true;
		}).rebuildResponse(response -> {
			return response.header("signby", "hudaming");
		}).mock();
	}
	
	private static Mock mockDemo2() {
		return new CatchRequest().eval(request -> {
			return "www.baidu.com".equals(request.host()) &&
					("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri()));
		}).mockResponse(httpRequest -> {
			byte[] googleLogo = readFile("/mock/google.png");
			HttpResponse response = new HttpResponse();
			return response.body(googleLogo).header("Content-Type", "image/gif");
		}).mock();
	}
	
	private static byte[] readFile(String file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(JmitmServerRun.class.getResource(file).getFile()));
			byte[] bytes = new byte[fileInputStream.available()];
			fileInputStream.read(bytes);
			fileInputStream.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Mock mockDemo1() {
		// 将wiredog.com重定向到localhost:8080
		return new CatchRequest().eval(request -> {
			return "wiredog.com".equals(request.host());
		}).rebuildRequest(request -> {
			return request.header("Host", "localhost:8080");
		}).mock();
	}
	
	private static Mock mockRMGS() {
		return new CatchRequest().eval(request -> {
			return ("datatest.miji"
					+ "aoyu.cn").equals(request.host()) && "/teachingplan/teaching/aimdatasave".equals(request.uri()) && HttpMethod.POST.equals(request.method());
		}).rebuildRequest(request -> {
			System.out.println("mock");
			return request.header("Host", "localhost:2100").uri("/teaching/aimdatasave");
		}).mock();
	}

	
	private static Mock mockClue() {
		return new CatchRequest().eval(request -> {
			return request.host().contains("datatest.mijiaoyu.cn") && request.uri().startsWith("/clue/") && (HttpMethod.GET.equals(request.method()) || HttpMethod.POST.equals(request.method()));
		}).rebuildRequest(request -> {
			System.out.println("bingo");
			request.host("localhost:3400");
			request.uri(request.uri().replaceFirst("/clue/", "/"));
			System.out.println(request.uri());
			return request;
		}).rebuildResponse(resp -> {
			resp.header("Access-Control-Allow-Origin", "*");
			return resp;
		}).mock();
	}
	
	private static Mock mockComServiceSchedule() {
		// https://datatest.mijiaoyu.cn/contract/contract/selectStudentPermissions?stuId=34388
		return new CatchRequest().eval(request -> {
			return request.host().contains("datatest.mijiaoyu.cn") && request.uri().startsWith("/user/") && (HttpMethod.GET.equals(request.method()) || HttpMethod.POST.equals(request.method()));
		}).rebuildRequest(request -> {
			System.out.println("bingo");
			request.host("localhost:1100");
			request.uri(request.uri().replaceFirst("/user/", "/"));
			System.out.println(request.uri());
			return request;
		}).rebuildResponse(resp -> {
			resp.header("Access-Control-Allow-Origin", "*");
			return resp;
		}).mock();
	}
	
	private static Mock mockTeachingPlan() {
		// https://datatest.mijiaoyu.cn/contract/contract/selectStudentPermissions?stuId=34388
		return new CatchRequest().eval(request -> {
			return request.host().contains("datatest.mijiaoyu.cn") && request.uri().startsWith("/teachingplan") && (HttpMethod.GET.equals(request.method()) || HttpMethod.POST.equals(request.method()));
		}).rebuildRequest(request -> {
			System.out.println("bingo");
			request.host("localhost:2100");
			request.uri(request.uri().replaceFirst("/teachingplan", ""));
			System.out.println(request.uri());
			return request;
		}).rebuildResponse(resp -> {
			resp.header("Access-Control-Allow-Origin", "*");
			return resp;
		}).mock();
	}
	
	private static Mock mockRMGSSchedule() {
		// https://datatest.mijiaoyu.cn/contract/contract/selectStudentPermissions?stuId=34388
		return new CatchRequest().eval(request -> {
			return request.host().contains("datatest.mijiaoyu.cn") && request.uri().startsWith("/schedule") && (HttpMethod.GET.equals(request.method()) || HttpMethod.POST.equals(request.method()));
		}).rebuildRequest(request -> {
			System.out.println("bingo");
			request.host("localhost:1800");
			request.uri(request.uri().replaceFirst("/schedule", ""));
			System.out.println(request.uri());
			return request;
		}).rebuildResponse(resp -> {
			resp.header("Access-Control-Allow-Origin", "*");
			return resp;
		}).mock();
	}
	
	private static Mock mockRMGSSchedule4UAT() {
		// https://datatest.mijiaoyu.cn/contract/contract/selectStudentPermissions?stuId=34388
		return new CatchRequest().eval(request -> {
			return request.host().contains("datauat.mijiaoyu.cn") && request.uri().startsWith("/schedule") && (HttpMethod.GET.equals(request.method()) || HttpMethod.POST.equals(request.method()));
		}).rebuildRequest(request -> {
			System.out.println("bingo");
			request.host("localhost:1800");
			request.uri(request.uri().replaceFirst("/schedule", ""));
			System.out.println(request.uri());
			return request;
		}).rebuildResponse(resp -> {
			resp.header("Access-Control-Allow-Origin", "*");
			return resp;
		}).mock();
	}
}


