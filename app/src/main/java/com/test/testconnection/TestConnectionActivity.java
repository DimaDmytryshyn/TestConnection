package com.test.testconnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class TestConnectionActivity extends AppCompatActivity {

    private final DnsPingManager manager = new DnsPingManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_connection);
        final String[] servers = getResources().getStringArray(R.array.server_list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean result = manager.isConnectionAvailable(servers[0]);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestConnectionActivity.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
