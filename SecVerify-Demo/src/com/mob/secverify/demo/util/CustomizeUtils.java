package com.mob.secverify.demo.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mob.MobSDK;
import com.mob.secverify.datatype.LandUiSettings;
import com.mob.secverify.datatype.UiSettings;
import com.mob.secverify.demo.R;
import com.mob.secverify.ui.AgreementPage;
import com.mob.tools.utils.ResHelper;

import java.util.ArrayList;
import java.util.List;

public class CustomizeUtils {
	private static String url;

	public static UiSettings customizeUi(){
		return new UiSettings.Builder()
				.setAgreementUncheckHintType(0)
				/** 标题栏 */
				// 标题栏背景色资源ID
				.setNavColorId(R.color.sec_verify_demo_text_color_blue)
				//标题栏是否透明
				.setNavTransparent(true)
				//标题栏是否隐藏
				.setNavHidden(false)
				//设置背景图片
//				.setBackgroundImgId(R.drawable.sec_verify_background_demo)
				//设置背景是否点击关闭页面
//				.setBackgroundClickClose(false)
				// 标题栏标题文字资源ID
//				.setNavTextId(R.string.sec_verify_demo_verify)
				// 标题栏文字颜色资源ID
//				.setNavTextColorId(R.color.sec_verify_demo_text_color_common_black)
				// 标题栏左侧关闭按钮图片资源ID
				.setNavCloseImgId(R.drawable.sec_verify_demo_close)
				//标题栏返回按钮是否隐藏
				.setNavCloseImgHidden(false)
				/** Logo */
				// Logo图片资源ID，默认使用应用图标
				.setLogoImgId(R.drawable.ic_launcher)
				//logo是否隐藏
				.setLogoHidden(false)
//				//logo宽度
				.setLogoWidth(R.dimen.sec_verify_demo_logo_width)
				//logo高度
				.setLogoHeight(R.dimen.sec_verify_demo_logo_height)
				//logo x轴偏移量
//				.setLogoOffsetX(R.dimen.sec_verify_demo_logo_offset_x)
				//logo y轴偏移量
//				.setLogoOffsetY(R.dimen.sec_verify_demo_logo_offset_y)
				/** 手机号 */
				// 脱敏手机号字体颜色资源ID
				.setNumberColorId(R.color.sec_verify_demo_text_color_common_black)
				// 脱敏手机号字体大小资源ID
				.setNumberSizeId(R.dimen.sec_verify_demo_text_size_m)
				//脱敏手机号 x轴偏移量
//				.setNumberOffsetX(R.dimen.sec_verify_demo_number_field_offset_x)
				//脱敏手机号 y轴偏移量
//				.setNumberOffsetY(R.dimen.sec_verify_demo_number_field_offset_y)
				/** 切换帐号 */
				// 切换账号字体颜色资源ID
				.setSwitchAccColorId(R.color.sec_verify_demo_text_color_blue)
				//切换账号 字体大小
				.setSwitchAccTextSize(R.dimen.sec_verify_demo_text_size_s)
				// 切换账号是否显示，默认显示
				.setSwitchAccHidden(false)
				//切换账号 x轴偏移量
//				.setSwitchAccOffsetX(R.dimen.sec_verify_demo_switch_acc_offset_x)
				//切换账号 y轴偏移量
//				.setSwitchAccOffsetY(R.dimen.sec_verify_demo_switch_acc_offset_y)

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
//				.setLoginBtnWidth(R.dimen.sec_verify_demo_login_btn_width)
				//登录按钮 height
//				.setLoginBtnHeight(R.dimen.sec_verify_demo_login_btn_height)
				//登录按钮 x轴偏移
//				.setLoginBtnOffsetX(R.dimen.sec_verify_demo_login_btn_offset_x)
				//登录按钮 y轴偏移
//				.setLoginBtnOffsetY(R.dimen.sec_verify_demo_login_btn_offset_y)
				/** 隐私协议 */
				//是否隐藏复选框(设置此属性true时setCheckboxDefaultState不会生效)
				.setCheckboxHidden(false)
				// 隐私协议复选框背景图资源ID，建议使用selector
				.setCheckboxImgId(R.drawable.sec_verify_demo_customized_checkbox_selector)
				// 隐私协议复选框默认状态，默认为“选中”
//				.setCheckboxDefaultState(true)
				// 隐私协议字体颜色资源ID（自定义隐私协议的字体颜色也受该值影响）
//				.setAgreementColorId(R.color.sec_verify_demo_main_color)
				// 自定义隐私协议一文字资源ID
				.setCusAgreementNameId1(R.string.sec_verify_demo_customize_agreement_name_1)
				// 自定义隐私协议一URL
				.setCusAgreementUrl1("http://www.baidu.com")
//				自定义隐私协议一颜色
				.setCusAgreementColor1(R.color.sec_verify_demo_main_color)
				// 自定义隐私协议二文字资源ID
				.setCusAgreementNameId2(R.string.sec_verify_demo_customize_agreement_name_2)
				// 自定义隐私协议二URL
				.setCusAgreementUrl2("https://www.jianshu.com")
//				自定义隐私协议二颜色
				.setCusAgreementColor2(R.color.sec_verify_demo_main_color)
				.setCusAgreementNameId3("自有服务策略")
				.setCusAgreementUrl3("http://www.baidu.com")
				.setCusAgreementColor3(R.color.blue)
				.setAgreementTextAnd3("&")
				//隐私协议是否左对齐，默认居中
				.setAgreementGravityLeft(true)
				//隐私协议其他文字颜色
//				.setAgreementBaseTextColorId(R.color.sec_verify_demo_text_color_common_black)
				//隐私协议 x轴偏移量，默认30dp
				.setAgreementOffsetX(R.dimen.sec_verify_demo_agreement_offset_x)
				//隐私协议 rightMargin右偏移量，默认30dp
				.setAgreementOffsetRightX(R.dimen.sec_verify_demo_agreement_offset_x)
				//隐私协议 y轴偏移量
//				.setAgreementOffsetY(R.dimen.sec_verify_demo_agreement_offset_y)
				//隐私协议 底部y轴偏移量
//				.setAgreementOffsetBottomY(R.dimen.sec_verify_demo_agreement_offset_bottom_y)
				/** slogan */
				//slogan文字大小
				.setSloganTextSize(R.dimen.sec_verify_demo_text_size_xs)
				//slogan文字颜色
				.setSloganTextColor(R.color.sec_verify_demo_text_color_common_gray)
				//slogan x轴偏移量
//				.setSloganOffsetX(R.dimen.sec_verify_demo_slogan_offset_x)
				//slogan y轴偏移量
//				.setSloganOffsetY(R.dimen.sec_verify_demo_slogan_offset_y)
				//slogan 底部y轴偏移量(设置此属性时，setSloganOffsetY不生效)
//				.setSloganOffsetBottomY(R.dimen.sec_verify_demo_slogan_offset_bottom_y)
				//设置状态栏为透明状态栏，5.0以上生效
				.setImmersiveTheme(false)
				//设置状态栏文字颜色为黑色，只在6.0以上生效
				.setImmersiveStatusTextColorBlack(false)
				//使用平移动画
//				.setTranslateAnim(true)
				.setStartActivityTransitionAnim(R.anim.translate_in,R.anim.translate_out)
				.setFinishActivityTransitionAnim(R.anim.translate_in,R.anim.translate_out)
				//设置隐私协议文字起始
//				.setAgreementTextStart(R.string.sec_verify_demo_agreement_text_start)
//				//设置隐私协议文字连接
//				.setAgreementTextAnd1(R.string.sec_verify_demo_agreement_text_and1)
//				//设置隐私协议文字连接
//				.setAgreementTextAnd2(R.string.sec_verify_demo_agreement_text_and2)
//				//设置隐私协议文字结束
//				.setAgreementTextEnd(R.string.sec_verify_demo_agreement_text_end)
//				//设置移动隐私协议文字
//				.setAgreementCmccText(R.string.sec_verify_demo_agreement_text_cmcc)
//				//设置联通隐私协议文字
//				.setAgreementCuccText(R.string.sec_verify_demo_agreement_text_cucc)
//				//设置电信隐私协议文字
//				.setAgreementCtccText(R.string.sec_verify_demo_agreement_text_ctcc)
				.setAgreementText(buildSpanString())
//				.setAgreementUncheckHintText(R.string.ct_account_brand_text)
				.setAgreementUncheckHintText("请阅读并勾选隐私协议")
				.build();
	}

