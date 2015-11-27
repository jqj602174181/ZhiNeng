package com.centerm.autofill.dev.printer;

import com.centerm.autofill.dev.printer.PrinterDialog.CompleteListener;


import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;


@SuppressLint("NewApi") 
public class PrinterActivity extends Activity implements CompleteListener{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String starttitle = getIntent().getStringExtra("starttitle");
        String endtitle = getIntent().getStringExtra("endtitle");
        String bustile = getIntent().getStringExtra("bustile");
        String businfo = getIntent().getStringExtra("businfo");
        if (starttitle == null)
        	starttitle = "正在打印";
        if (endtitle == null)
        	endtitle = "打印完成，请取走凭条";
        
        PrinterDialog dlg = new PrinterDialog( starttitle, endtitle, bustile, businfo );
		dlg.setReadCompleteListener(this);
		dlg.show( getFragmentManager(), "print" );
    }

	//身份证阅读结束时
	@Override
	public void onComplete(int nStatus) {		
		Intent intent=new Intent();
		
		finish();
	}	
}
