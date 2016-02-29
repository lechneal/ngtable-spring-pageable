package com.lechneralexander.spring.utils.angularjs;

import org.json.JSONArray;
import org.json.JSONStringer;
import org.json.JSONWriter;

public class NgTableUtils {
	public static String toTableData(JSONArray array, long count){
		JSONWriter writer = new JSONStringer().object()
	            .key("list")
                .value(array)
            .key("count")
                .value(count)
        .endObject();
		return writer.toString();
	}
}
