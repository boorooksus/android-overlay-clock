package com.boorooksus.android_overlay_clock;


import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


// 백그라운드 실행을 위한 서비스 관리 클래스
public class ServiceManager extends Service {

    @SuppressLint("StaticFieldLeak")
    static Activity activity;  // 메인 액티비티
    @SuppressLint("StaticFieldLeak")
    WindowManager windowManager;
    View overlayView;
    TextView textView;
    private static Timer timer;  // 모니터링 타이머


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint({"RtlHardcoded", "InflateParams"})
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint({"RtlHardcoded", "InflateParams"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Set notification for ForegroundService
        setNotification();

        // create overlay for detecting touch event
        createOverlay();

        return Service.START_STICKY;
    }

    // 변수 세팅 함수
    // 서비스 시작 전에 수행해야 함
    public void setArgs(Activity activity) {
        ServiceManager.activity = activity;
    }

    // 오버레이 생성 함수
    public void createOverlay() {
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        // 오버레이 세팅
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                /*ViewGroup.LayoutParams.MATCH_PARENT*/300,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                30, 30, // X, Y 좌표
                TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        // 오버레이 위치 설정
        params.gravity = Gravity.RIGHT | Gravity.TOP;

        overlayView = inflate.inflate(R.layout.overlay_view, null);
        textView = overlayView.findViewById(R.id.textView4);
        // 오버레이 생성
        windowManager.addView(overlayView, params);


        // 타이머
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable(){
                    public void run() {
                        SimpleDateFormat simpleTime = new SimpleDateFormat("hh시 mm분 ss초");
                        String timeData = new String();

                        Date date = new Date();
                        timeData = simpleTime.format(date);
                        textView.setText(timeData);
                    }
                });
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 타이머 주기를 1초로 설정하고 작동
                timer.schedule(timerTask, 0, 1);
            }
        }).start();
    }

    // Notification 설정
    // Foreground Service가 실행되면 5초 이내에 notification 정보를 시스템에 보내야 함
    public void setNotification() {

        // Set Notification Channel
        String NOTIFICATION_CHANNEL_ID = "com.fos.anomalydetectionapp";
        String channelName = "Anomaly Detection Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        // set Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Monitoring Service s Running")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    // 서비스 종료
    @Override
    public void onDestroy() {
        super.onDestroy();


        // terminate overlay
        if(windowManager != null) {
            if(overlayView != null) {
                windowManager.removeView(overlayView);
                overlayView = null;
            }
            windowManager = null;
        }

        stopForeground(true);
        stopSelf();
    }
}
