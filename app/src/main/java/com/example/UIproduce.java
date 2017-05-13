package com.example;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Coin on 2017/3/12.
 */

public class UIproduce {
    private Activity activity;

    public UIproduce(Activity activity){
        this.activity = activity;
        setFont();
        setClickEvent();
    }

    //修改部分控件的字体样式
    private void setFont(){
        Typeface typeFace1 = Typeface.createFromAsset(activity.getAssets(), "fonts/stand.ttf");//楷体
        Typeface typeFace2 = Typeface.createFromAsset(activity.getAssets(), "fonts/cute.ttf");//胖圆字体

        TextView newpage1_title = (TextView)activity.findViewById(R.id.newpage1_title);
        newpage1_title.setTypeface(typeFace1);
        TextView newpage1_tip = (TextView)activity.findViewById(R.id.newpage1_tip);
        newpage1_tip.setTypeface(typeFace1);
        TextView newpage2_title = (TextView)activity.findViewById(R.id.newpage2_title);
        newpage2_title.setTypeface(typeFace1);
        ( (TextView)activity.findViewById(R.id.about_context) ).setTypeface(typeFace1);

        Button newpage1_button = (Button)activity.findViewById(R.id.newpage1_button);
        newpage1_button.setTypeface(typeFace2);
        ( (TextView)activity.findViewById(R.id.about_title) ).setTypeface(typeFace2);
    }

