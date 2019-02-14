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


import com.snatik.storage.Storage;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

public class SincabsServiceMensagemSistema extends Service {

    final static int TIME_INTERVAL = 1000 * 60 * 10; // 10 em 10 minutos;

    SincabsServer sincabsServer;
    Handler handler;
    Runnable runnable;

    @Override
    public void onCreate() {

        sincabsServer = new SincabsServer(getApplicationContext());

        handler = new Handler();

        runnable = new Runnable() {

            @Override
            public void run() {

                sincabsServer.verificarMensagemDoSistema(new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        String msg_id = "msg_0";
                        String mensagem = "Confira os suspeitos adicionados recentemente!";

                        try {

                            msg_id = extra.getString("msg_id");
                            mensagem = extra.getString("Mensagem");
                        }
                        catch (Exception e) { }

                        Storage storage = new Storage(getApplicationContext());

                        if (!storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + msg_id)) {

                            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                            while (currentHour < 8 && currentHour > 22) {

                                try {

                                    Thread.sleep(1000 * 60);
                                }
                                catch (Exception e) { }

                                currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            }

                            Intent notificationIntent = new Intent(getApplicationContext(), SincabsActivity.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            notificationIntent.putExtra("SincabsMessage", mensagem);

                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            Notification.Builder builder = new Notification.Builder(getApplicationContext());

                            builder.setSmallIcon(R.drawable.icon_notification);
                            builder.setContentTitle("Aviso da plataforma");
                            builder.setContentText(mensagem);
                            builder.setStyle(new Notification.BigTextStyle().bigText(mensagem));
                            builder.setVibrate(new long[] {0, 1000, 1000, 1000});
                            builder.setLights(0xFFFF0000, 200, 200);
                            builder.setContentIntent(pendingIntent);
                            builder.setAutoCancel(true);
                            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            Notification notification = builder.getNotification();

                            notificationManager.notify(0, notification);

                            storage.createFile(storage.getInternalFilesDirectory() + File.separator + msg_id, "{ok}");
                        }
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
