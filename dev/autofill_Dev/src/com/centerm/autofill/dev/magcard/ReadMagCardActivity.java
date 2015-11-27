package com.centerm.autofill.dev.magcard;

import com.centerm.autofill.dev.magcard.ReadMagCardDialog.ReadMagCardListener;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;

public class ReadMagCardActivity extends Activity implements ReadMagCardListener{
	
	final String DEF_TITLE = "��ˢ�ſ�";//Ĭ����ʾ�ı���

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ȡ������
        Intent intent = getIntent();
        String title = intent.getStringExtra( "title" );
        if( title == null )
        	title = DEF_TITLE;
        
        ReadMagCardDialog dlg = new ReadMagCardDialog( title );
        dlg.setReadCompleteListener(this);
		dlg.show(getFragmentManager(), "readcard");
    }
    
    public void onReadCardComplete( int resCode, String str ) {		
		Intent intent=new Intent();
		if( resCode == ReadMagCardDialog.RES_OK ){
			intent.putExtra("no", str);
			setResult( 0, intent );			
		}else{//ʧ��
			
			setResult( -1, intent );
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
    
    
    
}