    //设置控件点击事件
    private void setClickEvent(){
        TextView newpage1_jump = (TextView)activity.findViewById(R.id.newpage1_jump);
        newpage1_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainIntoSetting();
            }
        });

        TextView newpage1_back = (TextView)activity.findViewById(R.id.newpage1_back);
        newpage1_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingIntoMain();
            }
        });

        Button rejectBn_toPage2 = (Button)activity.findViewById(R.id.rejectBn_toPage2);
        rejectBn_toPage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageType = "message1";
                settingIntoPage2("拒接电话短信内容");
            }
        });

        Button helpBn_toPage2 = (Button)activity.findViewById(R.id.helpBn_toPage2);
        helpBn_toPage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageType = "message2";
                settingIntoPage2("紧急求救短信内容");
            }
        });

        Button page2_leftBn = (Button)activity.findViewById(R.id.page2_leftBn);
        page2_leftBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page2_Bn_Event(true);//true表示左按钮
            }
        });

        Button page2_rightBn = (Button)activity.findViewById(R.id.page2_rightBn);
        page2_rightBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page2_Bn_Event(false);//false表示右按钮
            }
        });

        Button newpage1_bn_about = (Button)activity.findViewById(R.id.newpage1_bn_about);
        newpage1_bn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bn_about_event();
            }
        });

        setSwitch( (Switch)activity.findViewById(R.id.switch1),"switch1" );
        setSwitch( (Switch)activity.findViewById(R.id.switch2),"switch2" );
        setSwitch( (Switch)activity.findViewById(R.id.switch3),"switch3" );


    }

    //编辑页面的两个按钮事件
    private boolean editing = false;
    private String messageType;//表示正在编辑的文本是忙时短信，还是求救短信
    private void page2_Bn_Event(boolean left){
        Button page2_leftBn = (Button)activity.findViewById(R.id.page2_leftBn);
        Button page2_rightBn = (Button)activity.findViewById(R.id.page2_rightBn);
        EditText editText = (EditText)activity.findViewById(R.id.editText);

        if(left && !editing){
            page2IntoSetting();
        }
        else if(left && editing){
            page2_leftBn.setText("←");
            page2_rightBn.setText("✎");

            editText.setFocusable(false);
            SharedPreferences pref = activity.getSharedPreferences("data",activity.MODE_PRIVATE);
            String str = pref.getString(messageType,"");
            editText.setText(str);
            editing = false;
            closeSoftInput(editText);
        } else if (!left && !editing) {
            page2_leftBn.setText("ㄨ");
            page2_rightBn.setText("✓");

            editText.setFocusable(true);
            editText.setEnabled(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocusFromTouch();
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            editText.setSelection(editText.getText().length());
            editing = true;
        }
        else if(!left && editing){
            page2_leftBn.setText("←");
            page2_rightBn.setText("✎");

            editText.setFocusable(false);
            SharedPreferences.Editor spe = activity.getSharedPreferences("data", activity.MODE_PRIVATE).edit();
            String str = editText.getText().toString();
            spe.putString(messageType,str);
            spe.apply();
            editing = false;
            closeSoftInput(editText);
        }
    }
    private void closeSoftInput(EditText editText){//关闭软键盘
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0) ;
    }


    //将3个switch控件的状态存储到SharedPreferences
    private void setSwitch(Switch s, final String name){
        SharedPreferences pref = activity.getSharedPreferences("data",activity.MODE_PRIVATE);
        boolean b = pref.getBoolean(name,true);
        s.setChecked(b);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                SharedPreferences.Editor spe = activity.getSharedPreferences("data", activity.MODE_PRIVATE).edit();
                spe.putBoolean(name,isChecked);
                spe.apply();
            }
        });
    }


    //"关于"界面的显示和关闭
    private boolean page_about_static = false;
    private int page_about_shade=0;//用于保持title的阴影
    private void Bn_about_event(){
        final FrameLayout newpage1_title_fl = (FrameLayout)activity.findViewById(R.id.newpage1_title_fl);
        final LinearLayout newpage1_about = (LinearLayout)activity.findViewById(R.id.newpage1_about);
        if(!page_about_static){//如果page_about没有显示
            page_about_shade = (int)newpage1_title_fl.getElevation();
            newpage1_title_fl.setElevation(16);
            page_about_static = true;

            newpage1_about.setVisibility(View.VISIBLE);
            viewSlide(newpage1_about,-newpage1_about.getHeight(),0,0.5F,1);
        }else{
            newpage1_title_fl.setElevation(page_about_shade);
            page_about_static = false;

            viewSlide(newpage1_about ,0 ,-newpage1_about.getHeight() ,1 ,0.5F);
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    newpage1_about.setVisibility(View.INVISIBLE);
                }
            }, 320);
        }
    }


    //从编辑页面跳转回设置页面
    private void page2IntoSetting(){
        final LinearLayout page1 = (LinearLayout)activity.findViewById(R.id.page1);
        final LinearLayout page2 = (LinearLayout)activity.findViewById(R.id.page2);
        page1.setVisibility(View.VISIBLE);

        editViewSlide(page2 ,0 ,page2.getWidth() ,1 ,0.5F);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                page2.setVisibility(View.INVISIBLE);
            }
        }, 500);
    }


    //从设置页面跳转回编辑页面
    boolean messageInit = true;//用于初始化message内容
    private void settingIntoPage2(String title){
        SharedPreferences pref = activity.getSharedPreferences("data",activity.MODE_PRIVATE);
        messageInit = pref.getBoolean("messageInit",true);
        if(messageInit){
            SharedPreferences.Editor spe = activity.getSharedPreferences("data", activity.MODE_PRIVATE).edit();
            spe.putString("message1","正在忙，不方便接电话。");
            spe.putString("message2","我现在遇到紧急情况，快来救我。");
            spe.putBoolean("messageInit",false);
            spe.apply();
        }
        //设置内容
        ( (TextView)activity.findViewById(R.id.newpage2_title) ).setText(title);
        String str = pref.getString(messageType,"");
        final EditText editText = (EditText)activity.findViewById(R.id.editText);
        editText.setFocusable(false);
        editText.setText(str);

        //设置动画
        final LinearLayout page1 = (LinearLayout)activity.findViewById(R.id.page1);
        final LinearLayout page2 = (LinearLayout)activity.findViewById(R.id.page2);
        page2.setVisibility(View.VISIBLE);
        editViewSlide(page2 ,page2.getWidth() ,0 ,0.5F ,1);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                page1.setVisibility(View.INVISIBLE);
            }
        }, 500);
    }

    //编辑页面的跳转动画
    private void editViewSlide(View v,float s1,float e1 , float s2 ,float e2){
        DecelerateInterpolator interpolator = new  DecelerateInterpolator();

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(v, "x", s1, e1);
        animator1.setInterpolator(interpolator);
        animator1.setDuration(500);
        animator1.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(v, "Alpha", s2, e2);
        animator2.setInterpolator(interpolator);
        animator2.setDuration(500);
        animator2.start();
    }

    //主页面跳转至设置页面
    private void mainIntoSetting(){
        final LinearLayout newpage1_setting = (LinearLayout)activity.findViewById(R.id.newpage1_setting);
        final FrameLayout newpage1_title_fl = (FrameLayout)activity.findViewById(R.id.newpage1_title_fl);
        final LinearLayout newpage1_main    = (LinearLayout)activity.findViewById(R.id.newpage1_main);

        newpage1_main.setVisibility(View.INVISIBLE);
        newpage1_title_fl.setElevation(0);

        newpage1_setting.setVisibility(View.VISIBLE);
        viewSlide(newpage1_setting, newpage1_setting.getHeight()*3/5 ,0 ,0 ,1);
    }

    //从设置页面跳转到主页面
    private void settingIntoMain(){
        final LinearLayout newpage1_setting = (LinearLayout)activity.findViewById(R.id.newpage1_setting);
        final FrameLayout newpage1_title_fl = (FrameLayout)activity.findViewById(R.id.newpage1_title_fl);
        final LinearLayout newpage1_main    = (LinearLayout)activity.findViewById(R.id.newpage1_main);

        newpage1_main.setVisibility(View.VISIBLE);

        newpage1_title_fl.setElevation(8);
        viewSlide(newpage1_setting,0, newpage1_setting.getHeight()*3/5,1,0);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                newpage1_setting.setVisibility(View.INVISIBLE);
            }
        }, 500);
    }

    //view在Y轴滑动并改变透明度，用于“关于”页面和设置页面
    private void viewSlide(View v,float s1,float e1 , float s2 ,float e2){
        DecelerateInterpolator interpolator = new  DecelerateInterpolator();

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(v, "y",
                s1, e1);
        animator1.setInterpolator(interpolator);
        animator1.setDuration(500);
        animator1.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(v, "Alpha",
                s2, e2);
        animator2.setInterpolator(interpolator);
        animator2.setDuration(500);
        animator2.start();

    }

}