	public static UiSettings customizeUi1(){
		return new UiSettings.Builder()
				.setAgreementUncheckHintType(1)
				/** 标题栏 */
				// 标题栏背景色资源ID
				.setNavColorId(R.color.sec_verify_demo_text_color_blue)
				//标题栏是否透明
				.setNavTransparent(true)
				//标题栏是否隐藏
				.setNavHidden(false)
				//设置背景图片
//				.setBackgroundImgId(R.drawable.sec_verify_background_demo)
				//设置背景是否点击关闭页面
				.setBackgroundClickClose(false)
				// 标题栏标题文字资源ID
				.setNavTextId(R.string.sec_verify_demo_verify)
				//标题栏文字大小
				.setNavTextSize(R.dimen.sec_verify_demo_text_size_s)
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
//				//logo宽度
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
				////切换账号 文本内容
				.setSwitchAccText(R.string.sec_verify_demo_other_login)
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
				.setCheckboxDefaultState(false)
				// 隐私协议复选框背景图资源ID，建议使用selector
				.setCheckboxImgId(R.drawable.sec_verify_demo_customized_checkbox_selector)
				// 隐私协议复选框默认状态，默认为“选中”
//				.setCheckboxDefaultState(true)
				// 隐私协议字体颜色资源ID（自定义隐私协议的字体颜色也受该值影响）
				.setAgreementColorId(R.color.sec_verify_demo_main_color)
				// 自定义隐私协议一文字资源ID
				.setCusAgreementNameId1(R.string.sec_verify_demo_customize_agreement_name_1)
				// 自定义隐私协议一URL
				.setCusAgreementUrl1("http://www.baidu.com")
//				自定义隐私协议一颜色
				.setCusAgreementColor1(R.color.sec_verify_demo_main_color)
				// 自定义隐私协议二文字资源ID
				.setCusAgreementNameId2(R.string.sec_verify_demo_customize_agreement_name_2)
				// 自定义隐私协议二URL
				.setCusAgreementUrl2("https://www.jianshu.com")
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
//				.setAgreementOffsetBottomY(R.dimen.sec_verify_demo_agreement_offset_bottom_y_customize)
				//隐私协议 靠屏幕右边
				.setAgreementAlignParentRight(true)
				//隐私协议 文字大小
				.setAgreementTextSize(R.dimen.sec_verify_demo_text_size_s)
				/** slogan */
				//slogan文字大小
				.setSloganTextSize(R.dimen.sec_verify_demo_text_size_s)
				//slogan文字颜色
				.setSloganTextColor(R.color.sec_verify_demo_main_color)
				//slogan x轴偏移量
//				.setSloganOffsetX(R.dimen.sec_verify_demo_slogan_offset_x_customize)
				//slogan y轴偏移量
				.setSloganOffsetY(R.dimen.sec_verify_demo_slogan_offset_y_customize)
				//slogan 底部y轴偏移量(设置此属性时，setSloganOffsetY不生效)
//				.setSloganOffsetBottomY(R.dimen.sec_verify_demo_slogan_offset_bottom_y_customize)
				//slogan x轴右偏移量
//				.setSloganOffsetRightX(R.dimen.sec_verify_demo_slogan_offset_right_x_customize)
				//slogan 靠屏幕右边
//				.setSloganAlignParentRight(true)
				//设置状态栏为透明状态栏，5.0以上生效
				.setImmersiveTheme(true)
				//设置状态栏文字颜色为黑色，只在6.0以上生效
				.setImmersiveStatusTextColorBlack(true)
//				.setZoomAnim(true)
				.setStartActivityTransitionAnim(R.anim.zoom_in,R.anim.zoom_out)
				.setFinishActivityTransitionAnim(R.anim.zoom_in,R.anim.zoom_out)
				.setBackgroundClickClose(true)
				.build();
	}

