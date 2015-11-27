package com.centerm.autofill.app.nmnx.widget;

import com.centerm.autofill.app.nmnx.common.R;
import com.centerm.autofill.appframework.listener.SelectListener;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IDInfoEditorDialog extends Dialog implements OnClickListener{
	private String name = "";		//���֤����
	private String idno = "";		//���֤��
	private String police = "";		//��֤����
	private EditText etName;		//����
	private EditText etIDNO;		//����
	private EditText etPolice;		//��֤����
	
	private SelectListener selectListener;
	
	private String inputIdNumber;
	private String inputName;
	private String inputPolice;	//��ʾ�����뷢֤����
	
	public IDInfoEditorDialog(Context context,SelectListener selectListener) {
		super(context, R.style.dialog_style );//�ޱ��⣬�ޱ������߿�
		setContentView( R.layout.dialog_idinfo_editor );
		this.selectListener = selectListener;
		inputIdNumber = context.getResources().getString(R.string.inputIdNumber);
		inputName 	  = context.getResources().getString(R.string.inputName);
		inputPolice = context.getResources().getString(R.string.inputIdPolice );
		etName = (EditText)findViewById( R.id.EDIT_NAME );
		etIDNO = (EditText)findViewById( R.id.EDIT_NUMBER );
		etPolice = (EditText)findViewById( R.id.EDIT_POLICE );
		
		Button btn = (Button)findViewById( R.id.BTN_OK);
		btn.setOnClickListener( this );
	}
	
	public void showTip(String text)
	{
		Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onClick(View view) {
		if( view.getId() == R.id.BTN_OK ){
			name = etName.getText().toString();
			idno = etIDNO.getText().toString();
			police = etPolice.getText().toString();
			
			if(name.length()==0){
				showTip(inputName);
				return;
			}
			
			if(idno.length() !=18 ){
				showTip(inputIdNumber);
				return;
			}		
			
			if( police.length() == 0 ){
				showTip( inputPolice );
				return;
			}
			
			if(selectListener!=null){
				selectListener.select(0);
			}
			this.dismiss();
		}
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return idno;
	}
	
	public String getPolice(){
		return police;
	}

	@Override
	public void dismiss() {
		super.dismiss();
	
	}
	
	
	
	
}
