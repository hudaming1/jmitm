package org.hum.wiretiger.starter;

import java.io.File;
import java.io.FileInputStream;

import org.hum.wiretiger.console.common.listener.Console4WsListener;
import org.hum.wiretiger.console.http.ConsoleServer;
import org.hum.wiretiger.console.http.config.WtConsoleConfig;
import org.hum.wiretiger.console.websocket.WebSocketServer;
import org.hum.wiretiger.proxy.config.WtCoreConfig;
import org.hum.wiretiger.proxy.mock.Mock;
import org.hum.wiretiger.proxy.mock.CatchRequest;
import org.hum.wiretiger.proxy.mock.CatchResponse;
import org.hum.wiretiger.proxy.server.WtServerBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WiretigerServerRun2 {
	
	public static void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// config
				WtCoreConfig config = new WtCoreConfig();
				config.setPort(52007);
				config.setThreads(Runtime.getRuntime().availableProcessors());
				config.setDebug(false);
				
				// DEMO1：将「wiretiger.com」重定向到「localhost:8080」，等效于配置host:   wiretiger.com    127.0.0.1:8080
				config.addMock(mockDemo1());
				
				// DEMO2：修改了百度首页的Logo，打开https://www.baidu.com，会发现首页Logo变成了360So
				config.addMock(mockDemo2());

				// DEMO3：修改百度活动页的Logo，读取本地GoogleLogo文件
				config.addMock(mockDemo3());
				
				// DEMO4：拦截所有响应，对响应打标记
				config.addMock(mockDemo4());

				// DEMO5：MockResponse
				config.addMock(mockDemo5());
				
				WtServerBuilder.init(config).addEventListener(new Console4WsListener()).build().start();
			}

		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					WtConsoleConfig consoleConfig = new WtConsoleConfig();
					consoleConfig.setHttpPort(8080);
					consoleConfig.setWebRoot(ConsoleServer.class.getResource("/webroot").getFile());
					consoleConfig.setWebXmlPath(ConsoleServer.class.getResource("/webroot/WEB-INF/web.xml").getFile());
					ConsoleServer.startJetty(consoleConfig);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();


		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new WebSocketServer(52996).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) throws Exception {
		WiretigerServerRun2.start();
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
			return "www.baidu.com".equals(request.headers().get("Host").split(":")[0]) && request.uri().contains("dong_30a61f45c8d4634ca14da8829046271f"); 
		}).rebuildResponse(response -> {
			try {
				FileInputStream fileInputStream = new FileInputStream(new File(WiretigerServerRun2.class.getResource("/mock/google.png").getFile()));
				byte[] bytes = new byte[fileInputStream.available()];
				fileInputStream.read(bytes);
				fileInputStream.close();
				log.info("mock google logo=" + response);
				response.headers().add("wiretiger_mock", "mock google_logo");
				response.content().clear();
				response.content().writeBytes(bytes);
				response.headers().set("Content-Length", bytes.length);
			}catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}).mock();
	}

	private static Mock mockDemo2() {
		return new CatchRequest().eval(request -> {
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
			return "wiretiger.com".equals(request.headers().get("Host").split(":")[0]);
		}).rebuildRequest(request -> {
			request.headers().set("Host", "localhost:8080");
			return request;
		}).mock();
	}

	private static Mock mockDemo5() {
		return new CatchRequest().eval(request -> {
			return request.uri().contains("/sms/replenishment/transfer/listTransferByPage");
		}).rebuildResponse(response -> {
			String json = "{\"code\":200,\"message\":\"操作成功\",\"result\":{\"total\":241,\"list\":[{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"MockWarehouse\",\"pageNum\":1,\"pageSize\":10,\"id\":1322,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00031-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 20:03:34\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":-1,\"warehouseId\":530,\"warehouseOutTime\":null,\"warehouseInTime\":null,\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":1,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":3101,\"cancelTime\":\"2020-09-18 20:03:39\",\"cancelVerifyUserId\":3101,\"cancelVerifyTime\":\"2020-09-18 20:03:45\",\"cancelVerifyComment\":\"\",\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"每日优鲜武汉\",\"transferStatusStr\":\"已取消\",\"createTypeStr\":\"手动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"DASI\",\"cancelVerifyUserName\":\"DASI\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":false},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"YXTJW\",\"pageNum\":1,\"pageSize\":10,\"id\":1321,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00029-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 20:02:50\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":-1,\"warehouseId\":530,\"warehouseOutTime\":null,\"warehouseInTime\":null,\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":1,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":3101,\"cancelTime\":\"2020-09-18 20:03:19\",\"cancelVerifyUserId\":3101,\"cancelVerifyTime\":\"2020-09-18 20:03:32\",\"cancelVerifyComment\":\"\",\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"优鲜新天津外大仓\",\"transferStatusStr\":\"已取消\",\"createTypeStr\":\"手动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"DASI\",\"cancelVerifyUserName\":\"DASI\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":true},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"MockWarehouse\",\"pageNum\":1,\"pageSize\":10,\"id\":1320,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00027-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 19:59:40\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":2,\"warehouseId\":530,\"warehouseOutTime\":\"2020-09-18 19:59:56\",\"warehouseInTime\":\"2020-09-18 20:00:28\",\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":0,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":null,\"cancelTime\":null,\"cancelVerifyUserId\":null,\"cancelVerifyTime\":null,\"cancelVerifyComment\":null,\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"每日优鲜武汉\",\"transferStatusStr\":\"主仓已入库\",\"createTypeStr\":\"自动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"\",\"cancelVerifyUserName\":\"\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"DASI\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":false},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"MockWarehouse\",\"pageNum\":1,\"pageSize\":10,\"id\":1319,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00025-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 19:58:03\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":-1,\"warehouseId\":530,\"warehouseOutTime\":null,\"warehouseInTime\":null,\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":0,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":3101,\"cancelTime\":\"2020-09-18 19:58:36\",\"cancelVerifyUserId\":3101,\"cancelVerifyTime\":\"2020-09-18 19:58:49\",\"cancelVerifyComment\":\"\",\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"每日优鲜武汉\",\"transferStatusStr\":\"已取消\",\"createTypeStr\":\"自动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"DASI\",\"cancelVerifyUserName\":\"DASI\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":false},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"MRYXBJS\",\"pageNum\":1,\"pageSize\":10,\"id\":1318,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00023-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 19:55:03\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":-1,\"warehouseId\":530,\"warehouseOutTime\":null,\"warehouseInTime\":null,\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":1,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":3101,\"cancelTime\":\"2020-09-18 19:56:37\",\"cancelVerifyUserId\":null,\"cancelVerifyTime\":null,\"cancelVerifyComment\":null,\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"每日优鲜北京北\",\"transferStatusStr\":\"已取消\",\"createTypeStr\":\"手动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"DASI\",\"cancelVerifyUserName\":\"\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":false},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"YXTJW\",\"pageNum\":1,\"pageSize\":10,\"id\":1317,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00021-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 19:54:29\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":-1,\"warehouseId\":530,\"warehouseOutTime\":null,\"warehouseInTime\":null,\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":1,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":3101,\"cancelTime\":\"2020-09-18 19:54:50\",\"cancelVerifyUserId\":null,\"cancelVerifyTime\":null,\"cancelVerifyComment\":null,\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"优鲜新天津外大仓\",\"transferStatusStr\":\"已取消\",\"createTypeStr\":\"手动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"DASI\",\"cancelVerifyUserName\":\"\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":true},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"MockWarehouse\",\"pageNum\":1,\"pageSize\":10,\"id\":1316,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00019-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 19:06:01\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":4,\"warehouseId\":530,\"warehouseOutTime\":null,\"warehouseInTime\":null,\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":1,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":null,\"cancelTime\":null,\"cancelVerifyUserId\":null,\"cancelVerifyTime\":null,\"cancelVerifyComment\":null,\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"每日优鲜武汉\",\"transferStatusStr\":\"待调出\",\"createTypeStr\":\"手动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"\",\"cancelVerifyUserName\":\"\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":false},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"MRYXBJS\",\"pageNum\":1,\"pageSize\":10,\"id\":1315,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00017-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 19:01:54\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":-1,\"warehouseId\":530,\"warehouseOutTime\":null,\"warehouseInTime\":null,\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":1,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":3101,\"cancelTime\":\"2020-09-18 19:05:30\",\"cancelVerifyUserId\":null,\"cancelVerifyTime\":null,\"cancelVerifyComment\":null,\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"每日优鲜北京北\",\"transferStatusStr\":\"已取消\",\"createTypeStr\":\"手动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"DASI\",\"cancelVerifyUserName\":\"\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":false},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"MRYXBJS\",\"pageNum\":1,\"pageSize\":10,\"id\":1314,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00015-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 18:58:23\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":-1,\"warehouseId\":530,\"warehouseOutTime\":null,\"warehouseInTime\":null,\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":1,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":null,\"cancelUserId\":3101,\"cancelTime\":\"2020-09-18 18:59:49\",\"cancelVerifyUserId\":null,\"cancelVerifyTime\":null,\"cancelVerifyComment\":null,\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"每日优鲜北京北\",\"transferStatusStr\":\"已取消\",\"createTypeStr\":\"手动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"DASI\",\"cancelVerifyUserName\":\"\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":false},{\"operateSubWarehouseId\":null,\"mainWarehouseCode\":\"YXTJW\",\"pageNum\":1,\"pageSize\":10,\"id\":1313,\"transferRecordNo\":\"DB-20200918-MRYXTJW-00013-N\",\"createUserId\":3101,\"createTime\":\"2020-09-18 16:34:19\",\"updateUserId\":null,\"updateTime\":null,\"transferStatus\":3,\"warehouseId\":530,\"warehouseOutTime\":\"2020-09-18 16:34:40\",\"warehouseInTime\":\"2020-09-18 16:34:58\",\"warehouseOutStatus\":null,\"warehouseInStatus\":null,\"syncAtpOutStatus\":null,\"syncAtpInStatus\":null,\"distributionTimes\":null,\"createType\":0,\"syncCancelStatus\":null,\"inboundUserId\":null,\"inboundUser\":null,\"rejectUserId\":null,\"rejectUser\":null,\"rejectTime\":\"2020-09-18 16:34:58\",\"cancelUserId\":null,\"cancelTime\":null,\"cancelVerifyUserId\":null,\"cancelVerifyTime\":null,\"cancelVerifyComment\":null,\"rejectReason\":\"\",\"relationReturnBillCode\":\"\",\"shardingId\":null,\"materialId\":null,\"materialNum\":null,\"materialName\":null,\"militaryRegionCode\":\"JQEEE\",\"militaryRegionName\":\"Mock军区\",\"skuCode\":null,\"creatorName\":\"DASI\",\"warehouseName\":\"天津每日优鲜大寺站(平行)\",\"mainMarehouseName\":\"MRYXTJW\",\"mainWarehouseName\":\"优鲜新天津外大仓\",\"transferStatusStr\":\"主仓拒绝入库\",\"createTypeStr\":\"自动\",\"subWareHouseCode\":\"MRYXTJW-DASI\",\"cancelUserName\":\"\",\"cancelVerifyUserName\":\"\",\"transferReasonTypeStr\":\"\",\"warehouseOutUserName\":\"DASI\",\"createSource\":0,\"createSourceStr\":\"PC\",\"batchCode\":\"\",\"relationBillNo\":\"\",\"wmsFlag\":true}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":1,\"endRow\":10,\"pages\":25,\"prePage\":0,\"nextPage\":2,\"isFirstPage\":true,\"isLastPage\":false,\"hasPreviousPage\":false,\"hasNextPage\":true,\"navigatePages\":8,\"navigatepageNums\":[1,2,3,4,5,6,7,8],\"navigateFirstPage\":1,\"navigateLastPage\":8,\"firstPage\":1,\"lastPage\":8}}";
			// XXX retain
			response.content().retain();
			response.content().clear();
			response.content().writeBytes(json.getBytes());
			response.headers().set("Content-Length", json.getBytes().length);
			return response;
		}).mock();
	}

}
