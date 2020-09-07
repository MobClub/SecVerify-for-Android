package com.mob.secverify.demo.ui.component;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.secverify.demo.R;
import com.mob.secverify.demo.util.OperatorUtils;
import com.mob.secverify.ui.AgreementPage;
import com.mob.secverify.ui.component.LoginAdapter;
import com.mob.tools.utils.ResHelper;

/**
 * 使用Adapter的方式修改授权页面ui,通过修改授权页面的控件属性，达到修改目的
 *
 *  * todo 需要注意以下内容
 *  *  1、在结束当前授权页面时需要调用SecVerify.finishOAuthPage();来结束，否则会影响下次进入
 *  *  2、在点击登录之后，不论登录成功或者失败，需要SecVerify.finishOAuthPage();结束当前页面， 否则会影响电信授权页面的回调导致页面无法结束
 *  *  3、设置脱敏手机号需要在onResume中设置，onCreate中可能还未拿到这个脱敏手机号
 */
public class DemoAdapter extends LoginAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
	private Activity activity;
	private ViewGroup vgBody;
	private ViewGroup vgContainer;
	private RelativeLayout rlTitle;
	private ImageView ivLogo;
	private Button btnLogin;
	private TextView tvSecurityPhone;
	private CheckBox cbAgreement;
	private RelativeLayout rlPhone;
	private TextView tvSwitchAcc;
	private RelativeLayout rlAgreement;
	private TextView tvSlogan;
	private ImageView ivLeftClose;
	private TextView tvCenterText;
	private TextView tvAgreement;
	private String operator;
	private String url;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
		setImmTheme();
		requestOrientation();
		if (activity.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			//当前为竖屏
			rebuildBodyContent();
		} else {
			//当前为横屏设置，参考rebuildBodyContent();设置
		}
	}

	//Android 8.0不可设置为固定方向
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

	private void rebuildBodyContent() {
		//导航栏以下背景图片设置
		vgBody.setBackground(activity.getResources().getDrawable(R.color.sec_verify_text_color_common_white));
		//整个授权页面背景图片设置
		vgContainer.setBackground(activity.getResources().getDrawable(R.color.sec_verify_text_color_common_white));

		//头部导航栏设置
		rlTitle.setVisibility(View.VISIBLE);
		rlTitle.setBackgroundColor(activity.getResources().getColor(R.color.sec_verify_demo_main_color));
		rlTitle.getBackground().setAlpha(0);//透明度
		LinearLayout.LayoutParams rlTitleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		rlTitleParams.height = ResHelper.dipToPx(activity,50);
		rlTitle.setLayoutParams(rlTitleParams);

		//关闭图标相关设置
		ivLeftClose.setVisibility(View.VISIBLE);
		ivLeftClose.setImageDrawable(activity.getResources().getDrawable(R.drawable.sec_verify_page_one_key_login_close));
		ivLeftClose.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		RelativeLayout.LayoutParams ivLeftCloseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ivLeftCloseParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		ivLeftCloseParams.addRule(RelativeLayout.CENTER_VERTICAL);
		ivLeftCloseParams.leftMargin = ResHelper.dipToPx(activity,20);
		ivLeftClose.setLayoutParams(ivLeftCloseParams);
		//导航栏标题
		tvCenterText.setText("一键登录");
		tvCenterText.setVisibility(View.VISIBLE);
		tvCenterText.setTypeface(Typeface.DEFAULT_BOLD);
		tvCenterText.setTextSize(16);
		tvCenterText.setTextColor(activity.getResources().getColor(R.color.sec_verify_demo_text_color_common_black));
		RelativeLayout.LayoutParams tvCenterTextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tvCenterTextParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		tvCenterText.setLayoutParams(tvCenterTextParams);
		//logo
		ivLogo.setVisibility(View.VISIBLE);
		ivLogo.setImageDrawable(activity.getResources().getDrawable(R.drawable.sec_verify_page_one_key_login_logo));
		ivLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
		RelativeLayout.LayoutParams ivLogoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ivLogoParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ivLogoParams.width = ResHelper.dipToPx(activity,80);
		ivLogoParams.height = ResHelper.dipToPx(activity,80);
		ivLogoParams.topMargin = ResHelper.dipToPx(activity,30);
		ivLogo.setLayoutParams(ivLogoParams);
		//脱敏手机号
		tvSecurityPhone.setTextColor(activity.getResources().getColor(R.color.sec_verify_demo_text_color_common_black));
		tvSecurityPhone.setTextSize(20);
		tvSecurityPhone.setTypeface(Typeface.DEFAULT_BOLD);
		tvSecurityPhone.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams tvSecurityPhoneParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tvSecurityPhoneParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tvSecurityPhoneParams.topMargin = ResHelper.dipToPx(activity,135);
		rlPhone.setLayoutParams(tvSecurityPhoneParams); //注意是rlPhone 不是tvSecurityPhone
		//切换其他方式登录
		tvSwitchAcc.setVisibility(View.VISIBLE);
		tvSwitchAcc.setTypeface(Typeface.DEFAULT_BOLD);
		tvSwitchAcc.setTextSize(16);
		tvSwitchAcc.setTextColor(activity.getResources().getColor(R.color.sec_verify_demo_text_color_blue));
		tvSwitchAcc.setText("切换其他方式登录");
		RelativeLayout.LayoutParams tvSwitchAccParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tvSwitchAccParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tvSwitchAccParams.topMargin = ResHelper.dipToPx(activity,180);
		tvSwitchAcc.setLayoutParams(tvSwitchAccParams);
		//登录按钮
		btnLogin.setText("登录");
		btnLogin.setBackground(activity.getResources().getDrawable(R.drawable.sec_verify_demo_shape_rectangle));
		btnLogin.setTextColor(activity.getResources().getColor(R.color.sec_verify_demo_text_color_common_white));
		btnLogin.setTextSize(16);
		btnLogin.setTypeface(Typeface.DEFAULT_BOLD);
		btnLogin.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams btnLoginParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		btnLoginParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
		btnLoginParams.height = ResHelper.dipToPx(activity,45);
		btnLoginParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		btnLoginParams.topMargin = ResHelper.dipToPx(activity,220);
		btnLoginParams.leftMargin = ResHelper.dipToPx(activity,30);
		btnLoginParams.rightMargin = ResHelper.dipToPx(activity,30);
		btnLogin.setLayoutParams(btnLoginParams);
		//隐私协议
		cbAgreement.setVisibility(View.VISIBLE);
		cbAgreement.setButtonDrawable(activity.getResources().getDrawable(R.drawable.customized_checkbox_selector));
		cbAgreement.setChecked(false);
		tvAgreement.setText(buildSpanString());
		tvAgreement.setHighlightColor(activity.getResources().getColor(android.R.color.transparent));
		tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
		rlAgreement.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams rlAgreementParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		rlAgreementParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rlAgreementParams.topMargin = ResHelper.dipToPx(activity,280);
		rlAgreementParams.leftMargin = ResHelper.dipToPx(activity,30);
		rlAgreementParams.rightMargin = ResHelper.dipToPx(activity,30);
		rlAgreementParams.addRule(Gravity.LEFT);
		rlAgreement.setLayoutParams(rlAgreementParams);

		//slogan
		String operatorSlogan = "";
		if (OperatorUtils.getCellularOperatorType() == 1) {
			operatorSlogan = "中国移动提供认证服务";
		} else if (OperatorUtils.getCellularOperatorType() == 2) {
			operatorSlogan = "中国联通提供认证服务";
		} else if (OperatorUtils.getCellularOperatorType() == 3) {
			operatorSlogan = "中国电信提供认证服务";
		}
		tvSlogan.setText(operatorSlogan);
		tvSlogan.setTextSize(12);
		tvSlogan.setTextColor(activity.getResources().getColor(R.color.sec_verify_demo_text_color_common_black));
		tvSlogan.setTypeface(Typeface.DEFAULT_BOLD);
		tvSlogan.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams tvSloganParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tvSloganParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tvSloganParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		tvSloganParams.bottomMargin = ResHelper.dipToPx(activity,30);
		tvSlogan.setLayoutParams(tvSloganParams);
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

	private void init() {
		vgBody = getBodyView();
		vgContainer = getContainerView();
		activity = getActivity();
		rlTitle = getTitlelayout();
		ivLeftClose = getLeftCloseImage();
		tvCenterText = getCenterText();
		ivLogo = getLogoImage();
		btnLogin = getLoginBtn();
		tvSecurityPhone = getSecurityPhoneText();
		cbAgreement = getAgreementCheckbox();
		rlPhone = getPhoneLayout();
		tvSwitchAcc = getSwitchAccText();
		rlAgreement = getAgreementLayout();
		tvSlogan = getSloganText();
		tvAgreement = getAgreementText();
		operator = getOperatorName();

		//不支持匿名内部的方法来执行点击事件，会出现无法正常登录的情况
		btnLogin.setOnClickListener(this);
		ivLeftClose.setOnClickListener(this);
		tvSwitchAcc.setOnClickListener(this);
		cbAgreement.setOnClickListener(this);
		cbAgreement.setOnCheckedChangeListener(this);
	}


	@Override
	public void onClick(View v) {
		if (v.getId() == btnLogin.getId()) {
			Toast.makeText(activity.getApplicationContext(), "自定义Adapter 登录按钮点击事件", Toast.LENGTH_SHORT).show();
		} else if (v.getId() == ivLeftClose.getId()) {
			Toast.makeText(activity.getApplicationContext(), "自定义Adapter 关闭按钮点击事件", Toast.LENGTH_SHORT).show();
		} else if (v.getId() == tvSwitchAcc.getId()) {
			Toast.makeText(activity.getApplicationContext(), "自定义Adapter 切换登录按钮点击事件", Toast.LENGTH_SHORT).show();
		} else if (v.getId() == cbAgreement.getId()) {
			Toast.makeText(activity.getApplicationContext(), "自定义Adapter 复选框点击事件", Toast.LENGTH_SHORT).show();
		}
		//一定要记得加上这句
		super.onClick(v);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		//如果不加这句，那么OtherOAuthPageCallBack 不会有复选框改变的回调
		super.onCheckedChanged(buttonView, isChecked);
	}
}
