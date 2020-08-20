package org.hum.wiretiger.console.helper;

import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.internal.StringUtil;

public class HttpMessageUtil {
	
	private static final String[] SUPPORT_PARSED_STRING = new String[] {
		"application/javascript", "text/css", "text/html", "application/x-javascript"
	};

    public static StringBuilder appendRequest(StringBuilder buf, DefaultHttpRequest req) {
		appendInitialLine(buf, req);
		appendHeaders(buf, req.headers());
		removeLastNewLine(buf);
        return buf;
    }

    public static StringBuilder appendRequest(StringBuilder buf, List<? extends DefaultHttpRequest> reqList) {
		for (DefaultHttpRequest req : reqList) {
			appendRequest(buf, req);
			buf.append(StringUtil.NEWLINE).append("====================").append(StringUtil.NEWLINE);
		}
        return buf;
    }
    
	public static StringBuilder appendResponse(StringBuilder buf, FullHttpResponse res) {
		appendInitialLine(buf, res);
		appendHeaders(buf, res.headers());
		removeLastNewLine(buf);
		return buf;
	}
    
    public static StringBuilder appendResponse(StringBuilder buf, List<? extends FullHttpResponse> resList) {
    	for (FullHttpResponse res : resList) {
    		appendResponse(buf, res);
    	    buf.append(StringUtil.NEWLINE).append("====================").append(StringUtil.NEWLINE);
    	}
    	return buf;
    }

    static StringBuilder appendFullRequest(StringBuilder buf, FullHttpRequest req) {
        appendFullCommon(buf, req);
        appendInitialLine(buf, req);
        appendHeaders(buf, req.headers());
        appendHeaders(buf, req.trailingHeaders());
        removeLastNewLine(buf);
        return buf;
    }

    static StringBuilder appendFullResponse(StringBuilder buf, FullHttpResponse res) {
        appendFullCommon(buf, res);
        appendInitialLine(buf, res);
        appendHeaders(buf, res.headers());
        appendHeaders(buf, res.trailingHeaders());
        removeLastNewLine(buf);
        return buf;
    }

    private static void appendFullCommon(StringBuilder buf, FullHttpMessage msg) {
        buf.append(StringUtil.simpleClassName(msg));
        buf.append("(decodeResult: ");
        buf.append(msg.decoderResult());
        buf.append(", version: ");
        buf.append(msg.protocolVersion());
        buf.append(", content: ");
        buf.append(msg.content());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendInitialLine(StringBuilder buf, HttpRequest req) {
        buf.append(req.method());
        buf.append(' ');
        buf.append(req.uri());
        buf.append(' ');
        buf.append(req.protocolVersion());
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendInitialLine(StringBuilder buf, HttpResponse res) {
        buf.append(res.protocolVersion());
        buf.append(' ');
        buf.append(res.status());
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendHeaders(StringBuilder buf, HttpHeaders headers) {
        for (Map.Entry<String, String> e: headers) {
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }

    private static void removeLastNewLine(StringBuilder buf) {
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
    }
    
	public static String unescape(String content) {
		if (content == null) {
			return "";
		}
		content = content.replaceAll("'", "&apos;");
		content = content.replaceAll("\"", "&quot;");
		content = content.replaceAll("\t", "&nbsp;&nbsp;");// 替换跳格
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		return content;
	}
    
    public static boolean isSupportParseString(String contentType) {
    	if (contentType == null) {
    		return false;
    	}
    	for (String supportParsedString : SUPPORT_PARSED_STRING) {
    		if (contentType.toLowerCase().contains(supportParsedString)) {
    			return true;
    		}
    	}
    	return false;
    }

    private HttpMessageUtil() { }
}
