package com.centerm.autofill.appframework.view;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


//多个radiogroup组合
public class MultiRadioGroup implements OnCheckedChangeListener{
	
	RadioButton btnChecked;		//被选中的button
	RadioGroup rgChecked;		//含有被选中状态的group
	RadioGroup[] rgs;
	
	public MultiRadioGroup( RadioGroup... rgs ){		
			this.rgs = rgs;
			
			//实现同一时间只选中其中一项
			for( int i = 0; i < rgs.length; i++ ){
				RadioGroup rg = rgs[i];
				rg.setOnCheckedChangeListener(this);
				int checkedId = rg.getCheckedRadioButtonId();
				if( checkedId != -1 ){
					btnChecked = (RadioButton)rg.findViewById( checkedId );
					rgChecked = rg;
				}				
			}
		}	
	
	@Override
	public void onCheckedChanged(RadioGroup rg, int checkedId) {
		//只处理选中状态
		if( checkedId != -1 ){
			RadioButton radio = (RadioButton)rg.findViewById( checkedId );
			if( rg != rgChecked ){
				rgChecked.clearCheck();
				rgChecked = rg;
			}
			btnChecked = radio;
		}
	}
	
	//得以选中项的id值
	public int getCheckedRadioButtonId (){
		return btnChecked.getId();
	}
	
	//得到选中项的索引值
	public int getCheckedRadioButtonIndex(){
		int baseIndex = 0;
		for( int i = 0; i < rgs.length; i++ ){
			RadioGroup rg = rgs[i];
			if( rgChecked != rg ){
				baseIndex += rg.getChildCount();
			}else{
				return baseIndex + rg.indexOfChild( btnChecked );
			}
		}
		return 0;
	}
}
