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
		wtBuilder.parseHttps(true);
		wtBuilder.proxyPort(52008).threads(400);
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
				,mockSupportOptionsMethod()
				,mockAllInternelRequestForwardLocalhost()
//				,mockAllReplenishmentRequestForwardLocalhost()
//				,mockAllStockRequestForwardLocalhost()
				,mockTest()
//				,mockOffShelfQuery()
//				,mockOffShelfSubmit()
				);
		
		wtBuilder.build().start();
	}
	
//	private static final String USER_ID = "15412";
	private static final String USER_ID = "3103";

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
			return "/api/menu/personal".equals(request.uri());
		}).rebuildResponse(response -> {
			log.info("mock personal menu...");
			// 注入的JS代码
			String json = "{\"data\":{\"sentinel\":[{\"id\":\"397\",\"parentId\":\"0\",\"title\":\"流控首1页\",\"orderNumber\":1,\"path\":\"/sentinel\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"398\",\"parentId\":\"0\",\"title\":\"实时监控\",\"orderNumber\":2,\"path\":\"/sentinel/dashboard\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"401\",\"parentId\":\"0\",\"title\":\"资源列表\",\"orderNumber\":3,\"path\":\"/sentinel/resources\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"400\",\"parentId\":\"0\",\"title\":\"流控规则\",\"orderNumber\":4,\"path\":\"/sentinel/flows\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"403\",\"parentId\":\"0\",\"title\":\"降级规则\",\"orderNumber\":5,\"path\":\"/sentinel/degrades\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"404\",\"parentId\":\"0\",\"title\":\"热点规则\",\"orderNumber\":6,\"path\":\"/sentinel/params\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"405\",\"parentId\":\"0\",\"title\":\"授权规则\",\"orderNumber\":7,\"path\":\"/sentinel/authorities\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"406\",\"parentId\":\"0\",\"title\":\"系统规则\",\"orderNumber\":8,\"path\":\"/sentinel/systems\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"407\",\"parentId\":\"0\",\"title\":\"机器列表\",\"orderNumber\":9,\"path\":\"/sentinel/machines\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"408\",\"parentId\":\"0\",\"title\":\"规则生效查看\",\"orderNumber\":10,\"path\":\"/sentinel/rules\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"448\",\"parentId\":\"0\",\"title\":\"应用规则开关\",\"orderNumber\":900,\"path\":\"/sentinel/appManage\",\"type\":1,\"children\":[],\"hidden\":true}],\"chaos\":[{\"id\":\"445\",\"parentId\":\"442\",\"title\":\"演练计划\",\"orderNumber\":100,\"path\":\"/chaosManage/planList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"443\",\"parentId\":\"442\",\"title\":\"Agent列表\",\"orderNumber\":300,\"path\":\"/chaosManage/agentList\",\"type\":1,\"children\":[],\"hidden\":true}],\"log\":[{\"id\":\"411\",\"parentId\":\"0\",\"title\":\"应用日志\",\"orderNumber\":0,\"path\":\"/appLog\",\"type\":0,\"children\":[{\"id\":\"412\",\"parentId\":\"411\",\"title\":\"日志查询\",\"orderNumber\":0,\"path\":\"/logSystem\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"413\",\"parentId\":\"411\",\"title\":\"实时日志(Beta)\",\"orderNumber\":1,\"path\":\"/realtimeLog\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"414\",\"parentId\":\"0\",\"title\":\"网关/Nginx日志\",\"orderNumber\":1,\"path\":\"/nginxLog\",\"type\":0,\"children\":[{\"id\":\"415\",\"parentId\":\"414\",\"title\":\"日志查询\",\"orderNumber\":0,\"path\":\"/nginxLog\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"416\",\"parentId\":\"0\",\"title\":\"移动端日志\",\"orderNumber\":2,\"path\":\"/mobileLog\",\"type\":0,\"children\":[],\"hidden\":true},{\"id\":\"346\",\"parentId\":\"0\",\"title\":\"模块管理\",\"orderNumber\":3,\"path\":\"/moduleManage\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"347\",\"parentId\":\"0\",\"title\":\"链路管理\",\"orderNumber\":4,\"path\":\"/traceManage\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"380\",\"parentId\":\"0\",\"title\":\"下载记录\",\"orderNumber\":7,\"path\":\"/downloadHistory\",\"type\":1,\"children\":[],\"hidden\":true}],\"appcenter\":[{\"id\":\"187\",\"parentId\":\"0\",\"title\":\"应用管理中心\",\"orderNumber\":10,\"path\":\"/home\",\"type\":0,\"children\":[],\"hidden\":true},{\"id\":\"193\",\"parentId\":\"0\",\"title\":\"脚手架111\",\"orderNumber\":30,\"path\":\"\",\"type\":0,\"children\":[{\"id\":\"207\",\"parentId\":\"193\",\"title\":\"初始化工程\",\"orderNumber\":1,\"path\":\"/projectManage\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"194\",\"parentId\":\"193\",\"title\":\"数据库相关代码生成\",\"orderNumber\":13,\"path\":\"/dbManage\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"106\",\"parentId\":\"0\",\"title\":\"机器管理中心\",\"orderNumber\":99,\"path\":\"/machineManage\",\"type\":0,\"children\":[],\"hidden\":true},{\"id\":\"3\",\"parentId\":\"0\",\"title\":\"系统管理\",\"orderNumber\":100,\"path\":\"\",\"type\":0,\"children\":[{\"id\":\"6\",\"parentId\":\"3\",\"title\":\"用户管理\",\"orderNumber\":0,\"path\":\"/userManage\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"7\",\"parentId\":\"3\",\"title\":\"角色管理\",\"orderNumber\":1,\"path\":\"/roleManage\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"2\",\"parentId\":\"3\",\"title\":\"系统菜单\",\"orderNumber\":2,\"path\":\"/systemMenu\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"73\",\"parentId\":\"3\",\"title\":\"部门管理\",\"orderNumber\":3,\"path\":\"/departManage\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"367\",\"parentId\":\"0\",\"title\":\"修改应用部门\",\"orderNumber\":100,\"path\":\"/appcenter-updatedept\",\"type\":2,\"children\":[],\"hidden\":true}],\"dubboAdmin\":[{\"id\":\"335\",\"parentId\":\"0\",\"title\":\"服务治理\",\"orderNumber\":1,\"path\":\"/serviceGovernance\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"334\",\"parentId\":\"0\",\"title\":\"zookeeper\",\"orderNumber\":2,\"path\":\"/zookeeper\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"337\",\"parentId\":\"0\",\"title\":\"注册中心\",\"orderNumber\":3,\"path\":\"/register\",\"type\":1,\"children\":[],\"hidden\":true}],\"configcenter\":[{\"id\":\"341\",\"parentId\":\"0\",\"title\":\"创建配置\",\"orderNumber\":2,\"path\":\"/createconfig\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"340\",\"parentId\":\"0\",\"title\":\"审核列表\",\"orderNumber\":3,\"path\":\"/reviewlist\",\"type\":1,\"children\":[],\"hidden\":true}],\"esjob\":[{\"id\":\"357\",\"parentId\":\"0\",\"title\":\"全局配置\",\"orderNumber\":1,\"path\":null,\"type\":0,\"children\":[{\"id\":\"361\",\"parentId\":\"357\",\"title\":\"注册中心配置\",\"orderNumber\":1,\"path\":\"/globalConfiguration\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"360\",\"parentId\":\"357\",\"title\":\"事件追踪数据源配置\",\"orderNumber\":2,\"path\":\"/eventToData\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"358\",\"parentId\":\"0\",\"title\":\"作业操作\",\"orderNumber\":2,\"path\":null,\"type\":0,\"children\":[{\"id\":\"362\",\"parentId\":\"358\",\"title\":\"作业维度\",\"orderNumber\":1,\"path\":\"/dimension\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"363\",\"parentId\":\"358\",\"title\":\"服务器维度\",\"orderNumber\":2,\"path\":\"/serverDimension\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"359\",\"parentId\":\"0\",\"title\":\"作业历史\",\"orderNumber\":3,\"path\":null,\"type\":0,\"children\":[{\"id\":\"364\",\"parentId\":\"359\",\"title\":\"历史轨迹\",\"orderNumber\":1,\"path\":\"/historyTrace\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"365\",\"parentId\":\"359\",\"title\":\"历史状态\",\"orderNumber\":2,\"path\":\"/historyState\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true}],\"pressure\":[{\"id\":\"355\",\"parentId\":\"0\",\"title\":\"机器管理\",\"orderNumber\":1,\"path\":\"/machine\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"354\",\"parentId\":\"0\",\"title\":\"流量录制\",\"orderNumber\":2,\"path\":\"/record\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"353\",\"parentId\":\"0\",\"title\":\"流量回放\",\"orderNumber\":3,\"path\":\"/playback\",\"type\":1,\"children\":[],\"hidden\":true}],\"RocketMq\":[{\"id\":\"309\",\"parentId\":\"0\",\"title\":\"Dashboard\",\"orderNumber\":1,\"path\":\"/dashboard\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"308\",\"parentId\":\"0\",\"title\":\"Cluster\",\"orderNumber\":3,\"path\":\"/cluster\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"307\",\"parentId\":\"0\",\"title\":\"Topic\",\"orderNumber\":4,\"path\":\"/topic\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"306\",\"parentId\":\"0\",\"title\":\"Consumer\",\"orderNumber\":5,\"path\":\"/consumer\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"305\",\"parentId\":\"0\",\"title\":\"Producer\",\"orderNumber\":6,\"path\":\"/producer\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"304\",\"parentId\":\"0\",\"title\":\"Message\",\"orderNumber\":7,\"path\":\"/message\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"312\",\"parentId\":\"0\",\"title\":\"ClusterList\",\"orderNumber\":10,\"path\":\"/clusterList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"451\",\"parentId\":\"0\",\"title\":\"DelayQueue\",\"orderNumber\":11,\"path\":\"/RocketMq/delayQueueLIst\",\"type\":1,\"children\":[],\"hidden\":true}],\"abtest\":[{\"id\":\"352\",\"parentId\":\"0\",\"title\":\"试验列表\",\"orderNumber\":1,\"path\":\"/abtlist\",\"type\":1,\"children\":[],\"hidden\":true}],\"es_bg\":[{\"id\":\"418\",\"parentId\":\"0\",\"title\":\"管理平台首页\",\"orderNumber\":1,\"path\":\"/es_manage/home\",\"type\":null,\"children\":[],\"hidden\":true},{\"id\":\"422\",\"parentId\":\"0\",\"title\":\"查询管理\",\"orderNumber\":20,\"path\":\"/es_manage/searchForm\",\"type\":0,\"children\":[{\"id\":\"424\",\"parentId\":\"422\",\"title\":\"数据查询\",\"orderNumber\":1,\"path\":\"/es_manage/searchForm\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"423\",\"parentId\":\"422\",\"title\":\"下载列表\",\"orderNumber\":2,\"path\":\"/es_manage/downloadList\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"384\",\"parentId\":\"0\",\"title\":\"集群管理\",\"orderNumber\":30,\"path\":\"/esClutser\",\"type\":0,\"children\":[],\"hidden\":true},{\"id\":\"385\",\"parentId\":\"0\",\"title\":\"索引管理\",\"orderNumber\":40,\"path\":\"/\",\"type\":0,\"children\":[{\"id\":\"388\",\"parentId\":\"385\",\"title\":\"索引列表\",\"orderNumber\":1,\"path\":\"/esIndex\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"389\",\"parentId\":\"385\",\"title\":\"索引别名映射\",\"orderNumber\":2,\"path\":\"/esIndexAlias\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"386\",\"parentId\":\"0\",\"title\":\"模版管理\",\"orderNumber\":50,\"path\":\"/\",\"type\":1,\"children\":[{\"id\":\"391\",\"parentId\":\"386\",\"title\":\"模板列表\",\"orderNumber\":1,\"path\":\"/esTemplate\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"390\",\"parentId\":\"386\",\"title\":\"索引模板映射\",\"orderNumber\":2,\"path\":\"/esTemplateIndex\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"420\",\"parentId\":\"0\",\"title\":\"网格数据迁移\",\"orderNumber\":60,\"path\":\"/es_manage/dataMigrateList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"419\",\"parentId\":\"0\",\"title\":\"备份管理\",\"orderNumber\":70,\"path\":\"/es_manage/backupManage\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"383\",\"parentId\":\"0\",\"title\":\"定时任务管理\",\"orderNumber\":80,\"path\":\"/scheduledTask\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"425\",\"parentId\":\"0\",\"title\":\"权限管理\",\"orderNumber\":90,\"path\":\"/es_manage/permissionList\",\"type\":0,\"children\":[{\"id\":\"427\",\"parentId\":\"425\",\"title\":\"权限列表\",\"orderNumber\":1,\"path\":\"/es_manage/permissionList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"426\",\"parentId\":\"425\",\"title\":\"权限集成\",\"orderNumber\":2,\"path\":\"/es_manage/integrationPermissionList\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"387\",\"parentId\":\"0\",\"title\":\"日志管理\",\"orderNumber\":100,\"path\":\"/logManage\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"421\",\"parentId\":\"0\",\"title\":\"审计日志\",\"orderNumber\":110,\"path\":\"/es_manage/auditQuery\",\"type\":1,\"children\":[],\"hidden\":true}],\"es_gateway\":[{\"id\":\"432\",\"parentId\":\"0\",\"title\":\"网关主页\",\"orderNumber\":10,\"path\":\"/es_gateway/home\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"433\",\"parentId\":\"0\",\"title\":\"模板管理\",\"orderNumber\":20,\"path\":\"/es_gateway/templateList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"434\",\"parentId\":\"0\",\"title\":\"集群同步\",\"orderNumber\":30,\"path\":\"/es_gateway/clusterSync\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"435\",\"parentId\":\"0\",\"title\":\"集群映射\",\"orderNumber\":40,\"path\":\"/es_gateway/clusterMapping\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"431\",\"parentId\":\"0\",\"title\":\"ES客户端\",\"orderNumber\":50,\"path\":\"/es_gateway/esClient\",\"type\":1,\"children\":[],\"hidden\":true}],\"alert\":[{\"id\":\"331\",\"parentId\":\"0\",\"title\":\"通知管理\",\"orderNumber\":1,\"path\":\"/noticeManage\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"330\",\"parentId\":\"0\",\"title\":\"告警用户\",\"orderNumber\":2,\"path\":\"/alarmUser\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"381\",\"parentId\":\"0\",\"title\":\"告警详情查询\",\"orderNumber\":4,\"path\":\"/alarmDetailList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"392\",\"parentId\":\"0\",\"title\":\"告警统计(new)\",\"orderNumber\":10,\"path\":\"/alarmStatisticalNew\",\"type\":0,\"children\":[],\"hidden\":true},{\"id\":\"329\",\"parentId\":\"0\",\"title\":\"告警统计\",\"orderNumber\":11,\"path\":\"/alarmStatistical\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"394\",\"parentId\":\"0\",\"title\":\"告警配置\",\"orderNumber\":14,\"path\":\"/alarmSetting\",\"type\":1,\"children\":[],\"hidden\":true}],\"redisKey\":[{\"id\":\"351\",\"parentId\":\"0\",\"title\":\"shardingRedis\",\"orderNumber\":1,\"path\":\"/redisManage/shardingRedis\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"436\",\"parentId\":\"0\",\"title\":\"mysql\",\"orderNumber\":2,\"path\":\"/redisManage/mysqlList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"437\",\"parentId\":\"0\",\"title\":\"redisCluster\",\"orderNumber\":3,\"path\":\"/redisManage/redisCluster\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"438\",\"parentId\":\"0\",\"title\":\"missJedis\",\"orderNumber\":4,\"path\":\"/redisManage/missJedis\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"439\",\"parentId\":\"0\",\"title\":\"tair\",\"orderNumber\":5,\"path\":\"/redisManage/tairList\",\"type\":null,\"children\":[],\"hidden\":true},{\"id\":\"440\",\"parentId\":\"0\",\"title\":\"脱敏组件\",\"orderNumber\":6,\"path\":\"/redisManage/cryptoList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"441\",\"parentId\":\"0\",\"title\":\"动态线程池\",\"orderNumber\":7,\"path\":\"/redisManage/threadPoolList\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"395\",\"parentId\":\"0\",\"title\":\"分布式id\",\"orderNumber\":8,\"path\":\"/redisManage/idgenerator\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"368\",\"parentId\":\"0\",\"title\":\"修改\",\"orderNumber\":9,\"path\":\"/redisKey-update\",\"type\":2,\"children\":[],\"hidden\":true}],\"jiaotu\":[{\"id\":\"372\",\"parentId\":\"0\",\"title\":\"DASHBOARD\",\"orderNumber\":1,\"path\":\"/pepper_dashboard\",\"type\":0,\"children\":[],\"hidden\":true},{\"id\":\"373\",\"parentId\":\"0\",\"title\":\"API GATEWAY\",\"orderNumber\":2,\"path\":\"/\",\"type\":0,\"children\":[{\"id\":\"375\",\"parentId\":\"373\",\"title\":\"INFO\",\"orderNumber\":1,\"path\":\"/pepper_info\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"376\",\"parentId\":\"373\",\"title\":\"SERVICES\",\"orderNumber\":2,\"path\":\"/pepper_services\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"377\",\"parentId\":\"373\",\"title\":\"ROUTES\",\"orderNumber\":3,\"path\":\"/pepper_routes\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"378\",\"parentId\":\"373\",\"title\":\"PLUGINS\",\"orderNumber\":4,\"path\":\"/pepper_plugins\",\"type\":1,\"children\":[],\"hidden\":true},{\"id\":\"379\",\"parentId\":\"373\",\"title\":\"UPSTREAMS\",\"orderNumber\":5,\"path\":\"/pepper_upstreams\",\"type\":1,\"children\":[],\"hidden\":true}],\"hidden\":true},{\"id\":\"374\",\"parentId\":\"0\",\"title\":\"APPLICATION\",\"orderNumber\":3,\"path\":\"/\",\"type\":0,\"children\":[],\"hidden\":true}]},\"code\":0,\"msg\":\"Success!\",\"success\":true}";
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
