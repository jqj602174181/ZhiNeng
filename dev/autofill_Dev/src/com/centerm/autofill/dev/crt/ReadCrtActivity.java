package com.centerm.autofill.dev.crt;


import com.centerm.autofill.dev.crt.ReadCrtDialog.ReadCrtListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class ReadCrtActivity extends Activity implements ReadCrtListener{
	
	final String DEF_TITLE = "请插卡";//默认提示的标题
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView( R.layout.activity_main );
        //取出标题
        Intent intent = getIntent();
        String title = intent.getStringExtra( "title" );
        if( title == null )
        	title = DEF_TITLE;
        
        ReadCrtDialog dlg = new ReadCrtDialog( title );
        dlg.setReadCompleteListener(this);
		dlg.show(getFragmentManager(), "readiccard");
    }

	@Override
	public void onReadCardComplete(int resCode, String str) {
		Intent intent=new Intent();
		if( resCode == ReadCrtDialog.RES_OK ){
			intent.putExtra("no", str);
			setResult( 0, intent );			
		}else{//失败
			setResult( -1, intent );
		}
		finish();
	}
	
}
