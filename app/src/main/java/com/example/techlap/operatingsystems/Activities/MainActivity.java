package com.example.techlap.operatingsystems.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.techlap.operatingsystems.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toFCFS(View view) {
        startActivity(new Intent(MainActivity.this,FcfsActivity.class));
    }

    public void toSJF(View view) {
        startActivity(new Intent(MainActivity.this,SjfActivity.class));
    }

    public void toPriority(View view) {
        startActivity(new Intent(MainActivity.this,PQActivity.class));
    }

    public void toRR(View view) {
        startActivity(new Intent(MainActivity.this,RRActivity.class));
    }
}
