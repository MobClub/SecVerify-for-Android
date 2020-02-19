package com.mob.secverify.demo.ui.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mob.secverify.demo.ResultListener;
import com.mob.tools.utils.ResHelper;

public class PrivacyDialog extends Dialog implements View.OnClickListener {

	private static PrivacyDialog dialog;
	private Context context;
	private WindowManager windowManager;
	private SpannableString dialogText;
	private ResultListener callback;

	public PrivacyDialog(Context context) {
		super(context, ResHelper.getStyleRes(context, "Dialog_Common"));
		this.context = context;
	}

	public PrivacyDialog(Context context, SpannableString dialogText, ResultListener callback) {
		super(context, ResHelper.getStyleRes(context, "Dialog_Common"));
		this.context = context;
		this.dialogText = dialogText;
		this.callback = callback;
	}

	public PrivacyDialog(Context context, int themeResId) {
		super(context, themeResId);
		this.context = context;
	}

//	public static void showPrivacyDialog(Context context) {
//		showPrivacyDialog(context, "", null);
//	}

	public static void showPrivacyDialog(Context context, SpannableString dialogText, ResultListener callback) {
		dismissPrivacyDialog();
		dialog = new PrivacyDialog(context, dialogText, callback);
		dialog.show();
	}

	public static void dismissPrivacyDialog() {
		try {
			if ((dialog != null) && dialog.isShowing()) {
				dialog.dismiss();
			}
		} catch (final IllegalArgumentException e) {
			// Handle or log or ignore
		} catch (final Exception e) {
			// Handle or log or ignore
		} finally {
			dialog = null;
		}

//		if (dialog != null && dialog.isShowing()) {
//			dialog.dismiss();
//			dialog = null;
//		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		View view = getLayoutInflater().inflate(ResHelper.getLayoutRes(context, "sec_verify_demo_common_alert_dialog"), null);
		int width = (int) (getDeviceWidth(context) * 0.8);
		int height = LinearLayout.LayoutParams.WRAP_CONTENT;
		setContentView(view, new LinearLayout.LayoutParams(width, height));

		initView();
	}

	private void initView() {
		TextView dialogCancel = findViewById(ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_cancel"));
		TextView dialogAllow = findViewById(ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_allow"));
		TextView dialogTitle = findViewById(ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_title"));
		TextView dialogTextTv = findViewById(ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_text"));
		dialogTitle.setText("服务授权");
		dialogAllow.setText("同意");
		dialogCancel.setText("拒绝");
		dialogAllow.setOnClickListener(this);
		dialogCancel.setOnClickListener(this);

		dialogTextTv.setText(dialogText);
		dialogTextTv.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private int getDeviceWidth(Context context) {
		if (windowManager == null) {
			windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {//按键回调方法
		if (keyCode == KeyEvent.KEYCODE_BACK) {//判断按键键值做出相应操作
			dismissPrivacyDialog();
		}
		return super.onKeyDown(keyCode, event);//其他按键继承系统属性
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_cancel")) {
			dismissPrivacyDialog();
			callback.onFailure(null);
		} else if (id == ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_allow")) {
			dismissPrivacyDialog();
			callback.onComplete(null);
		}
	}
}
