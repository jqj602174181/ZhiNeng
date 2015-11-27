package com.centerm.autofill.setting.manage;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.centerm.autofill.setting.R;

public class MenuFragment extends ListFragment {
	
	String[] subMenuTitles = new String[]{"网络设置", "更新检测"};//菜单
	private static final String TAG = "MenuFragment";
	private ListView selfList;
	private OnMenuSelectedListener listener;
	
	//定义选择
	public interface OnMenuSelectedListener{
		public void onMenuItemSelected( int position );
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//设置监听菜单按下的父容器
		try {
			listener = (OnMenuSelectedListener)activity;
		} catch (ClassCastException e) {
			// TODO: handle exception
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =  inflater.inflate( R.layout.menu_fragment, container, false );
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//创建简要的数组列表
		this.setListAdapter( new ArrayAdapter<String>( getActivity(), R.layout.fragment_list_item, subMenuTitles));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		selfList = getListView();
		int position = 0;
		selfList.requestFocusFromTouch();
		selfList.setSelection(position);
		selfList.performItemClick(selfList.getAdapter().getView(position, null, null), position, position);
	}

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		//Viewl.getChildAt(position)
		//l.setItemChecked(position, true );
		v.setActivated( true );
		listener.onMenuItemSelected( position );
		//v.setBackground( getResources().getDrawable(R.drawable.menu_actived_bg));
	}
	
	
	
	

}
