package kankan.wheel.widget.adapters;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class SpinAdapter extends BaseAdapter {

	protected ArrayList<String> list;
	protected int selPosition = 0;


	public SpinAdapter(String[] listItems) {
		list = new ArrayList<String>();
		for (int i = 0; i < listItems.length; i++) {
			list.add(listItems[i]);
		}
	}
	
	public ArrayList<String> getList() {
		return list;
	}
	
	public int getSelPosition() {
		return selPosition;
	}

	public void setSelPosition(int selPosition) {
		this.selPosition = selPosition;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
	
		return null;

	}

}
