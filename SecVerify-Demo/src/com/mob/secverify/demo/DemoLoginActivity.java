package com.mob.secverify.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.mob.secverify.SecVerify;

public class DemoLoginActivity extends Activity implements View.OnClickListener {
	private Button doLoginBtn;
	private ImageView wechatIv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= 21){
			// 设置沉浸式状态栏
			View decorView = getWindow().getDecorView();
			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			decorView.setSystemUiVisibility(option);
//			 设置状态栏透明
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
			if (Build.VERSION.SDK_INT >= 23){
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
		}
		setContentView(R.layout.activity_demo_login);
		init();
	}

	private void init() {
		doLoginBtn = findViewById(R.id.sec_verify_demo_login_do_login);
		wechatIv = findViewById(R.id.sec_verify_demo_login_wechat);
		wechatIv = findViewById(R.id.sec_verify_demo_login_wechat);
		findViewById(R.id.sec_verify_demo_title_bar_left).setOnClickListener(this);

		doLoginBtn.setOnClickListener(this);
		wechatIv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == doLoginBtn.getId()) {
			goSuccessPage();
			SecVerify.finishOAuthPage();
		} else if (id == wechatIv.getId()) {
			SecVerify.finishOAuthPage();
		}
		this.finish();
	}

	private void goSuccessPage() {
		Intent i = new Intent(this, ResultActivity.class);
		i.putExtra("sec_verify_demo_verify_success", true);
		startActivity(i);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		onCreate(null);
	}
}
