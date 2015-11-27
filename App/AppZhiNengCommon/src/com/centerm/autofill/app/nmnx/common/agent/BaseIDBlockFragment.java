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

//���֤��fragment,д�ɹ�����
public class BaseIDBlockFragment extends BaseScrollFragment{

	protected TextView mtvTitle;
	protected String tipTitle = "";
	int transactorType = FROM_SELF;	//ˢ����֤���˵�����
	protected Button btnReadAgent, btnEditSelf, btnReadSelf;

	public RadioGroup rg_Ransaction;
	protected LinearLayout agentPhone;

	private IDInfoEditorDialog dlgIDEditor;		//ID�༭��
	public String editName="";  //���ڱ༭������
	public String editId = "";  //���ڱ༭�����֤
	public String editPolice = "";  //���ڱ༭ʱ�ķ�֤����
	public boolean isEditInfoValid = false;	//�༭����Ϣ�Ƿ���Ч
	private boolean canEditIDInfo = false;		//�Ƿ�ɱ༭���֤��Ϣ
	protected boolean isIdcardInfoValid = false;	//���֤��Ϣ�Ƿ���Ч

	protected TextView tvPersonInfo;		//������Ϣ
	protected ImageView imgViewPhoto;		//����֤��Ƭ

	protected boolean isAutoRun = true;			//ҳ�����ʱ�Զ�����������֤
	protected boolean bAfterInit = false;	//�Ƿ��ѳ�ʼ��һ��

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState, int layoutid) {
		View view = super.onCreateView(inflater, container, savedInstanceState, layoutid);

		initView(view);

		special();

		putFixMapData("year", DateUtil.getYear());//���ڵ���
		putFixMapData("month", DateUtil.getMonth());//���ڵ���
		putFixMapData("day", DateUtil.getDayofMonth());//���ڵ���

		return view;
	}

	//����������⴦��(��д����)
	protected void special() {

	}

	//��ֹ������ʾ������������֤
	public void disableAutoRun(){
		isAutoRun = false;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		//���δ����ָ���ı��⣬�����Ĭ�ϵı���
		if( tipTitle == null || tipTitle.length() == 0 ){
			tipTitle = getString(R.string.readIDCardOfApplicant);
		}
	}

	//��������������д
	protected void initView(View view) {

		mtvTitle = (TextView)view.findViewById(R.id.LABEL_title);

		tvPersonInfo = (TextView)view.findViewById( R.id.tv_person_info );
		imgViewPhoto = (ImageView)view.findViewById( R.id.iv_photo );
		agentPhone = (LinearLayout)view.findViewById( R.id.LAYOUT_AgentPhone );

		btnReadAgent = (Button)view.findViewById(R.id.BTN_ReadIDAgent); //��ȡ�����˶���֤��ť
		if(btnReadAgent != null){
			btnReadAgent.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					startReadIDCard(tipTitle, null, transactorType);	
				}
			});
		}

		//�༭���֤dialog
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

		//��ȡ����֤��ť
		btnReadSelf = (Button)view.findViewById(R.id.BTN_READIDCARD); 
		if(btnReadSelf != null){
			btnReadSelf.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					startReadIDCard(getString(R.string.readIDCardOfApplicant), null, FROM_SELF);
				}
			});
		}

		//������
		rg_Ransaction =	(RadioGroup)view.findViewById(R.id.RG_Ransaction);
		if( rg_Ransaction != null){
			rg_Ransaction.setOnCheckedChangeListener( new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup rg, int checkedId) {
					if(checkedId == R.id.RB_SelfTransaction) {
						activity.enableFragment("�����������Ϣ", false);
						agentPhone.setVisibility(View.GONE);
						deleteDataItem(R.id.EDIT_AgentMobile);
					} else if (checkedId == R.id.RB_ProxyTransaction) {
						activity.enableFragment("�����������Ϣ", true);
						agentPhone.setVisibility(View.VISIBLE);
						addDataItem(new InputDataItem( R.id.EDIT_AgentMobile, "agent_phone", InputDataItem.VALIDATETYPE_PHONE, "����д�����˵绰��"));
					}
				}
			});
		}

		//������֤��Ϣ��Ч������ʾ�༭�Ȱ�ť
		if( !isIdcardInfoValid ){
			showEditLayout();
			if( isEditInfoValid )
				setPersioInfo();
		}
	}

	//��ʾ������֤�ͱ༭��ť
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

	//���û�����������֤ʱ
	protected void onSkipReadIDCard(){
		showEditLayout();
	}

	//���ɹ���ȡ����֤ʱ
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
		String mstrInfo = "������" + editName +  "     ���֤�ţ�" + editId + "\r\n��֤���أ�" + editPolice;
		tvPersonInfo.setText(mstrInfo);
	}

	//����༭���֤��Ϣ
	public void enableEditIDInfo(){
		canEditIDInfo = true;
	}

	//���ö�����֤���˵�����
	public void setTransactorType( int type ){
		transactorType = type;
	}

	@Override
	protected HashMap<String, String> getRecordMap() {
		if(!this.isVisiable())
			return null;

		//��д�ɱ༭��ѡ��
		HashMap<String, String> map = new HashMap<String, String>();		
		Map<String, String> editmap = super.getRecordMap();
		if( editmap != null){
			map.putAll( editmap );
		}

		return map;
	}

	/*
	 * idCardMsgΪ���֤��Ϣ
	 * ���ڴ��һЩ���֤��������Ϣ
	 * ������������,Ҫ������д
	 */
	public void setIdentityCard(IDCardMsg idCardMsg,HashMap<String, String> map)
	{
		if( idCardMsg != null) {
			map.put("ID_type", "1");//��ʾ�������֤
			map.put("ID_num", idCardMsg.getId_num());
			map.put("address", idCardMsg.getAddress());//��ͥסַ
			map.put("name", idCardMsg.getName());
			map.put("sex", idCardMsg.getSex().equals("��")?"1":"2");
			map.put( "police", idCardMsg.getSign_office());
			map.put( "birthday_year", idCardMsg.getBirth_year());
			map.put( "birthday_month", idCardMsg.getBirth_month());
			map.put( "birthday_day", idCardMsg.getBirth_day());
			map.put( "ID_deadline_year", idCardMsg.getUseful_e_date_year());
			map.put( "ID_deadline_month", idCardMsg.getUseful_e_date_month());
			map.put( "ID_deadline_day", idCardMsg.getUseful_e_date_day());
			map.put("nation",idCardMsg.getNation_str());
			//�������28�ַ���ĩβ�ÿո��룬��������ϵͳҲ���Զ�����һ��ƴ�����Ҳ�׼ȷ����Ҫ���ǵ�
			String pinyinpadding = StringUtil.padding(idCardMsg.getPinyin(), 28);
			map.put( "name_pinyin", pinyinpadding );

			//�������ͺ͵�����
			//			if( idCardMsg.isLongTerm()){
			//				map.put( "ID_deadline_type", "2" );
			//			}else{
			//				map.put( "ID_deadline_type", "1" );
			//				map.put( "ID_deadline", idCardInfo.getValidEndYYYYMMDD());
			//			}
		}
	}

}
