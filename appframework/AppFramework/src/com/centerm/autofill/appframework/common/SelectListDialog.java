package com.centerm.autofill.appframework.common;

import java.util.ArrayList;

import com.centerm.autofill.appframework.R;
import com.centerm.autofill.appframework.listener.SelectListener;


import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DialerFilter;
import android.widget.ListView;
import android.widget.PopupWindow;

public class SelectListDialog extends Dialog{
	private View mainView;
	private Context context;
	private ListView listView;
	private SelectListAdapter selectListAdapter;
	private SelectListener listener;
//	private ArrayList<String> selectList;
	public SelectListDialog(Context context,int width,ArrayList<String> selectList,SelectListener listener)
	{
		super(context, R.style.dialog_style);
		this.listener =listener;
		this.context = context;
		init(width, selectList);
	}
	public SelectListDialog(Context context,int width,ArrayList<String> selectList)
	{
		super(context, R.style.dialog_style);
		this.listener 	=	null;
		this.context 	= context;
		init(width, selectList);
	}
	
	
	private void init(int width,ArrayList<String> selectList)
	{
		mainView 			= ((Activity)context).getLayoutInflater().inflate(R.layout.select_list_dialog, null);
		listView 			= (ListView)mainView.findViewById(R.id.SelectListDialog_List);
		listView.getLayoutParams().width = width;
		selectListAdapter = new SelectListAdapter(context, selectList);
		listView.setAdapter(selectListAdapter);
		
		this.setContentView(mainView);
		/*
		setWidth(width);
		setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.framework_transparent_drawable));
		*/
		setListView();
	}
	
	private void setListView()
	{
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(listener!=null)
					listener.select(arg2);//所对应的选项的索引
				
			}
		});
	}
	public void setListViewBackground(int drawable)
	{
		listView.setBackgroundResource(drawable);
	}

	
	public void setSelectLister(SelectListener selectListener)
	{
		this.listener = selectListener;
	}
	/*
	 * 显示在控件下
	 */
	public void showDialog(View showView){
		if(!isShowing()){
		//	this.showAsDropDown(showView);
			show();
		}
	}
	/*
	 * 居中显示
	 */
	public void showDialog()
	{
		if(!isShowing()){
			//this.showAtLocation(mainView, Gravity.CENTER, 0,0);
		}
	}
	public void closeDialog()
	{
		if(isShowing()){
			dismiss();
		}
	}

	
	public void addSelectObject(String object)
	{
		selectListAdapter.addObject(object);
	}


}
