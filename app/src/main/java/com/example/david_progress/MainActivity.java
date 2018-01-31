package com.example.david_progress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    BourceView mBourceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBourceView = (BourceView) findViewById(R.id.bourceview);
    }

    public void start(View view){

        mBourceView.startTotalAnimation();
    }
}
