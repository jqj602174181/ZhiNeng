package com.centerm.autofill.appframework.view;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


//���radiogroup���
public class MultiRadioGroup implements OnCheckedChangeListener{
	
	RadioButton btnChecked;		//��ѡ�е�button
	RadioGroup rgChecked;		//���б�ѡ��״̬��group
	RadioGroup[] rgs;
	
	public MultiRadioGroup( RadioGroup... rgs ){		
			this.rgs = rgs;
			
			//ʵ��ͬһʱ��ֻѡ������һ��
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
		//ֻ����ѡ��״̬
		if( checkedId != -1 ){
			RadioButton radio = (RadioButton)rg.findViewById( checkedId );
			if( rg != rgChecked ){
				rgChecked.clearCheck();
				rgChecked = rg;
			}
			btnChecked = radio;
		}
	}
	
	//����ѡ�����idֵ
	public int getCheckedRadioButtonId (){
		return btnChecked.getId();
	}
	
	//�õ�ѡ���������ֵ
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
