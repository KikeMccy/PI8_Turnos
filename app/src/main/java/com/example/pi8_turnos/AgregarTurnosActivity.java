package com.example.pi8_turnos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class AgregarTurnosActivity extends AppCompatActivity {
    String hourFin = "";
    String minuteFin = "";
    String hour = "";
    String minute = "";
    private int horas, minutos,horasFin, minutosFin;
    TextView tvHoraInicio,tvHoraFin,tvFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_turnos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_add_turnos);
        setSupportActionBar(toolbar);
        tvHoraInicio = (TextView) findViewById(R.id.horaInicio);
        tvHoraFin = (TextView) findViewById(R.id.horaFin);
        tvFecha = (TextView) findViewById(R.id.fecha);


        String fecha=getIntent().getStringExtra("fecha");
        tvFecha.setText(fecha);
       // listaDispositivos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void clickHoraInicio(View view){
        final Calendar c = Calendar.getInstance();
        if(hour!=""){
            horas=Integer.parseInt(hour);
            minutos=Integer.parseInt(minute);
        }
        else{
            horas = c.get(Calendar.HOUR);
            minutos = c.get(Calendar.MINUTE);
        }

        TimePickerDialog ingresarHora = new TimePickerDialog(AgregarTurnosActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int tpHoras, int tpMinutos) {
                        hour = Integer.toString(tpHoras);
                        minute = Integer.toString(tpMinutos);
                        if (hour.length() == 1)
                            hour = "0" + hour;
                        if (minute.length() == 1)
                            minute = "0" + minute;
                        tvHoraInicio.setText(hour + ":" + minute);
                    }
                },
                horas,
                minutos,
                true);
        ingresarHora.show();
    }

    public void clickHoraFin(View view){
        final Calendar c = Calendar.getInstance();
        if(hourFin!=""){
            horasFin=Integer.parseInt(hourFin);
            minutosFin=Integer.parseInt(minuteFin);
        }
        else{
            horasFin = c.get(Calendar.HOUR);
            minutosFin = c.get(Calendar.MINUTE);
        }

        TimePickerDialog ingresarHora = new TimePickerDialog(AgregarTurnosActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int tpHoras, int tpMinutos) {
                        hourFin = Integer.toString(tpHoras);
                        minuteFin = Integer.toString(tpMinutos);
                        if (hourFin.length() == 1)
                            hourFin = "0" + hourFin;
                        if (minuteFin.length() == 1)
                            minuteFin = "0" + minuteFin;
                        tvHoraFin.setText(hourFin + ":" + minuteFin);
                    }
                },
                horasFin,
                minutosFin,
                true);
        ingresarHora.show();
    }
}