	public static UiSettings customizeUi2(){
		Resources resources = MobSDK.getContext().getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		float density = dm.density;
		int width = dm.widthPixels/2;
		int height = dm.heightPixels;
		return new UiSettings.Builder()
				.setLogoOffsetX(ResHelper.pxToDip(MobSDK.getContext(),width)-40)
				.setNumberOffsetX(ResHelper.pxToDip(MobSDK.getContext(),width)-50)
				.setSwitchAccOffsetX(ResHelper.pxToDip(MobSDK.getContext(),width)-40)
				.setNumberOffsetY(100)
				.setLoginBtnOffsetY(200)
				.setSwitchAccOffsetY(250)
				.setBackgroundClickClose(false)
				.setImmersiveTheme(false)
				//设置状态栏文字颜色为黑色，只在6.0以上生效
				.setImmersiveStatusTextColorBlack(false)
				.setDialogMaskBackgroundClickClose(true)
				.setBottomTranslateAnim(true)
				.setDialogTheme(true)
				.setDialogHeight(500)
				.setDialogAlignBottom(true)
				.build();
	}

	public static UiSettings customizeUi3(){
		return new UiSettings.Builder()
				.setNavHidden(true)
				.setLogoHidden(true)
				.setNumberHidden(true)
				.setSwitchAccHidden(true)
				.setLoginBtnHidden(true)
				.setAgreementHidden(true)
				.setSloganHidden(true)
				.build();
	}

