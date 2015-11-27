package com.centerm.autofill.setting;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.centerm.autofill.setting.utils.IPInputFilter;
import com.centerm.autofill.setting.utils.StringUtil;

/**
 * �����˲�����
 *
 */
public class IdcardFragment extends Fragment {
	private EditText edIp;//IP�ؼ�
	private EditText edPort;//�˿ڿؼ�
	private EditText edOrgId;//�����ſؼ�
	private TextView tvTip;//��ʾ�ؼ�
	
	private String sIp;
	private String sPort;
	private String sOrgId;
	
	private AutofillSettings conf = null;//����
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.manage_fm_idcardserver, container, false );
		
		tvTip = (TextView)view.findViewById( R.id.LABEL_TIP );
		edPort = (EditText)view.findViewById( R.id.EDIT_PORT );
		edOrgId = (EditText)view.findViewById( R.id.EDIT_ORG );
		
		//����IP��ַ����
		edIp = (EditText)view.findViewById( R.id.EDIT_IP);		
		InputFilter[] filters = new InputFilter[]{new IPInputFilter()};
		edIp.setFilters(filters);
		
		conf = new AutofillSettings( getActivity() );
		
		//�������
		Button btn = (Button)view.findViewById( R.id.BTN_SAVE );
		btn.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				sIp = edIp.getText().toString();
				sPort= edPort.getText().toString();
				sOrgId = edOrgId.getText().toString();
				
				//У��IP
				if( !StringUtil.isIp( sIp )){
					showTip( "�����˲�IP�������" );
				}else if( !StringUtil.isPort( sPort ) ){
					showTip( "�˲�˿����������Ч��Χ��1~65535��" );
				}else if( sOrgId.trim().length() == 0 ){
					showTip( "�������������" );
				}else{//OK					
					conf.setCheckIDServerIp( sIp );
					conf.setCheckIDServerPort( Integer.valueOf(sPort));
					conf.setCheckIDServerOrg( sOrgId );
					showTip( true, "���óɹ���", false );
				}
			}
		});
		
		
		//��ȡ���ò����½���
		edIp.setText( conf.getCheckIDServerIp() );
		edPort.setText( String.valueOf(conf.getCheckIDServerPort()));
		edOrgId.setText( conf.getCheckIDServerOrg() );
		return view;
	}
	
	//��ʾ��������ʾ��Ϣ
	private void showTip( String text ){ showTip( true, text, true );}
	private void showTip( boolean shown, String text, boolean iserr ){
		if( shown ){
			if( iserr )
				tvTip.setTextColor( getResources().getColor(R.color.red_tiperr) );
			else
				tvTip.setTextColor( getResources().getColor(R.color.green_tipok) );
			tvTip.setText( text );
			tvTip.setVisibility( View.VISIBLE );
		}else{
			tvTip.setVisibility( View.INVISIBLE );
		}
	}
	
	
	
}

