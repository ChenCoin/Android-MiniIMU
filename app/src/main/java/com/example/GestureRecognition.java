package com.example;

import android.widget.Toast;

/**
 * Created by Coin on 2017/3/13.
 */

public class GestureRecognition {
    public static int Event_No = 0;
    public static int Event_X  = 1;
    public static int Event_Y  = 2;
    public static int Event_Z  = 3;
    public static float[] standard = {0,0,0};

    private int eventTime = 0;//在一个摆动事件发生后，在一个短暂的时间内不检测事件

    public int work(float [] fData){
        if(eventTime==0){
            float x = fData[0]-standard[0];
            float y = fData[1]-standard[1];
            float z = fData[2]-standard[2];
            boolean xEvent=false,yEvent=false,zEvent=false,hasEvent=false;
            if(x>2 || x<-2)xEvent=true;
            if(y>2 || y<-2)yEvent=true;
            if(z>2 || z<-2)zEvent=true;

            if(xEvent || yEvent || zEvent){
                eventTime=18;
            }else{
                return Event_No;
            }
            if(zEvent)return Event_Z;
            if(xEvent && yEvent)return Event_No;
            if(xEvent)return Event_X;
            return Event_Y;//这里只剩Y事件这一种可能

        }else{
            eventTime--;
            if(eventTime<0) eventTime = 0;
            return Event_No;
        }
    }

}