	public static UiSettings customizeUi4(){
		return new UiSettings.Builder()
				.setNavCloseImgOffsetRightX(R.dimen.sec_verify_demo_nav_close_img_offset_right_x_customize)
				.setDialogMaskBackgroundClickClose(true)
				.setStartActivityTransitionAnim(R.anim.fade_in,R.anim.fade_out)
				.setFinishActivityTransitionAnim(R.anim.fade_in,R.anim.fade_out)
				.setBackgroundImgId(R.drawable.sec_verify_background_demo_dialog)
				.setDialogTheme(true)
				.setDialogAlignBottom(false)
				.setDialogWidth(R.dimen.sec_verify_demo_dialog_width)
				.setDialogHeight(R.dimen.sec_verify_demo_dialog_height)
				.setDialogOffsetX(R.dimen.sec_verify_demo_dialog_offset_x)
				.setDialogOffsetY(R.dimen.sec_verify_demo_dialog_offset_y)
				.build();
	}

	public static LandUiSettings customizeUi5(Context context){
		return new LandUiSettings.Builder()
				.setNavColorId(0xffffffff)
				.setNavTextId("一键登录")
				.setNavTextColorId(0xff000000)
				.setNavCloseImgId(context.getResources().getDrawable(R.drawable.sec_verify_demo_close))
				.setNavHidden(false)
				.setNavTransparent( true)
				.setNavCloseImgHidden( false)
				.setNavTextSize(16)
				.setNavCloseImgWidth(30)
				.setNavCloseImgHeight(30)
				.setNavCloseImgOffsetX( 15)
//				.setNavCloseImgOffsetRightX( 30)
				.setNavCloseImgOffsetY(15)

				.setLogoImgId(context.getResources().getDrawable(R.drawable.sec_verify_page_one_key_login_logo))
				.setLogoWidth( 80)
				.setLogoHeight( 80)
				.setLogoOffsetX( 150)
				.setLogoOffsetY( 30)
				.setLogoHidden( false)
//				.setLogoOffsetBottomY( 130)
				.setLogoOffsetRightX( 15)
				.setLogoAlignParentRight( false)

				.setNumberColorId(0xff000000)
				.setNumberSizeId(20)
				.setNumberOffsetX( 30)
				.setNumberOffsetY( 40)
				.setNumberHidden( false)
//				.setNumberOffsetBottomY( 110)
				.setNumberOffsetRightX( 150)
				.setNumberAlignParentRight( true)

				.setSwitchAccColorId(0xff4e96ff)
				.setSwitchAccTextSize(16)
				.setSwitchAccHidden(false)
				.setSwitchAccOffsetX(15)
				.setSwitchAccOffsetY(85)
				.setSwitchAccText( "其他方式登录")
//				.setSwitchAccOffsetBottomY( 90)
				.setSwitchAccOffsetRightX( 160)
				.setSwitchAccAlignParentRight( true)

				.setCheckboxImgId(context.getResources().getDrawable(R.drawable.customized_checkbox_selector))
				.setCheckboxDefaultState(false)
				.setCheckboxHidden( false)

				.setAgreementColorId(0xfffe7a4e)
				.setAgreementOffsetX( 50)
				.setAgreementOffsetRightX( 50)
				.setAgreementOffsetY( 210)
//				.setAgreementOffsetBottomY( 15)
				.setAgreementGravityLeft( false)
				.setAgreementBaseTextColorId(0xff000000)
				.setAgreementTextSize( 15)
				.setAgreementCmccText( "《中国移动服务条款》")
				.setAgreementCuccText( "《中国联通服务条款》")
				.setAgreementCtccText( "《中国电信服务条款》")
				.setAgreementTextStart( "同意")
				.setAgreementTextAnd1( "和")
				.setAgreementTextAnd2( "、")
				.setAgreementTextEnd( "并使用本机号登录")
				.setAgreementHidden( false)
				.setAgreementAlignParentRight( false)

				.setCusAgreementNameId1( "隐私服务协议一")
				.setCusAgreementUrl1("http://baidu.com")
				.setCusAgreementNameId2( "隐私服务协议二")
				.setCusAgreementUrl2("http://baidu.com")
				.setCusAgreementColor1(0xfffe7a4e)
				.setCusAgreementColor2(0xfffe7a4e)

				.setLoginBtnImgId(context.getResources().getDrawable(R.drawable.sec_verify_demo_shape_rectangle))
				.setLoginBtnTextId( "登录")
				.setLoginBtnTextColorId( 0xffffffff)
				.setLoginBtnTextSize( 16)
				.setLoginBtnWidth( 200)
				.setLoginBtnHeight(40)
//				.setLoginBtnOffsetX( 15)
				.setLoginBtnOffsetY( 150)
//				.setLoginBtnOffsetRightX( 15)
//				.setLoginBtnAlignParentRight( false)
//				.setLoginBtnOffsetBottomY( 40)
				.setLoginBtnHidden( false)

				.setBackgroundImgId(context.getResources().getDrawable(R.color.sec_verify_demo_common_bg))
				.setBackgroundClickClose( false)

				.setSloganOffsetRightX( 15)
				.setSloganAlignParentRight( true)
				.setSloganOffsetX( 15)
				.setSloganOffsetY( 200)
				.setSloganOffsetBottomY(15)
				.setSloganTextSize( 16)
				.setSloganTextColor(0xffFF6347)
				.setSloganHidden( false)
				.setStartActivityTransitionAnim(R.anim.zoom_in, R.anim.zoom_out)
				.setFinishActivityTransitionAnim(R.anim.zoom_in, R.anim.zoom_out)
				.setImmersiveTheme( false)
				.setImmersiveStatusTextColorBlack( true)
//
//				.setDialogTheme( true)
//				.setDialogAlignBottom( false)
//				.setDialogOffsetX( 80)
//				.setDialogOffsetY( 80)
//				.setDialogWidth( 400)
//				.setDialogHeight( 400)
//				.setDialogMaskBackground(context.getResources().getDrawable(R.drawable.sec_verify_demo_common_progress_dialog_bg))
//				.setDialogMaskBackgroundClickClose( true)
//
				.setTranslateAnim( true)
				.setZoomAnim( true)
				.setFadeAnim( true)
				.build();

	}

