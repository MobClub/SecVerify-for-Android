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

a.开发者需提前申请READ_PHONE_STATE权限（Android6.0以上需要动态申请该权限）,否则会走失败回调

b.免密登录能力必须经过运营商网关取号，因此必须在手机打开移动蜂窝网络的前提下才会成功。  

c.预登录接口用于向运营商进行预取号操作，建议在实际调用登录接口前提前调用预登录接口（比如应用启动时或进入注册或登录页面时），这将极大地加快登录接口执行耗时，提高用户体验。  


### 1 预登录
**接口描述:**

获取临时凭证
可以提前获知当前用户的手机网络环境是否符合一键登录的使用条件，成功后将得到用于一键登录使用的临时凭证, 默认的凭证有效期60s(电信)/30min(联通)/60min(移动)。
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


	 
### 6 刷新授权页面

SecVerify提供了以下方法用于刷新授权页面：
```
SecVerify.refreshOAuthPage(boolean isFinish);
```

关于该方法，作以下说明：

- 如果需要在授权页面修改界面内容，可以通过设置ui属性的hidden方法来隐藏界面控件并用自定义控件设置自己的UI
   
# 四、授权页界面修改

## 收取页面控件

SecVerify提供默认的授权页面样式，在不指定的情况下显示默认样式

**说明**：

1. 该方法需在 `登录` 接口之前调用。
2. 除个别属性外，所有属性设置方法的参数统一使用资源ID（若传入非资源ID，将无效）
3. 控件背景色的设置，建议使用 `selector` 以及 `shape` 实现，具体方法请参考Demo的 `MainActivity`。
4. 所有控件的X、RightX、Y、BottomY属性分别代表了控件距离屏幕左右上下的距离
5. 设置了BottomY属性后，会默认靠屏幕底部，同时Y属性的设置即距屏幕上方距离无效。
6. 根据运营商要求，手机号、登录按钮、运营商协议及品牌部分不可隐藏

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
<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
UiSettings.Builder builder = new UiSettings.Builder();
//LandUiSettings.Builder builder = new LandUiSettings.Builder();
builder
//设置状态栏为透明状态栏，5.0以上生效
.setImmersiveTheme(false)
//设置状态栏文字颜色为黑色，只在6.0以上生效
.setImmersiveStatusTextColorBlack(false)
```
</details>

#### 导航栏
| 方法 | 意义 |
|--------|--------|
| setNavColorId(int navColorId) | 标题栏背景色资源ID |
| setNavTextId(int navTextId) | 标题栏标题文字资源ID |
| setNavTextSize(int navTextSizeId) | 标题栏标题文字大小ID |
| setNavTextColorId(int navTextColorId) | 标题栏文字颜色资源ID |
| setNavCloseImgId(int navCloseImgId) | 标题栏左侧关闭按钮图片资源ID |
| setNavCloseImgWidth(int navCloseImgWidth) | 标题栏左侧关闭按钮图片宽度资源ID |
| setNavCloseImgHeight(int navCloseImgHeight) | 标题栏左侧关闭按钮图片高度资源ID |
| setNavCloseImgOffsetX(int navCloseImgOffsetX) | 标题栏左侧关闭按钮图片左偏移量资源ID |
| setNavCloseImgOffsetRightX(int navCloseImgOffsetRightX) | 标题栏左侧关闭按钮图片右偏移量资源ID（设置之后关闭图标会默认靠屏幕右边） |
| setNavCloseImgOffsetY(int navCloseImgOffsetY) | 标题栏左侧关闭按钮图片上偏移量资源ID |
| setNavTransparent(boolean navTransparent) | 标题栏是否透明,默认透明 |
| setNavHidden(boolean navHidden) | 标题栏是否隐藏，默认不隐藏 |
| setNavCloseImgHidden(boolean navCloseImgHidden) | 标题栏左侧关闭按钮是否隐藏，默认不隐藏 |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
// 标题栏背景色资源ID
.setNavColorId(R.color.sec_verify_demo_text_color_blue)
// 标题栏标题文字资源ID
.setNavTextId(R.string.sec_verify_demo_verify)
//标题栏文字大小
.setNavTextSize(R.dimen.sec_verify_demo_text_size_s)
// 标题栏文字颜色资源ID
.setNavTextColorId(R.color.sec_verify_demo_text_color_common_black)
// 标题栏左侧关闭按钮图片资源ID
.setNavCloseImgId(R.drawable.sec_verify_demo_close)
//标题栏左侧关闭按钮图片宽度资源ID
.setNavCloseImgWidth(R.dimen.sec_verify_demo_close_with)
//标题栏左侧关闭按钮图片高度资源ID |
.setNavCloseImgHeight(R.dimen.sec_verify_demo_close_height)
//标题栏左侧关闭按钮图片左偏移量资源ID
.setNavCloseImgOffsetX(R.dimen.sec_verify_demo_close_offset_x)
//标题栏左侧关闭按钮图片右偏移量资源ID（设置之后关闭图标会默认靠屏幕右边）
.setNavCloseImgOffsetRightX(R.dimen.sec_verify_demo_close_offset_right_x)
//标题栏左侧关闭按钮图片上偏移量资源ID
.setNavCloseImgOffsetY(R.dimen.sec_verify_demo_close_offset_y)
//标题栏是否透明
.setNavTransparent(false)
//标题栏是否隐藏
.setNavHidden(false)
//标题栏返回按钮是否隐藏
.setNavCloseImgHidden(false)
```
</details>

