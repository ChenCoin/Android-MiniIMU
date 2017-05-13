package com.example;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DataMonitor extends Activity {

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService mBluetoothService = null;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    int RunMode = 0;
    int iCurrentGroup = 3;

    //新添加的代码
    private TextView TipTextView;
    private Button B;//主按钮
    private Control control;
    private boolean firstUse = true;
    private boolean firstClick = true;
    private boolean bluetooth_state = true;

    @Override
    public void onBackPressed() {//覆写返回键事件，改为直接返回主页面，不关闭程序
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

    public void myButtonClick(View v) {//主按钮事件，功能有连接、退出、重连
        new Handler().postDelayed(new Runnable(){
            public void run() {
                if (firstClick) {
                    connect();
                    firstClick = false;
                    B.setText("退出");
                } else if (bluetooth_state) {
                    finish();
                } else {
                    onClickedBTSet();
                }
            }
        }, 300);

    }

    //新添加代码完结

    private final Handler mHandler = new Handler() {
        // 匿名内部类写法，实现接口Handler的一些方法
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            if (firstUse) {
                                TipTextView.setText("已找到传感器，校正中");
                                firstUse = false;
                            } else TipTextView.setText("已连接传感器");
                            bluetooth_state = true;
                            B.setText("退出");//mConnectedDeviceName为所连接的设备名称
                            iCurrentGroup = 1;
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            TipTextView.setText("正在连接传感器...");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            TipTextView.setText("与传感器断开连接");
                            bluetooth_state = false;
                            B.setText("重连");
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    try {
                        float[] fData = msg.getData().getFloatArray("Data");
                        if (RunMode == 0 && iCurrentGroup == 1) {
                            control.logicControl(fData);//关键代码，对传感器数据进行处理
                        }
                    } catch (Exception e) {
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    //mConnectedDeviceName = msg.getData().getString("device_name");
                    //Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    //Toast.makeText(getApplicationContext(), msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private static final int REQUEST_CONNECT_DEVICE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将软件界面设为沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.newlayout);
        SoftInputAdjust.assistActivity(this);

        B = (Button) findViewById(R.id.newpage1_button);
        TipTextView = (TextView)findViewById(R.id.newpage1_tip);
        new UIproduce(this);

        control = new Control(this, TipTextView);

        SelectFragment(0);//未知功能

    }

    private void connect() {//连接传感器
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                //Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
                TipTextView.setText("蓝牙不可用");
            }else {
                if (!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();
                if (mBluetoothService == null)
                    mBluetoothService = new BluetoothService(this, mHandler); // 用来管理蓝牙的连接
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        } catch (Exception err) {
        }
    }

    public void onClickedBTSet() {//重新连接传感器
        try {
            if (!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();
            if (mBluetoothService == null)
                mBluetoothService = new BluetoothService(this, mHandler); // 用来管理蓝牙的连接
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        } catch (Exception err) {
        }
    }

    private void SelectFragment(int Index) {
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            GetSelected();
            SharedPreferences mySharedPreferences = getSharedPreferences("Output", Activity.MODE_PRIVATE);
            IcType = Integer.parseInt(mySharedPreferences.getString("IC", "0"));
            Log.i("IC", String.format("%d", IcType));
            SetICType(IcType);
        } catch (Exception err) {
        }

    }

    public synchronized void onResume() {
        super.onResume();
        if (mBluetoothService != null) {
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                mBluetoothService.start();
            }
        }
    }

    @Override
    public void onDestroy() {//退出程序时，关闭后台线程
        if (mBluetoothService != null) mBluetoothService.stop();
        super.onDestroy();
    }

    // 利用startActivityForResult 和 onActivityResult，在两个activity间传递数据
    public BluetoothDevice device;
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:// When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);// Get the device MAC address
                    device = mBluetoothAdapter.getRemoteDevice(address);// Get the BLuetoothDevice object
                    mBluetoothService.connect(device);// Attempt to connect to the device
                }
                break;
        }
    }

    boolean[] selected = new boolean[]{false, true, true, true, false, false, false, false, false, false, false};
    public void RefreshButtonStatus() {}
    public int IcType = 0;

    public void GetSelected() {
        SharedPreferences mySharedPreferences = getSharedPreferences("Output", Activity.MODE_PRIVATE);
        try {
            int iOut = Integer.parseInt(mySharedPreferences.getString("Out", "15"));
            for (int i = 0; i < selected.length; i++) {
                selected[i] = ((iOut >> i) & 0x01) == 0x01;
            }
            RefreshButtonStatus();
        } catch (Exception err) {
        }
    }

    public void SetICType(int type) {
        if (type == 0) {
            short sOut = 0x0e;
            for (int i = 0; i < selected.length; i++) {
                selected[i] = ((sOut >> i) & 0x01) == 0x01;
            }
            RefreshButtonStatus();
            SharedPreferences mySharedPreferences = getSharedPreferences("Output", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("Out", String.format("%d", sOut));
            editor.commit();
        }
        SharedPreferences mySharedPreferences = getSharedPreferences("Output", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("IC", String.format("%d", type));
        editor.commit();
    }
}