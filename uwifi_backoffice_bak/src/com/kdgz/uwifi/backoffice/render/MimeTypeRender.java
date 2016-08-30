package com.kdgz.uwifi.backoffice.render;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import com.jfinal.render.Render;
import com.kdgz.uwifi.backoffice.util.StringUtil;

public class MimeTypeRender extends Render {

	private static final long serialVersionUID = 1L;
	
	private static String URL = "";
	private static boolean caseInsensitive = true;
	
	public MimeTypeRender() {
    }
	
	public MimeTypeRender(String url, boolean isCaseInsensitive) {
		URL = url;
		caseInsensitive = isCaseInsensitive;
    }
	@Override
	public void render() {
		ServletOutputStream sos = null;
        try {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("gif");
            sos = response.getOutputStream();
            StringUtil.generateQRCode(URL, sos);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (sos != null){
            	try {
                    sos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}

}
