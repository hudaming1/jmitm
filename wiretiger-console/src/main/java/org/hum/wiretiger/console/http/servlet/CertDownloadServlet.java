package org.hum.wiretiger.console.http.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CertDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private byte[] CERT_ARRAY = null;
	
	public CertDownloadServlet() {
		try {
			File certFile = new File(CertDownloadServlet.class.getResource("/cert/client.cer").getFile());
			InputStream is = new FileInputStream(certFile);
			CERT_ARRAY = new byte[(int) certFile.length()];
			is.read(CERT_ARRAY);
			is.close();
		} catch (Exception ce) {
			log.error("read cert error", ce);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("content-disposition", "attachment;filename=Wiretiger.cer");
		ServletOutputStream out = resp.getOutputStream();
		out.write(CERT_ARRAY);
		out.flush();
		out.close();
	}
}
