package com.example;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Coin on 2017/2/28.
 */

public class Control {

    private int correcting = 20;//采集20次数据，校正加速度
    private float[][] correctingNum = new float[3][20];//用于校正加速度的临时数据
    private float[] standard = {0,0,0};//标准X、Y、Z的值
    private int eventTime = 0;//在一个摆动事件发生后，在一个短暂的时间内不检测事件

    private DataMonitor dataMonitor;
    private TextView tipText;
    private MusicControl MC;
    private PhoneHandle phoneHandle;
    private GestureRecognition GR;

    public Control(DataMonitor dm , TextView tiptext){
        dataMonitor = dm;
        this.tipText = tiptext;
        MC = new MusicControl(dm);
        phoneHandle = new PhoneHandle(dataMonitor);
        GR = new GestureRecognition();
    }

    public static boolean isCalling=false ,islistening=false;//isCalling代表响铃或通话，isListening代表通话
    public void logicControl(float [] fData){
        //校正传感器加速度数据
        if(correcting>0){
            correcting--;
            correctingNum[0][correcting] = fData[0];
            correctingNum[1][correcting] = fData[1];
            correctingNum[2][correcting] = fData[2];
            if(correcting==0){
                for(int j=0 ;j<3 ; j++){
                    for(int n=0 ; n<20 ;n++){
                        standard[j] += correctingNum[j][n];
                    }
                    standard[j] /= 20;
                    GestureRecognition.standard[j] = standard[j];
                }
                Toast.makeText(dataMonitor.getApplicationContext(), "校正完成", Toast.LENGTH_SHORT).show();
                tipText.setText("已连接传感器");
            }
            return;
        }

        //可以监听手势事件
        int event = GR.work(fData);
        if( event>0 ){//有手势事件发生
            if(isCalling) {
                if (event==GestureRecognition.Event_X || event==GestureRecognition.Event_Y) phoneHandle.rejectCall();
                if ((!islistening) && event==GestureRecognition.Event_Z) phoneHandle.acceptCall();
            }else{
                if(event==GestureRecognition.Event_X){
                    Toast.makeText(dataMonitor.getApplicationContext(), "X事件：播放暂停", Toast.LENGTH_SHORT).show();
                    MC.play_or_stop();
                }else if(event==GestureRecognition.Event_Y){
                    Toast.makeText(dataMonitor.getApplicationContext(), "Y事件：下一曲", Toast.LENGTH_SHORT).show();
                    MC.next();
                }else if(event==GestureRecognition.Event_Z){
                    Toast.makeText(dataMonitor.getApplicationContext(),"Z：音量调节",Toast.LENGTH_SHORT).show();
                    MC.soundChange();
                }else{
                    Toast.makeText(dataMonitor.getApplicationContext(),"这是个奇怪的错误",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
