package com.boorooksus.android_overlay_clock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonStatus;  // 목록 새로고침 버튼
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    String colorRunning = "#41A541";  // 러닝 중일 때 버튼 색상(녹색)
    String colorStopped = "#FFFFFF";  // 중단 됐을 때 버튼 색상(회색)
    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionChecker permissionChecker = new PermissionChecker(MainActivity.this);
        ServiceManager serviceManager = new ServiceManager();
        serviceManager.setArgs(MainActivity.this);

        // 뷰 id로 불러오기
        buttonStatus = findViewById(R.id.buttonStatus);

//        // actionbar setting
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        actionBar = getSupportActionBar();
//        assert actionBar != null;
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);

//        // 마지막 스위치 상태 가져오기
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean isRunning = preferences.getBoolean("isRunning", false);  // 스위치가 켜졌는지 여부

        // 리스트뷰, 스위치, 버튼 세팅
        buttonStatus.setBackgroundColor(Color.parseColor(colorStopped));
        buttonStatus.setText("Start");

        if(isRunning){
            // 스위치가 이미 켜진 경우 시계 작동

            stopService(new Intent(MainActivity.this, ServiceManager.class));

            buttonStatus.setBackgroundColor(Color.parseColor(colorRunning));
            buttonStatus.setText("Running");

            startForegroundService(new Intent(MainActivity.this, ServiceManager.class));
        }


        // 시작 버튼 기능 설정
        buttonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                boolean isRunning = preferences.getBoolean("isRunning", false);  // 스위치가 켜졌는지 여부

                if (!isRunning) {
                    // 스위치 켰을 때

                    // 권한 확인
                    if (permissionChecker.checkAllPermissions()) {
                        // 모든 권한 있는 경우

                        // 작동 여부 공유 변수 true로 변경
                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putBoolean("isRunning", true); // 스위치 상태 변수 세팅
                        editor.apply(); // 스위치 상태 변수 저장
//                        isRunning[0] = true;

//                        // 모니터링 시작
                        startForegroundService(new Intent(MainActivity.this, ServiceManager.class));
//                        createOverlay();
//                        // 버튼 변경
                        buttonStatus.setBackgroundColor(Color.parseColor(colorRunning));
                        buttonStatus.setText("Running");
                    }
                } else {
                    // 스위치 끄면 모니터링 중지
                    buttonStatus.setBackgroundColor(Color.parseColor(colorStopped));
                    buttonStatus.setText("Start");
                    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
//                    isRunning[0] = false;
                    editor.putBoolean("isRunning", false); // 스위치 상태 변수 세팅
                    editor.apply(); // 스위치 상태 변수 저장

                    // 서비스 중지
                    stopService(new Intent(MainActivity.this, ServiceManager.class));
                }
            }
        });
    }

}