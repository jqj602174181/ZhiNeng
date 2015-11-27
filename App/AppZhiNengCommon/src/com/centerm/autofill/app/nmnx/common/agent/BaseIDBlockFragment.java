package com.centerm.autofill.app.nmnx.common.agent;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.centerm.autofill.app.nmnx.common.R;
import com.centerm.autofill.app.nmnx.widget.IDInfoEditorDialog;
import com.centerm.autofill.appframework.base.BaseScrollFragment;
import com.centerm.autofill.appframework.listener.SelectListener;
import com.centerm.autofill.appframework.utils.DateUtil;
import com.centerm.autofill.appframework.utils.StringUtil;
import com.centerm.autofill.dev.IDCardMsg;

//身份证的fragment,写成公共的
public class BaseIDBlockFragment extends BaseScrollFragment{

	protected TextView mtvTitle;
	protected String tipTitle = "";
	int transactorType = FROM_SELF;	//刷二代证的人的类型
	protected Button btnReadAgent, btnEditSelf, btnReadSelf;

	public RadioGroup rg_Ransaction;
	protected LinearLayout agentPhone;

	private IDInfoEditorDialog dlgIDEditor;		//ID编辑框
	public String editName="";  //用于编辑的姓名
	public String editId = "";  //用于编辑的身份证
	public String editPolice = "";  //用于编辑时的发证机关
	public boolean isEditInfoValid = false;	//编辑的信息是否有效
	private boolean canEditIDInfo = false;		//是否可编辑身份证信息
	protected boolean isIdcardInfoValid = false;	//身份证信息是否有效

	protected TextView tvPersonInfo;		//个人信息
	protected ImageView imgViewPhoto;		//二代证照片

