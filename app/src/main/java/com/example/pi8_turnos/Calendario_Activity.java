package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sun.bob.mcalendarview.MCalendarView;

public class Calendario_Activity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    private CalendarView calendarView;
    String idInstitucion="";
    String sDia="",sMes="",sAnio="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_calendario);
        setSupportActionBar(toolbar);

        idInstitucion=getIntent().getStringExtra("id_institucion");

        calendarView = (CalendarView) findViewById(R.id.calendarView);
       // long a=calendarView.getDate();
        calendarView.setOnDateChangeListener(this);

        //MCalendarView mCalendarView = (MCalendarView) findViewById(R.id.calendar);
        //mCalendarView.setOnDateClickListener(n);
    }


    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] items = null;//new CharSequence[3];

        //CONVERTIR A STRING LA FECHA SELECCIONADA
        sDia = String.valueOf(i2);
        sMes = String.valueOf(i1 + 1);
        sAnio = String.valueOf(i);
        if (sDia.length() == 1)
            sDia = "0" + sDia;
        if (sMes.length() == 1)
            sMes = "0" + sMes;

        String fecha = sDia + "-" + sMes + "-" + sAnio;

        //OBTENER FECHA Y HORA ACTUAL
        Calendar calendario = Calendar.getInstance();
        String kdia = String.valueOf(calendario.get(Calendar.DAY_OF_MONTH));
        String kmes = String.valueOf(calendario.get(Calendar.MONDAY) + 1);
        String kanio = String.valueOf(calendario.get(Calendar.YEAR));
        String khoras = String.valueOf(calendario.get(Calendar.HOUR_OF_DAY));
        String kminutos = String.valueOf(calendario.get(Calendar.MINUTE));
        if (kdia.length() == 1)
            kdia = "0" + kdia;
        if (kmes.length() == 1)
            kmes = "0" + kmes;
        if (khoras.length() == 1)
            khoras = "0" + khoras;
        if (kminutos.length() == 1)
            kminutos = "0" + kminutos;
        String fechaActual = kdia + "-" + kmes + "-" + kanio + " " + khoras + ":" + kminutos + ":00";

        //OBTENER FECHA SELECCIONADA EN CALENDARIO
        String fechaSeleccionada = sDia + "-" + sMes + "-" + sAnio + " " + khoras + ":" + kminutos + ":00";


        try {
            //DAR FORMATO A LA FECHA
            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            Date dateActual = sdf.parse(fechaActual);
            Date dateSeleccionado = sdf.parse(fechaSeleccionada);

            //COMPARAR LAS FECHAS
            String com = String.valueOf(dateActual.compareTo(dateSeleccionado));

            //SI LA FECHA ES MAYOR O IGUAL SE ABRE UN DIALOG
            if (com.equals("-1") || com.equals("0")) {
                items = new CharSequence[3];
                items[0] = "Agregar Turnos";
                items[1] = "Ver Turnos";
                items[2] = "Cancelar";

                builder.setTitle("Seleccione una Actividad");
                builder.setCancelable(false);
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent(getApplication(), AgregarTurnosActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("fecha", fecha);
                            bundle.putString("id_institucion",idInstitucion);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else if (i == 1) {
                            Intent intent = new Intent(getApplication(), TurnosProgramadosActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("fecha", fecha);
                            bundle.putString("id_institucion",idInstitucion);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            return;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            //CASO CONTRARIO OTRO DIALOG SIN LA OPCION DE AGREGAR RECORDATORIO
            else {
                items = new CharSequence[2];
                items[0] = "Ver Turnos";
                items[1] = "Cancelar";

                builder.setTitle("Seleccione una Actividad");
                builder.setCancelable(false);
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent(getApplication(), TurnosProgramadosActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("fecha", fecha);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else {
                            return;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        /*MCalendarView calendarView = (MCalendarView) findViewById(R.id.calendar);
        calendarView.markDate(2021, 2, 23);
        calendarView.markDate(2021, 2, 27);
        calendarView.markDate(2021, 2, 25);*/
    }
}