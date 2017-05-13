package com.example;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * Created by Coin on 2017/3/2.
 */

public class PhoneListener extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
        } else {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING://正在响铃
                    Control.isCalling = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://通话中
                    Control.islistening=true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE://挂断电话
                    Control.isCalling = false;
                    Control.islistening=false;
                    break;
            }
        }
    }
}
