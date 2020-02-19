package com.mob.secverify.demo.ui.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mob.secverify.demo.ResultListener;
import com.mob.tools.utils.ResHelper;

public class CommonAlertDialog extends Dialog implements View.OnClickListener {

	private Context context;
	private static CommonAlertDialog dialog;
	private WindowManager windowManager;
	private String dialogText;
	private SpannableString spannableDialogText;
	private ResultListener callback;

	public CommonAlertDialog(Context context) {
		super(context, ResHelper.getStyleRes(context, "Dialog_Common"));
		this.context = context;
	}

	public CommonAlertDialog(Context context, String dialogText,ResultListener callback) {
		super(context, ResHelper.getStyleRes(context, "Dialog_Common"));
		this.context = context;
		this.dialogText = dialogText;
		this.callback = callback;
	}

	public CommonAlertDialog(Context context, SpannableString dialogText,ResultListener callback) {
		super(context, ResHelper.getStyleRes(context, "Dialog_Common"));
		this.context = context;
		this.spannableDialogText = dialogText;
		this.callback = callback;
	}

	public CommonAlertDialog(Context context,int themeResId) {
		super(context, themeResId);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		int width = (int) (getDeviceWidth(context) * 0.6);
		int height = (int) (getDeviceWidth(context) * 0.5);
		View view = getLayoutInflater().inflate(ResHelper.getLayoutRes(context, "sec_verify_demo_common_alert_dialog"), null);
		setContentView(view, new LinearLayout.LayoutParams(width, height));

		initView();
	}

	private void initView() {
		TextView dialogCancel = findViewById(ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_cancel"));
		TextView dialogAllow = findViewById(ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_allow"));
		TextView dialogTitle = findViewById(ResHelper.getIdRes(context, "sec_verify_demo_alert_dialog_title"));
		TextView dialogTextTv = findViewById(ResHelper.getIdRes(context,"sec_verify_demo_alert_dialog_text"));
		dialogTitle.setText("无法使用移动网络");
		dialogAllow.setText("前往");
		dialogCancel.setText("取消");
		if (!TextUtils.isEmpty(dialogText)){
			dialogTextTv.setText(dialogText);
		} else if (!TextUtils.isEmpty(spannableDialogText)){
			dialogTextTv.setText(spannableDialogText);
		}
	}

	public static void showProgressDialog(Context context) {
		showProgressDialog(context,"",null);
	}

	public static void showProgressDialog(Context context, String dialogText, ResultListener callback) {
		dismissProgressDialog();
		dialog = new CommonAlertDialog(context,dialogText,callback);
		dialog.show();
	}

	public static void showProgressDialog(Context context, SpannableString dialogText, ResultListener callback) {
		dismissProgressDialog();
		dialog = new CommonAlertDialog(context,dialogText,callback);
		dialog.show();
	}

	public static void dismissProgressDialog() {
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


	private int getDeviceWidth(Context context) {
		if (windowManager == null) {
			windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){//按键回调方法
		if(keyCode == KeyEvent.KEYCODE_BACK){//判断按键键值做出相应操作
			dismissProgressDialog();
		}
		return super.onKeyDown(keyCode,event);//其他按键继承系统属性
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == ResHelper.getIdRes(context,"sec_verify_demo_alert_dialog_cancel")){
			dismissProgressDialog();
		} else if (id == ResHelper.getIdRes(context,"sec_verify_demo_alert_dialog_allow")){
			dismissProgressDialog();
			callback.onComplete(null);
		}
	}
}
