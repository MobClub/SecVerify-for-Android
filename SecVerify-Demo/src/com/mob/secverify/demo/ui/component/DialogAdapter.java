package com.mob.secverify.demo.ui.component;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mob.MobSDK;
import com.mob.secverify.demo.R;
import com.mob.secverify.demo.util.OperatorUtils;
import com.mob.secverify.ui.AgreementPage;
import com.mob.secverify.ui.component.LoginAdapter;
import com.mob.tools.utils.ResHelper;

/**
 * 使用Adapter的方式修改授权页面ui，支持使用自己的inflate的xml布局
 */
public class DialogAdapter extends LoginAdapter {
	private Activity activity;
	private ViewGroup vgBody;
	private LinearLayout vgContainer;
	private RelativeLayout rlTitle;
	private Button btnLogin;
	private TextView tvSecurityPhone;
	private TextView tvOwnPhone;
	private TextView tvAgreement;
	private CheckBox cbAgreement;
	private View contentView;
	//可用于判断展示运营商隐私协议
	private String operator;
	private String url;

	@Override
	public void onCreate() {
		super.onCreate();
		//获取授权页面原有控件
		init();
		//设置授权页面主题
		setImmTheme();
		//设置授权页面方向
		requestOrientation();
		//隐藏 授权页面原有内容
		vgBody.setVisibility(View.GONE);
		rlTitle.setVisibility(View.GONE);
		//获取自己的View
		contentView = View.inflate(activity, R.layout.sec_verify_demo_dialog_one_key_login, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params.width = ResHelper.dipToPx(activity, 300);
		params.height = ResHelper.dipToPx(activity, 300);
		vgContainer.setGravity(Gravity.CENTER);
		vgContainer.setBackgroundColor(activity.getResources().getColor(R.color.sec_verify_demo_background_transparent));
		//添加自己的View到授权页面上，注意不要使用Activity来设置
		vgContainer.addView(contentView, params);

		initOwnView();
	}

	private void initOwnView() {
		tvOwnPhone = contentView.findViewById(R.id.sec_verify_page_one_key_login_phone);
		tvOwnPhone.setText(tvSecurityPhone.getText());

		tvAgreement = contentView.findViewById(R.id.sec_verify_page_login_use_this_number);
		tvAgreement.setText(buildSpanString());
		tvAgreement.setHighlightColor(activity.getResources().getColor(android.R.color.transparent));
		tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());

		contentView.findViewById(R.id.sec_verify_page_login_login_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击自己登录按钮时需要将默认的复选框设置为选中，并且点击原有的授权页面登录按钮
				cbAgreement.setChecked(true);
				btnLogin.performClick();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		//将授权页面本身控件的文本设置到自己的View上面
		tvOwnPhone.setText(tvSecurityPhone.getText());
	}

	private void init() {
		vgBody = getBodyView();
		vgContainer = (LinearLayout) getContainerView();
		activity = getActivity();
		rlTitle = getTitlelayout();
		btnLogin = getLoginBtn();
		tvSecurityPhone = getSecurityPhoneText();
		cbAgreement = getAgreementCheckbox();
		operator = getOperatorName();


//		//不支持匿名内部的方法来执行点击事件，会出现无法正常登录的情况
//		btnLogin.setOnClickListener(this);
//		ivLeftClose.setOnClickListener(this);
//		tvSwitchAcc.setOnClickListener(this);
//		cbAgreement.setOnClickListener(this);
//		cbAgreement.setOnCheckedChangeListener(this);
	}

	//UI主题为透明，所以Android 8.0不可设置为固定方向为横屏或者竖屏
	private void requestOrientation() {
//		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}


	private void setImmTheme() {
		if (Build.VERSION.SDK_INT >= 21) {
			// 设置沉浸式状态栏
			View decorView = activity.getWindow().getDecorView();
			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			decorView.setSystemUiVisibility(option);
//			 设置状态栏透明
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
			if (Build.VERSION.SDK_INT >= 23) {
				activity.getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
		}
		//是否占用状态栏的位置，false为占用，true为不占用
		vgContainer.setFitsSystemWindows(false);
		//是否全屏
//		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//如果不想适配P以上的水滴屏和刘海屏，可以在这里设置layoutInDisplayCutoutMode为其他的值
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
			lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
			activity.getWindow().setAttributes(lp);
		}
	}


	private SpannableString buildSpanString() {
		String operatorText = "";
		if (OperatorUtils.getCellularOperatorType() == 1) {
			operatorText = "《中国移动认证服务条款》";
			url = "https://wap.cmpassport.com/resources/html/contract.html";
		} else if (OperatorUtils.getCellularOperatorType() == 2) {
			operatorText = "《中国联通认证服务条款》";
			url = "https://ms.zzx9.cn/html/oauth/protocol2.html";
		} else if (OperatorUtils.getCellularOperatorType() == 3) {
			operatorText = "《中国电信认证服务条款》";
			url = "https://e.189.cn/sdk/agreement/content.do?type=main&appKey=&hidetop=true&returnUrl=";
		}
		String ageementText = "同意" + operatorText + "及《自有隐私协议》和" +
				"《自有服务策略》、《其他隐私协议》并授权秒验使用本机号码登录";
		String cusPrivacy1 = "《自有隐私协议》";
		String cusPrivacy2 = "《自有服务策略》";
		String cusPrivacy3 = "《其他隐私协议》";
		int baseColor = MobSDK.getContext().getResources().getColor(R.color.sec_verify_demo_text_color_common_black);
		int privacyColor = Color.parseColor("#FFFE7A4E");
		int cusPrivacyColor1 = Color.parseColor("#FF4E96FF");
		int cusPrivacyColor2 = Color.parseColor("#FF4E96FF");
		int cusPrivacyColor3 = Color.parseColor("#FFFE7A4E");
		SpannableString spanStr = new SpannableString(ageementText);
		int privacyIndex = ageementText.indexOf(operatorText);
		spanStr.setSpan(new ForegroundColorSpan(baseColor)
				, 0, ageementText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//设置文字的单击事件
		spanStr.setSpan(new ClickableSpan() {
			@Override
			public void updateDrawState(TextPaint ds) {
				ds.setUnderlineText(false);
			}

			@Override
			public void onClick(View widget) {
				gotoAgreementPage(url, "");
			}
		}, privacyIndex, privacyIndex + operatorText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//设置文字的前景色
		spanStr.setSpan(new ForegroundColorSpan(privacyColor), privacyIndex, privacyIndex + operatorText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (!TextUtils.isEmpty(cusPrivacy1)) {
			int privacy1Index = ageementText.indexOf(cusPrivacy1);
			//设置文字的单击事件
			spanStr.setSpan(new ClickableSpan() {
				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setUnderlineText(false);
				}

				@Override
				public void onClick(View widget) {
					gotoAgreementPage("https://www.mob.com", null);
//					if (wrapper != null && wrapper.cusAgreement1Clicked != null){
//						wrapper.cusAgreement1Clicked.handle();
//					}
				}
			}, privacy1Index, privacy1Index + "《自有隐私协议》".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//设置文字的前景色
			spanStr.setSpan(new ForegroundColorSpan(cusPrivacyColor1), privacy1Index, privacy1Index + cusPrivacy1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (!TextUtils.isEmpty(cusPrivacy2)) {
			int privacy2Index = ageementText.lastIndexOf(cusPrivacy2);
			//设置文字的单击事件
			spanStr.setSpan(new ClickableSpan() {
				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setUnderlineText(false);
				}

				@Override
				public void onClick(View widget) {
					gotoAgreementPage("https://www.baidu.com", null);
				}
			}, privacy2Index, privacy2Index + cusPrivacy2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//设置文字的前景色
			spanStr.setSpan(new ForegroundColorSpan(cusPrivacyColor2), privacy2Index, privacy2Index + cusPrivacy2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (!TextUtils.isEmpty(cusPrivacy3)) {
			int privacy3Index = ageementText.lastIndexOf(cusPrivacy3);
			//设置文字的单击事件
			spanStr.setSpan(new ClickableSpan() {
				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setUnderlineText(false);
					ds.linkColor = Color.parseColor("#FFFFFF");
				}

				@Override
				public void onClick(View widget) {
					gotoAgreementPage("https://www.baidu.com", null);
				}
			}, privacy3Index, privacy3Index + cusPrivacy3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//设置文字的前景色
			spanStr.setSpan(new ForegroundColorSpan(cusPrivacyColor3), privacy3Index, privacy3Index + cusPrivacy3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spanStr;
	}

	//可替换为跳转自己的webview
	private static void gotoAgreementPage(String agreementUrl, String title) {
		if (TextUtils.isEmpty(agreementUrl)) {
			return;
		}
		AgreementPage page = new AgreementPage();
		Intent i = new Intent();
		i.putExtra("extra_agreement_url", agreementUrl);
		if (!TextUtils.isEmpty(title)) {
			i.putExtra("privacy", title);
		}
		page.show(MobSDK.getContext(), i);
	}
}