#### 背景
| 方法 | 意义 |
|--------|--------|
| setBackgroundClickClose（boolean backgroundClickClose） | 设置点击授权页面背景是否关闭页面，默认不关闭页面 |
| setBackgroundImgId(int backgroundImgId) | 设置背景图片资源ID |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
//设置背景图片
.setBackgroundImgId(R.drawable.sec_verify_background_demo)
//设置点击授权页面背景是否关闭页面
.setBackgroundClickClose(false)
```
</details>

#### Logo
| 方法 | 意义 |
|--------|--------|
| setLogoImgId(int logoImgId) | Logo图片资源ID，默认使用应用图标 |
| setLogoHidden(boolean logoHidden) | Logo是否隐藏，默认不隐藏 |
| setLogoWidth(int logoWidth) | Logo宽度大小资源ID |
| setLogoHeight(int logoHeight) | Logo高度大小资源ID |
| setLogoOffsetX(int logoOffsetX) | Logo左偏移量大小资源ID |
| setLogoOffsetY(int logoOffsetY) | Logo上偏移量大小资源ID |
| setLogoOffsetBottomY(int logoOffsetBottomY) | Logo下偏移量大小资源ID |
| setLogoOffsetRightX(int logoOffsetRightX) | Logo右偏移量大小资源ID |
| setLogoAlignParentRight(boolean logoAlignParentRight) | Logo是否靠屏幕右边 |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
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
//logo BottomY轴偏移量
.setLogoOffsetBottomY(R.dimen.sec_verify_demo_logo_offset_bottom_y_customize)
//logo x轴右偏移量
.setLogoOffsetRightX(R.dimen.sec_verify_demo_logo_offset_right_x_customize)
//logo 是否靠屏幕右边
.setLogoAlignParentRight(false)
```
</details>

#### 脱敏手机号
| 方法 | 意义 |
|--------|--------|
| setNumberColorId(int numberColorId) | 脱敏手机号字体颜色资源ID |
| setNumberSizeId(int numberSizeId) | 脱敏手机号字体大小资源ID |
| setNumberOffsetX(int numberOffsetX) | 脱敏手机号 左偏移量大小资源ID |
| setNumberOffsetY(int numberOffsetY) | 脱敏手机号 上偏移量大小资源ID |
| setNumberOffsetBottomY(int numberOffsetBottomY) | 脱敏手机号 下偏移量大小资源ID |
| setNumberOffsetRightX(int numberOffsetRightX) | 脱敏手机号 右偏移量大小资源ID |
| setNumberAlignParentRight(boolean numberAlignParentRight) | 脱敏手机号是否靠屏幕右边 |
| setNumberHidden(boolean numberHidden) | 脱敏手机号隐藏 |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
```
builder
 // 脱敏手机号字体颜色资源ID
.setNumberColorId(R.color.sec_verify_demo_text_color_common_black)
// 脱敏手机号字体大小资源ID
.setNumberSizeId(R.dimen.sec_verify_demo_text_size_m)
//脱敏手机号 x轴偏移量
.setNumberOffsetX(R.dimen.sec_verify_demo_number_field_offset_x_customize)
//脱敏手机号 y轴偏移量
.setNumberOffsetY(R.dimen.sec_verify_demo_number_field_offset_y_customize)
//脱敏手机号  BottomY轴偏移量
.setNumberOffsetBottomY(R.dimen.sec_verify_demo_number_field_offset_bottom_y_customize)
//脱敏手机号 x轴右偏移量
.setNumberOffsetRightX(R.dimen.sec_verify_demo_number_field_offset_right_x_customize)
//脱敏手机号 是否靠屏幕右边
.setNumberAlignParentRight(true)
//脱敏手机号隐藏
.setNumberHidden(false)
```
</details>

