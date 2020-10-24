package org.hum.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.hum.wiretiger.console.common.codec.IContentCodec;
import org.hum.wiretiger.console.common.codec.impl.CodecFactory;
import org.hum.wiretiger.provider.WiretigerBuilder;
import org.hum.wiretiger.proxy.mock.CatchRequest;
import org.hum.wiretiger.proxy.mock.CatchResponse;
import org.hum.wiretiger.proxy.mock.Mock;
import org.hum.wiretiger.proxy.mock.wiretiger.HttpRequest;
import org.hum.wiretiger.proxy.mock.wiretiger.HttpRequestInterceptor;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;

import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunTest1 {
	
	public static void main(String[] args) throws Exception {
		WiretigerBuilder wtBuilder = new WiretigerBuilder();
		wtBuilder.parseHttps(true);
		wtBuilder.proxyPort(52007).threads(400);
		wtBuilder.consoleHttpPort(8080).consoleWsPort(52996);
		wtBuilder.pipeHistory(10).sessionHistory(200);
		wtBuilder.addMock(
				mockTest(),
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
				mockDemo6(),
				mockDemo7(),
				mockDemo8(),
				mockDemo9(),
				mockDemo10()
				);
		wtBuilder.webRoot(RunTest1.class.getResource("/webroot").getFile());
		wtBuilder.webXmlPath(RunTest1.class.getResource("/webroot/WEB-INF/web.xml").getFile());
		
		wtBuilder.build().start();
	}
	
	private static Mock mockTest() {
		return new CatchRequest().eval(new HttpRequestInterceptor() {
			@Override
			public boolean isHit(HttpRequest request) {
				if (request.host() == null) {
					return false;
				}
				return request.host().contains("so.com");
			}
		}).rebuildNettyRequest(req -> {
			req.headers().set("Host", "www.baidu.com");
			return req;
		}).mock();
	}

	private static Mock mockDemo6() {
		return new CatchRequest().evalNettyRequest(request -> {
			if (request.headers() == null || request.headers().get("Host") == null) {
				return false;
			}
			return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) && "/".equals(request.uri());
		}).rebuildNettyResponse(response -> {
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
		return new CatchResponse().evalNettyResponse(response -> {
			return true;
		}).rebuildNettyResponse(response -> {
			response.headers().set("signby", "hudaming");
			return response;
		}).mock();
	}

	private static Mock mockDemo3() {
		return new CatchRequest().evalNettyRequest(request -> {
			if (request.headers() == null || request.headers().get("Host") == null) {
				return false;
			}
			return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) &&
					("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri()));
		}).rebuildNettyResponse(response -> {
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
			FileInputStream fileInputStream = new FileInputStream(new File(RunTest1.class.getResource(file).getFile()));
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
		return new CatchRequest().evalNettyRequest(request -> {
			if (request.headers() == null || request.headers().get("Host") == null) {
				return false;
			}
			return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) &&
					("/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png".equals(request.uri()) || "/img/flexible/logo/pc/result.png".equals(request.uri()) || "/img/flexible/logo/pc/result@2.png".equals(request.uri())); 
		}).rebuildNettyRequest(request -> {
			log.info("hit baidu_logo");
			request.headers().set("Host", "p.ssl.qhimg.com:443");
			request.setUri("/t012cdb572f41b93733.png");
			return request;
		}).rebuildNettyResponse(response -> {
			response.headers().add("wiretiger_mock", "redirect to 360_search");
			return response;
		}).mock();
	}

	private static Mock mockDemo1() {
		// 将wiretiger.com重定向到localhost:8080
		return new CatchRequest().evalNettyRequest(request -> {
			if (request.headers() == null || request.headers().get("Host") == null) {
				return false;
			}
			return "wiretiger.com".equals(request.headers().get("Host").split(":")[0]);
		}).rebuildNettyRequest(request -> {
			request.headers().set("Host", "localhost:8080");
			return request;
		}).mock();
	}

	private static Mock mockDemo5() {
		return new CatchRequest().evalNettyRequest(request -> {
			return request.uri().contains("/sms/internal/staticCheck/listByPage") && request.method() == HttpMethod.POST;
		}).rebuildNettyResponse(response -> {
			String json = "{\"code\":200,\"message\":\"操作成功\",\"result\":{\"total\":13102,\"list\":[{\"id\":775780,\"checkNumber\":\"PD2020101300045-N\",\"createTime\":\"2020-10-13 03:42:28\",\"effectiveTime\":\"2020-10-13 03:42:29\",\"resultCommitTime\":\"2020-10-13 03:43:14\",\"approvalCompleteTime\":\"2020-10-13 03:43:14\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775779,\"checkNumber\":\"PD2020101300044-N\",\"createTime\":\"2020-10-13 03:42:14\",\"effectiveTime\":\"2020-10-13 03:42:15\",\"resultCommitTime\":\"2020-10-13 03:42:15\",\"approvalCompleteTime\":\"2020-10-13 03:42:15\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775616,\"checkNumber\":\"PD2020101202662-N\",\"createTime\":\"2020-10-12 22:26:19\",\"effectiveTime\":\"2020-10-12 22:26:20\",\"resultCommitTime\":\"2020-10-12 22:26:44\",\"approvalCompleteTime\":\"2020-10-12 22:26:44\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775437,\"checkNumber\":\"PD2020101202328-N\",\"createTime\":\"2020-10-12 20:30:43\",\"effectiveTime\":\"2020-10-12 20:30:44\",\"resultCommitTime\":\"2020-10-12 20:30:44\",\"approvalCompleteTime\":\"2020-10-12 20:30:44\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775306,\"checkNumber\":\"PD2020101202087-N\",\"createTime\":\"2020-10-12 19:15:41\",\"effectiveTime\":\"2020-10-12 19:15:42\",\"resultCommitTime\":\"2020-10-12 19:15:42\",\"approvalCompleteTime\":\"2020-10-12 19:15:42\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775266,\"checkNumber\":\"PD2020101202032-N\",\"createTime\":\"2020-10-12 19:01:44\",\"effectiveTime\":\"2020-10-12 19:01:45\",\"resultCommitTime\":\"2020-10-12 19:01:45\",\"approvalCompleteTime\":\"2020-10-12 19:01:45\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775147,\"checkNumber\":\"PD2020101201812-N\",\"createTime\":\"2020-10-12 17:45:39\",\"effectiveTime\":\"2020-10-12 17:45:40\",\"resultCommitTime\":\"2020-10-12 17:45:40\",\"approvalCompleteTime\":\"2020-10-12 17:45:40\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775146,\"checkNumber\":\"PD2020101201811-N\",\"createTime\":\"2020-10-12 17:45:33\",\"effectiveTime\":\"2020-10-12 17:45:34\",\"resultCommitTime\":\"2020-10-12 17:45:34\",\"approvalCompleteTime\":\"2020-10-12 17:45:34\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775145,\"checkNumber\":\"PD2020101201810-N\",\"createTime\":\"2020-10-12 17:45:27\",\"effectiveTime\":\"2020-10-12 17:45:29\",\"resultCommitTime\":\"2020-10-12 17:45:29\",\"approvalCompleteTime\":\"2020-10-12 17:45:29\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null},{\"id\":775144,\"checkNumber\":\"PD2020101201809-N\",\"createTime\":\"2020-10-12 17:45:22\",\"effectiveTime\":\"2020-10-12 17:45:23\",\"resultCommitTime\":\"2020-10-12 17:45:23\",\"approvalCompleteTime\":\"2020-10-12 17:45:23\",\"checkWay\":\"明盘\",\"operator\":\"dxxwangjing\",\"checker\":\"dxxwangjing\",\"printTime\":null,\"status\":\"已审批\",\"statusEnum\":\"APPROVED\",\"items\":null,\"warehouseId\":1396,\"warehouseName\":\"北京大猩猩望京店\",\"warehouseCode\":\"MRYXBJS-DXXWANGJINGDIAN\",\"isAreaLocationOn\":null,\"areaLocationOn\":null}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":1,\"endRow\":10,\"pages\":1311,\"prePage\":0,\"nextPage\":2,\"isFirstPage\":true,\"isLastPage\":false,\"hasPreviousPage\":false,\"hasNextPage\":true,\"navigatePages\":8,\"navigatepageNums\":[1,2,3,4,5,6,7,8],\"navigateFirstPage\":1,\"navigateLastPage\":8,\"firstPage\":1,\"lastPage\":8}}";
			response.content().retain().clear().writeBytes(json.getBytes());
			response.headers().remove("Content-Encoding");
			response.headers().set("Content-Length", json.getBytes().length);
			return response;
		}).mock();
	}
	
	private static Mock mockDemo7() {
		return new CatchRequest().evalNettyRequest(request -> {
			return request.uri().contains("/sms/internal/staticCheck/listByPage") && request.method() == HttpMethod.POST;
		}).rebuildNettyRequest(request-> {
			request.headers().set("HOST", "localhost:8888");
			request.headers().set("X-User-Id", "3101");
			request.setUri("/migrate/sms/internal/staticCheck/listByPage");
			System.out.println("redirect to localhost:8888");
			return request;
		}).rebuildNettyResponse(response -> {
			response.headers().set("Access-Control-Allow-Credentials", true);
			response.headers().set("Access-Control-Allow-Origin", "*");
			return response;
		}).mock();
	}
	
	private static Mock mockDemo8() {
		return new CatchRequest().evalNettyRequest(request -> {
			return request.uri().contains("/sms/internal/returnGoods/getStockNumByMaterialId") && request.method() == HttpMethod.GET;
		}).rebuildNettyRequest(request-> {
			request.headers().set("HOST", "localhost:8888");
			request.headers().set("X-User-Id", "3101");
			request.setUri(request.uri().replace("/sms/internal/returnGoods/getStockNumByMaterialId", "/migrate/sms/internal/returnGoods/getStockNumByMaterialId"));
			System.out.println("redirect to localhost:8888");
			return request;
		}).rebuildNettyResponse(response -> {
			response.headers().set("Access-Control-Allow-Credentials", true);
			response.headers().set("Access-Control-Allow-Origin", "*");
			return response;
		}).mock();
	}
	
	private static Mock mockDemo9() {
		return new CatchRequest().evalNettyRequest(request -> {
			return request.uri().contains("/sms/internal/stockTransfer/shelveMaterials") && request.method() == HttpMethod.POST;
		}).rebuildNettyRequest(request-> {
			request.headers().set("HOST", "localhost:8888");
			request.headers().set("X-User-Id", "3101");
			request.setUri("/migrate/sms/internal/stockTransfer/shelveMaterials");
			System.out.println("redirect to localhost:8888/shelveMaterials");
			return request;
		}).rebuildNettyResponse(response -> {
			response.headers().set("Access-Control-Allow-Credentials", true);
			response.headers().set("Access-Control-Allow-Origin", "*");
			return response;
		}).mock();
	}
	
	private static Mock mockDemo10() {
		return new CatchRequest().evalNettyRequest(request -> {
			return request.uri().contains("/sms/internal/stockTransfer/unshelveByArea") && request.method() == HttpMethod.POST;
		}).rebuildNettyRequest(request-> {
			request.headers().set("HOST", "localhost:8888");
			request.headers().set("X-User-Id", "3101");
			request.setUri("/migrate/sms/internal/stockTransfer/unshelveByArea");
			System.out.println("redirect to localhost:8888/unshelveByArea");
			return request;
		}).rebuildNettyResponse(response -> {
			response.headers().set("Access-Control-Allow-Credentials", true);
			response.headers().set("Access-Control-Allow-Origin", "*");
			return response;
		}).mock();
	}
}
