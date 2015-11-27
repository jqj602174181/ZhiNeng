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
 * 联网核查设置
 *
 */
public class IdcardFragment extends Fragment {
	private EditText edIp;//IP控件
	private EditText edPort;//端口控件
	private EditText edOrgId;//机构号控件
	private TextView tvTip;//提示控件
	
	private String sIp;
	private String sPort;
	private String sOrgId;
	
	private AutofillSettings conf = null;//配置
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate( R.layout.manage_fm_idcardserver, container, false );
		
		tvTip = (TextView)view.findViewById( R.id.LABEL_TIP );
		edPort = (EditText)view.findViewById( R.id.EDIT_PORT );
		edOrgId = (EditText)view.findViewById( R.id.EDIT_ORG );
		
		//设置IP地址过滤
		edIp = (EditText)view.findViewById( R.id.EDIT_IP);		
		InputFilter[] filters = new InputFilter[]{new IPInputFilter()};
		edIp.setFilters(filters);
		
		conf = new AutofillSettings( getActivity() );
		
		//保存操作
		Button btn = (Button)view.findViewById( R.id.BTN_SAVE );
		btn.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				sIp = edIp.getText().toString();
				sPort= edPort.getText().toString();
				sOrgId = edOrgId.getText().toString();
				
				//校验IP
				if( !StringUtil.isIp( sIp )){
					showTip( "联网核查IP输入错误！" );
				}else if( !StringUtil.isPort( sPort ) ){
					showTip( "核查端口输入错误！有效范围是1~65535。" );
				}else if( sOrgId.trim().length() == 0 ){
					showTip( "机构号输入错误！" );
				}else{//OK					
					conf.setCheckIDServerIp( sIp );
					conf.setCheckIDServerPort( Integer.valueOf(sPort));
					conf.setCheckIDServerOrg( sOrgId );
					showTip( true, "设置成功。", false );
				}
			}
		});
		
		
		//读取配置并更新界面
		edIp.setText( conf.getCheckIDServerIp() );
		edPort.setText( String.valueOf(conf.getCheckIDServerPort()));
		edOrgId.setText( conf.getCheckIDServerOrg() );
		return view;
	}
	
	//显示或隐藏提示信息
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

