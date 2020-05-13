package com.mob.secverify.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mob.tools.utils.ResHelper;

public abstract class BaseActivity extends Activity implements View.OnClickListener {
	private TextView leftTv;
	private TextView centerTv;
	private ImageView rightIv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		if (Build.VERSION.SDK_INT >= 21) {
//			// 设置沉浸式状态栏
//			View decorView = getWindow().getDecorView();
//			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//			decorView.setSystemUiVisibility(option);
//			// 设置状态栏透明
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			getWindow().setStatusBarColor(Color.TRANSPARENT);
//		}
		super.onCreate(savedInstanceState);

		// 加载View
		LayoutInflater inflater = LayoutInflater.from(this);
		ViewGroup container = (ViewGroup) inflater.inflate(R.layout.sec_verify_demo_container, null);
		int contentId = getContentViewId();
		if (contentId > 0) {
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			View content = inflater.inflate(contentId, null);
			container.addView(content, params);
		}
		setContentView(container);
		// 通知子类View加载完毕
		onViewCreated();
		initView();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == leftTv.getId()) {
			// 通知子类左上角View点击事件
			boolean handled = onLeftEvent();
			if (!handled) {
				finish();
			}
		} else if (id == rightIv.getId()) {
			// 通知子类右上角View点击事件
			onRightEvent();
		} else {
			// 其余点击传递给子类
			onViewClicked(v);
		}

	}

	protected abstract int getContentViewId();
	/**
	 * 定义TitleBar样式
	 *
	 * @param titleStyle TitleBar样式，默认为“左按钮ON” “右按钮OFF”
	 */
	protected abstract void getTitleStyle(TitleStyle titleStyle);
	protected abstract void onViewCreated();
	protected void onRightEvent(){}
	/**
	 * 当左上角返回按钮点击时调用
	 * @return true 事件被消耗
	 */
	protected boolean onLeftEvent() {
		return false;
	}
	protected void onViewClicked(View v) {}

	private void initView() {
		// 初始化TitleBar
		TitleStyle titleStyle = new TitleStyle();
		getTitleStyle(titleStyle);
		leftTv = findViewById(R.id.sec_verify_demo_title_bar_left);
		leftTv.setOnClickListener(this);
		rightIv = findViewById(R.id.sec_verify_demo_title_bar_right);
		rightIv.setOnClickListener(this);
		centerTv = findViewById(R.id.sec_verify_demo_title_bar_center);
		if (titleStyle != null) {
			if (titleStyle.showLeft) {
				leftTv.setVisibility(View.VISIBLE);
			} else {
				leftTv.setVisibility(View.GONE);
			}
			if (titleStyle.showRight) {
				rightIv.setVisibility(View.VISIBLE);
			} else {
				rightIv.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(titleStyle.titleResName)) {
				int titleResId = ResHelper.getStringRes(this, titleStyle.titleResName);
				if (titleResId > 0) {
					centerTv.setText(titleResId);
				}
			}
		}
	}

	protected class TitleStyle {
		public boolean showLeft = true;
		public boolean showRight = false;
		public String titleResName;
	}
}
