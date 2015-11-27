package com.centerm.autofill.setting;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ��������
 *
 */
public class UpdateFragment extends Fragment {
	

	
	

	private UpdateUiOperator updateUiOperator;
	public static boolean isStartQuery = true;//�Ƿ���Ҫ�汾��ѯ
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

		startQuery();//ִ�а汾��ѯ		
			
	}	
	
	
	
	
	//��ѯ�汾��Ϣ
		public void startQuery(){
			
			updateUiOperator.startQuery();
		
			
		}
	
	//�����ʱ�Ĳ����ӿ�
	public interface OnProcessEvent{
		
		public void onStartProcessEvent();//��ʼ�¼�
		public void onEndProcessEvent();//�����¼�
	};

	

}
