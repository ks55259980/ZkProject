package com.wemarklinks.util;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtil {

    private static final Logger log = LoggerFactory.getLogger(RequestUtil.class);

    public static String getXmlFromRequest(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            request.getReader();
            br = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("cannot get encrypted xml from wechat server, detail: {}", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