#### 切换账号
| 方法 | 意义 |
|--------|--------|
| setSwitchAccTextSize(int switchAccTextSize) | 切换账号字体大小资源ID |
| setSwitchAccColorId(int switchAccColorId) | 切换账号字体颜色资源ID |
| setSwitchAccHidden(boolean switchAccHidden) | 切换账号是否显示，默认显示 |
| setSwitchAccOffsetX(int switchAccOffsetX) | 切换账号 左偏移量大小资源ID |
| setSwitchAccOffsetY(int switchAccOffsetY) | 切换账号 上偏移量大小资源ID |
| setSwitchAccOffsetBottomY(int switchAccOffsetBottomY) | 切换账号 下偏移量大小资源ID |
| setSwitchAccOffsetRightX(int switchAccOffsetRightX) | 切换账号 右偏移量大小资源ID |
| setSwitchAccAlignParentRight(boolean switchAccAlignParentRight) | 切换账号 是否靠屏幕右边 |
| setSwitchAccText(int switchAccText) | 切换账号 文本内容 |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
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
//切换账号 BottomY轴偏移量
.setSwitchAccOffsetY(R.dimen.sec_verify_demo_switch_acc_offset_bottom_y_customize)
//脱敏手机号 x轴右偏移量
.setSwitchAccOffsetRightX(R.dimen.sec_verify_demo_switch_acc_offset_right_x_customize)
//脱敏手机号 是否靠屏幕右边
.setSwitchAccAlignParentRight(true)
//脱敏手机号 文本内容
.setSwitchAccAlignParentRight(R.string.sec_verify_demo_number_content)
```
</details>

#### 隐私协议栏
| 方法 | 意义 |
|--------|--------|
| setCheckboxImgId(int checkboxImgId) | 隐私协议复选框背景图资源ID，建议使用selector |
| setCheckboxDefaultState(boolean checkboxDefaultState) | 隐私协议复选框默认状态，默认为“选中” |
| setCheckboxHidden(boolean checkboxHidden) | 隐私协议复选框是否隐藏，若设置隐藏，则默认状态设置不生效 |
| setAgreementColorId(int agreementColorId) | 隐私协议字体颜色资源ID |
| setCusAgreementNameId1(int cusAgreementNameId1) | 自定义隐私协议一文字资源ID |
| setCusAgreementColor1(int cusAgreementColor1) | 自定义隐私协议一颜色资源ID |
| setCusAgreementUrl1(String cusAgreementUrl1) | 自定义隐私协议一URL |
| setCusAgreementNameId2(int cusAgreementNameId2) | 自定义隐私协议二文字资源ID |
| setCusAgreementColor2(int cusAgreementColor2) | 自定义隐私协议二颜色资源ID |
| setCusAgreementUrl2(String cusAgreementUrl2) | 自定义隐私协议二URL |
| setAgreementGravityLeft(boolean agreementGravityLeft) | 隐私协议文字是否左对齐 |
| setAgreementBaseTextColorId(int agreementBaseTextColorId) | 隐私协议其他文字颜色资源ID |
| setAgreementOffsetX(int agreementOffsetX) | 隐私协议左偏移量大小资源ID |
| setAgreementOffsetRightX(int agreementOffsetRightX) | 隐私协议右偏移量大小资源ID |
| setAgreementOffsetY(int agreementOffsetY) | 隐私协议右偏移量大小资源ID |
| setAgreementOffsetBottomY(int agreementOffsetBottomY) | 隐私协议下偏移量大小资源ID |
| setAgreementCmccText(int cmccTextId)	| 设置移动隐私协议显示文本资源ID|
| setAgreementCuccText(int cuccTextId)	| 设置联通隐私协议显示文本资源ID|
| setAgreementCtccText(int ctccTextId)	| 设置电信隐私协议显示文本资源ID|
| setAgreementTextStart(int startTextId)	| 设置隐私协议文本开头资源ID|
| setAgreementTextAnd1(int andTextId1)	| 设置隐私协议连接文本1资源ID|
| setAgreementTextAnd2(int andTextId2)	| 设置隐私协议连接文本2资源ID|
| setAgreementTextEnd(int endTextId)	| 设置隐私协议结束文本资源ID|
| setAgreementTextSize(int agreementTextSize)	| 设置隐私协议文字大小资源ID|
| setAgreementAlignParentRight(boolean agreementAlignParentRight)	| 设置隐私协议是否靠屏幕右边|
| setAgreementHidden(boolean agreementHidden)	| 设置隐私协议隐藏|

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
// 隐私协议复选框背景图资源ID，建议使用selector
.setCheckboxImgId(R.drawable.sec_verify_demo_customized_checkbox_selector)
// 隐私协议复选框默认状态，默认为“选中”
.setCheckboxDefaultState(true)
//是否隐藏复选框(设置此属性true时setCheckboxDefaultState不会生效)
.setCheckboxHidden(false)
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
//设置移动隐私协议显示文本资源ID
.setAgreementCmccText(R.string.sec_verify_demo_agreement_text_cmcc)
//设置联通隐私协议显示文本资源ID
.setAgreementCuccText(R.string.sec_verify_demo_agreement_text_cucc)	
//设置电信隐私协议显示文本资源ID
.setAgreementCtccText(R.string.sec_verify_demo_agreement_text_ctcc)	
//设置隐私协议文本开头资源ID
.setAgreementTextStart(R.string.sec_verify_demo_agreement_text_start)	
//设置隐私协议连接文本1资源ID
.setAgreementTextAnd1(R.string.sec_verify_demo_agreement_text_and1)	
//设置隐私协议连接文本2资源ID
.setAgreementTextAnd2(R.string.sec_verify_demo_agreement_text_and2)	
//设置隐私协议结束文本资源ID
.setAgreementTextEnd(R.string.sec_verify_demo_agreement_text_end)	
//设置隐私协议文字大小资源ID
.setAgreementTextSize(R.dimen.sec_verify_demo_text_size_s)	
//设置隐私协议是否靠屏幕右边
.setAgreementAlignParentRight(true)	
//设置隐私协议隐藏
setAgreementHidden(false)
```
</details>

