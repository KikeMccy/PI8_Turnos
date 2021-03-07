package com.example.pi8_turnos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.installations.internal.FidListener;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRCodeActivity extends AppCompatActivity {

    String TAG="GenerateQrCode";
    ImageView qrimg;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    private String mensaje, inputvalue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_codigo_qr);
        setSupportActionBar(toolbar);
        mensaje=getIntent().getStringExtra("mensaje");
        qrimg=(ImageView)findViewById(R.id.qr_code);
        ActivityCompat.requestPermissions(QRCodeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(QRCodeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        inputvalue= mensaje;
        if(inputvalue.length()>0){
            WindowManager manager=(WindowManager)getSystemService(WINDOW_SERVICE);
            Display display=manager.getDefaultDisplay();
            Point point=new Point();
            display.getSize(point);
            int width=point.x;
            int heigth=point.y;
            int smallerdimension=width<heigth ? width:heigth;
            smallerdimension=smallerdimension*3/4;
            qrgEncoder=new QRGEncoder(inputvalue, null, QRGContents.Type.TEXT,smallerdimension);
            try {
                bitmap=qrgEncoder.encodeAsBitmap();
                qrimg.setImageBitmap(bitmap);
            }catch (WriterException e){
                Log.v(TAG,e.toString());
            }

        }else {
            Toast.makeText(QRCodeActivity.this, "Error QRCode", Toast.LENGTH_SHORT).show();
        }


    }

}