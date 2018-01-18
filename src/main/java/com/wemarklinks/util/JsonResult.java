package com.wemarklinks.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class JsonResult {

	public static Map<String, Object> RetJsonPage(int code, String msg, Object data, String[] field) {

		Map<String, Object> RetMap = new HashMap<>();
		RetMap.put("code", code);
		RetMap.put("msg", msg);
//		if (null == data) {
			RetMap.put("data", "");
//		} else {
//			RetMap.put("data", FilterMap(data, field));
//		}

		return RetMap;

	}

//	private static Map<String, Object> FilterMap(Object data, String[] field) {
//
//		String[] arr = { "pageNum", "total", "list", "pages" };
//
//		Map<String, Object> RetMap = new HashMap<>();
//		Map<Object, Object> datamap = new BeanMap(data);
//		if (null == field || field.length == 0) {
//			field = arr;
//		}
//
//		for (int i = 0; i < arr.length; i++) {
//			String key = arr[i];
//			RetMap.put(key, datamap.get(key));
//		}
//		return RetMap;
//
//	}

	public static Map<String, Object> RetJsone(int code, String msg, Object data) {

		Map<String, Object> RetMap = new HashMap<>();
		RetMap.put("code", code);
		RetMap.put("msg", msg);
		RetMap.put("data", data);

		return RetMap;

	}

	public static Object GetJsonVal(String str, String key) {
		Map mapTypes = JSON.parseObject(str);
		Map<String, Object> map = new HashMap<String, Object>();
		return ((JSONObject) mapTypes).getString(key);
	}

	public static List<Map<String, Object>> GetJsonList(String str) {
		List<Map<String, Object>> retmap = JSON.parseObject(str, new TypeReference<List<Map<String, Object>>>() {
		});
		return retmap;
	}

}
