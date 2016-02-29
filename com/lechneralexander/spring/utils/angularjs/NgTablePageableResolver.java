package com.lechneralexander.spring.utils.angularjs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;


public class NgTablePageableResolver implements WebArgumentResolver {
	private static String PRE_SORTING = "sorting";
	private static String PRE_FILTER = "filter";

	@Override
	public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
		if (methodParameter.getParameterType().equals(NgTablePageable.class)) {
			//Set Defaults
			NgTablePageable pageRequest = new NgTablePageable();

			Integer count = NgTablePageable.DEFAULT_SIZE;
			Integer page = NgTablePageable.DEFAULT_PAGE;
			
			//Try to parse basic params
			String tmpCount = webRequest.getParameter("count");
			String tmpPage = webRequest.getParameter("page");
			if (tmpCount != null){
				count = Integer.parseInt(tmpCount);
			}
			if (tmpPage != null){
				page = Integer.parseInt(tmpPage) - 1;
			}
			
			//Parse sorting and filter
			ArrayList<Order> orders = new ArrayList<Order>();
			HashMap<String, String[]> filters = new HashMap<String, String[]>();
			for(Iterator<String> it = webRequest.getParameterNames(); it.hasNext(); ) {
				String parameterName = it.next();
			    if (parameterName.startsWith(PRE_SORTING + "[")){
			    	String sortingParam = parameterName.substring(PRE_SORTING.length() + 1, parameterName.length() - 1);
			    	if (webRequest.getParameter(parameterName).equals("asc")){
				    	orders.add(new Order(Direction.ASC, sortingParam));
			    	}else{
				    	orders.add(new Order(Direction.DESC, sortingParam));
			    	}
			    }
			    if (parameterName.startsWith(PRE_FILTER + "[")){
			    	String filterParam = parameterName.substring(PRE_FILTER.length() + 1, parameterName.length() - 1);
			    	filters.put(filterParam, webRequest.getParameterValues(parameterName));
			    }
			}
			
			//Create pageable
			if (orders.size() > 0){
				pageRequest = new NgTablePageable(page, count, new Sort(orders), filters);
			}else{
				pageRequest = new NgTablePageable(page, count, filters);
			}			

			return pageRequest;
		}
		return UNRESOLVED;
	}
}
