package com.mob.secverify.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.secverify.CustomUIRegister;
import com.mob.secverify.CustomViewClickListener;
import com.mob.secverify.SecVerify;
import com.mob.secverify.OperationCallback;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.datatype.UiSettings;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.secverify.demo.entity.LoginResult;
import com.mob.secverify.demo.exception.DemoException;
import com.mob.secverify.demo.login.LoginTask;
import com.mob.secverify.demo.ui.component.CommonProgressDialog;
import com.mob.secverify.demo.util.Const;
import com.mob.secverify.exception.VerifyErr;
import com.mob.secverify.exception.VerifyException;
import com.mob.secverify.log.VerifyLog;
import com.mob.secverify.ui.component.VerifyCommonButton;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	private static final int REQUEST_CODE = 1001;
	private GifImageView logoIv;
	private VerifyCommonButton oneKeyLoginBtn;
	private VerifyCommonButton verifyBtn;
	private TextView versionTv;
	private boolean devMode = false;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_main;
	}

	@Override
	protected void getTitleStyle(TitleStyle titleStyle) {
		titleStyle.showLeft = false;
	}

	@Override
	protected void onViewCreated() {

		initView();
		checkPermissions();
		// 建议提早调用该接口进行预登录，这将极大地加快验证流程
		preVerify();
	}

	@Override
	protected void onViewClicked(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.sec_verify_demo_main_verify: {
				verify();
				break;
			}
			case R.id.sec_verify_demo_main_version: {
				switchDevMode();
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			// Demo为了演示效果，从验证成功页面返回时可以重复验证，因此在onActivityResult中再次做预登录
			preVerify();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// Demo为了演示效果，动态授权结束后做一次预登录
		preVerify();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		logoIv = findViewById(R.id.sec_verify_demo_main_logo);
		oneKeyLoginBtn = findViewById(R.id.sec_verify_demo_main_one_key_login);
		verifyBtn = findViewById(R.id.sec_verify_demo_main_verify);
		versionTv = findViewById(R.id.sec_verify_demo_main_version);
		versionTv.setText(SecVerify.getVersion());

		logoIv.setImageResource(R.drawable.sec_verify_demo_tradition);
		oneKeyLoginBtn.setOnClickListener(this);
		verifyBtn.setOnClickListener(this);
		versionTv.setOnClickListener(this);
	}

	/* 检查使用权限 */
	private void checkPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			try {
				PackageManager pm = getPackageManager();
				PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
				ArrayList<String> list = new ArrayList<String>();
				for (String p : pi.requestedPermissions) {
					if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
						list.add(p);
					}
				}
				if (list.size() > 0) {
					String[] permissions = list.toArray(new String[list.size()]);
					if (permissions != null) {
						requestPermissions(permissions, 1);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}


	/**
	 * 预登录
	 *
	 * 建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
	 */
	private void preVerify() {
		SecVerify.preVerify(new OperationCallback() {
			@Override
			public void onComplete(Object data) {
				// Nothing to do
				if (devMode) {
					Toast.makeText(MainActivity.this, "预登录成功", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(VerifyException e) {
				// Nothing to do
				if (devMode) {
					// 登录失败
					Log.e(TAG, "preVerify failed", e);
					// 错误码
					int errCode = e.getCode();
					// 错误信息
					String errMsg = e.getMessage();
					// 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
					Throwable t = e.getCause();
					String errDetail = null;
					if (t != null) {
						errDetail = t.getMessage();
					}

					String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
					if (!TextUtils.isEmpty(errDetail)) {
						msg += "\n详细信息: " + errDetail;
					}
					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 免密登录
	 */
	private void verify() {
		// 自定义UI
		customizeUi();
		// 添加自定义控件
		// addCustomView();

		CommonProgressDialog.showProgressDialog(this);
		SecVerify.verify(new VerifyCallback() {
			@Override
			public void onOtherLogin() {
				// 用户点击“其他登录方式”，处理自己的逻辑
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, "其他账号登录", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onUserCanceled() {
				// 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, "用户取消登录", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(VerifyResult data) {
				CommonProgressDialog.dismissProgressDialog();
				if (data != null) {
					Log.d(TAG, data.toJSONString());
					// 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
					CommonProgressDialog.showProgressDialog(MainActivity.this);
					LoginTask.getInstance().login(data, new ResultListener<com.mob.secverify.demo.entity.LoginResult>() {
						@Override
						public void onComplete(com.mob.secverify.demo.entity.LoginResult data) {
							CommonProgressDialog.dismissProgressDialog();
							Log.d(TAG, "Login success. data: " + data.toJSONString());
							vibrate();
							// 服务端登录成功，跳转成功页
							gotoSuccessActivity(data);
						}

						@Override
						public void onFailure(DemoException e) {
							// 登录失败
							Log.e(TAG, "login failed", e);
							CommonProgressDialog.dismissProgressDialog();
							// 错误码
							int errCode = e.getCode();
							// 错误信息
							String errMsg = e.getMessage();
							// 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
							Throwable t = e.getCause();
							String errDetail = null;
							if (t != null) {
								errDetail = t.getMessage();
							}

							String msg = "获取授权码成功，应用服务器登录失败" + "\n错误码: " + errCode + "\n错误信息: " + errMsg;
							if (!TextUtils.isEmpty(errDetail)) {
								msg += "\n详细信息: " + errDetail;
							}
							if (!devMode) {
								msg = "当前网络异常";
							}
							Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

							// Demo为了演示效果，登录失败时也做一次预登录
							preVerify();
						}
					});
				}
			}

			@Override
			public void onFailure(VerifyException e) {
				// 登录失败
				CommonProgressDialog.dismissProgressDialog();
				Log.e(TAG, "verify failed", e);
				// 错误码
				int errCode = e.getCode();
				// 错误信息
				String errMsg = e.getMessage();
				// 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
				Throwable t = e.getCause();
				String errDetail = null;
				if (t != null) {
					errDetail = t.getMessage();
				}

				String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
				if (!TextUtils.isEmpty(errDetail)) {
					msg += "\n详细信息: " + errDetail;
				}
				if (!devMode) {
					msg = "当前网络异常";
					if (errCode == VerifyErr.C_LACK_OF_PERMISSIONS.getCode()
					|| errCode == VerifyErr.C_NO_SIM.getCode()
					|| errCode == VerifyErr.C_UNSUPPORTED_OPERATOR.getCode()
					|| errCode == VerifyErr.C_CELLULAR_DISABLED.getCode()) {
						msg = errMsg;
					}
				}
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 自定义授权页面UI样式
	 */
	private void customizeUi() {
		UiSettings uiSettings = new UiSettings.Builder()
				/** 标题栏 */
				// 标题栏背景色资源ID
				.setNavColorId(R.color.sec_verify_demo_common_bg)
				// 标题栏标题文字资源ID
//				.setNavTextId(R.string.sec_verify_demo_verify)
				// 标题栏文字颜色资源ID
//				.setNavTextColorId(R.color.sec_verify_demo_text_color_common_black)
				// 标题栏左侧关闭按钮图片资源ID
				.setNavCloseImgId(R.drawable.sec_verify_demo_close)
				/** Logo */
				// Logo图片资源ID，默认使用应用图标
				.setLogoImgId(R.drawable.ic_launcher)
				/** 手机号 */
				// 脱敏手机号字体颜色资源ID
				.setNumberColorId(R.color.sec_verify_demo_text_color_common_black)
				// 脱敏手机号字体大小资源ID
				.setNumberSizeId(R.dimen.sec_verify_demo_text_size_m)
				/** 切换帐号 */
				// 切换账号字体颜色资源ID
				.setSwitchAccColorId(R.color.sec_verify_demo_text_color_blue)
				// 切换账号是否显示，默认显示
				.setSwitchAccHidden(false)
				/** 登录按钮 */
				// 登录按钮背景图资源ID，建议使用shape
				.setLoginBtnImgId(R.drawable.sec_verify_demo_shape_rectangle)
				// 登录按钮文字资源ID
				.setLoginBtnTextId(R.string.sec_verify_demo_login)
				// 登录按钮字体颜色资源ID
				.setLoginBtnTextColorId(R.color.sec_verify_demo_text_color_common_white)
				/** 隐私协议 */
				// 隐私协议复选框背景图资源ID，建议使用selector
				.setCheckboxImgId(R.drawable.sec_verify_demo_customized_checkbox_selector)
				// 隐私协议复选框默认状态，默认为“选中”
				.setCheckboxDefaultState(true)
				// 隐私协议字体颜色资源ID（自定义隐私协议的字体颜色也受该值影响）
				.setAgreementColorId(R.color.sec_verify_demo_main_color)
//				// 自定义隐私协议一文字资源ID
//				.setCusAgreementNameId1(R.string.sec_verify_demo_customize_agreement_name_1)
//				// 自定义隐私协议一URL
//				.setCusAgreementUrl1("http://www.baidu.com")
//				// 自定义隐私协议二文字资源ID
//				.setCusAgreementNameId2(R.string.sec_verify_demo_customize_agreement_name_2)
//				// 自定义隐私协议二URL
//				.setCusAgreementUrl2("https://www.jianshu.com")
				.build();
		SecVerify.setUiSettings(uiSettings);
	}

	/**
	 * 添加自定义view
	 */
	private void addCustomView() {
		// view仅用于使btn1和btn2水平居中显示
		View view = new View(this);
		view.setId(R.id.customized_view_id);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1,1);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.bottomMargin = 250;
		view.setLayoutParams(params);

		// 自定义按钮1
		Button btn1 = new Button(this);
		btn1.setId(R.id.customized_btn_id_1);
		btn1.setText("按钮1");
		btn1.setTextColor(Color.BLACK);
		btn1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.addRule(RelativeLayout.LEFT_OF, view.getId());
		params1.bottomMargin = 250;
		params1.rightMargin = 10;
		btn1.setLayoutParams(params1);

		// 自定义按钮2
		Button btn2 = new Button(this);
		btn2.setId(R.id.customized_btn_id_2);
		btn2.setText("按钮2");
		btn2.setTextColor(Color.BLACK);
		btn2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params2.addRule(RelativeLayout.RIGHT_OF, view.getId());
		params2.bottomMargin = 250;
		params2.leftMargin = 10;
		btn2.setLayoutParams(params2);

		List<View> views = new ArrayList<View>();
		views.add(view);
		views.add(btn1);
		views.add(btn2);

		CustomUIRegister.addCustomizedUi(views, new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					msg = "按钮1 clicked";
					// 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
					SecVerify.finishOAuthPage();
				} else if (id == R.id.customized_btn_id_2) {
					msg = "按钮2 clicked";
				}
				// 关闭加载框
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void gotoSuccessActivity(LoginResult data) {
		Intent i = new Intent(this, SuccessActivity.class);
		i.putExtra(Const.EXTRAS_DEMO_LOGIN_RESULT, data);
		startActivityForResult(i, REQUEST_CODE);
	}

	private void vibrate() {
		Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		if (vibrator != null) {
			if (android.os.Build.VERSION.SDK_INT >= 26) {
				VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, 20);
				vibrator.vibrate(vibrationEffect);
			} else {
				vibrator.vibrate(500);
			}
		}
	}

	private void switchDevMode() {
		if (devMode) {
			devMode = false;
			Toast.makeText(this, "开发者模式：Off", Toast.LENGTH_SHORT).show();
		} else {
			devMode = true;
			Toast.makeText(this, "开发者模式：On", Toast.LENGTH_SHORT).show();
		}
	}
}