#### Slogan
| 方法 | 意义 |
|--------|--------|
| setSloganTextSize(int sloganTextSize) | Slogan文字大小资源ID |
| setSloganTextColor(int sloganTextColor) | Slogan文字颜色资源ID |
| setSloganOffsetX(int sloganOffsetX) | Slogan左偏移量大小资源ID |
| setSloganOffsetY(int sloganOffsetY) | Slogan上偏移量大小资源ID |
| setSloganOffsetBottomY(int sloganOffsetBottomY) | Slogan下偏移量大小资源ID，设置此属性时，上偏移量大小不生效 |
| setSloganOffsetRightX(boolean sloganOffsetRightX) | Slogan右偏移量大小资源ID |
| setSloganAlignParentRight(boolean sloganAlignParentRight) | Slogan是否靠屏幕右边|
| setSloganHidden(boolean sloganHidden) | Slogan隐藏 |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
//slogan文字大小
.setSloganTextSize(R.dimen.sec_verify_demo_text_size_s)
//slogan文字颜色
.setSloganTextColor(R.color.sec_verify_demo_main_color)
//slogan x轴偏移量
.setSloganOffsetX(R.dimen.sec_verify_demo_slogan_offset_x_customize)
//slogan y轴偏移量
.setSloganOffsetY(R.dimen.sec_verify_demo_slogan_offset_y_customize)
//slogan 底部y轴偏移量(设置此属性时，setSloganOffsetY不生效)
.setSloganOffsetBottomY(R.dimen.sec_verify_demo_slogan_o
bottom_y_customize)
//slogan x轴右偏移量
.setSloganOffsetRightX(R.dimen.sec_verify_demo_slogan_offset_right_x_customize)  
//slogan 靠屏幕右边
.setSloganAlignParentRight(true)
//slogan 隐藏
.setSloganHidden( false)
```
</details>

#### 登录按钮
| 方法 | 意义 |
|--------|--------|
| setLoginBtnImgId(int loginBtnImgId) | 登录按钮背景图资源ID，建议使用shape |
| setLoginBtnTextId(int loginBtnTextId) | 登录按钮文字资源ID |
| setLoginBtnTextColorId(int loginBtnTextColorId) | 登录按钮字体颜色资源ID |
| setLoginBtnTextSize(int loginBtnTextSize ) | 登录按钮文字大小资源ID |
| setLoginBtnWidth(int loginBtnWidth) | 登录按钮宽度大小资源ID |
| setLoginBtnHeight(int loginBtnHeight) | 登录按钮高度大小资源ID |
| setLoginBtnOffsetX(int loginBtnOffsetX) | 登录按钮左偏移量大小资源ID |
| setLoginBtnOffsetY(int loginBtnOffsetY) | 登录按钮上偏移量大小资源ID |
| setLoginBtnOffsetBottomY(int loginBtnOffsetBottomY) | 登录按钮下偏移量大小资源ID |
| setLoginBtnOffsetRightX(int loginBtnOffsetRightX) | 登录按钮右偏移量大小资源ID |
| setLoginBtnAlignParentRight(int loginBtnAlignParentRight) | 登录按钮是否靠屏幕右边 |
| setLoginBtnHidden(boolean loginBtnHidden) | 登录按钮上偏移量大小资源ID |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
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
//登录按钮 BottomY轴偏移
.setLoginBtnOffsetBottomY(R.dimen.sec_verify_demo_login_btn_offset_bottom_y_customize)
//登录按钮 x轴右偏移
.setLoginBtnOffsetRightX(R.dimen.sec_verify_demo_login_btn_offset_right_x_customize)
//登录按钮 靠屏幕右边
.setLoginBtnAlignParentRight(true)
//登录按钮 隐藏
.setLoginBtnHidden(false)
```
</details>

