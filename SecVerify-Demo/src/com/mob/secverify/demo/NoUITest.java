package com.mob.secverify.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.secverify.GetTokenCallback;
import com.mob.secverify.OAuthPageEventCallback;
import com.mob.secverify.PageCallback;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.ResultCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.UiLocationHelper;
import com.mob.secverify.VerifyResultCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.UiSettings;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.secverify.demo.entity.LoginResult;
import com.mob.secverify.demo.exception.DemoException;
import com.mob.secverify.demo.login.LoginTask;
import com.mob.secverify.demo.ui.component.CommonProgressDialog;
import com.mob.secverify.demo.util.Const;
import com.mob.secverify.demo.util.CustomizeUtils;
import java.util.HashMap;
import java.util.List;

public class NoUITest extends Activity implements View.OnClickListener {
    private static final String TAG = "NoUITest-Demo";
    private Button preverify;

    private Button loginAdapter;
    private Button loginUiSetting;

    private Button verify1;
    private Button verify2;
    private Button verify3;

    private Button privacyBtn;
    private Button funcEnable;
    private Button rejectBtn;
    private Button agreeBtn;
    private Button eventTest;
    private EditText timeoutEdt;
    private Button loginFinish;
    private Button loginunFinish;
    private TextView content;
    private static final int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sec_noui);
        initView();
    }

    private void initView(){
        preverify = findViewById(R.id.stand_pre_test);
        loginUiSetting = findViewById(R.id.secverify_uisetting);
        loginAdapter = findViewById(R.id.secverify_adapter);

        verify1 = findViewById(R.id.secverify_callback1);
        verify2 = findViewById(R.id.secverify_callback2);
        verify3 = findViewById(R.id.secverify_callback3);
        eventTest = findViewById(R.id.secverify_event_test);
        funcEnable = findViewById(R.id.sec_func_enable);
        privacyBtn = findViewById(R.id.privacy_test);
        rejectBtn = findViewById(R.id.reject_policy);
        agreeBtn = findViewById(R.id.agree_policy);
        loginFinish = findViewById(R.id.login_finish);
        loginunFinish = findViewById(R.id.login_unfinish);
        content = findViewById(R.id.output);


        timeoutEdt = findViewById(R.id.noui_timeout);
        timeoutEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String timeout = s.toString();
                try {
                    int time = Integer.valueOf(timeout);
                    SecVerify.setTimeOut(time);
                } catch (Throwable throwable){
                }
            }
        });

        preverify.setOnClickListener(this);
        loginAdapter.setOnClickListener(this);
        loginUiSetting.setOnClickListener(this);
        verify1.setOnClickListener(this);
        verify2.setOnClickListener(this);
        verify3.setOnClickListener(this);
        eventTest.setOnClickListener(this);
        funcEnable.setOnClickListener(this);
        privacyBtn.setOnClickListener(this);
        agreeBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
        loginFinish.setOnClickListener(this);
        loginunFinish.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stand_pre_test:
                SecVerify.preVerify(new PreVerifyCallback() {
                    @Override
                    public void onComplete(Void data) {
                        content.setText("Standard預取号成功");
                    }

                    @Override
                    public void onFailure(VerifyException e) {
                        showExceptionMsg(e);
                    }
                });
                break;
            case R.id.secverify_callback1:
                SecVerify.verify(new GetTokenCallback() {
                    @Override
                    public void onComplete(VerifyResult data) {
                        //Toast.makeText(NoUITest.this,"Standard取号成功"+data.toJSONString(),Toast.LENGTH_SHORT).show();
                        content.setText(data.toJSONString());
                        tokenToPhone(data);
                    }

                    @Override
                    public void onFailure(VerifyException e) {
                        showExceptionMsg(e);

                    }
                });
                break;
            case R.id.secverify_callback2:
                SecVerify.verify(new VerifyResultCallback() {
                    @Override
                    public void initCallback(VerifyCallCallback callback) {
                        callback.onCancel(new CancelCallback() {
                            @Override
                            public void handle() {
                                content.setText("User cancel grant");
                            }
                        });
                        callback.onOtherLogin(new OtherLoginCallback() {
                            @Override
                            public void handle() {
                                content.setText("User request other login");
                            }
                        });
                        callback.onComplete(new ResultCallback.CompleteCallback<VerifyResult>() {
                            @Override
                            public void handle(VerifyResult result) {
                                content.setText(result.toJSONString());
                                tokenToPhone(result);
                            }
                        });
                        callback.onFailure(new ResultCallback.ErrorCallback() {
                            @Override
                            public void handle(VerifyException t) {
                                showExceptionMsg(t);
                            }
                        });
                    }
                });
                break;
            case R.id.secverify_callback3:
                SecVerify.verify(new PageCallback() {
                    @Override
                    public void pageCallback(int code, String desc) {
                        content.setText(code + desc);
                    }
                }, new GetTokenCallback() {
                    @Override
                    public void onComplete(VerifyResult data) {
                        content.setText(data.toJSONString());
                        tokenToPhone(data);
                    }

                    @Override
                    public void onFailure(VerifyException e) {
                        showExceptionMsg(e);
                    }
                });
                break;
            case R.id.secverify_event_test:
                SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
                    @Override
                    public void initCallback(OAuthPageEventResultCallback cb) {
                        cb.pageOpenCallback(new PageOpenedCallback() {
                            @Override
                            public void handle() {
                                Toast.makeText(NoUITest.this,"pageOpened",Toast.LENGTH_SHORT).show();
                            }
                        });
                        cb.loginBtnClickedCallback(new LoginBtnClickedCallback() {
                            @Override
                            public void handle() {
                                Toast.makeText(NoUITest.this,"loginBtnClicked",Toast.LENGTH_SHORT).show();
                            }
                        });
                        cb.agreementPageClosedCallback(new AgreementPageClosedCallback() {
                            @Override
                            public void handle() {
                                Toast.makeText(NoUITest.this,"agreementPageClosed",Toast.LENGTH_SHORT).show();
                            }
                        });
                        cb.agreementPageOpenedCallback(new AgreementClickedCallback() {
                            @Override
                            public void handle() {
                                Toast.makeText(NoUITest.this,"agreementPageOpened",Toast.LENGTH_SHORT).show();
                            }
                        });
                        cb.cusAgreement1ClickedCallback(new CusAgreement1ClickedCallback() {
                            @Override
                            public void handle() {
                                Toast.makeText(NoUITest.this,"cusAgreement1ClickedCallback",Toast.LENGTH_SHORT).show();
                            }
                        });
                        cb.cusAgreement2ClickedCallback(new CusAgreement2ClickedCallback() {
                            @Override
                            public void handle() {
                                Toast.makeText(NoUITest.this,"cusAgreement2ClickedCallback",Toast.LENGTH_SHORT).show();
                            }
                        });
                        cb.checkboxStatusChangedCallback(new CheckboxStatusChangedCallback() {
                            @Override
                            public void handle(boolean b) {
                                Toast.makeText(NoUITest.this,"current status is "+b,Toast.LENGTH_SHORT).show();
                            }
                        });
                        cb.pageCloseCallback(new PageClosedCallback() {
                            @Override
                            public void handle() {
                                Toast.makeText(NoUITest.this,"pageClosed",Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.sec_func_enable:
                if (SecVerify.isVerifySupport()){
                    content.setText("当前环境支持取号");
                    //Toast.makeText(this,"当前环境支持取号",Toast.LENGTH_SHORT).show();
                } else {
                    content.setText("当前环境不支持取号");
                    //Toast.makeText(this,"当前环境不支持取号",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.privacy_test:
                boolean isForb = MobSDK.isForb();
                int auth = MobSDK.isAuth();
                if (isForb){
                    content.setText("SDK功能被禁用");
                    //Toast.makeText(this,"SDK功能被禁用",Toast.LENGTH_SHORT).show();
                } else if (auth <= 0){
                    content.setText("隐私不合规");
                    //Toast.makeText(this,"隐私不合规",Toast.LENGTH_SHORT).show();
                } else {
                    content.setText("秒验可以使用");
                    //Toast.makeText(this,"秒验可以使用",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.agree_policy:
                MobSDK.submitPolicyGrantResult(true, new com.mob.OperationCallback<Void>() {
                    @Override
                    public void onComplete(Void aVoid) {
                        boolean isForb = MobSDK.isForb();
                        int auth = MobSDK.isAuth();
                        content.setText("isForb: "+isForb+",auth: "+auth);
                        //Toast.makeText(NoUITest.this,"isForb: "+isForb+",auth: "+auth,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
                break;
            case R.id.reject_policy:
                MobSDK.submitPolicyGrantResult(false, new com.mob.OperationCallback<Void>() {
                    @Override
                    public void onComplete(Void aVoid) {
                        boolean isForb = MobSDK.isForb();
                        int auth = MobSDK.isAuth();
                        content.setText("isForb: "+isForb+",auth: "+auth);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
                break;
            case R.id.login_finish:
                SecVerify.autoFinishOAuthPage(true);
                break;
            case R.id.login_unfinish:
                SecVerify.autoFinishOAuthPage(false);
                break;
            case R.id.secverify_uisetting:
                SecVerify.setUiSettings(CustomizeUtils.customizeUi());
                SecVerify.setLandUiSettings(CustomizeUtils.customizeLandUi());
                break;
            case R.id.secverify_adapter:
                new UiSettings.Builder().setDialogMaskBackgroundClickClose(false);
                SecVerify.setAdapterFullName("com.mob.secverify.demo.ui.component.DialogAdapter");
                break;
        }

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

    private void tokenToPhone(VerifyResult data) {
        com.mob.secverify.ui.component.CommonProgressDialog.dismissProgressDialog();
        if (data != null) {
            Log.d(TAG, data.toJSONString());
            // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
            CommonProgressDialog.showProgressDialog(NoUITest.this);
            LoginTask.getInstance().login(data, new ResultListener<LoginResult>() {
                @Override
                public void onComplete(LoginResult data) {
                    CommonProgressDialog.dismissProgressDialog();
                    Log.d(TAG, "Login success. data: " + data.toJSONString());
                    vibrate();
                    // 服务端登录成功，跳转成功页
                    Toast.makeText(NoUITest.this,"当前手机号：" + data.getPhone(),Toast.LENGTH_LONG).show();
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
                    Toast.makeText(NoUITest.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void goResultActivity(LoginResult data) {
        Intent i = new Intent(this, ResultActivity.class);
        if (data != null) {
            i.putExtra("sec_verify_demo_verify_success", true);
            i.putExtra(Const.EXTRAS_DEMO_LOGIN_RESULT, data);
        } else {
            i.putExtra("sec_verify_demo_verify_success", false);
        }
        if (data != null){
            startActivityForResult(i, REQUEST_CODE);
            SecVerify.finishOAuthPage();
        }
        com.mob.secverify.ui.component.CommonProgressDialog.dismissProgressDialog();
    }

    public void showExceptionMsg(VerifyException e) {
        // 登录失败
        //SecVerify.finishOAuthPage();
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

        String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
        if (!TextUtils.isEmpty(errDetail)) {
            msg += "\n详细信息: " + errDetail;
        }
        content.setText(msg);
        //Toast.makeText(NoUITest.this, msg, Toast.LENGTH_SHORT).show();
        goResultActivity(null);
    }
}
