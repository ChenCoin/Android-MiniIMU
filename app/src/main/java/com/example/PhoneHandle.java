package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.IOException;
import java.lang.reflect.Method;


/**
 * Created by Coin on 2017/3/2.
 */

public class PhoneHandle {

    private Activity activity;
    public PhoneHandle(Activity activity){
        this.activity = activity;
    }

    public void rejectCall() {}

    public void acceptCall() {}

}
