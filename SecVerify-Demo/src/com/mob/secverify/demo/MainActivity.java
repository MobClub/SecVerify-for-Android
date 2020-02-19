package com.mob.secverify.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.PrivacyPolicy;
import com.mob.secverify.CustomUIRegister;
import com.mob.secverify.CustomViewClickListener;
import com.mob.secverify.OAuthPageEventCallback;
import com.mob.secverify.OperationCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.UiLocationHelper;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.datatype.LandUiSettings;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.secverify.demo.entity.LoginResult;
import com.mob.secverify.demo.exception.DemoException;
import com.mob.secverify.demo.login.LoginTask;
import com.mob.secverify.demo.ui.component.CommonAlertDialog;
import com.mob.secverify.demo.ui.component.CommonProgressDialog;
import com.mob.secverify.demo.ui.component.PrivacyDialog;
import com.mob.secverify.demo.util.Const;
import com.mob.secverify.demo.util.CustomizeUtils;
import com.mob.secverify.exception.VerifyErr;
import com.mob.secverify.exception.VerifyException;
import com.mob.secverify.ui.AgreementPage;
import com.mob.tools.utils.SharePrefrenceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	private static final int REQUEST_CODE = 1001;
	private GifImageView logoIv;
	private Button verifyBtn;
	private TextView versionTv;
	private boolean devMode = false;
	private int defaultUi = 0;
	private boolean isFirstTime = true;
	private SharePrefrenceHelper sharePrefrenceHelper;

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

		initView();
		checkPermissions();
		// 建议提早调用该接口进行预登录，这将极大地加快验证流程
		preVerify();
		showMobPrivacy();
	}

	private void showMobPrivacy() {
		MobSDK.setAllowDialog(true);

		sharePrefrenceHelper = new SharePrefrenceHelper(MobSDK.getContext());
		sharePrefrenceHelper.open("privacy", 1);

		if (sharePrefrenceHelper.getBoolean("isGrant")) {
			return;
		}

		MobSDK.getPrivacyPolicyAsync(MobSDK.POLICY_TYPE_URL, new PrivacyPolicy.OnPolicyListener() {
			@Override
			public void onComplete(PrivacyPolicy data) {
				if (data != null) {
					// 富文本内容
					final String url = data.getContent();
					String content = MainActivity.this.getResources().getString(R.string.sec_verify_demo_privacy_text);
					SpannableString spanStr = new SpannableString(content);
//					spanStr.setSpan(new ForegroundColorSpan(baseColor), 0, agreement.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					String mobService = "《MobService隐私条款》";
					int privacyIndex = content.indexOf(mobService);
					spanStr.setSpan(new ForegroundColorSpan(Color.parseColor("#FE7A4E")), privacyIndex, privacyIndex + mobService.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					spanStr.setSpan(new ClickableSpan() {
						@Override
						public void updateDrawState(TextPaint ds) {
							ds.setUnderlineText(false);
						}

						@Override
						public void onClick(View widget) {
							AgreementPage page = new AgreementPage();
							Intent i = new Intent();
							i.putExtra("extra_agreement_url", url);
							page.show(MainActivity.this, i);
						}
					}, privacyIndex, privacyIndex + mobService.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


					PrivacyDialog.showPrivacyDialog(MainActivity.this, spanStr, new ResultListener() {
						@Override
						public void onComplete(Object data) {
							submitPolicyGrantResult(true);
							sharePrefrenceHelper.putBoolean("isGrant", true);
						}

						@Override
						public void onFailure(DemoException e) {
							submitPolicyGrantResult(false);
							sharePrefrenceHelper.putBoolean("isGrant", true);
						}
					});

				}
			}

			@Override
			public void onFailure(Throwable t) {
				Log.d(TAG, "隐私协议内容文本获取失败");
			}
		});
	}

	private void submitPolicyGrantResult(boolean grantResult) {
		MobSDK.submitPolicyGrantResult(grantResult, new com.mob.OperationCallback<Void>() {
			@Override
			public void onComplete(Void data) {
				Log.d(TAG, "隐私协议授权结果提交：成功");
			}

			@Override
			public void onFailure(Throwable t) {
				Log.d(TAG, "隐私协议授权结果提交：失败");
			}
		});
	}

	@Override
	protected void onViewClicked(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.sec_verify_demo_main_verify: {
				// 添加自定义控件
				// 自定义UI
				if (defaultUi == 0) {
					addCustomView();
					customizeUi();
				} else if (defaultUi == 1) {
					customizeUi1();
					SecVerify.autoFinishOAuthPage(false);
					SecVerify.otherLoginAutoFinishOAuthPage(false);
				} else if (defaultUi == 2) {
					addCustomView1();
					customizeUi2();
					SecVerify.autoFinishOAuthPage(true);
					SecVerify.otherLoginAutoFinishOAuthPage(true);
				} else if (defaultUi == 3) {
					addCustomView4();
					customizeUi4();
				}
				verify();
				break;
			}
			case R.id.sec_verify_demo_main_version: {
				switchDevMode();
				break;
			}
			case R.id.sec_verify_demo_main_logo: {
				defaultUi++;
				if (defaultUi > 3) {
					defaultUi = 0;
				}
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
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
		verifyBtn = findViewById(R.id.sec_verify_demo_main_verify);
		versionTv = findViewById(R.id.sec_verify_demo_main_version);
		versionTv.setText(SecVerify.getVersion());

		logoIv.setImageResource(R.drawable.sec_verify_demo_tradition);
		verifyBtn.setOnClickListener(this);
		versionTv.setOnClickListener(this);
		logoIv.setOnClickListener(this);
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
	 * <p>
	 * 建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
	 */
	private void preVerify() {
		//设置在1000-10000之内
		SecVerify.setTimeOut(5000);
		//移动的debug tag 是CMCC-SDK,电信是CT_
		SecVerify.setDebugMode(true);
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
				Throwable t = e.getCause();
				String errDetail = null;
				if (t != null){
					errDetail = t.getMessage();
				}

				if (devMode) {
					// 登录失败
					Log.e(TAG, "preVerify failed", e);
					// 错误码
					int errCode = e.getCode();
					// 错误信息
					String errMsg = e.getMessage();
					// 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
					String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
					if (!TextUtils.isEmpty(errDetail)) {
						msg += "\n详细信息: " + errDetail;
					}
					Log.e(TAG,msg);
					Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				dealWithOppo(errDetail);

			}
		});
	}

	/**
	 * 免密登录
	 */
	private void verify() {
		CommonProgressDialog.showProgressDialog(this);
		//需要在verify之前设置
		SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
			@Override
			public void initCallback(OAuthPageEventResultCallback cb) {
				cb.pageOpenCallback(new PageOpenedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " pageOpened");
					}
				});
				cb.loginBtnClickedCallback(new LoginBtnClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " loginBtnClicked");
					}
				});
				cb.agreementPageClosedCallback(new AgreementPageClosedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " agreementPageClosed");
					}
				});
				cb.agreementPageOpenedCallback(new AgreementClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " agreementPageOpened");
					}
				});
				cb.cusAgreement1ClickedCallback(new CusAgreement1ClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " cusAgreement1ClickedCallback");
					}
				});
				cb.cusAgreement2ClickedCallback(new CusAgreement2ClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " cusAgreement2ClickedCallback");
					}
				});
				cb.checkboxStatusChangedCallback(new CheckboxStatusChangedCallback() {
					@Override
					public void handle(boolean b) {
						Log.i(TAG,System.currentTimeMillis() + " current status is " + b);
					}
				});
				cb.pageCloseCallback(new PageClosedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " pageClosed");
						HashMap<String, List<Integer>> map = UiLocationHelper.getInstance().getViewLocations();
						if (map == null) {
							return;
						}
						for (String key : map.keySet()) {
							List<Integer> locats = map.get(key);
							if (locats != null && locats.size() > 0) {
								for (int i : locats) {
									Log.i(TAG, i + " xywh");
								}
							}
						}
					}
				});
			}
		});
		SecVerify.verify(new VerifyCallback() {
			@Override
			public void onOtherLogin() {
				if (defaultUi == 1){
					//成功之后不会自动关闭授权页面，需要手动关闭
					SecVerify.finishOAuthPage();
				}
				// 用户点击“其他登录方式”，处理自己的逻辑
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, "其他方式登录", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onUserCanceled() {
				// 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, "用户取消登录", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(VerifyResult data) {
				if (defaultUi == 1){
					//成功之后不会自动关闭授权页面，需要手动关闭
					SecVerify.finishOAuthPage();
				}
				CommonProgressDialog.dismissProgressDialog();
				if (data != null) {
					Log.d(TAG, data.toJSONString());
					// 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
					CommonProgressDialog.showProgressDialog(MainActivity.this);
					LoginTask.getInstance().login(data, new ResultListener<LoginResult>() {
						@Override
						public void onComplete(LoginResult data) {
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
								msg = "当前网络不稳定";
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
				if (defaultUi == 1){
					//失败之后不会自动关闭授权页面，需要手动关闭
					SecVerify.finishOAuthPage();
				}
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
					msg = "当前网络不稳定";
					if (errCode == VerifyErr.C_LACK_OF_PERMISSIONS.getCode()
							|| errCode == VerifyErr.C_NO_SIM.getCode()
							|| errCode == VerifyErr.C_UNSUPPORTED_OPERATOR.getCode()
							|| errCode == VerifyErr.C_CELLULAR_DISABLED.getCode()) {
						msg = errMsg;
					}
				}
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
				dealWithOppo(errDetail);
			}
		});
	}

	private void dealWithOppo(String errDetail){
		if (!Build.BRAND.equalsIgnoreCase("OPPO") || TextUtils.isEmpty(errDetail)){
			return;
		}
		if (errDetail.contains("\"result\":80007,\"msg\":\"IO异常-presdk-Failed to connect to ") ||
				errDetail.contains("\"resultCode\":\"200023\",\"resultString\":\"登录超时\"") ||
				errDetail.contains("\"msg\":\"网络访问异常\",\"code\":1,\"response\":null,\"seq\":null,\"status\":302002")) {
			if (isFirstTime){
				isFirstTime = false;
				//需要额外的 CHANGE_WIFI_STATE 权限
				WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
				wifiManager.disconnect();
				wifiManager.reconnect();
			} else {
				CommonAlertDialog.showProgressDialog(MainActivity.this, "OPPO系统首次需手动关闭WIFI并允许使用移动网络或者前往双卡与移动网络设置中的 使用WLAN与移动网络的应用 设置当前APP可使用移动数据", new ResultListener() {
					@Override
					public void onComplete(Object data) {
						startActivity(new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS));
					}

					@Override
					public void onFailure(DemoException e) {

					}
				});
			}
		}
	}


	private void customizeUi() {
		SecVerify.setUiSettings(CustomizeUtils.customizeUi());
		SecVerify.setLandUiSettings(null);
	}


	private void customizeUi1() {
		SecVerify.setUiSettings(CustomizeUtils.customizeUi1());
		SecVerify.setLandUiSettings(CustomizeUtils.customizeUi5(this));
//		LandUiSettings uiSettings1  = new LandUiSettings.Builder()
//				.setTranslateAnim(true)
//				.setImmersiveTheme(true)
//				.setImmersiveStatusTextColorBlack(true)
//				.build();
//		SecVerify.setLandUiSettings(uiSettings1);
	}


	private void customizeUi2() {
		SecVerify.setUiSettings(CustomizeUtils.customizeUi2());
		SecVerify.setLandUiSettings(null);
//		LandUiSettings uiSettings1  = new LandUiSettings.Builder()
//				.setDialogMaskBackgroundClickClose(true)
//				.setStartActivityTransitionAnim(R.anim.sec_verify_translate_bottom_in,R.anim.sec_verify_translate_bottom_out)
//				.setFinishActivityTransitionAnim(R.anim.sec_verify_translate_bottom_in,R.anim.sec_verify_translate_bottom_out)
//				.setDialogTheme(true)
//				.setDialogAlignBottom(true)
//				.build();
//		SecVerify.setLandUiSettings(uiSettings1);
	}

	private void customizeUi3() {
		SecVerify.setUiSettings(CustomizeUtils.customizeUi3());
		SecVerify.setLandUiSettings(null);
	}

	private void customizeUi4() {
		SecVerify.setUiSettings(CustomizeUtils.customizeUi4());
		LandUiSettings uiSettings1  = new LandUiSettings.Builder()
				.setDialogMaskBackgroundClickClose(true)
				.setDialogMaskBackground(R.drawable.sec_verify_common_progress_dialog_bg)
				.setFadeAnim(true)
				.setBackgroundImgId(R.drawable.sec_verify_background_demo_dialog)
				.setDialogTheme(true)
				.setDialogAlignBottom(false)
				.setDialogWidth(R.dimen.sec_verify_demo_dialog_width)
				.setDialogHeight(R.dimen.sec_verify_demo_dialog_height)
				.setDialogOffsetX(R.dimen.sec_verify_demo_dialog_offset_x)
				.setDialogOffsetY(R.dimen.sec_verify_demo_dialog_offset_y)
				.build();
		SecVerify.setLandUiSettings(uiSettings1);
	}

	/**
	 * 添加自定义view
	 */
	private void addCustomView() {
		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView(this), new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					msg = "微信 clicked";
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

	private void addCustomView1() {
		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView(this), new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				if (id == R.id.customized_btn_id_1) {
					customizeUi3();
					addCustomView3();
					SecVerify.refreshOAuthPage();
				}
			}
		});
		View view = LayoutInflater.from(this).inflate(R.layout.sec_verify_demo_loading,null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.setLayoutParams(params);
		CustomUIRegister.setCustomizeLoadingView(view);

	}

	private void addCustomView2() {
		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView2(this), new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					msg = "用户取消登录";
					// 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
					SecVerify.finishOAuthPage();
					CommonProgressDialog.dismissProgressDialog();
				} else if (id == R.id.customized_view_id) {
					return;
				}
				// 关闭加载框
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void addCustomView3() {

		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView3(this), new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					msg = "按钮1 clicked";
					// 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
					addCustomView1();
					customizeUi2();
					SecVerify.refreshOAuthPage();
				} else if (id == R.id.customized_btn_id_0){
					msg = "关闭返回 ";
					SecVerify.finishOAuthPage();
				} else if (id == R.id.customized_btn_id_3){
					msg = "登录返回";
					SecVerify.finishOAuthPage();
				}
				// 关闭加载框
				CommonProgressDialog.dismissProgressDialog();
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void addCustomView4() {
		CustomUIRegister.addCustomizedUi(CustomizeUtils.buildCustomView4(this), new CustomViewClickListener() {
			@Override
			public void onClick(View view) {
				int id = view.getId();
				String msg = "";
				if (id == R.id.customized_btn_id_1) {
					msg = "用户取消登录";
					// 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
					SecVerify.finishOAuthPage();
					CommonProgressDialog.dismissProgressDialog();
				} else if (id == R.id.customized_view_id) {
					return;
				}
				// 关闭加载框
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
			if (Build.VERSION.SDK_INT >= 26) {
				VibrationEffect vibrationEffect = VibrationEffect.createOneShot(500, 20);
				vibrator.vibrate(vibrationEffect);
			} else {
				vibrator.vibrate(500);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (defaultUi == 1 || defaultUi == 3) {
			//处理部分机型调起授权页面的Activity设置固定方向之后，授权页面无法横竖屏切换
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//根据需要设置为横屏或者竖屏
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (defaultUi == 1 || defaultUi == 3) {
			//处理部分机型调起授权页面的Activity设置固定方向之后，授权页面无法横竖屏切换
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
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
