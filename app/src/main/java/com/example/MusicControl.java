package com.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by Coin on 2017/2/28.
 */

public class MusicControl {
    private Activity dataMonitor;
    public MusicControl(DataMonitor dm){
        this.dataMonitor = dm;
    }

    public void play_or_stop(){
        long eventtime = SystemClock.uptimeMillis();

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        dataMonitor.sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        dataMonitor.sendOrderedBroadcast(upIntent, null);
    }

    public void next(){
        long eventtime = SystemClock.uptimeMillis();

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,   KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        dataMonitor.sendOrderedBroadcast(downIntent, null);
    }

    private int maxVolume,currentVolume,soundNow;
    private boolean soundNum = false;
    public void soundChange(){
        AudioManager mAudioManager = (AudioManager) dataMonitor.getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        if(max == current){
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }else{
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        }

    }

}
