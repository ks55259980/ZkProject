package com.wemarklinks.common;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

	public static String requestInfo(HttpServletRequest request) {
		StringBuilder params = new StringBuilder();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			String value = request.getParameter(name);
			params.append(String.format("&%s=%s", name, value));
		}
		if (params.length() > 0) {
			params = params.replace(0, 1, "?");
		}
		return String.format("%s %s%s qs=%s", request.getMethod(), request.getRequestURI(), params,  request.getQueryString());
	}

	public static Map<String,String> handleMap(Map<String,String[]> map){
		Map<String,String> rst = new HashMap<String,String>();
		Set<String> keys = map.keySet();
		for(String str : keys){
			String[] strs = null;
			if((strs = map.get(str))!=null);
			rst.put(str, strs[0]);
		}
		return rst;
	}
	
}
