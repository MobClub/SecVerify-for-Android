package com.mob.secverify.demo;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mob.secverify.demo.entity.LoginResult;
import com.mob.secverify.demo.util.Const;

public class SuccessActivity extends BaseActivity {
	private static final String TAG = "SuccessActivity";
	private Button toHomepageBtn;
	private TextView phoneTv;
	private String phone;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_success;
	}

	@Override
	protected void getTitleStyle(TitleStyle titleStyle) {
		titleStyle.showLeft = false;
	}

	@Override
	protected void onViewCreated() {
		Intent i = getIntent();
		if (i != null) {
			LoginResult result = (LoginResult) i.getSerializableExtra(Const.EXTRAS_DEMO_LOGIN_RESULT);
			if (result != null) {
				phone = result.getPhone();
			}
		}
		initView();
	}

	@Override
	protected void onViewClicked(View v) {
		int id = v.getId();
		if (id == toHomepageBtn.getId()) {
			finish();
		}
	}

	private void initView() {
		toHomepageBtn = findViewById(R.id.sec_verify_demo_success_one_more_try_btn);
		toHomepageBtn.setOnClickListener(this);
		phoneTv = findViewById(R.id.sec_verify_demo_success_phone_tv);
		phoneTv.setText(phone);
	}
}
