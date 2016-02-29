package com.lechneralexander.spring.utils.angularjs;

import java.util.HashMap;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class NgTablePageable extends PageRequest{
	public static int DEFAULT_PAGE = 0;
	public static int DEFAULT_SIZE = 10;
	
	private HashMap<String, String[]> filters = new HashMap<String, String[]>();

	public NgTablePageable(){
		super(DEFAULT_PAGE, DEFAULT_SIZE);
	}
	public NgTablePageable(int page, int size) {
		super(page, size);
	}
	public NgTablePageable(int page, int size, HashMap<String, String[]> filters) {
		super(page, size);
		this.filters = filters;
	}
	public NgTablePageable(int page, int size, Sort sort) {
		super(page, size, sort);
	}
	public NgTablePageable(int page, int size, Sort sort, HashMap<String, String[]> filters) {
		super(page, size, sort);
		this.filters = filters;
	}
	public void addFilter(String name, String[] value){
		filters.put(name, value);
	}
	public HashMap<String, String[]> getFilters(){
		return filters;
	}
	public String[] getFilterByKey(String key){
		return hasFilters() ? filters.get(key) : null;
	}
	public boolean hasFilters() {
		return !filters.isEmpty();
	}
}
