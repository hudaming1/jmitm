package org.hum.wiretiger.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.hum.wiretiger.console.common.codec.impl.CodecFactory;
import org.hum.wiretiger.provider.WiretigerBuilder;
import org.hum.wiretiger.proxy.mock.CatchRequest;
import org.hum.wiretiger.proxy.mock.CatchResponse;
import org.hum.wiretiger.proxy.mock.Mock;

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
				// DEMO3：修改了百度首页的Logo，读取本地GoogleLogo文件，首页Logo变为Google
				mockDemo3(), 
				// DEMO4：拦截所有响应，对响应打标记
				mockDemo4(),
				// DEMO6：对百度首页注入一段JS代码（根据请求拦截响应报文，并追加一段代码）
				mockDemo6()
				// Mock Test
//				,mockTest()
//				,mockTest2()
				,mockTest3()
				);
		wtBuilder.webRoot(WiretigerServerRun.class.getResource("/webroot").getFile());
		wtBuilder.webXmlPath(WiretigerServerRun.class.getResource("/webroot/WEB-INF/web.xml").getFile());
		
		wtBuilder.build().start();
	}

	private static Mock mockDemo6() {
		return new CatchRequest().eval(request -> {
			return "www.baidu.com".equals(request.host()) && "/".equals(request.uri());
		}).rebuildResponse(response -> {
			log.info("inject js...");
			// 注入的JS代码
			String json = "<!--add by wiretigher--><script type='text/javascript'>alert('Wiretiger say hello');</script>";
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
	
	static Mock mockTest2() {
		return new CatchRequest().eval(request -> {
			return "/sms/internal/pda/stockTransfer/queryStockForUnshelve".equals(request.uri());
		}).rebuildRequest(request-> {
			System.out.println("mockTest2");
			request.host("172.16.187.66:8081");
			request.header("X-User-Id", "3016");
			request.uri("/migrate/sms/internal/pda/stockTransfer/queryStockForUnshelve");
			return request;
		}).mock();
	}
	
	static Mock mockTest3() {
		return new CatchRequest().eval(request -> {
			return "/sms/internal/pda/stockTransfer/pdaUnshelveItems".equals(request.uri());
		}).rebuildRequest(request-> {
			System.out.println("mockTest3");
			request.host("172.16.187.66:8081");
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

	private static Mock mockDemo1() {
		// 将wiretiger.com重定向到localhost:8080
		return new CatchRequest().eval(request -> {
			return "wiretiger.com".equals(request.host());
		}).rebuildRequest(request -> {
			return request.header("Host", "localhost:8080");
		}).mock();
	}
}
