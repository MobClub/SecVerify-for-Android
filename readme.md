# 概述

* 秒验整合了三大运营商的网关认证能力, 为开发者提供一键登录功能, 优化登录注册流程
* SDK支持api16及以上版本。
* 网络取号时候请务必开启手机流量
    * 电信只支持4G网络取号
    * 移动, 联通支持4G, 3G, 2G网络取号但在非4G网络情况下容易取号失败
* 针对双卡双待手机只取当前流量卡号

# 一、集成准备:
### 1. 注册应用申请MobKey和MobSecret: [点击查看注册流程](http://bbs.mob.com/forum.php?mod=viewthread&tid=8212&extra=page%3D1)
# 二、配置集成

* 将下面的脚本添加到您的根模块build.gradle中

```
buildscript {
    repositories {
        ...
    }
    dependencies {
        ...
        classpath "com.mob.sdk:MobSDK:2018.0319.1724"
    }
}
```

![share1](http://download.sdk.mob.com/2019/07/20/11/1563595085392/841_360_36.76.png)  

* 在使用SecVerify模块的build.gradle中，添加插件和扩展，需要将appKey和appSecret的值替换为在上面申请到的MobKey和MobSecret如：
```
// 添加插件
apply plugin: 'com.mob.sdk'

// 注册SecVerify的相关信息
MobSDK {
    appKey "申请Mob的appkey"
    appSecret "申请Mob的AppSecret"
    SecVerify {}
}
```
* 指定.so库（如需）

电信免密登录能力使用了so库，若开发者应用同时使用了其他so库，则需根据自身应用支持的cpu架构，选择使用SecVerify的不同so库。
```
// 根据需要选择对应的.so库
android {
    defaultConfig {
        // 应用的applicationId、versionCode等配置信息
        ...
        applicationId '您的ApplicationId'

        ndk {
            // 选择要添加的对应 cpu 类型的 .so 库，多个abi以“,”分隔。
            abiFilters 'armeabi-v7a'
            // 可指定的值为 'armeabi-v7a', 'arm64-v8a', 'armeabi', 'x86', 'x86_64'，
        }
    }
}
```
* 指定Android9.0 http协议支持

由于Android 9.0开始，系统强制使用https请求，运营商方面暂不支持https取号，因此需要关闭9.0系统的强制https功能，请在manifest的 Application 节点增加 usesCleartextTraffic 设置，如下所示：
```
<application
		android:icon="@drawable/ic_launcher"
		android:label="@string/app_name"
		android:usesCleartextTraffic="true">
```

# 三、使用SDK 

 **SecVerify包含两个接口：`预登录` 与 `登录`。**  

在使用API前，请注意以下说明： 

a.免密登录能力必须经过运营商网关取号，因此必须在手机打开移动蜂窝网络的前提下才会成功。  

b.预登录接口用于向运营商进行预取号操作，建议在实际调用登录接口前提前调用预登录接口（比如应用启动时或进入注册或登录页面时），这将极大地加快登录接口执行耗时，提高用户体验。  


### 1 预登录
**接口描述:**

获取临时凭证
可以提前获知当前用户的手机网络环境是否符合一键登录的使用条件，成功后将得到用于一键登录使用的临时凭证, 默认的凭证有效期10min(电信)/60min(联通)/60min(移动)。
```java 
//建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
    SecVerify.preVerify(new OperationCallback<Void>() {
        @Override
        public void onComplete(Void data) {
            //TODO处理成功的结果
        }
        @Override
        public void onFailure(VerifyException e) {
            //TODO处理失败的结果
        }
    });

```

**参数说明：**

|接口|参数列表|必须|说明|
|:---|:---|:---|:-----|
|preVerify|OperationCallback<Void\> callback|是|callback： 接口回调|

OperationCallback包含以下回调方法：

|回调方法|意义|
|:---|:---|
|onComplete(Void data)|操作成功|
|onFailure(VerifyException e)|操作失败|

VerifyException ：

|接口|参数列表|意义|
|:---|:---|:---|
|getCode|无|获取错误码|
|getMessage|无|获取SDK返回的错误信息|
|getCause|无|获取运营商返回的错误信息|

### 2登录

**接口描述:**

调用一键登录方法将立即拉起授权页面

```java
SecVerify.verify(new VerifyCallback() {
		   @Override
		   public void onOtherLogin() {
				// 用户点击“其他登录方式”，处理自己的逻辑
		   }
		   @Override
		   public void onUserCanceled() {
				// 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
			}
           @Override
		   public void onComplete(VerifyResult data) {
         // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
                LoginTask.getInstance().login(data,new ResultListener<LoginResult>() {
		             @Override
                     public void onComplete(LoginResult data) {
                     //TODO处理成功的结果
                     }
                     @Override
					 public void onFailure(DemoException e) {
					 //TODO处理失败的结果
					 }
				});
           }
		   @Override
		   public void onFailure(VerifyException e) {
			//TODO处理失败的结果
		   }
	    });
}

```


|接口|参数列表|必须|说明|
|:---|:---|:---|:-----|
|verify|VerifyCallback callback|是|callback： 接口回调|

VerifyCallback包含以下回调方法：

|回调方法|意义|
|:---|:---|
|onComplete(VerifyResult data)|操作成功|
|onOtherLogin()|其他登录方式回调|
|onFailure(VerifyException e)|操作失败|

VerifyResult定义：

|成员变量|意义|
|:---|:---|
|String opToken|运营商token|
|String token|服务器token|
|String operator|运营商类型，[CMCC:中国移动，CUCC：中国联通，CTCC：中国电信]|

**注意**：登录接口响应成功后，开发者需要将得到的token信息传给应用服务器，再由应用服务器请求SecVerify服务器，进行登录确认，并将最终结果返回给应用客户端，流程结束。此部分功能需由开发者自行实现，应用服务器与SecVerify服务器的对接请参考 `服务端接口文档`。

### 4 手动关闭登录界面
SecVerify提供了以下方法用于由开发者主动关闭授权页面：

```
SecVerify.finishOAuthPage();
```

关于该方法，作以下说明：

- `登录` 接口默认在回调接口时，SDK内部会自动关闭授权页，开发者**不需要**再次调用上述方法进行关闭操作
- 开发者添加的 `自定义控件`点击时默认不关闭授权页，如需关闭，可以使用该方法
- 通过该方法主动关闭授权页时，不会触发 `登录` 接口的任何回调


### 5 是否自动关闭授权页面

SecVerify提供了以下方法用于由开发者决定登录成功或失败是否自动关闭授权页面如果不需要可以不调用：
```
SecVerify.autoFinishOAuthPage(boolean isFinish);
```

关于该方法，作以下说明：

- 如果未设置或设置为true，登录接口在回调时默认会自动关闭授权页面

- 如果设置为false，那么在登录成功或者失败的回调中，必须要手动调用SecVerify.finishOAuthPage();方法来关闭授权页面


	 
	 
### 刷新授权页面

SecVerify提供了以下方法用于刷新授权页面：
```
SecVerify.refreshOAuthPage();
```

关于该方法，作以下说明：

- 如果需要在授权页面修改界面内容，可以通过设置ui属性的hidden方法来隐藏界面控件并用自定义控件设置自己的UI

### 获取授权页面的其他回调
SecVerify提供了以下方法用于获取授权页面的其他回调，此方法需放在调用verify方法之前：
```java
SecVerify.OtherOAuthPageCallBack(new OAuthPageEventCallback() {
			@Override
			public void initCallback(OAuthPageEventResultCallback cb) {
				cb.pageOpenCallback(new OAuthPageEventCallback.PageOpenedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " pageOpened");
					}
				});
				cb.loginBtnClickedCallback(new OAuthPageEventCallback.LoginBtnClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " loginBtnClicked");
					}
				});
				cb.agreementPageClosedCallback(new OAuthPageEventCallback.AgreementPageClosedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " agreementPageClosed");
					}
				});
				cb.agreementPageOpenedCallback(new OAuthPageEventCallback.AgreementClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " agreementPageOpened");
					}
				});
				cb.cusAgreement1ClickedCallback(new OAuthPageEventCallback.CusAgreement1ClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " cusAgreement1ClickedCallback");
					}
				});
				cb.cusAgreement2ClickedCallback(new OAuthPageEventCallback.CusAgreement2ClickedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " cusAgreement2ClickedCallback");
					}
				});
				cb.pageCloseCallback(new OAuthPageEventCallback.PageClosedCallback() {
					@Override
					public void handle() {
						Log.i(TAG, System.currentTimeMillis() + " pageClosed");
					}
				});
				cb.checkboxStatusChangedCallback(new CheckboxStatusChangedCallback() {
					@Override
					public void handle(boolean b) {
						Log.i(TAG, System.currentTimeMillis() + " current status is " + b);
					}
				});
			}
		});
```

```kotlin
SecVerify.OtherOAuthPageCallBack(cb -> {
			cb.pageOpenCallback(() -> Log.i(TAG, System.currentTimeMillis() + " pageOpened"));
			cb.loginBtnClickedCallback(() -> Log.i(TAG, System.currentTimeMillis() + " loginBtnClicked"));
			cb.agreementPageClosedCallback(() -> Log.i(TAG, System.currentTimeMillis() + " agreementPageClosed"));
			cb.agreementPageOpenedCallback(() -> Log.i(TAG, System.currentTimeMillis() + " agreementPageOpened"));
			cb.cusAgreement1ClickedCallback(() -> Log.i(TAG, System.currentTimeMillis() + " cusAgreement1ClickedCallback"));
			cb.cusAgreement2ClickedCallback(() -> Log.i(TAG, System.currentTimeMillis() + " cusAgreement2ClickedCallback"));
			cb.pageCloseCallback(() -> {
				Log.i(TAG, System.currentTimeMillis() + " pageClosed");
			});
		});

```
**参数说明：**

|接口|参数列表|必须|说明|
|:---|:---|:---|:-----|
|OtherOAuthPageCallBack|OAuthPageEventCallback callback|是|callback： 接口回调|

OAuthPageEventCallback包含以下方法：

|回调方法|意义|
|:---|:---|
|pageOpenCallback(PageOpenedCallback callback)|授权页面打开回调|
|pageCloseCallback(PageClosedCallback callback)|授权页面关闭回调|
|loginBtnClickedCallback(LoginBtnClickedCallback callback)|点击登录按钮回调|
|agreementPageClosedCallback(AgreementPageClosedCallback callback)|隐私协议页面关闭回调|
|agreementPageOpenedCallback(AgreementClickedCallback callback)|点击运营商隐私协议回调|
|cusAgreement1ClickedCallback(CusAgreement1ClickedCallback callback)|点击自定义隐私协议一回调|
|cusAgreement2ClickedCallback(CusAgreement2ClickedCallback callback)|点击自定义隐私协议二回调|
|cusAgreement3ClickedCallback(CusAgreement3ClickedCallback callback)|点击自定义隐私协议三回调|
|checkboxStatusChangedCallback(CheckboxStatusChangedCallback callback)|复选框状态修改回调|

可在以上各个方法的回调中处理对应的事件


### 设置debug模式
SecVerify提供了以下方法用于设置debug模式：
```
SecVerify.setDebugMode(boolean isDebug);
```

### 超时设置

SecVerify提供了以下方法用于设置超时时间，单位为ms，取值在1000-10000之间：
```
SecVerify.setTimeOut(int time);
```

### 是否使用缓存

SecVerify提供了以下方法用于关闭默认的缓存功能，默认为使用缓存，在进入授权页面之后取消登录和切换登录不会清除缓存，登录成功或者失败会清除缓存
```
SecVerify.setUseCache(boolean useCache);
```
# 四、授权页界面修改

## 收取页面控件

SecVerify提供默认的授权页面样式，在不指定的情况下显示默认样式

**说明**：

1. 该方法需在 `登录` 接口之前调用。
2. 授权页面属性设置方法的参数可以设置资源ID也可以直接设置值，同时设置时优先使用资源ID
3. 控件背景色的设置，建议使用 `selector` 以及 `shape` 实现，具体方法请参考Demo的 `MainActivity`。
4. 所有控件的X、RightX、Y、BottomY属性分别代表了控件距离屏幕左右上下的距离
5. 设置了BottomY属性后，会默认靠屏幕底部，同时Y属性的设置即距屏幕上方距离无效。
6. 根据运营商要求，手机号、登录按钮、运营商协议及品牌部分不可隐藏

具体方法如下

```
SecVerify.setUiSettings(UiSettings uiSettings)

SecVerify.setLandUiSettings(LandUiSettings uiSettings)
```

* `UiSettings` 对象由 `Uisettings.Builder` 进行构建，用于设置竖屏授权页面 

* `LandUiSettings`对象由`LandUiSettings.Builder`进行构建，用于设置横屏授权页面  

* 同时设置UiSettings和LandUiSettings时可横竖屏切换  

* Android 8.0由于系统原因，无法固定设置横竖屏，如果只设置了UiSettings或LandUiSettings，屏幕方向由上一个页面的方向决定

* `Uisettings.Builder` 和`LandUiSettings.Builder` 都包含以下方法：

#### 状态栏

| 方法 | 意义 |
|--------|--------|
| setImmersiveTheme(boolean immersiveTheme)	| 状态栏是否透明 （5.0以上生效）|
| setImmersiveStatusTextColorBlack(boolean textColorBlack) | 状态栏文字颜色是否为黑色（6.0以上生效）|

#### 导航栏

<table class="tg">
<tr>
<th class="tg-0pky">方法</th>
<th class="tg-0pky">参数</th>
<th class="tg-0pky">意义</th>
</tr>
<tr>
<td class="tg-0pky" rowspan="2">setNavColorId</td>
<td class="tg-0pky">int navColorId</td>
<td class="tg-0pky">标题栏背景色资源ID 例：R.color.sec_verify_nav_color</td>
</tr>
<tr>
<td class="tg-0pky">int color</td>
<td class="tg-0pky">16进制色值，例：0xffffffff</td>
</tr>
<tr>
<td class="tg-0pky" rowspan="2">setNavTextId</td>
<td class="tg-0pky">int navTextId</td>
<td class="tg-0pky">标题栏标题文字资源ID 例：R.string.sec_verify_demo_verify</td>
</tr>
<tr>
<td class="tg-0lax">String navText</td>
<td class="tg-0lax">标题栏标题文字 例：“一键登录”</td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2">setNavTextSize</td>
<td class="tg-0lax">int navTextSizeId</td>
<td class="tg-0lax">标题栏标题文字大小ID 例：R.dimen.sec_verify_demo_text_size_s</td>
</tr>
<tr>
<td class="tg-0lax">int textSize</td>
<td class="tg-0lax">标题栏标题文字大小（单位：sp）例：16</td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2">setNavTextColorId</td>
<td class="tg-0lax">int navTextColorId</td>
<td class="tg-0lax">标题栏文字颜色资源ID例：R.color.sec_verify_nav_text_color</td>
</tr>
<tr>
<td class="tg-0lax">int color</td>
<td class="tg-0lax">16进制色值，例：0xffffffff</td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2">setNavCloseImgId</td>
<td class="tg-0lax">int navCloseImgId</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片资源ID 例：R.drawable.sec_verify_nav_close_img</td>
</tr>
<tr>
<td class="tg-0lax">Drawable navCloseImg</td>
<td class="tg-0lax">Drawable对象</td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2">setNavCloseImgWidth</td>
<td class="tg-0lax">int navCloseImgWidthId</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片宽度资源ID例：R.dimen.sec_verify_demo_text_size_s</td>
</tr>
<tr>
<td class="tg-0lax">int navCloseImgWidth</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片宽度（单位：dp） 例：16</td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2">setNavCloseImgHeight</td>
<td class="tg-0lax">int navCloseImgHeightId</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片高度资源ID例：R.dimen.sec_verify_demo_text_size_s</td>
</tr>
<tr>
<td class="tg-0lax">int navCloseImgHeight</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片高度（单位：dp） 例：16</td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2">setNavCloseImgOffsetX</td>
<td class="tg-0lax">int navCloseImgOffsetXId</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片左偏移量资源ID例：R.dimen.sec_verify_demo_text_size_s</td>
</tr>
<tr>
<td class="tg-0lax">int navCloseImgOffsetX</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片宽度（单位：dp）例：16</td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2">setNavCloseImgOffsetRightX</td>
<td class="tg-0lax">int navCloseImgOffsetRightXId</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片右偏移量资源ID（设置之后关闭图标会默认靠屏幕右边）例：R.dimen.sec_verify_demo_text_size_s</td>
</tr>
<tr>
<td class="tg-0lax">int navCloseImgOffsetRightX</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片右偏移量（单位：dp）例：16</td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2">setNavCloseImgOffsetY</td>
<td class="tg-0lax">int navCloseImgOffsetYId</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片上偏移量资源ID例：R.dimen.sec_verify_demo_text_size_s</td>
</tr>
<tr>
<td class="tg-0lax">int navCloseImgOffsetY</td>
<td class="tg-0lax">标题栏左侧关闭按钮图片上偏移量（单位：dp）例：16</td>
</tr>
<tr>
<td class="tg-0lax">setNavTransparent</td>
<td class="tg-0lax">boolean navTransparent</td>
<td class="tg-0lax">标题栏是否透明,默认透明</td>
</tr>
<tr>
<td class="tg-0lax">setNavHidden</td>
<td class="tg-0lax">boolean navHidden</td>
<td class="tg-0lax">标题栏是否隐藏，默认不隐藏</td>
</tr>
<tr>
<td class="tg-0lax">setNavCloseImgHidden</td>
<td class="tg-0lax">boolean navCloseImgHidden</td>
<td class="tg-0lax">标题栏左侧关闭按钮是否隐藏，默认不隐藏</td>
</tr>
</table>



#### 背景

<table class="tg">
<tr>
<th class="tg-0pky">方法</th>
<th class="tg-0pky">参数</th>
<th class="tg-0pky">意义</th>
</tr>
<tr>
<td class="tg-0pky"><br>setBackgroundClickClose（boolean<br>backgroundClickClose）<br></td>
<td class="tg-0pky"><br>boolean backgroundClickClose<br></td>
<td class="tg-0pky"><br>设置点击授权页面背景是否关闭页面，默认不关闭页面<br></td>
</tr>
<tr>
<td class="tg-0pky" rowspan="2"><br>setBackgroundImgId<br></td>
<td class="tg-0pky"><br>int backgroundImgId<br></td>
<td class="tg-0pky"><br>设置背景图片资源ID 例：R.drawable.background<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int backgroundImg<br></td>
<td class="tg-0lax"><br>Drawable 对象<br></td>
</tr>
</table>


#### Logo

<table class="tg">
<tr>
<th class="tg-cly1"><br>&nbsp;方法<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;参数<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;意义<br>&nbsp;</th>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setLogoImgId<br></td>
<td class="tg-cly1"><br>int logoImgId<br></td>
<td class="tg-cly1"><br>Logo图片资源ID，默认使用应用图标<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>Drawable logoImg<br></td>
<td class="tg-cly1"><br>Drawable对象<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>setLogoHidden<br></td>
<td class="tg-cly1"><br>boolean logoHidden<br></td>
<td class="tg-cly1"><br>Logo是否隐藏，默认false<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLogoWidth<br></td>
<td class="tg-0lax"><br>int logoWidthId<br></td>
<td class="tg-0lax"><br>Logo宽度大小资源ID 例：R.dimen.sec_verify_demo_logo_width<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int logoWidth<br></td>
<td class="tg-0lax"><br>Logo宽度（单位：dp） 例：80<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLogoHeight<br></td>
<td class="tg-0lax"><br>int logoHeightId<br></td>
<td class="tg-0lax"><br>Logo高度大小资源ID例：R.dimen.sec_verify_demo_logo_height<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int logoHeight<br></td>
<td class="tg-0lax"><br>Logo高度（单位：dp） 例：80<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLogoOffsetX<br></td>
<td class="tg-0lax"><br>int logoOffsetXId<br></td>
<td class="tg-0lax"><br>Logo左偏移量大小资源ID例：R.dimen.sec_verify_demo_logo_offset_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int logoOffsetX<br></td>
<td class="tg-0lax"><br>Logo左偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLogoOffsetY<br></td>
<td class="tg-0lax"><br>int logoOffsetYId<br></td>
<td class="tg-0lax"><br>Logo上偏移量大小资源ID例：R.dimen.sec_verify_demo_logo_offset_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int logoOffsetY<br></td>
<td class="tg-0lax"><br>Logo上偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLogoOffsetBottomY<br></td>
<td class="tg-0lax"><br>int logoOffsetBottomYId<br></td>
<td class="tg-0lax"><br>Logo下偏移量大小资源ID例：R.dimen.sec_verify_demo_logo_offset_bottom_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int logoOffsetBottomY<br></td>
<td class="tg-0lax"><br>Logo下偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLogoOffsetRightX<br></td>
<td class="tg-0lax"><br>int logoOffsetRightXId<br></td>
<td class="tg-0lax"><br>Logo右偏移量大小资源ID例：R.dimen.sec_verify_demo_logo_offset_right_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int logoOffsetRightX<br></td>
<td class="tg-0lax"><br>Logo右偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setLogoAlignParentRight<br></td>
<td class="tg-0lax"><br>boolean logoAlignParentRight<br></td>
<td class="tg-0lax"><br>Logo是否靠屏幕右边<br></td>
</tr>
</table>


#### 脱敏手机号

<table class="tg">
<tr>
<th class="tg-cly1"><br>&nbsp;方法<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;参数<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;意义<br>&nbsp;</th>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setNumberColorId<br></td>
<td class="tg-cly1"><br>int numberColorId<br></td>
<td class="tg-cly1"><br>标题栏文字颜色资源ID例：R.color.sec_verify_demo_number_color<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int numberColor<br></td>
<td class="tg-cly1"><br>16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setNumberSizeId<br></td>
<td class="tg-cly1"><br>int numberSizeId<br></td>
<td class="tg-cly1"><br>脱敏手机号字体大小资源ID例：R.dimen.sec_verify_demo_text_size_s<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int numberSize<br></td>
<td class="tg-cly1"><br>脱敏手机号上偏移量（单位：sp） 例：16<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setNumberOffsetX<br></td>
<td class="tg-cly1"><br>int numberOffsetXId<br></td>
<td class="tg-cly1"><br>脱敏手机号 左偏移量大小资源ID例：R.dimen.sec_verify_demo_number_offset_x<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int numberOffsetX<br></td>
<td class="tg-cly1"><br>脱敏手机号 左偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setNumberOffsetY<br></td>
<td class="tg-cly1"><br>int numberOffsetYId<br></td>
<td class="tg-cly1"><br>脱敏手机号 上偏移量大小资源ID例：R.dimen.sec_verify_demo_number_offset_y<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int numberOffsetY<br></td>
<td class="tg-cly1"><br>脱敏手机号 上偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setNumberOffsetBottomY<br></td>
<td class="tg-0lax"><br>int numberOffsetBottomYId<br></td>
<td class="tg-0lax"><br>脱敏手机号 下偏移量大小资源ID例：R.dimen.sec_verify_demo_number_offset_bottom_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int numberOffsetBottomY<br></td>
<td class="tg-0lax"><br>脱敏手机号 下偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setNumberOffsetRightX<br></td>
<td class="tg-0lax"><br>int numberOffsetRightXId<br></td>
<td class="tg-0lax"><br>脱敏手机号 右偏移量大小资源ID例：R.dimen.sec_verify_demo_number_offset_right_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int numberOffsetRightX<br></td>
<td class="tg-0lax"><br>脱敏手机号 右偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setNumberAlignParentRight<br></td>
<td class="tg-0lax"><br>boolean numberAlignParentRight<br></td>
<td class="tg-0lax"><br>脱敏手机号是否靠屏幕右边<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setNumberHidden<br></td>
<td class="tg-0lax"><br>boolean numberHidden<br></td>
<td class="tg-0lax"><br>脱敏手机号隐藏，默认false<br></td>
</tr>
</table>

#### 切换账号

<table class="tg">
<tr>
<th class="tg-cly1"><br>&nbsp;方法<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;参数<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;意义<br>&nbsp;</th>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSwitchAccTextSize<br></td>
<td class="tg-cly1"><br>int switchAccTextSizeId<br></td>
<td class="tg-cly1"><br>切换账号字体大小资源ID例：R.dimen.sec_verify_demo_text_size_s<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int switchAccTextSize<br></td>
<td class="tg-cly1"><br>切换账号字体大小（单位：sp） 例：16<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSwitchAccColorId<br></td>
<td class="tg-cly1"><br>int switchAccColorId<br></td>
<td class="tg-cly1"><br>切换账号字体颜色资源ID例：R.color.sec_verify_demo_switch_acc_color<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int switchAccColor<br></td>
<td class="tg-cly1"><br>切换账号字体颜色16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>setSwitchAccHidden<br></td>
<td class="tg-cly1"><br>boolean switchAccHidden<br></td>
<td class="tg-cly1"><br>切换账号是否隐藏，默认false<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSwitchAccOffsetX<br></td>
<td class="tg-cly1"><br>int switchAccOffsetXId<br></td>
<td class="tg-cly1"><br>切换账号 左偏移量大小资源ID 例：R.dimen.sec_verify_demo_swithc_acc_offset_x<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int switchAccOffsetX<br></td>
<td class="tg-cly1"><br>切换账号 左偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSwitchAccOffsetY<br></td>
<td class="tg-cly1"><br>int switchAccOffsetYId<br></td>
<td class="tg-cly1"><br>切换账号 上偏移量大小资源ID例：R.dimen.sec_verify_demo_swithc_acc_offset_y<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int switchAccOffsetY<br></td>
<td class="tg-cly1"><br>切换账号 上偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setSwitchAccOffsetBottomY<br></td>
<td class="tg-0lax"><br>int switchAccOffsetBottomYId<br></td>
<td class="tg-0lax"><br>切换账号 下偏移量大小资源ID例：R.dimen.sec_verify_demo_swithc_acc_offset_bottom_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int switchAccOffsetBottomY<br></td>
<td class="tg-0lax"><br>切换账号 下偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setSwitchAccOffsetRightX<br></td>
<td class="tg-0lax"><br>int switchAccOffsetRightXId<br></td>
<td class="tg-0lax"><br>切换账号 右偏移量大小资源ID例：R.dimen.sec_verify_demo_swithc_acc_offset_right_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int switchAccOffsetRightX<br></td>
<td class="tg-0lax"><br>切换账号 右偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setSwitchAccAlignParentRight<br></td>
<td class="tg-0lax"><br>boolean switchAccAlignParentRight<br></td>
<td class="tg-0lax"><br>切换账号 是否靠屏幕右边，默认false<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setSwitchAccText<br></td>
<td class="tg-0lax"><br>int switchAccTextId<br></td>
<td class="tg-0lax"><br>切换账号 文本内容资源Id 例： R.string.sec_verify_switch_acc_text<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String switchAccText<br></td>
<td class="tg-0lax"><br>切换账号 文本内容例： 其他方式登录<br></td>
</tr>
</table>


#### 隐私协议栏

<table class="tg">
<tr>
<th class="tg-cly1"><br>&nbsp;方法<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;参数<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;意义<br>&nbsp;</th>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setCheckboxImgId<br></td>
<td class="tg-cly1"><br>int checkboxImgId<br></td>
<td class="tg-cly1"><br>隐私协议复选框背景图资源ID，建议使用selector 例：R.drawable.sec_verify_demo_checkbox_selector<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>Drawable checkboxImgId<br></td>
<td class="tg-cly1"><br>Drawable对象<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>setCheckboxDefaultState<br></td>
<td class="tg-cly1"><br>boolean checkboxDefaultState<br></td>
<td class="tg-cly1"><br>隐私协议复选框默认状态，默认为true<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>setCheckboxHidden<br></td>
<td class="tg-cly1"><br>boolean checkboxHidden<br></td>
<td class="tg-cly1"><br>隐私协议复选框是否隐藏，若设置隐藏，则默认状态设置不生效<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setAgreementColorId<br></td>
<td class="tg-cly1"><br>int agreementColorId<br></td>
<td class="tg-cly1"><br>隐私协议字体颜色资源ID例：R.color.sec_verify_demo_agreement_color<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int agreementColor<br></td>
<td class="tg-cly1"><br>隐私协议字体颜色16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setCusAgreementNameId1<br></td>
<td class="tg-cly1"><br>int cusAgreementNameId1<br></td>
<td class="tg-cly1"><br>自定义隐私协议一文字资源ID例：R.string.sec_verify_demo_cus_agreement_1<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int cusAgreementName1<br></td>
<td class="tg-cly1"><br>自定义隐私协议一文字 例：“隐私协议一”<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setCusAgreementColor1<br></td>
<td class="tg-cly1"><br>int cusAgreementColorId1<br></td>
<td class="tg-cly1"><br>自定义隐私协议一颜色资源ID例：R.color.sec_verify_demo_cus_agreement_color_1<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int cusAgreementColor1<br></td>
<td class="tg-0lax"><br>自定义隐私协议一颜色16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setCusAgreementUrl1<br></td>
<td class="tg-0lax"><br>String cusAgreementUrl1<br></td>
<td class="tg-0lax"><br>自定义隐私协议一URL 例：http://www.mob.com<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setCusAgreementNameId2<br></td>
<td class="tg-0lax"><br>int cusAgreementNameId2<br></td>
<td class="tg-0lax"><br>自定义隐私协议二文字资源ID 例：R.string.sec_verify_demo_cus_agreement_2<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int cusAgreementNameId2<br></td>
<td class="tg-0lax"><br>自定义隐私协议二文字 例：“隐私协议二”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setCusAgreementColor2<br></td>
<td class="tg-0lax"><br>int cusAgreementColorId2<br></td>
<td class="tg-0lax"><br>自定义隐私协议二颜色资源ID例：R.color.sec_verify_demo_cus_agreement _color_2<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int cusAgreementColor2<br></td>
<td class="tg-0lax"><br>自定义隐私协议二颜色16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setCusAgreementUrl2<br></td>
<td class="tg-0lax"><br>String cusAgreementUrl2<br></td>
<td class="tg-0lax"><br>自定义隐私协议二URL 例：http://www.mob.com<br></td>
</tr>

<tr>
<td class="tg-0lax" rowspan="2"><br>setCusAgreementNameId3<br></td>
<td class="tg-0lax"><br>int cusAgreementNameId3<br></td>
<td class="tg-0lax"><br>自定义隐私协议三文字资源ID 例：R.string.sec_verify_demo_cus_agreement_3<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int cusAgreementNameId3<br></td>
<td class="tg-0lax"><br>自定义隐私协议三文字 例：“隐私协议三”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setCusAgreementColor3<br></td>
<td class="tg-0lax"><br>int cusAgreementColorId3<br></td>
<td class="tg-0lax"><br>自定义隐私协议三颜色资源ID例：R.color.sec_verify_demo_cus_agreement _color_3<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int cusAgreementColor3<br></td>
<td class="tg-0lax"><br>自定义隐私协议三颜色16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setCusAgreementUrl3<br></td>
<td class="tg-0lax"><br>String cusAgreementUrl3<br></td>
<td class="tg-0lax"><br>自定义隐私协议三URL 例：http://www.mob.com<br></td>
</tr>

<tr>
<td class="tg-0lax"><br>setAgreementGravityLeft<br></td>
<td class="tg-0lax"><br>boolean agreementAlignParentRight<br></td>
<td class="tg-0lax"><br>隐私协议文字是否左对齐,默认false<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementBaseTextColorId<br></td>
<td class="tg-0lax"><br>int agreementBaseTextColorId<br></td>
<td class="tg-0lax"><br>隐私协议其他文字颜色资源ID 例：R.color.sec_verify_demo_agreement_base_text_color<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int agreementBaseTextColor<br></td>
<td class="tg-0lax"><br>隐私协议其他文字颜色  16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementOffsetX<br></td>
<td class="tg-0lax"><br>int agreementOffsetXId<br></td>
<td class="tg-0lax"><br>隐私协议左偏移量大小资源ID例：R.dimen.sec_verify_demo_agreement_offset_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int agreementOffsetX<br></td>
<td class="tg-0lax"><br>隐私协议左偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementOffsetRightX<br></td>
<td class="tg-0lax"><br>int agreementOffsetRightXId<br></td>
<td class="tg-0lax"><br>隐私协议右偏移量大小资源ID例：R.dimen.sec_verify_demo_agreement_offset_right_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int agreementOffsetRightX<br></td>
<td class="tg-0lax"><br>隐私协议右偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementOffsetY<br></td>
<td class="tg-0lax"><br>int agreementOffsetYId<br></td>
<td class="tg-0lax"><br>隐私协议上偏移量大小资源ID例：R.dimen.sec_verify_demo_agreement_offset_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int agreementOffsetY<br></td>
<td class="tg-0lax"><br>隐私协议上偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementOffsetBottomY<br></td>
<td class="tg-0lax"><br>int agreementOffsetBottomYId<br></td>
<td class="tg-0lax"><br>隐私协议下偏移量大小资源ID 例：R.dimen.sec_verify_demo_agreement_offset_bottom_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int agreementOffsetBottomY<br></td>
<td class="tg-0lax"><br>隐私协议下偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementCmccText<br></td>
<td class="tg-0lax"><br>int cmccTextId<br></td>
<td class="tg-0lax"><br>设置移动隐私协议显示文本资源ID 例：R.string.sec_verify_demo_cmcc_privacy<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String cmccText<br></td>
<td class="tg-0lax"><br>移动隐私协议显示文本 例：“《中国移动隐私协议》”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementCuccText<br></td>
<td class="tg-0lax"><br>int cuccTextId<br></td>
<td class="tg-0lax"><br>设置联通隐私协议显示文本资源ID 例：R.string.sec_verify_demo_cucc_privacy<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String cuccText<br></td>
<td class="tg-0lax"><br>联通隐私协议显示文本例：“《中国联通隐私协议》”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementCtccText<br></td>
<td class="tg-0lax"><br>int ctccTextId<br></td>
<td class="tg-0lax"><br>设置电信隐私协议显示文本资源ID 例：R.string.sec_verify_demo_ctcc_privacy<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String ctccText<br></td>
<td class="tg-0lax"><br>电信隐私协议显示文本例：“《中国电信隐私协议》”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementTextStart<br></td>
<td class="tg-0lax"><br>int startTextId<br></td>
<td class="tg-0lax"><br>设置隐私协议文本开头资源ID 例：R.string.sec_verify_demo_privacy_start<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String startText<br></td>
<td class="tg-0lax"><br>隐私协议文本开头 例：“登录即同意”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementTextAnd1<br></td>
<td class="tg-0lax"><br>int andTextId1<br></td>
<td class="tg-0lax"><br>设置隐私协议连接文本1资源ID 例：R.string.sec_verify_demo_privacy_and_1<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String andText1<br></td>
<td class="tg-0lax"><br>隐私协议连接文本1 例：“和”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementTextAnd2<br></td>
<td class="tg-0lax"><br>int andTextId2<br></td>
<td class="tg-0lax"><br>设置隐私协议连接文本2资源ID 例：R.string.sec_verify_demo_privacy_and_2<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String andText2<br></td>
<td class="tg-0lax"><br>隐私协议连接文本2 例：“及”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementTextAnd3<br></td>
<td class="tg-0lax"><br>int andTextId3<br></td>
<td class="tg-0lax"><br>设置隐私协议连接文本3资源ID 例：R.string.sec_verify_demo_privacy_and_3<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String andText3<br></td>
<td class="tg-0lax"><br>隐私协议连接文本3 例：“及”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementTextEnd<br></td>
<td class="tg-0lax"><br>int endTextId<br></td>
<td class="tg-0lax"><br>设置隐私协议结束文本资源ID 例：R.string.sec_verify_demo_privacy_end<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String endText<br></td>
<td class="tg-0lax"><br>隐私协议结束文本 例：“并使用本手机号登录”<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementTextSize<br></td>
<td class="tg-0lax"><br>int agreementTextSizeId<br></td>
<td class="tg-0lax"><br>设置隐私协议文字大小资源ID 例：R.dimen.sec_verify_demo_text_size_s<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int agreementTextSize<br></td>
<td class="tg-0lax"><br>隐私协议文字大小 （单位：sp） 例：16<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setAgreementAlignParentRight<br></td>
<td class="tg-0lax"><br>boolean agreementAlignParentRight<br></td>
<td class="tg-0lax"><br>设置隐私协议是否靠屏幕右边，默认false<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setAgreementHidden<br></td>
<td class="tg-0lax"><br>boolean agreementAlignParentRight<br></td>
<td class="tg-0lax"><br>设置隐私协议隐藏，默认false<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setAgreementText<br></td>
<td class="tg-0lax"><br>SpannableString agreementText<br></td>
<td class="tg-0lax"><br>设置自定义的完整的隐私协议内容<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setAgreementUncheckHintText<br></td>
<td class="tg-0lax"><br>int agreementUncheckHintTextId<br></td>
<td class="tg-0lax"><br>设置隐私协议复选框未选中时提示的文本小资源ID 例：R.dimen.sec_verify_demo_agreement_hint_text<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>String agreementUncheckHintText<br></td>
<td class="tg-0lax"><br>设置隐私协议复选框未选中时提示的文本 例: "请阅读并勾选隐私协议"<br></td>
</tr>

<tr>
<td class="tg-0lax"><br>setAgreementUncheckHintType<br></td>
<td class="tg-0lax"><br>int agreementUncheckHintType<br></td>
<td class="tg-0lax"><br>设置隐私协议复选框未选中时提示类型<br></td>
</tr>
</table>



#### Slogan

<table class="tg">
<tr>
<th class="tg-cly1"><br>&nbsp;方法<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;参数<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;意义<br>&nbsp;</th>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSloganTextSize<br></td>
<td class="tg-cly1"><br>int sloganTextSizeId<br></td>
<td class="tg-cly1"><br>Slogan文字大小资源ID 例：R.dimen.sec_verify_demo_text_size_s<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int sloganTextSize<br></td>
<td class="tg-cly1"><br>Slogan文字大小（单位：sp） 例：16<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSloganTextColor<br></td>
<td class="tg-cly1"><br>int sloganTextColorId<br></td>
<td class="tg-cly1"><br>Slogan文字颜色资源ID例：R.color.sec_verify_demo_slogan_color<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int sloganTextColor<br></td>
<td class="tg-cly1"><br>Slogan文字颜色 16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSloganOffsetX<br></td>
<td class="tg-cly1"><br>int sloganOffsetXId<br></td>
<td class="tg-cly1"><br>Slogan左偏移量大小资源ID例：R.dimen.sec_verify_demo_slogan_offset_x<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int sloganOffsetX<br></td>
<td class="tg-cly1"><br>Slogan左偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSloganOffsetY<br></td>
<td class="tg-cly1"><br>int sloganOffsetYId<br></td>
<td class="tg-cly1"><br>Slogan上偏移量大小资源ID例：R.dimen.sec_verify_demo_slogan_offset_y<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int sloganOffsetY<br></td>
<td class="tg-cly1"><br>Slogan上偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setSloganOffsetBottomY<br></td>
<td class="tg-cly1"><br>int sloganOffsetBottomYId<br></td>
<td class="tg-cly1"><br>Slogan下偏移量大小资源ID，设置此属性时，上偏移量大小不生效 例：R.dimen.sec_verify_demo_slogan_offset_bottom_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int sloganOffsetBottomY<br></td>
<td class="tg-0lax"><br>Slogan下偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setSloganOffsetRightX<br></td>
<td class="tg-0lax"><br>int sloganOffsetRightXId<br></td>
<td class="tg-0lax"><br>Slogan右偏移量大小资源ID例：R.dimen.sec_verify_demo_slogan_offset_right_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int sloganOffsetRightX<br></td>
<td class="tg-0lax"><br>Slogan右偏移量 （单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setSloganAlignParentRight<br></td>
<td class="tg-0lax"><br>boolean sloganAlignParentRight<br></td>
<td class="tg-0lax"><br>Slogan是否靠屏幕右边,默认false<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setSloganHidden<br></td>
<td class="tg-0lax"><br>boolean sloganHidden<br></td>
<td class="tg-0lax"><br>Slogan隐藏,默认false<br></td>
</tr>
</table>



#### 登录按钮

<table class="tg">
<tr>
<th class="tg-cly1"><br>&nbsp;方法<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;参数<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;意义<br>&nbsp;</th>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setLoginBtnImgId<br></td>
<td class="tg-cly1"><br>int loginBtnImgId<br></td>
<td class="tg-cly1"><br>登录按钮背景图资源ID，建议使用shape例： R.drawable.sec_verify_demo_login_btn_shape<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>Drawable loginBtnImg<br></td>
<td class="tg-cly1"><br>Drawable 对象<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setLoginBtnTextId<br></td>
<td class="tg-cly1"><br>int loginBtnTextId<br></td>
<td class="tg-cly1"><br>登录按钮文字资源ID 例：R.string.sec_verify_demo_login_btn_<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>String loginBtnText<br></td>
<td class="tg-cly1"><br>登录按钮文字 例：“一键登录”<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setLoginBtnTextColorId<br></td>
<td class="tg-cly1"><br>int loginBtnTextColorId<br></td>
<td class="tg-cly1"><br>登录按钮字体颜色资源ID 例：R.color.sec_verify_demo_login_btn_color<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int loginBtnTextColor<br></td>
<td class="tg-cly1"><br>登录按钮字体颜色 16进制色值，例：0xffffffff<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setLoginBtnTextSize<br></td>
<td class="tg-cly1"><br>int loginBtnTextSizeId<br></td>
<td class="tg-cly1"><br>登录按钮文字大小资源ID例：R.dimen.sec_verify_demo_text_size_s<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int loginBtnTextSize<br></td>
<td class="tg-cly1"><br>登录按钮文字大小（单位：sp） 例：16<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setLoginBtnWidth<br></td>
<td class="tg-cly1"><br>int loginBtnWidthId<br></td>
<td class="tg-cly1"><br>登录按钮宽度大小资源ID例：R.dimen.sec_verify_demo_login_btn_width<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int loginBtnWidth<br></td>
<td class="tg-0lax"><br>登录按钮宽度大小（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLoginBtnHeight<br></td>
<td class="tg-0lax"><br>int loginBtnHeightId<br></td>
<td class="tg-0lax"><br>登录按钮高度大小资源ID例：R.dimen.sec_verify_demo_login_btn_height<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int loginBtnHeight<br></td>
<td class="tg-0lax"><br>登录按钮高度大小（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLoginBtnOffsetX<br></td>
<td class="tg-0lax"><br>int loginBtnOffsetXId<br></td>
<td class="tg-0lax"><br>登录按钮左偏移量大小资源ID例：R.dimen.sec_verify_demo_login_btn_offset_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int loginBtnOffsetX<br></td>
<td class="tg-0lax"><br>登录按钮左偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLoginBtnOffsetY<br></td>
<td class="tg-0lax"><br>int loginBtnOffsetYId<br></td>
<td class="tg-0lax"><br>登录按钮上偏移量大小资源ID例：R.dimen.sec_verify_demo_login_btn_offset_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int loginBtnOffsetY<br></td>
<td class="tg-0lax"><br>登录按钮上偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLoginBtnOffsetBottomY<br></td>
<td class="tg-0lax"><br>int loginBtnOffsetBottomYId<br></td>
<td class="tg-0lax"><br>登录按钮下偏移量大小资源ID例：R.dimen.sec_verify_demo_login_btn_offset_bottom_y<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int loginBtnOffsetBottomY<br></td>
<td class="tg-0lax"><br>登录按钮下偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax" rowspan="2"><br>setLoginBtnOffsetRightX<br></td>
<td class="tg-0lax"><br>int loginBtnOffsetRightXId<br></td>
<td class="tg-0lax"><br>登录按钮右偏移量大小资源ID例：R.dimen.sec_verify_demo_login_btn_offset_right_x<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int loginBtnOffsetRightX<br></td>
<td class="tg-0lax"><br>登录按钮右偏移量（单位：dp） 例：30<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setLoginBtnAlignParentRight<br></td>
<td class="tg-0lax"><br>boolean loginBtnAlignParentRight<br></td>
<td class="tg-0lax"><br>登录按钮是否靠屏幕右边,默认false<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setLoginBtnHidden<br></td>
<td class="tg-0lax"><br>Boolean loginBtnHidden<br></td>
<td class="tg-0lax"><br>登录按钮是否隐藏,默认false<br></td>
</tr>
</table>


#### 动画
| 方法 | 意义 |
|--------|--------|
| setTranslateAnim(boolean translateAnim) | 设置授权页面从左往右平移动画 |
| setRightTranslateAnim(boolean rightAnim) | 设置进入授权页面从从右往左平移动画 |
| setBottomTranslateAnim(boolean bottomAnim) | 设置进入授权页面从下往上平移，退出方向相反 |
| setZoomAnim(boolean zoomAnim) | 设置授权页面从大到小动画 |
| setFadeAnim(boolean fadeAnim) | 设置授权页面从透明到不透明动画 |
| setStartActivityTransitionAnim(int startInAnim,int startOutAnim) | 设置授权页面进入动画ID |
| setFinishActivityTransitionAnim(int finishInAnim,int finishOutAnim) | 设置授权页面结束动画ID |

#### 弹窗模式

<table class="tg">
<tr>
<th class="tg-cly1"><br>&nbsp;方法<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;参数<br>&nbsp;</th>
<th class="tg-cly1"><br>&nbsp;意义<br>&nbsp;</th>
</tr>
<tr>
<td class="tg-cly1"><br>setDialogTheme<br></td>
<td class="tg-cly1"><br>boolean dialogTheme<br></td>
<td class="tg-cly1"><br>设置是否使用弹窗模式，默认false<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>setDialogAlignBottom<br></td>
<td class="tg-cly1"><br>boolean dialogAlignBottom<br></td>
<td class="tg-cly1"><br>设置弹窗是否靠屏幕底部，默认false<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setDialogOffsetX<br></td>
<td class="tg-cly1"><br>int dialogOffsetXId<br></td>
<td class="tg-cly1"><br>设置弹窗左右偏移量资源ID例：R.dimen.sec_verify_demo_dialog_offset_x<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int dialogOffsetX<br></td>
<td class="tg-cly1"><br>弹窗左右偏移量（单位：dp）  例：60<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setDialogOffsetY<br></td>
<td class="tg-cly1"><br>int dialogOffsetYId<br></td>
<td class="tg-cly1"><br>设置弹窗上下偏移量资源ID例：R.dimen.sec_verify_demo_dialog_offset_y<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int dialogOffsetY<br></td>
<td class="tg-cly1"><br>弹窗上下偏移量（单位：dp） 例：80<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setDialogWidth<br></td>
<td class="tg-cly1"><br>int dialogWidthId<br></td>
<td class="tg-cly1"><br>设置弹窗宽度资源ID例：R.dimen.sec_verify_demo_dialog_width<br></td>
</tr>
<tr>
<td class="tg-cly1"><br>int dialogWidth<br></td>
<td class="tg-cly1"><br>弹窗宽度（单位：dp） 例：100<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setDialogHeight<br></td>
<td class="tg-cly1"><br>int dialogHeightId<br></td>
<td class="tg-cly1"><br>设置弹窗高度资源ID例：R.dimen.sec_verify_demo_dialog_height<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>int dialogHeight<br></td>
<td class="tg-0lax"><br>弹窗高度（单位：dp） 例：200<br></td>
</tr>
<tr>
<td class="tg-cly1" rowspan="2"><br>setDialogMaskBackground<br></td>
<td class="tg-cly1"><br>int dialogBackground<br></td>
<td class="tg-cly1"><br>设置弹窗蒙版背景 例： R.drawable.sec_verify_demo_dialog_background<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>Drawable dialogBackground<br></td>
<td class="tg-0lax"><br>Drawable对象<br></td>
</tr>
<tr>
<td class="tg-0lax"><br>setDialogMaskBackgroundClickClose<br></td>
<td class="tg-0lax"><br>boolean dialogBackgroundClickClose<br></td>
<td class="tg-0lax"><br>设置点击弹窗蒙版是否关闭页面，默认false<br></td>
</tr>
</table>


示例代码（可参考Demo的MainActivity）：

```	
     
	/**
     * 自定义授权页面UI样式
     */
    private void customizeUi() {
       UiSettings uiSettings = new UiSettings.Builder()
            /** 标题栏 */
            // 标题栏背景色资源ID
            .setNavColorId(R.color.sec_verify_demo_text_color_blue)
            //标题栏是否透明
            .setNavTransparent(false)
            //标题栏是否隐藏
            .setNavHidden(false)
            //设置背景图片
            .setBackgroundImgId(R.drawable.sec_verify_background_demo)
            //设置背景是否点击关闭页面
            .setBackgroundClickClose(true)
            // 标题栏标题文字资源ID
            .setNavTextId(R.string.sec_verify_demo_verify)
            // 标题栏文字颜色资源ID
            .setNavTextColorId(R.color.sec_verify_demo_text_color_common_black)
            // 标题栏左侧关闭按钮图片资源ID
            .setNavCloseImgId(R.drawable.sec_verify_demo_close)
            //标题栏返回按钮是否隐藏
            .setNavCloseImgHidden(false)
            /** Logo */
            // Logo图片资源ID，默认使用应用图标
            .setLogoImgId(R.drawable.ic_launcher)
            //logo是否隐藏
            .setLogoHidden(false)
            //logo宽度
            .setLogoWidth(R.dimen.sec_verify_demo_logo_width_customize)
            //logo高度
            .setLogoHeight(R.dimen.sec_verify_demo_logo_height_customize)
            //logo x轴偏移量
            .setLogoOffsetX(R.dimen.sec_verify_demo_logo_offset_x_customize)
            //logo y轴偏移量
            .setLogoOffsetY(R.dimen.sec_verify_demo_logo_offset_y_customize)
            //logo x轴右偏移量
			.setLogoOffsetRightX(R.dimen.sec_verify_demo_logo_offset_right_x_customize)
			//logo 是否靠屏幕右边
			.setLogoAlignParentRight(false)
            /** 手机号 */
            // 脱敏手机号字体颜色资源ID
            .setNumberColorId(R.color.sec_verify_demo_text_color_common_black)
            // 脱敏手机号字体大小资源ID
            .setNumberSizeId(R.dimen.sec_verify_demo_text_size_m)
            //脱敏手机号 x轴偏移量
            .setNumberOffsetX(R.dimen.sec_verify_demo_number_field_offset_x_customize)
            //脱敏手机号 y轴偏移量
            .setNumberOffsetY(R.dimen.sec_verify_demo_number_field_offset_y_customize)
            //脱敏手机号 x轴右偏移量
			.setNumberOffsetRightX(R.dimen.sec_verify_demo_number_field_offset_right_x_customize)
			//脱敏手机号 是否靠屏幕右边
			.setNumberAlignParentRight(true)
		            /** 切换帐号 */
            // 切换账号字体颜色资源ID
            .setSwitchAccColorId(R.color.sec_verify_demo_text_color_blue)
            //切换账号 字体大小
            .setSwitchAccTextSize(R.dimen.sec_verify_demo_text_size_s)
            // 切换账号是否显示，默认显示
            .setSwitchAccHidden(false)
            //切换账号 x轴偏移量
            .setSwitchAccOffsetX(R.dimen.sec_verify_demo_switch_acc_offset_x_customize)
            //切换账号 y轴偏移量
            .setSwitchAccOffsetY(R.dimen.sec_verify_demo_switch_acc_offset_y_customize)
	        //脱敏手机号 x轴右偏移量
			.setSwitchAccOffsetRightX(R.dimen.sec_verify_demo_switch_acc_offset_right_x_customize)
			//脱敏手机号 是否靠屏幕右边
			.setSwitchAccAlignParentRight(true)
            /** 登录按钮 */
            // 登录按钮背景图资源ID，建议使用shape
            .setLoginBtnImgId(R.drawable.sec_verify_demo_shape_rectangle)
            // 登录按钮文字资源ID
            .setLoginBtnTextId(R.string.sec_verify_demo_login)
            // 登录按钮字体颜色资源ID
            .setLoginBtnTextColorId(R.color.sec_verify_demo_text_color_common_white)
            //登录按钮字体大小
            .setLoginBtnTextSize(R.dimen.sec_verify_demo_text_size_s)
            //登录按钮 width
            .setLoginBtnWidth(R.dimen.sec_verify_demo_login_btn_width_customize)
            //登录按钮 height
            .setLoginBtnHeight(R.dimen.sec_verify_demo_login_btn_height_customize)
            //登录按钮 x轴偏移
            .setLoginBtnOffsetX(R.dimen.sec_verify_demo_login_btn_offset_x_customize)
            //登录按钮 y轴偏移
            .setLoginBtnOffsetY(R.dimen.sec_verify_demo_login_btn_offset_y_customize)
            //登录按钮 x轴右偏移
			.setLoginBtnOffsetRightX(R.dimen.sec_verify_demo_login_btn_offset_right_x_customize)
			//登录按钮 靠屏幕右边
			.setLoginBtnAlignParentRight(true)
            /** 隐私协议 */
            //是否隐藏复选框(设置此属性true时setCheckboxDefaultState不会生效)
            .setCheckboxHidden(false)
            // 隐私协议复选框背景图资源ID，建议使用selector
            .setCheckboxImgId(R.drawable.sec_verify_demo_customized_checkbox_selector)
            // 隐私协议复选框默认状态，默认为“选中”
            .setCheckboxDefaultState(true)
            // 隐私协议字体颜色资源ID（自定义隐私协议的字体颜色也受该值影响）
            .setAgreementColorId(R.color.sec_verify_demo_main_color)
            // 自定义隐私协议一文字资源ID
            .setCusAgreementNameId1(R.string.sec_verify_demo_customize_agreement_name_1)
            // 自定义隐私协议一URL
            .setCusAgreementUrl1(http://www.baidu.com)
            //自定义隐私协议一颜色
            .setCusAgreementColor1(R.color.sec_verify_demo_main_color)
            // 自定义隐私协议二文字资源ID
            .setCusAgreementNameId2(R.string.sec_verify_demo_customize_agreement_name_2)
            // 自定义隐私协议二URL
            .setCusAgreementUrl2(https://www.jianshu.com)
            //自定义隐私协议二颜色
            .setCusAgreementColor2(R.color.sec_verify_demo_main_color)
            //隐私协议是否左对齐，默认居中
            .setAgreementGravityLeft(true)
            //隐私协议其他文字颜色
            .setAgreementBaseTextColorId(R.color.sec_verify_demo_text_color_common_black)
            //隐私协议 x轴偏移量，默认30
            .setAgreementOffsetX(R.dimen.sec_verify_demo_agreement_offset_x_customize)
            //隐私协议 x轴rightMargin右偏移量，默认30
          .setAgreementOffsetRightX(R.dimen.sec_verify_demo_agreement_offset_x_customize)
            //隐私协议 y轴偏移量
            .setAgreementOffsetY(R.dimen.sec_verify_demo_agreement_offset_y_customize)
            //隐私协议 底部y轴偏移量                            
  			.setAgreementOffsetBottomY(R.dimen.sec_verify_demo_agreement_offset_bottom_y_customize)
            /** slogan */
            //slogan文字大小
            .setSloganTextSize(R.dimen.sec_verify_demo_text_size_s)
            //slogan文字颜色
            .setSloganTextColor(R.color.sec_verify_demo_main_color)
            //slogan x轴偏移量
            .setSloganOffsetX(R.dimen.sec_verify_demo_slogan_offset_x_customize)
            //slogan y轴偏移量
            .setSloganOffsetY(R.dimen.sec_verify_demo_slogan_offset_y_customize)
            //slogan 底部y轴偏移量(设置此属性时，setSloganOffsetY不生效)
            .setSloganOffsetBottomY(R.dimen.sec_verify_demo_slogan_offset_botto    m_y_customize)
             //设置状态栏为透明状态栏，5.0以上生效
			.setImmersiveTheme(false)
			//设置状态栏文字颜色为黑色，只在6.0以上生效
			.setImmersiveStatusTextColorBlack(false)
			//使用平移动画
			.setTranslateAnim(true)
			//设置隐私协议文字起始
			.setAgreementTextStart(R.string.sec_verify_demo_agreement_ext_start)
			//设置隐私协议文字连接
			.setAgreementTextAnd1(R.string.sec_verify_demo_agreement_txt_and1)
			//设置隐私协议文字连接
			.setAgreementTextAnd2(R.string.sec_verify_demo_agreement_txt_and2)
			//设置隐私协议文字结束
			.setAgreementTextEnd(R.string.sec_verify_demo_agreement_tet_end)
			//设置移动隐私协议文字
			.setAgreementCmccText(R.string.sec_verify_demo_agreement_txt_cmcc)
			//设置联通隐私协议文字
			.setAgreementCuccText(R.string.sec_verify_demo_agreement_txt_cucc)
			//设置电信隐私协议文字
			.setAgreementCtccText(R.string.sec_verify_demo_agreement_txt_ctcc)
			
            .build();
      SecVerify.setUiSettings(uiSettings);
    }

```
### 添加自定义控件

SecVerify支持在授权页添加开发者自定义的控件，关于该功能，请注意以下说明：

- 该功能主要用于在授权页添加其他第三方登录方式（其他登录方式也可以通过页面的“切换账号”实现，区别仅在于其他登录方式是否显示在授权页）
- 不建议添加过多自定义控件

#### 1 标题栏下方位置添加自定义控件
接口：CustomUIRegister.addCustomizedUi(List<View> viewList, CustomViewClickListener listener) 

|	接口		| 参数列表 | 必须 | 说明 |
|----|----|----|----|
| addCustomizedUi | List viewList, | 是 | viewList： 自定义view列表 |
| addCustomizedUi | CustomViewClickListener listener | 否 | listener：自定义view事件监听器 |
#### 2 标题栏上添加自定义控件
接口：CustomUIRegister.addTitleBarCustomizedUi(List<View> viewList, CustomViewClickListener listener)  件

|	接口		| 参数列表 | 必须 | 说明 |
|----|----|----|----|
| addTitleBarCustomizedUi | List viewList | 是 | viewList： 自定义view列表 |
| addTitleBarCustomizedUi | CustomViewClickListener listener | 否 | listener：自定义view事件监听器 |
#### 3 用于设置自定义的LoadingView
接口：CustomUIRegister.setCustomizeLoadingView(View loadingView)  

|	接口		| 参数列表 | 必须 | 说明 |
|----|----|----|----|
| setCustomizeLoadingView | View loadingView | 是 | loadingView： 自定义LoadingView |

```
TextView view = new TextView(context);
view.setId(R.id.customized_view_id);
view.setText("其他方式登录");
RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
params.addRule(RelativeLayout.CENTER_HORIZONTAL);
params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
params.bottomMargin = ResHelper.dipToPx(context,120);
view.setLayoutParams(params);

ImageView iv1 = new ImageView(context);
iv1.setId(R.id.customized_btn_id_1);
iv1.setImageDrawable(context.getResources().getDrawable(R.drawable.sec_verify_demo_wechat));
RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
params1.bottomMargin = ResHelper.dipToPx(context,80);
iv1.setLayoutParams(params1);

List<View> views = new ArrayList<View>();
views.add(view);
views.add(iv1);
   CustomUIRegister.addCustomizedUi(views, new CustomViewClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                String msg = ;
                if (id == R.id.customized_btn_id_1) {
                    msg = 按钮1 clicked;
                    // 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可调用该方法
                    SecVerify.finishOAuthPage();
                } else if (id == R.id.customized_btn_id_2) {
                    msg = 按钮2 clicked;
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

```

##### 注意：

- 以上方法需在 `登录` 接口之前调用
- 自定义控件将被添加到一个相对布局中，因此控件的位置与大小，需使用`RelativeLayout.LayoutParams`进行设置
- 自定义控件仅支持View，不支持ViewGroup，请勿将多个自定义View放在一个ViewGroup（LinearLayout、RelativeLayout等）中传入，否则可能无法监听到每个子View的点击事件
- 自定义控件点击时，SecVerify默认不关闭授权页面，若需关闭，可通过`SecVerify.finishOAuthPage()`方法实现

# 五 混淆设置
SecVerify已经做了混淆处理，再次混淆会导致不可预期的错误，请在您的混淆脚本中添加如下的配置，跳过对SecVerify的混淆操作：
```
-ignorewarnings
# for SecVerify
-keep class com.mob.**{*;}
# for CTCC
-keep class cn.com.chinatelecom.account.**{*;}
# for CUCC
-keep class com.sdk.**{*;}
# for CMCC
-keep class com.cmic.sso.sdk.**{*;}
```
# 六 注意事项
1、 使用gradle集成时，默认为SecVerify提供最新版本的集成，如果您想锁定某个版本，可以在SecVerify下设置“version "某个版本"”来固定使用这个版本

```
MobSDK {
    appKey "申请Mob的appkey"
    appSecret "申请Mob的AppSecret"
    SecVerify {
        version '2.0.8'
    }
}
```

2、 如果使用插件的模块会被其它模块依赖，请确保依赖它的模块也引入插件，或在此模块的gradle中添加：

   ```xml
   apply plugin: 'com.mob.sdk'
   ```
	 
3、如果只需要某个运营商的功能，可以通过如下配置关闭其他运营商的功能

 ```
  MobSDK {
    appKey "申请Mob的appkey"
    appSecret "申请Mob的AppSecret"
    SecVerify {
        version '2.0.8'
         disable {
            CMCC {}
            CUCC {}
            CTCC {}
        }
    }
}
 ```
 
4、由于系统本身限制，OPPO系统首次进入且连接WIFI的时候无法获取到使用移动网络的权限，需要手动关闭WIFI或者进入设置中 “双卡与移动网络” 里的 “使用WLAN与移动网络的应用” 中设置当前APP允许使用移动网络 
 


# 七、错误码
## 服务器错误码列表

|错误码|说明|
|:----|:--|
|5119104|解密失败|
|5119105|服务错误|
|4119301|数据校验失败|
|4119302|数据不存在|
|4119303|数据已经存在|
|5119303|数据已经存在|
|4119310|token未找到（客户端错误）|
|5119310|token未找到（服务端错误）|
|4119311|token非法|
|4119330|App没有初始化|
|4119331|AppSecret错误|
|4119520|md5不匹配|
|4119521|包名没有配置|

## 客户端错误码列表

|错误码|说明|
|:----|:--|
|6119000|不支持的运营商|
|6119001|手机号不合法|
|6119002|无sim卡|
|6119003|缺少必要的权限|
|6119004|未打开蜂窝网络|
|6119005|未接受Mob隐私协议|
|6119401|获取运营商配置信息失败|
|6119402|登录失败|
|6119403|运营商预取号失败|
|6119404|运营商登录失败|
|6119405|用户取消授权|
|6119406|获取服务器token失败|
|6119095|缺少必要的参数|
|6119096|参数不合法|
|6119097|服务器返回数据异常|
|6119098|网络异常|
|6119099|未知错误|

### 移动错误码
|错误码|说明|
|:----|:--|
|103000| 成功|
|102507| 登录超时（授权页点登录按钮时）|
|103101| 请求签名错误|
|103102| 包签名/Bundle ID错误|
|103108| 短信验证码错误|
|103109| 短信验证码校验超时|
|103111| 网关IP错误|
|103119| appid不存在|
|103211| 其他错误，（如有需要请联系qq群609994083内的移动认证开发）|
|103901| 短验下发次数已达上限（5次/min,30次/day）|
|103902| scrip失效|
|103911| token请求过于频繁，10分钟内获取token且未使用的数量不超过30个|
|103273| 预取号联通重定向（暂不支持联通取号）|
|105002| 移动取号失败|
|105003| 电信取号失败|
|105021| 已达当天取号限额|
|105302| appid不在白名单|
|105313| 非法请求|
|200005| 用户未授权（READ_PHONE_STATE）|
|200020| 用户取消登录|
|200021| 数据解析异常|
|200022| 无网络|
|200023| 请求超时|
|200024| 数据网络切换失败|
|200025| 未知错误一般出现在线程捕获异常，请配合异常打印分析|
|200026| 输入参数错误|
|200027| 未开启数据网络|
|200028| 网络请求出错|
|200038| 异网取号网络请求失败|
|200039| 异网取号网关取号失败|
|200040| UI资源加载异常|
|200048| 用户未安装sim卡|
|200050| EOF异常|
|200060| 切换账号（未使用SDK短验时返回）|
|200072| CA根证书校验失败|
|200080| 本机号码校验仅支持移动手机号|
|200082| 服务器繁忙|

### 联通错误码
|错误码|说明|示例说明|
|:--- |:----|:----|
| 101001 |授权码不能为空使用SDK|调用置换接口时没有填入授权码|
|101002|认证的手机号不能为空使用SDK|认证置换时没有填入需要认证的手机号码|
|101003|UiConfig 不能为空|调用openActivity 接口时，必须配置UiConfig|
|101004|ApiKey 或PublicKey不能为空|未进行初始化，调用SDKManager.init()进行初始化|
|101005|超时|超过了接入方设置的时间|
|101006|公钥出错|公钥错误，请核对配置的公钥是否与申请的公钥一致|
|101007|用户取消登录|免密登录时，进入授权页执行了返回操作|
|102001|选择流量通道失败|取号功能必须使用流量访问，在wifi和流量同时开启的情况下，sdk 会选择使用流量进行访问，此返回码代表切换失败！（受不同机型的影响）|
|201001|"操作频繁请请稍后再试"|超出10 分钟之内只能访问30 次的限制|
|302001|SDK 解密异常|服务端返回数据时sdk会进行解密操作，如果解密出错则出现此错误|
|302002|网络访问异常sdk|网络请求异常|
|302003|服务端数据格式出错|服务端返回数据格式错误|



### 电信错误码
|错误码|说明|
|:----|:--|
|	0	|	处理结果正常	|
|	-2	|	appid为空	|
|	-6	|	appid-invalid	|
|	-32	|	AccessToken 存在问题	|
|	-61	|	AccessToken 不存在	|
|	-64	|	没有权限	|
|	-65	|	请求频率过高	|
|	-66	|	Token没有权限	|
|	-103	|	AccessToken 无效	|
|	-7999	|	服务不可用（其他错误，默认返回值）——电信版	|
|	-8000	|	网络错误（http非200状态码）——电信版	|
|	-8001	|	请求网络异常	|
|	-8002	|	请求参数异常	|
|	-8003	|	请求超时	|
|	-8004	|	移动网络未开启	|
|	-8005	|	请先初始化SDK	|
|	-8100	|	蜂窝数据网络未开启	|
|	-8101	|	获取失败（切换失败）	|
|	-8102	|	获取失败	|
|	-8103	|	域名解析异常	|
|	-8104	|	I0异常	|
|	-8200	|	关闭登录界面	|
|	-8201	|	响应码错误	|
|	-8202	|	登录结果异常	|
|	-8203	|	用户点击其他登录方式	|
|	-9999	|	其他异常	|
|	-10000	|	获取用户信息失败	|
|	-10001	|	调用失败	|
|	-10002	|	参数错误	|
|	-10003	|	解密失败	|
|	-10004	|	无效的IP	|
|	-10005	|	异网授权回调参数异常	|
|	-10006	|	Mdn授权失败，且属于电信网络	|
|	-10007	|	重定向到异网取号	|
|	-10008	|	超过预设取号阀值	|
|	-10009	|	时间超期	|
|	-10010	|	mobile-error	|
|	-10011	|	运营商不匹配	|
|	-10012	|	区域不匹配	|
|	-10013	|	业务类型不支持该运营商	|
|	-10014	|	AES解密失败	|
|	-10015	|	Ipv6取号失败	|
|	-10016	|	安全校验失败	|
|	-10017	|	redirect方式需要https的callback地址	|
|	-20005	|	签名非法	|
|	-20006	|	应用不存在	|
|	-20007	|	公钥数据不存在	|
|	-20100	|	内部解析错误	|
|	-20102	|	加密参数解析失败	|
|	-30001	|	时间戳非法	|
|	-30003	|	topClass失效	|
|	-99999	|	服务内部错误	|
|	-720001	|	切换异常	|
|	-720002	|	切换异常超时	|
|	404	|	API配置不存在	|
|	10000	|	处理结果正常	|
|	20101	|	参数错误	|
|	20102	|	参数转换异常	|
|	20103	|	参数为空	|
|	20104	|	权限处理异常	|
|	20105	|	返回结果转换Json 异常	|
|	20107	|	Token权限处理异常	|
|	20108	|	获取用户信息失败	|
|	20109	|	限流处理异常	|
|	20110	|	获取TOKEM异常	|
|	20123	|	底层返回302/301	|
|	30002	|	无法识别用户网络，返回两个重定向异网取号地址	|
|	30100	|	clientID为空	|
|	30101	|	时间戳为空	|
|	30102	|	时间戳无效	|
|	30103	|	accessCode为空	|
|	30201	|	ParamKey参数解密错误	|
|	30900	|	登录失败	|
|	30901	|	Code换Tokenfail	|
|	30902	|	移动回调失败	|
|	30903	|	电信回调失败	|
|	30904	|	联通回调失败	|
|	30909	|	内部错误	|
|	30910	|	topClass异常	|
|	48001	|	请求网络异常	|
|	48003	|	请求超时	|
|	49000	|	接口请求成功，认证失败	|
|	49001	|	返回结果格式异常	|
|	51001	|	参数不能为空	|
|	51002	|	接入不合法	|
|	51003	|	能力集不合法	|
|	51004	|	Appid和accesstoken不匹配	|
|	51005	|	没有AccessToken记录	|
|	51114	|	无法获取手机号数据	|
|	51207	|	获取accessCode使用的appid与本次操作的appid不一致	|
|	51208	|	"无效的accessCode,该accessCode无法在该业务中使用"	|
|	80000	|	请求超时	|
|	80001	|	请求网络异常	|
|	80002	|	响应码错误	|
|	80003	|	无网络连接	|
|	80004	|	移动网络未开启	|
|	80005	|	Socket超时异常	|
|	80006	|	域名解析异常	|
|	80007	|	I0异常	|
|	80008	|	No route to host	|
|	80009	|	nodename nor servname provided，or not known	|
|	80010	|	Socket closed by remote peer	|
|	80100	|	登录结果为空	|
|	80101	|	登录结果异常	|
|	80102	|	预登录异常	|
|	80103	|	SDK未初始化	|
|	80104	|	未调用预登录接口	|
|	80105	|	加载nib文件异常	|
|	80200	|	用户关闭界面	|
|	80201	|	其他登录方式	|
|	80800	|	WIFI切换异常	|
|	80801	|	WIFI切换超时	|
