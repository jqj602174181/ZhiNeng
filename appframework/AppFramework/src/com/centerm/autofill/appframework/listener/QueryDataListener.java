package com.centerm.autofill.appframework.listener;

import java.util.List;
import java.util.Map;


public interface QueryDataListener extends BaseListener{
	public void onQuerySuc(List<Map<String, Object>> list);
}