#### 动画
| 方法 | 意义 |
|--------|--------|
| setTranslateAnim(boolean translateAnim) | 设置授权页面平移动画 （授权页面进入和退出时从左往右平移显示和隐藏）|
| setZoomAnim(boolean zoomAnim) | 设置授权页面缩放动画 （授权页面进入和退出时从大到小显示和隐藏） |
| setFadeAnim(boolean fadeAnim) | 设置授权页面从透明到不透明动画（授权页面进入从透明到不透明显示，退出时相反） |
| setStartActivityTransitionAnim(int startInAnim,int startOutAnim) | 设置授权页面进入动画ID |
| setFinishActivityTransitionAnim(int finishInAnim,int finishOutAnim) | 设置授权页面结束动画ID |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
//使用平移动画
.setTranslateAnim(true)
//使用缩放动画
.setZoomAnim(false)
//使用透明动画
.setFadeAnim(false)
//设置授权页面进入动画ID
.setStartActivityTransitionAnim(R.anim.sec_verify_translate_bottom_in,R.anim.sec_verify_translate_bottom_out)
//设置授权页面结束动画ID
.setFinishActivityTransitionAnim(R.anim.sec_verify_translate_bottom_in,R.anim.sec_verify_translate_bottom_out)
```
</details>

#### 弹窗模式
| 方法 | 意义 |
|--------|--------|
| setDialogTheme(int dialogTheme) | 设置是否使用弹窗模式 |
| setDialogAlignBottom(int dialogAlignBottom) | 设置弹窗是否靠屏幕底部 |
| setDialogOffsetX(int dialogOffsetX) | 设置弹窗左右偏移量资源ID |
| setDialogOffsetY(int dialogOffsetY) | 设置弹窗上下偏移量资源ID |
| setDialogWidth(int dialogWidth) | 设置弹窗宽度资源ID |
| setDialogHeight(int dialogHeight) | 设置弹窗高度资源ID |
| setDialogMaskBackground(int dialogBackground) | 设置弹窗蒙版背景 |
| setDialogMaskBackgroundClickClose(int dialogBackgroundClickClose) | 设置点击弹窗蒙版是否关闭页面 |

<details>
  <summary><font color=#0099ff>示例代码</font></summary>
  
```
builder
//设置是否使用弹窗模式
.setDialogTheme(true)
//设置弹窗是否靠屏幕底部
.setDialogAlignBottom(false)
//设置弹窗宽度资源ID
.setDialogWidth(R.dimen.sec_verify_demo_dialog_width)
//设置弹窗高度资源ID
.setDialogHeight(R.dimen.sec_verify_demo_dialog_height)
//设置弹窗左右偏移量资源ID
.setDialogOffsetX(R.dimen.sec_verify_demo_dialog_offset_x)
//设置弹窗上下偏移量资源ID
.setDialogOffsetY(R.dimen.sec_verify_demo_dialog_offset_y)
//设置弹窗蒙版背景
.setDialogMaskBackground(R.drawable.sec_verify_demo_common_progress_dialog_bg)
//设置点击弹窗蒙版是否关闭页面
.setDialogMaskBackgroundClickClose(true)

SecVerify.setUiSettings(builder.build());
//SecVerify.setLandUiSettings(builder.build());
```
</details>


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
        version '2.0.3'
    }
}
```
2、 如果使用插件的模块会被其它模块依赖，请确保依赖它的模块也引入插件，或在此模块的gradle中添加：
```
   apply plugin: 'com.mob.sdk'
```
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