	public static List<View> buildCustomView(Context context){
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
		return views;
	}
	public static List<View> buildCustomView2(Context context){
		View view = new View(context);
		view.setId(R.id.customized_view_id);
		view.setBackground(context.getResources().getDrawable(R.drawable.sec_verify_background));
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.topMargin = ResHelper.dipToPx(context,260);
		view.setLayoutParams(params);


		// 自定义按钮1
		ImageView btn1 = new ImageView(context);
		btn1.setId(R.id.customized_btn_id_1);
		btn1.setImageDrawable(context.getResources().getDrawable(R.drawable.sec_verify_demo_close));
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params1.topMargin = ResHelper.dipToPx(context,280);
		params1.leftMargin = ResHelper.dipToPx(context,15);
		btn1.setLayoutParams(params1);

		List<View> views = new ArrayList<View>();
		views.add(view);
		views.add(btn1);
		return views;
	}
	public static List<View> buildCustomView3(Context context){


		ImageView iv0 = new ImageView(context);
		iv0.setId(R.id.customized_btn_id_0);
		iv0.setImageDrawable(context.getResources().getDrawable(R.drawable.sec_verify_demo_close));
		RelativeLayout.LayoutParams params0 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params0.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params0.topMargin = ResHelper.dipToPx(context,15);
		params0.leftMargin = ResHelper.dipToPx(context,15);
		iv0.setLayoutParams(params0);


		EditText et1 = new EditText(context);
		et1.setId(R.id.customized_et_id_0);
		et1.setBackground(null);
		et1.setHint("请输入账号");
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params2.addRule(RelativeLayout.BELOW,iv0.getId());
		params2.topMargin = ResHelper.dipToPx(context,30);
		params2.leftMargin = ResHelper.dipToPx(context,15);
		et1.setLayoutParams(params2);

		View view0 = new View(context);
		view0.setId(R.id.customized_view_id_div);
		view0.setBackgroundColor(context.getResources().getColor(R.color.sec_verify_demo_text_color_common_gray));
		RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
		params4.addRule(RelativeLayout.BELOW,et1.getId());
		params4.leftMargin = ResHelper.dipToPx(context,15);
		params4.rightMargin = ResHelper.dipToPx(context,15);
		view0.setLayoutParams(params4);

		EditText et2 = new EditText(context);
		et2.setId(R.id.customized_et_id_1);
		et2.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		et2.setHint("请输入密码");
		et2.setBackground(null);
		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params3.addRule(RelativeLayout.BELOW,view0.getId());
		params3.topMargin = ResHelper.dipToPx(context,30);
		params3.leftMargin = ResHelper.dipToPx(context,15);
		et2.setLayoutParams(params3);

		View view1 = new View(context);
		view1.setId(R.id.customized_view_id_div1);
		view1.setBackgroundColor(context.getResources().getColor(R.color.sec_verify_demo_text_color_common_gray));
		RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
		params5.addRule(RelativeLayout.BELOW,et2.getId());
		params5.leftMargin = ResHelper.dipToPx(context,15);
		params5.rightMargin = ResHelper.dipToPx(context,15);
		view1.setLayoutParams(params5);

		Button button = new Button(context);
		button.setId(R.id.customized_btn_id_3);
		button.setText("登录");
		button.setBackground(context.getResources().getDrawable(R.drawable.sec_verify_demo_shape_rectangle));
		RelativeLayout.LayoutParams params6 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params6.leftMargin = ResHelper.dipToPx(context,30);
		params6.rightMargin = ResHelper.dipToPx(context,30);
		params6.topMargin = ResHelper.dipToPx(context,30);
		params6.addRule(RelativeLayout.BELOW,view1.getId());
		button.setLayoutParams(params6);

		TextView view = new TextView(context);
		view.setId(R.id.customized_view_id);
		view.setText("其他方式登录");
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.bottomMargin = ResHelper.dipToPx(context,150);
		view.setLayoutParams(params);

		ImageView iv1 = new ImageView(context);
		iv1.setId(R.id.customized_btn_id_1);
		iv1.setImageDrawable(context.getResources().getDrawable(R.drawable.sec_verify_demo_wechat));
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params1.bottomMargin = ResHelper.dipToPx(context,120);
		iv1.setLayoutParams(params1);


		List<View> views = new ArrayList<View>();

		views.add(iv0);
		views.add(view);
		views.add(iv1);
		views.add(et1);
		views.add(view0);
		views.add(et2);
		views.add(view1);
		views.add(button);
		return views;
	}

