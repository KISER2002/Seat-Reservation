package com.example.seatreservation.Chat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.seatreservation.R;
import com.example.seatreservation.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ChatService extends Service {
    ChatThread thread;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private int count = 0;

    String mName, mId, mProfile;

    SessionManager sessionManager;


    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ChatThread(handler);
        thread.start();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            sessionManager = new SessionManager(getApplicationContext());

            ActivityManager activityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
            ComponentName cn = info.get(0).topActivity;

            String activityName = cn.getShortClassName().substring(1);

            HashMap<String, String> user = sessionManager.getUserDetail();
            mName = user.get(sessionManager.NAME);
            mId = user.get(sessionManager.ID);
            mProfile = user.get(sessionManager.PROFILE);

            String stMessage = msg.getData().getString("msg");

            try {
//                data를 json으로 변환
                JSONObject obj = new JSONObject(stMessage);
                String getMessage = obj.getString("message");
                String getUserId = obj.getString("id");
                String getUserName = obj.getString("name");
                String getUserProfile = obj.getString("profile");
                String getCreatedAt = obj.getString("createdAt");
                String getChatRoomIdx = obj.getString("chat_room");
                String getType = obj.getString("type");
                String getChatRoomName = obj.getString("chat_room_name");

                if(getType.equals("image")){
                    getMessage = "사진";
                }else{

                }

                if(!getUserId.equals(mId) && !activityName.equals("Chat.ChatActivity")) {
                    // 채널을 생성 및 전달해 줄수 있는 NotificationManager를 생성한다.
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    // 이동하려는 액티비티를 작성해준다.
                    Intent notificationIntent = new Intent(getApplicationContext(), ChatActivity.class);
                    // 노티를 눌러서 이동시 전달할 값을 담는다. // 전달할 값을 notificationIntent에 담습니다.
                    notificationIntent.putExtra("chat_room_idx", getChatRoomIdx);
                    notificationIntent.putExtra("chat_room_name", getChatRoomName);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
//                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                            .setContentTitle(getUserName)
                            .setContentText(getMessage)

                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                            .setAutoCancel(true); // 눌러야 꺼지는 설정

                    //OREO API 26 이상에서는 채널 필요
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
                        CharSequence channelName  = "노티페케이션 채널";
                        String description = "오레오 이상";
                        int importance = NotificationManager.IMPORTANCE_HIGH;// 우선순위 설정

                        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
                        channel.setDescription(description);

                        // 노티피케이션 채널을 시스템에 등록
                        assert notificationManager != null;
                        notificationManager.createNotificationChannel(channel);

                    }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

                    assert notificationManager != null;
                    notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작
                }else{

                }

            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
    };
}
