package org.hum.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.hum.wiredog.console.common.codec.impl.CodecFactory;
import org.hum.wiredog.provider.WiredogBuilder;
import org.hum.wiredog.proxy.mock.CatchRequest;
import org.hum.wiredog.proxy.mock.Mock;

import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkUseCase_SKU4vs1 {
	
	public static void main(String[] args) throws Exception {
		WiredogBuilder wtBuilder = new WiredogBuilder();
		wtBuilder.parseHttps(true);
		wtBuilder.proxyPort(52008).threads(40);
		wtBuilder.consoleHttpPort(8080).consoleWsPort(52996);
		wtBuilder.pipeHistory(10).sessionHistory(200);
		wtBuilder.addMock(
				// DEMO1：将「wiredog.com」重定向到「localhost:8080」，等效于配置host:   wiredog.com    127.0.0.1:8080
				mockDemo1(), 
				mockDemo3(),
				mockDemo6(),
				// Mock WorkUseCase1107 
				mockAreaType(),
				mockAreaCreate(),
				mockAreaUpdate(),
				mockAreaQuery(),
				mockAreaDisabled(),
				mockAreaEnable(),
				mockAreaQueryWords(),
				mockAreaSort(),
				mockAreaSortImport(),
				mockAreaUploadImport(),
				// Mock Location
				mockLocationQuery(),
				mockLocationAdd(),
				mockLocationDisable(),
				mockLocationEnable(),
				mockQueryLocationLike(),
				mockImport(),
				// Mock Location Bind
				mockLocationBindQuery()
		);
		
		wtBuilder.build().start();
	}

	private static Mock mockDemo6() {
		return new CatchRequest().eval(request -> {
			return "www.baidu.com".equals(request.host()) && "/".equals(request.uri());
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
		return new CatchRequest().eval(request -> {
			return "www.baidu.com".equals(request.host()) &&
					("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri()));
		}).rebuildResponse(response -> {
			System.out.println("mock google logo");
			byte[] googleLogo = readFile("/mock/google.png");
			return response.body(googleLogo).header("Content-Type", "image/gif");
		}).mock();
	}
	
	private static byte[] readFile(String file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(WiredogBuilder.class.getResource(file).getFile()));
			byte[] bytes = new byte[fileInputStream.available()];
			fileInputStream.read(bytes);
			fileInputStream.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*************************************************** 库区Mock ***************************************************/

	private static final String USER_ID = "3016";

	private static Mock mockAreaType() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/area/type") && HttpMethod.GET == request.method();
		}).rebuildRequest(request -> {
			print("库区类型列表");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/type");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAreaCreate() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/area/add") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("创建库区");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/add");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAreaUpdate() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/area/edit") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("修改库区");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/update");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAreaQuery() {
		return new CatchRequest().eval(request -> {
			return request.uri().equals("/sms/internal/area/query") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("查询库区");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/query");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAreaDisabled() {
		return new CatchRequest().eval(request -> {
			return (request.uri().contains("/sms/internal/area/disable") || request.uri().contains("/sms/basic/area/disable")) && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("禁用库区");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/disable");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAreaEnable() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/area/enable") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("启用库区");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/enable");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAreaQueryWords() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/area/queryByWords") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("模糊查询库区");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/queryByWords");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAreaSort() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/area/sort") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("修改库区动线");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/sort");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAreaSortImport() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/area/areaSort/import") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("上传库区动线");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/areaSort/import");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}

	private static Mock mockAreaUploadImport() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/area/import") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("批量修改库区");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/area/import");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}

	private static Mock mockDemo1() {
		// 将wiredog.com重定向到localhost:8080
		return new CatchRequest().eval(request -> {
			return "wiredog.com".equals(request.host());
		}).rebuildRequest(request -> {
			return request.header("Host", "localhost:8080");
		}).mock();
	}

	/*************************************************** 库位Mock ***************************************************/
	
	private static Mock mockLocationQuery() {
		return new CatchRequest().eval(request -> {
			return request.uri().equals("/sms/internal/location/listByPage") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("查询库位");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/location/listByPage");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}

	private static Mock mockLocationAdd() {
		return new CatchRequest().eval(request -> {
			return request.uri().equals("/sms/internal/location/add") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("添加库位");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/location/add");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockLocationDisable() {
		return new CatchRequest().eval(request -> {
			return request.uri().equals("/sms/internal/location/disable") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("禁用库位");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/location/disable");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockLocationEnable() {
		return new CatchRequest().eval(request -> {
			return request.uri().equals("/sms/internal/location/enable") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("启用库位");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/location/enable");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockQueryLocationLike() {
		return new CatchRequest().eval(request -> {
			return request.uri().equals("/sms/internal/location/queryByWords") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("模糊查询库位");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/location/queryByWords");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockImport() {
		return new CatchRequest().eval(request -> {
			return request.uri().equals("/sms/internal/location/import") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("批量导入库位");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/location/import");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}

	/*************************************************** 库位绑定关系Mock ***************************************************/

	private static Mock mockLocationBindQuery() {
		return new CatchRequest().eval(request -> {
			return request.uri().equals("/sms/internal/locationbind/listByPage") && HttpMethod.POST == request.method();
		}).rebuildRequest(request -> {
			print("查询库位绑定关系");
			request.forward("localhost:9005").header("X-User-Id", USER_ID);
			request.uri("/sms/basic/locationbind/listByPage");
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static void print(String text) {
		System.out.println("=======================================");
		System.out.println(text);
		System.out.println("=======================================");
	}
}
