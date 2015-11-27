package com.centerm.autofill.setting;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 升级设置
 *
 */
public class UpdateFragment extends Fragment {
	

	
	

	private UpdateUiOperator updateUiOperator;
	public static boolean isStartQuery = true;//是否需要版本查询
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.manage_fm_update, container, false );
		updateUiOperator = new UpdateUiOperator(getActivity(), view);
		return view;
	}

	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	

		startQuery();//执行版本查询		
			
	}	
	
	
	
	
	//查询版本信息
		public void startQuery(){
			
			updateUiOperator.startQuery();
		
			
		}
	
	//定义耗时的操作接口
	public interface OnProcessEvent{
		
		public void onStartProcessEvent();//开始事件
		public void onEndProcessEvent();//结束事件
	};

	

}
