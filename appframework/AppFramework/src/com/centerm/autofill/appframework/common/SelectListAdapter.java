package com.centerm.autofill.appframework.common;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.centerm.autofill.appframework.R;
import com.centerm.autofill.appframework.listener.SelectListener;

public class SelectListAdapter extends ArrayAdapter<String>{
	private LayoutInflater mInflater;
	  public SelectListAdapter(Context context,
	      List<String> objects) {
	    super(context,0, objects);
	    mInflater = LayoutInflater.from(context);
	  }
	  public SelectListAdapter(Context context,
		      List<String> objects,boolean isShowClose) {
		    super(context,0, objects);
		    mInflater = LayoutInflater.from(context);
		  }
	  
	
	  

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    final ViewHolder viewHolder;

	    if (convertView == null) {
	       convertView = mInflater.inflate(R.layout.select_list_dialog_item, parent,
	          false);

	      viewHolder = new ViewHolder();
	      
	      viewHolder.textView = (TextView) convertView.findViewById(R.id.SelectDialogListItem_textView);
	    
	      convertView.setTag(viewHolder);

	    } else {
	      viewHolder = (ViewHolder) convertView.getTag();
	    }
	  
	    final String text = getItem(position);
	    viewHolder.textView.setText(text);
	 
	  
	    return convertView;
	  }

	  private static class ViewHolder {
	    public TextView textView;
	   
	  
	  }

	  public void addObject(String text)
	  {
		  this.add(text);
		  this.notifyDataSetChanged();
	  }
}