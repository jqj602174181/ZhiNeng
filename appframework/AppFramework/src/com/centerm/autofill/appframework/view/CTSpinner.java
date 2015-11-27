package com.centerm.autofill.appframework.view;
//com.centerm.autofill.appframework.view.CTSpinner
import java.util.ArrayList;

import kankan.wheel.widget.adapters.SpinAdapter;

import com.centerm.autofill.appframework.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener; 

public class CTSpinner extends Button implements OnItemClickListener
{
	public static SelectDialog dialog = null;  
	public static String text;  
	private SpinAdapter spinAdapter;
	private ArrayList<String> list;
	public CTSpinner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setSelection(int position) {
		// TODO Auto-generated method stub
		//super.setSelection(position);
		spinAdapter.setSelPosition(position);
		this.setText(list.get(position));
	}

	@Override
	public boolean performClick()
	{
		Context context = getContext();
		final LayoutInflater inflater = LayoutInflater.from(getContext());
		final View view = inflater.inflate(R.layout.spinner_dialog_container, null);
		
		final ListView listview = (ListView) view.findViewById(R.id.LIST_Container);
		
		listview.setAdapter(spinAdapter);
		listview.setOnItemClickListener(this);
		listview.setSelection(spinAdapter.getSelPosition());
		
		this.setText(list.get(spinAdapter.getSelPosition()));
		dialog = new SelectDialog(context, R.style.spinner_dialog);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//Window window = dialog.getWindow();                      
		//WindowManager.LayoutParams wl = window.getAttributes(); 
		//wl.x = 0;
		//wl.y = 0;
		
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		dialog.addContentView(view, params);
		
		return true;
	}

	public void setAdapter(SpinAdapter adapter) {
		this.spinAdapter = adapter;
		list = spinAdapter.getList();
		this.setText(list.get(spinAdapter.getSelPosition()));
	}

	@Override  
    public void onItemClick(AdapterView<?> view, View itemView, int position, long id)
	{  
		itemView.setBackgroundColor(0xFF000000);
        setSelection(position);  
        setText(list.get(position));  
        if (dialog != null) 
        {  
            dialog.dismiss();  
            dialog = null;  
        }  
    }
	
  

  
    public class SelectDialog extends AlertDialog 
    {  
        public SelectDialog(Context context, int theme) 
        {  
            super(context, theme);  
        }  
      
        public SelectDialog(Context context) 
        {  
            super(context);  
        }  
      
        @Override  
        protected void onCreate(Bundle savedInstanceState) 
        {  
            super.onCreate(savedInstanceState);  
            // setContentView(R.layout.slt_cnt_type);  
        }  
    }  
    
}
