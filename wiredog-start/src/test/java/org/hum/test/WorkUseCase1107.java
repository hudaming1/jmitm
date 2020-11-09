package org.hum.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.hum.wiredog.console.common.codec.impl.CodecFactory;
import org.hum.wiredog.provider.WiredogBuilder;
import org.hum.wiredog.proxy.mock.CatchRequest;
import org.hum.wiredog.proxy.mock.CatchResponse;
import org.hum.wiredog.proxy.mock.Mock;
import org.hum.wiredog.proxy.mock.wiredog.HttpRequest;
import org.hum.wiredog.proxy.mock.wiredog.HttpResponse;
import org.hum.wiredog.proxy.mock.wiredog.HttpResponseMock;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkUseCase1107 {
	
	public static void main(String[] args) throws Exception {
		WiredogBuilder wtBuilder = new WiredogBuilder();
		wtBuilder.parseHttps(false);
		wtBuilder.proxyPort(52007).threads(400);
		wtBuilder.consoleHttpPort(8080).consoleWsPort(52996);
		wtBuilder.pipeHistory(10).sessionHistory(200);
		wtBuilder.addMock(
				// DEMO1：将「wiredog.com」重定向到「localhost:8080」，等效于配置host:   wiredog.com    127.0.0.1:8080
				mockDemo1(), 
				// DEMO3：修改了百度首页的Logo，读取本地GoogleLogo文件，首页Logo变为Google
				mockDemo3(), 
				// DEMO4：拦截所有响应，对响应打标记
				mockDemo4(),
				// DEMO6：对百度首页注入一段JS代码（根据请求拦截响应报文，并追加一段代码）
				mockDemo6()
				,mockResponse()
				// Mock WorkUseCase1107
//				,mockSupportOptionsMethod()
//				,mockAllInternelRequestForwardLocalhost()
//				,mockAllReplenishmentRequestForwardLocalhost()
//				,mockAllStockRequestForwardLocalhost()
//				,mockTest()
//				,mockOffShelfQuery()
//				,mockOffShelfSubmit()
				);
		
		wtBuilder.build().start();
	}
	
//	private static final String USER_ID = "15412";
	private static final String USER_ID = "3016";

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

	private static Mock mockAllInternelRequestForwardLocalhost() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/internal/") && HttpMethod.OPTIONS != request.method();
		}).rebuildRequest(request -> {
			request.forward("172.16.187.66:8081").header("X-User-Id", USER_ID);
			request.uri("/migrate" + request.uri());
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockResponse() {
		return new CatchRequest().eval(request -> {
			return "www.baidu.com".equals(request.host());
		}).mockResponse(new HttpResponseMock() {
			@Override
			public HttpResponse eval(HttpRequest httpRequest) {
				if ("/mock".equals(httpRequest.uri())) {
					HttpResponse resp = new HttpResponse();
					resp.body("This is Mock Page".getBytes());
					return resp;
				} else if ("/1".equals(httpRequest.uri())) {
					HttpResponse resp = new HttpResponse();
					resp.body(("This is Page 1").getBytes());
					return resp;
				} else {
					return null;
				}
			}
		}).mock();
	}
	
	private static Mock mockAllReplenishmentRequestForwardLocalhost() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/replenishment/") && HttpMethod.OPTIONS != request.method();
		}).rebuildRequest(request -> {
			request.forward("172.16.187.66:9030").header("X-User-Id", USER_ID);
			request.uri("/migrate" + request.uri());
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}
	
	private static Mock mockAllStockRequestForwardLocalhost() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/stock/") && HttpMethod.OPTIONS != request.method();
		}).rebuildRequest(request -> {
			request.forward("172.16.187.66:9040").header("X-User-Id", USER_ID);
			request.uri("/migrate" + request.uri());
			return request;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");	
			return response;
		}).mock();
	}

	private static Mock mockSupportOptionsMethod() {
		return new CatchRequest().eval(request -> {
			return ("wuliu-ocean-gateway.b22.missfresh.net".equals(request.host()) || "wuliu-ocean-gateway.b22.missfresh.net".equals(request.header("Proxy-Client-IP")))
					&& request.method() == HttpMethod.OPTIONS;
		}).rebuildResponse(response -> {
			response.header("Access-Control-Allow-Credentials", true);
			response.header("Access-Control-Allow-Headers", "authorization, content-type, deviceid, devicetype, mfsig");
			response.header("Access-Control-Allow-Methods", "OPTIONS,HEAD,GET,PUT,POST,DELETE,PATCH");
			response.header("Access-Control-Allow-Origin", "http://56hub-web-staging.missfresh.net");
			response.header("Access-Control-Max-Age", 18000);
			response.bodyClear();
			response.toFullHttpResponse().setStatus(HttpResponseStatus.OK);
			return response;
		}).mock();
	}
	
	static Mock mockTest() {
		return new CatchRequest().eval(request -> {
			return "/sms/stock/inbound/statisticStock".equals(request.uri());
		}).rebuildResponse(response -> {
			log.info("mock test...");
			// 注入的JS代码
			String json = "{\"code\":200,\"message\":\"操作成功\",\"result\":{\"total\":2,\"list\":[{\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"HuMing测试军区\",\"warehouseCode\":\"MRYXBJS-JIUXIANQIAO\",\"warehouseName\":\"北京每日优鲜酒仙桥站(北京北)\",\"materialId\":1000082,\"materialName\":\"22cm网套\",\"stockQty\":232,\"offOccupiedQty\":0,\"freezeQty\":0,\"availableQty\":232,\"lockQty\":0,\"lackQty\":0,\"onOccupiedQty\":0,\"productionDate\":null,\"unit\":\"袋\",\"storeCondition\":\"ROOM\",\"shelfLife\":0,\"areaType\":2,\"areaTypeDesc\":\"拣货区\",\"areaCode\":\"JH\",\"locationCode\":\"JH-1-01\",\"parentCategory\":30270,\"parentCategoryDesc\":\"包材\"},{\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"华北测试军区\",\"warehouseCode\":\"MRYXBJS-JIUXIANQIAO\",\"warehouseName\":\"北京每日优鲜酒仙桥站(北京北)\",\"materialId\":1001691,\"materialName\":\"代发货标贴\",\"stockQty\":100,\"offOccupiedQty\":0,\"freezeQty\":0,\"availableQty\":100,\"lockQty\":0,\"lackQty\":0,\"onOccupiedQty\":0,\"productionDate\":null,\"unit\":\"袋\",\"storeCondition\":\"ROOM\",\"shelfLife\":0,\"areaType\":1,\"areaTypeDesc\":\"暂存区\",\"areaCode\":\"ZC\",\"locationCode\":\"ZC\",\"parentCategory\":30270,\"parentCategoryDesc\":\"包材\"}],\"pageNum\":1,\"pageSize\":10,\"size\":2,\"startRow\":0,\"endRow\":1,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1,\"firstPage\":1,\"lastPage\":1}}";
			// 解压后为了省事，就不再进行压缩
			return response.removeHeader("Content-Encoding").body(json.getBytes());
		}).mock();
	}
	
	static Mock mockOffShelfQuery() {
		return new CatchRequest().eval(request -> {
			return "/sms/internal/pda/stockTransfer/queryStockForUnshelve".equals(request.uri());
		}).rebuildRequest(request-> {
			System.out.println("mockTest2");
			request.forward("172.16.187.66:8081");
			request.header("X-User-Id", "3016");
			request.uri("/migrate/sms/internal/pda/stockTransfer/queryStockForUnshelve");
			return request;
		}).mock();
	}
	
	static Mock mockOffShelfSubmit() {
		return new CatchRequest().eval(request -> {
			return "/sms/internal/pda/stockTransfer/pdaUnshelveItems".equals(request.uri());
		}).rebuildRequest(request-> {
			System.out.println("mockTest3");
			request.forward("172.16.187.66:8081");
			request.header("X-User-Id", "3016");
			request.uri("/migrate/sms/internal/pda/stockTransfer/pdaUnshelveItems");
			return request;
		}).mock();
	}

	private static Mock mockDemo4() {
		return new CatchResponse().eval(response -> {
			return true;
		}).rebuildResponse(response -> {
			return response.header("signby", "hudaming");
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

	private static Mock mockDemo1() {
		// 将wiredog.com重定向到localhost:8080
		return new CatchRequest().eval(request -> {
			return "wiredog.com".equals(request.host());
		}).rebuildRequest(request -> {
			return request.header("Host", "localhost:8080");
		}).mock();
	}
}
