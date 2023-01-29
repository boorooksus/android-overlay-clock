package com.boorooksus.android_overlay_clock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;


// 앱의 권한 체크 클래스
public class PermissionChecker extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Activity activity;

    // Constructor
    public PermissionChecker(Activity activity) {
        PermissionChecker.activity = activity;
    }

    // 앱에 필요한 모든 권한 체크 함수
    public boolean checkAllPermissions(){
        return checkOverlayPermission();
    }


    // 다른 앱 위에 그리기 권한 체크 함수
    public boolean checkOverlayPermission() {
        if (!Settings.canDrawOverlays(activity)) {
            // 권한 설정이 안된 경우

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:" + activity.getPackageName()));
            openSettingPage("Allow Manage Overlay permission", intent);

            return false;
        }

        return true;
    }

    // 설정 페이지로 보내는 함수
    public void openSettingPage(String msg, Intent intent){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // 알림 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(msg);

                // 알림의 확인 버튼 기능 생성
                // 'Negative Button'이지만 확인 버튼으로 설정
                builder.setNegativeButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                activity.startActivity(intent);
                            }
                        });

                AlertDialog alertDialog = builder.create();

                // 알림의 확인 버튼 색상 변경
                // 기본 테마 변경으로 인해 설정 안하면 확인 버튼이 흰색이라 안보임
                alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000010"));
                    }
                });
                alertDialog.show();
            }
        });
    }
}
