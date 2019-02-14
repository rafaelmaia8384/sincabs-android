package br.com.sincabs.appsincabs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;



import org.json.JSONObject;

public class SincabsServiceAnaliseDocumental extends Service {

    final static int TIME_INTERVAL = 1000 * 60 * 5; // de 5 em 5 minutos vai checar se a documentação foi analisada

    SincabsServer sincabsServer;
    Handler handler;
    Runnable runnable;

    static String id_usuario = null;

    @Override
    public void onCreate() {

        sincabsServer = new SincabsServer(getApplicationContext());

        handler = new Handler();

        runnable = new Runnable() {

            @Override
            public void run() {

                sincabsServer.verificarAnaliseDocumental(id_usuario, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                        Notification.Builder builder = new Notification.Builder(getApplicationContext());

                        builder.setSmallIcon(R.drawable.icon_notification);
                        builder.setContentTitle("Análise documental");
                        builder.setContentText("Sua análise documental foi concluída.");
                        builder.setVibrate(new long[] {0, 1000, 1000, 1000});
                        builder.setLights(0xFFFF0000, 200, 200);
                        builder.setContentIntent(pendingIntent);
                        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notification = builder.getNotification();

                        notificationManager.notify(0, notification);

                        handler.removeCallbacksAndMessages(null);

                        stopSelf();
                    }

                    @Override
                    void onResponseError(String error) {

                    }

                    @Override
                    void onNoResponse(String error) {

                    }

                    @Override
                    void onPostResponse() {

                    }
                });

                handler.postDelayed(this, TIME_INTERVAL);
            }
        };

        handler.postDelayed(runnable, TIME_INTERVAL);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            if (id_usuario == null) {

                id_usuario = intent.getExtras().getString("id_usuario");
            }
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }
}