	public static List<View> buildCustomView4(Context context){
		return null;
	}


	private static SpannableString buildSpanString() {
		String operatorText = "";
		if (OperatorUtils.getCellularOperatorType() == 1){
			operatorText = "《中国移动认证服务条款》";
			url = "https://wap.cmpassport.com/resources/html/contract.html";
		} else if (OperatorUtils.getCellularOperatorType() == 2){
			operatorText = "《中国联通认证服务条款》";
			url = "https://ms.zzx9.cn/html/oauth/protocol2.html";
		} else  if (OperatorUtils.getCellularOperatorType() == 3){
			operatorText = "《中国电信认证服务条款》";
			url = "https://e.189.cn/sdk/agreement/content.do?type=main&appKey=&hidetop=true&returnUrl=";
		}
		String ageementText = "同意"+operatorText+"及《自有隐私协议》和" +
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
				gotoAgreementPage(url,"");
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

	private static void gotoAgreementPage(String agreementUrl, String title) {
		if (TextUtils.isEmpty(agreementUrl)) {
			return;
		}
		AgreementPage page = new AgreementPage();
		Intent i = new Intent();
		i.putExtra("extra_agreement_url", agreementUrl);
		if (!TextUtils.isEmpty(title)) {
			i.putExtra("privacy",title);
		}
		page.show(MobSDK.getContext(), i);
	}

}
