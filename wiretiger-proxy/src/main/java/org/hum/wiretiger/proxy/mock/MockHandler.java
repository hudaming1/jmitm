package org.hum.wiretiger.proxy.mock;

import java.util.List;

import org.hum.wiretiger.proxy.mock.enumtype.PictureOp;
import org.hum.wiretiger.proxy.mock.enumtype.RebuildOp;
import org.hum.wiretiger.proxy.mock.picture.InterceptorPicture;
import org.hum.wiretiger.proxy.mock.picture.RequestHeaderPicture;
import org.hum.wiretiger.proxy.mock.picture.RequestPicture;
import org.hum.wiretiger.proxy.mock.picture.RequestUriPicture;
import org.hum.wiretiger.proxy.mock.picture.StringEvalFn;
import org.hum.wiretiger.proxy.mock.rebuild.ReplaceValue;
import org.hum.wiretiger.proxy.mock.rebuild.RequestBodyRebuilder;
import org.hum.wiretiger.proxy.mock.rebuild.RequestHeaderRebuilder;
import org.hum.wiretiger.proxy.mock.rebuild.RequestRebuilder;
import org.hum.wiretiger.proxy.mock.rebuild.RequestUriRebuilder;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public class MockHandler {
	
	private final String WT_MOCK_SIGN = "WtMock";
	private List<Mock> mockList;

	public void mock(HttpRequest request, FullHttpResponse resp) {
		for (Mock mock : mockList) {
			if (request.headers().contains(WT_MOCK_SIGN) || isHit(mock.getPicture(), resp)) {
				rebuild(mock.getInterceptorRebuilder(), resp);
			}
		}
	}

	public void mock(HttpRequest request) {
		for (Mock mock : mockList) {
			if (isHit(mock.getPicture(), request)) {
				rebuild(mock.getInterceptorRebuilder(), request);
				request.headers().set(WT_MOCK_SIGN, mock.getId());
			}
		}
	}

	private boolean isHit(InterceptorPicture picture, HttpRequest request) {
		if (picture.getRequestPicture() == null) {
			return false;
		}
		
		RequestPicture reqPic = picture.getRequestPicture();
		RequestUriPicture uriPicture = reqPic.getUriPicture();
		List<RequestHeaderPicture> headerPictureList = reqPic.getHeaderPicture();
		// checkpoint -> 1.uri
		if (uriPicture != null) {
			if (uriPicture.getOp() == PictureOp.Equals && !uriPicture.getValue().equals(request.uri())) {
				return false;
			} else if (uriPicture.getOp() == PictureOp.Like && !request.uri().contains(uriPicture.getValue().toString())) {
				return false;
			} else if (uriPicture.getOp() == PictureOp.Prefix && !request.uri().startsWith(uriPicture.getValue().toString())) {
				return false;
			} else if (uriPicture.getOp() == PictureOp.Eval) {
				StringEvalFn evalFn = (StringEvalFn) uriPicture.getValue();
				if (!evalFn.isHit(request.uri())) {
					return false;
				}
			}
		}
		// checkpoint -> 2.header
		if (headerPictureList != null && !headerPictureList.isEmpty()) {
			for (RequestHeaderPicture headerPic : headerPictureList) {
				if (request.headers().get(headerPic.getHeader()) == null) {
					continue;
				}
				if (headerPic.getOp() == PictureOp.Equals && !headerPic.getValue().equals(request.headers().get(headerPic.getHeader()))) {
					return false;
				} else if (headerPic.getOp() == PictureOp.Like && !request.headers().get(headerPic.getHeader()).contains(headerPic.getValue().toString())) {
					return false;
				} else if (headerPic.getOp() == PictureOp.Prefix && !request.headers().get(headerPic.getHeader()).startsWith(headerPic.getValue().toString())) {
					return false;
				} else if (headerPic.getOp() == PictureOp.Eval) {
					StringEvalFn evalFn = (StringEvalFn) uriPicture.getValue();
					if (!evalFn.isHit(request.uri())) {
						return false;
					}
				}
			}
		}
		
		// checkpoint -> 3.body
		// TODO
		
		return true;
	}

	private boolean isHit(InterceptorPicture picture, FullHttpResponse resp) {
		// TODO
		
		// checkpoint -> 1.header
		
		// checkpoint -> 2.body
		
		return false;
	}

	private void rebuild(InterceptorRebuilder interceptorRebuilder, HttpRequest request) {
		// TODO Auto-generated method stub
		if (interceptorRebuilder.getRequestRebuilder() == null) {
			return ;
		}
		
		RequestRebuilder requestRebuilder = interceptorRebuilder.getRequestRebuilder();
		RequestUriRebuilder uriRebuilder = requestRebuilder.getUriRebuilder();
		RequestHeaderRebuilder headerRebuilder = requestRebuilder.getHeaderRebuilder();
		RequestBodyRebuilder bodyRebuilder = requestRebuilder.getBodyRebuilder();
		
		// rebuild-point -> 1.uri
		if (uriRebuilder != null) {
			if (uriRebuilder.getOp() == RebuildOp.Append) {
				request.setUri(request.uri() + uriRebuilder.getValue());
			} else if (uriRebuilder.getOp() == RebuildOp.Update) {
				request.setUri(uriRebuilder.getValue().toString());
			} else if (uriRebuilder.getOp() == RebuildOp.Replace) {
				ReplaceValue replaceVal = (ReplaceValue) uriRebuilder.getValue();
			}
		}
		
		// rebuild-point -> 2.header
		if (headerRebuilder != null) {
			
		}
		
		// rebuild-point -> 3.body		
		if (bodyRebuilder != null) {
			
		}
	}

	private void rebuild(InterceptorRebuilder interceptorRebuilder, FullHttpResponse resp) {
		// TODO Auto-generated method stub
		// rebuild-point -> 1.header
		// rebuild-point -> 2.body
	}
}
