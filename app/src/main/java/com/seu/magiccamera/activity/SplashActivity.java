package com.seu.magiccamera.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.seu.magiccamera.R;

public class SplashActivity extends Activity {

    private static final int DELAY = 0*11;
    private static final int TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        CheckPermission();

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DELAY:
                    Enter();
                    break;
            }
        }
    };

    private void CheckPermission(){
        if (PermissionChecker.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            handler.sendEmptyMessageDelayed(DELAY,TIME);
        }else {
            ActivityCompat.requestPermissions(SplashActivity.this,new String[] {Manifest.permission.CAMERA},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        for (int i = 0;i < grantResults.length; ++i){
            if (grantResults.length != 1 || grantResults[i] == PackageManager.PERMISSION_GRANTED || requestCode == 1) {
                handler.sendEmptyMessageDelayed(DELAY,TIME);
            } else if (requestCode != 1){
                GoPermission();
            }
        }

    }

    private void Enter(){
        startActivity(new Intent(SplashActivity.this,CameraActivity.class));
        finish();
    }

    private void GoPermission(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}
