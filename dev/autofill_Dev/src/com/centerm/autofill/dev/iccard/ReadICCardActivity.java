package com.centerm.autofill.dev.iccard;

import com.centerm.autofill.dev.R;
import com.centerm.autofill.dev.iccard.ReadICCardDialog.ReadICCardListener;
import com.centerm.autofill.dev.magcard.ReadMagCardDialog;
import com.centerm.util.StringUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;

public class ReadICCardActivity extends Activity implements ReadICCardListener{
	
	final String DEF_TITLE = "请插IC卡";//默认提示的标题
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView( R.layout.activity_main );
        //取出标题
        Intent intent = getIntent();
        String title = intent.getStringExtra( "title" );
        if( title == null )
        	title = DEF_TITLE;
        
        ReadICCardDialog dlg = new ReadICCardDialog( title );
        dlg.setReadCompleteListener(this);
		dlg.show(getFragmentManager(), "readiccard");
    }

	@Override
	public void onReadCardComplete(int resCode, String str) {
		Intent intent=new Intent();
		if( resCode == ReadICCardDialog.RES_OK ){
			intent.putExtra("no", str);
			setResult( 0, intent );			
		}else{//失败
			setResult( -1, intent );
		}
		finish();
	}
   
	
	
    
}
