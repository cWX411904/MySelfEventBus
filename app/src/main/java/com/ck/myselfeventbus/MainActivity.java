package com.ck.myselfeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ck.mybus.MyBus;
import com.ck.mybus.Subscrible;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "wsj";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyBus.getInstance().register(this);

        MyBus.getInstance().post("1", "我是MainActivity在onCreate发送的消息", "sdfsdf");
    }

    @Subscrible("1")
    private void test(String msg) {
        Log.d(TAG, "test: " + msg);
    }


}