	protected boolean isAutoRun = true;			//页面加载时自动启动读二代证
	protected boolean bAfterInit = false;	//是否已初始化一次

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState, int layoutid) {
		View view = super.onCreateView(inflater, container, savedInstanceState, layoutid);

		initView(view);

		special();

		putFixMapData("year", DateUtil.getYear());//日期的年
		putFixMapData("month", DateUtil.getMonth());//日期的月
		putFixMapData("day", DateUtil.getDayofMonth());//日期的日

		return view;
	}

	//子类界面特殊处理(重写方法)
	protected void special() {

	}

	//禁止界面显示后，立即读二代证
	public void disableAutoRun(){
		isAutoRun = false;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		//如果未设置指定的标题，则采用默认的标题
		if( tipTitle == null || tipTitle.length() == 0 ){
			tipTitle = getString(R.string.readIDCardOfApplicant);
		}
	}

	//子类根据情况可重写
	protected void initView(View view) {

		mtvTitle = (TextView)view.findViewById(R.id.LABEL_title);

		tvPersonInfo = (TextView)view.findViewById( R.id.tv_person_info );
		imgViewPhoto = (ImageView)view.findViewById( R.id.iv_photo );
		agentPhone = (LinearLayout)view.findViewById( R.id.LAYOUT_AgentPhone );

		btnReadAgent = (Button)view.findViewById(R.id.BTN_ReadIDAgent); //读取代理人二代证按钮
		if(btnReadAgent != null){
			btnReadAgent.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					startReadIDCard(tipTitle, null, transactorType);	
				}
			});
		}

		//编辑身份证dialog
		btnEditSelf = (Button)view.findViewById(R.id.BTN_EDITIDCARD); 
		if(btnEditSelf != null){
			btnEditSelf.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if( dlgIDEditor == null ){
						SelectListener selectListener = new SelectListener() {

							@Override
							public void select(int index) {
								if(index==0){
									editName = dlgIDEditor.getName();
									editId = dlgIDEditor.getId();
									editPolice = dlgIDEditor.getPolice();
									isEditInfoValid = true;
									setPersioInfo();
									//									isFirstEditIDInfo = false;
								}
							}
						};
						dlgIDEditor = new IDInfoEditorDialog( getActivity(), selectListener);
					}
					dlgIDEditor.show();
				}
			});
		}

		//读取二代证按钮
		btnReadSelf = (Button)view.findViewById(R.id.BTN_READIDCARD); 
		if(btnReadSelf != null){
			btnReadSelf.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					startReadIDCard(getString(R.string.readIDCardOfApplicant), null, FROM_SELF);
				}
			});
		}

		//代理人
		rg_Ransaction =	(RadioGroup)view.findViewById(R.id.RG_Ransaction);
		if( rg_Ransaction != null){
			rg_Ransaction.setOnCheckedChangeListener( new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup rg, int checkedId) {
					if(checkedId == R.id.RB_SelfTransaction) {
						activity.enableFragment("代理人身份信息", false);
						agentPhone.setVisibility(View.GONE);
						deleteDataItem(R.id.EDIT_AgentMobile);
					} else if (checkedId == R.id.RB_ProxyTransaction) {
						activity.enableFragment("代理人身份信息", true);
						agentPhone.setVisibility(View.VISIBLE);
						addDataItem(new InputDataItem( R.id.EDIT_AgentMobile, "agent_phone", InputDataItem.VALIDATETYPE_PHONE, "请填写代理人电话！"));
					}
				}
			});
		}

		//如果身份证信息无效，则显示编辑等按钮
		if( !isIdcardInfoValid ){
			showEditLayout();
			if( isEditInfoValid )
				setPersioInfo();
		}
	}

	//显示读二代证和编辑按钮
	private void showEditLayout(){
		if( imgViewPhoto != null ){
			imgViewPhoto.setVisibility( View.GONE );
		}

		Log.e("tag","btnEditIDCard is " + canEditIDInfo);
		if( canEditIDInfo && btnEditSelf != null ){
			btnEditSelf.setVisibility( View.VISIBLE );
			LayoutParams params = new LayoutParams( 740, LayoutParams.WRAP_CONTENT );
			params.topMargin = 20;
			tvPersonInfo.setLayoutParams(params);
		}
	}

	//当用户跳过读二代证时
	protected void onSkipReadIDCard(){
		showEditLayout();
	}

	//当成功读取二代证时
	protected void onOKReadIDCard(){
		if( imgViewPhoto != null ){
			imgViewPhoto.setVisibility( View.VISIBLE );
		}

		if( tvPersonInfo != null ){
			tvPersonInfo.setVisibility( View.VISIBLE );
		}

		if( btnReadSelf != null ){
			btnReadSelf.setVisibility( View.GONE );
		}

		if( canEditIDInfo && btnEditSelf != null ){
			btnEditSelf.setVisibility( View.GONE );
			LayoutParams params = new LayoutParams( 850, LayoutParams.WRAP_CONTENT );
			params.topMargin = 20;
			tvPersonInfo.setLayoutParams(params);
		}

		isIdcardInfoValid = true;
	}

	public boolean isIDCardInfoValid(){
		return isIdcardInfoValid;
	}

	public boolean isIDCardInfoEditValid(){
		return isEditInfoValid;
	}

	protected void  setPersioInfo( )
	{
		String mstrInfo = "姓名：" + editName +  "     身份证号：" + editId + "\r\n发证机关：" + editPolice;
		tvPersonInfo.setText(mstrInfo);
	}

	//允许编辑身份证信息
	public void enableEditIDInfo(){
		canEditIDInfo = true;
	}

	//设置读二代证的人的类型
	public void setTransactorType( int type ){
		transactorType = type;
	}

	@Override
	protected HashMap<String, String> getRecordMap() {
		if(!this.isVisiable())
			return null;

		//填写可编辑的选项
		HashMap<String, String> map = new HashMap<String, String>();		
		Map<String, String> editmap = super.getRecordMap();
		if( editmap != null){
			map.putAll( editmap );
		}

		return map;
	}

	/*
	 * idCardMsg为身份证信息
	 * 用于存放一些身份证公共的信息
	 * 如果有特殊情况,要进行重写
	 */
	public void setIdentityCard(IDCardMsg idCardMsg,HashMap<String, String> map)
	{
		if( idCardMsg != null) {
			map.put("ID_type", "1");//表示公民身份证
			map.put("ID_num", idCardMsg.getId_num());
			map.put("address", idCardMsg.getAddress());//家庭住址
			map.put("name", idCardMsg.getName());
			map.put("sex", idCardMsg.getSex().equals("男")?"1":"2");
			map.put( "police", idCardMsg.getSign_office());
			map.put( "birthday_year", idCardMsg.getBirth_year());
			map.put( "birthday_month", idCardMsg.getBirth_month());
			map.put( "birthday_day", idCardMsg.getBirth_day());
			map.put( "ID_deadline_year", idCardMsg.getUseful_e_date_year());
			map.put( "ID_deadline_month", idCardMsg.getUseful_e_date_month());
			map.put( "ID_deadline_day", idCardMsg.getUseful_e_date_day());
			map.put("nation",idCardMsg.getNation_str());
			//如果不足28字符，末尾用空格补齐，这是由于系统也会自动回显一个拼音，且不准确，需要覆盖掉
			String pinyinpadding = StringUtil.padding(idCardMsg.getPinyin(), 28);
			map.put( "name_pinyin", pinyinpadding );

			//期限类型和到期日
			//			if( idCardMsg.isLongTerm()){
			//				map.put( "ID_deadline_type", "2" );
			//			}else{
			//				map.put( "ID_deadline_type", "1" );
			//				map.put( "ID_deadline", idCardInfo.getValidEndYYYYMMDD());
			//			}
		}
	}

}
