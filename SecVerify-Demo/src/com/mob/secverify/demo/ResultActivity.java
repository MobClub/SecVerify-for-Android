package com.mob.secverify.demo;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mob.secverify.demo.entity.LoginResult;
import com.mob.secverify.demo.util.Const;

public class ResultActivity extends BaseActivity {
	private TextView tryAgainTv;
	private TextView phoneTv;
	private TextView successTv;
	private ImageView backIv;
	private ImageView resultImgIv;
	private ImageView phoneIv;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_result;
	}

	@Override
	protected void getTitleStyle(TitleStyle titleStyle) {
		titleStyle.showLeft = true;
	}

	@Override
	protected void onViewCreated() {
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
//			if (Build.VERSION.SDK_INT >= 23){
//				getWindow().getDecorView().setSystemUiVisibility(
//						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//			}
		}
		overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
		initView();
		initContent();
	}

	private void initContent() {
		Intent intent = getIntent();
		if (intent.getBooleanExtra("sec_verify_demo_verify_success", true)) {
			resultImgIv.setImageDrawable(getResources().getDrawable(R.drawable.sec_verify_demo_verify_success));
			if (intent.hasExtra(Const.EXTRAS_DEMO_LOGIN_RESULT)) {
				LoginResult result = (LoginResult) intent.getSerializableExtra(Const.EXTRAS_DEMO_LOGIN_RESULT);
				if (result != null) {
					phoneTv.setText(result.getPhone());
				}
			} else {
				phoneTv.setVisibility(View.GONE);
				phoneIv.setVisibility(View.GONE);
			}
		} else {
			resultImgIv.setImageDrawable(getResources().getDrawable(R.drawable.sec_verify_demo_verify_failed));
//			Exception exception = (Exception) intent.getSerializableExtra(Const.EXTRAS_DEMO_LOGIN_RESULT);
			successTv.setText("登录失败");
			phoneTv.setVisibility(View.GONE);
			phoneIv.setVisibility(View.GONE);
		}

	}

	private void initView() {
		tryAgainTv = findViewById(R.id.sec_verify_demo_verify_result_one_more_try);
		phoneTv = findViewById(R.id.sec_verify_demo_success_phone_tv);
		successTv = findViewById(R.id.sec_verify_demo_verify_result_success);
		resultImgIv = findViewById(R.id.sec_verify_demo_verify_result_image);
		backIv = findViewById(R.id.sec_verify_demo_title_bar_left_iv);
		phoneIv = findViewById(R.id.sec_verify_demo_success_phone_iv);

		backIv.setOnClickListener(this);
		tryAgainTv.setOnClickListener(this);
	}

	@Override
	protected void onViewClicked(View v) {
		int id = v.getId();
		if (id == tryAgainTv.getId()) {
//			startActivity(new Intent(ResultActivity.this,MainActivity.class));
			this.finish();
		} else if (id == backIv.getId()) {
			this.finish();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		onCreate(null);
	}
}
