package com.centerm.autofill.app.nmgnx.currentopenaccount;

import com.centerm.autofill.app.nmnx.common.agent.AgentIDBlockFragment;
import com.centerm.autofill.app.nmnx.common.agent.SpecialPersionFragment;
import com.centerm.autofill.appframework.base.BaseScrollActivity;

import android.os.Bundle;

public class MainActivity extends BaseScrollActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, -1);

		setTranCode( "nmg101" );

		fragmentItems = new FragmentItem[]{new FragmentItem(new MainFragment(), "本人身份信息", true),
				new FragmentItem(new AgentIDBlockFragment(), "代理人身份信息", false),
				new FragmentItem(new SpecialPersionFragment(), "转出方身份证", false)};
		loadFragment();
	}

	@Override
	protected void initResource() {
		super.initResource();
		title = "活期存款开户";
	}

}
