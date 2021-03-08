package com.example.pi8_turnos.Clases;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.pi8_turnos.MisTurnosActivity;
import com.example.pi8_turnos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userID = mAuth.getCurrentUser().getUid();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("TAG", "onNewToken" + s);
        guardarToken(s);
    }

    private void guardarToken(String s) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Tooken");
        ref.child(userID).setValue(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Log.e("TAG", "Mensaje Recibido");
        String from = remoteMessage.getFrom();
        //Log.e("TAG", "Mensaje recibido de " + from);
        if (remoteMessage.getData().size() > 0) {
           /* Log.e("TAG", "El titulo es: " + remoteMessage.getNotification().getTitle());
            Log.e("TAG", "El detalle es: " + remoteMessage.getNotification().getBody());*/
            String titulo=remoteMessage.getData().get("titulo");
            String detalle=remoteMessage.getData().get("detalle");
            mayorqueoreo(titulo,detalle);

        }
    }
        public void mayorqueoreo(String titulo, String detalle){
            String id="mensaje";
            NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder=new NotificationCompat.Builder(this,id);
            if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                NotificationChannel nc=new NotificationChannel(id,"nuevo",NotificationManager.IMPORTANCE_HIGH);
                nc.setShowBadge(true);
                assert nm!=null;
                nm.createNotificationChannel(nc);
            }

            builder.setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(titulo)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(detalle)
                    .setContentIntent(clickNotificacion())
                    .setContentInfo("nuevo");

            Random ramdon = new Random();
            int idNotify=ramdon.nextInt(8000);
            assert nm!=null;
            nm.notify(idNotify,builder.build());
        }

        public PendingIntent clickNotificacion(){
            Intent nf = new Intent(getApplicationContext(), MisTurnosActivity.class);
            //nf.putExtra("valor","valor");
            nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return PendingIntent.getActivity(this,0,nf,0);
        }


}
