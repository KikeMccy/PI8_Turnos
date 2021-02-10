package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CalendarView;

public class Calendario_Activity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    private CalendarView calendarView;

    int val=0;
    String sDia="",sMes="",sAnio="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_calendario);
        setSupportActionBar(toolbar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        long a=calendarView.getDate();
        calendarView.setOnDateChangeListener(this);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] items = new CharSequence[3];
        items[0] = "Agregar Turnos";
        items[1] = "Ver Turnos";
        items[2] = "Cancelar";


        int dia,mes, anio;
        dia=i2;
        mes=i1+1;
        anio=i;

        sDia = Integer.toString(dia);
        sMes = Integer.toString(mes);
        sAnio=Integer.toString(anio);
        if (sDia.length() == 1)
            sDia = "0" + sDia;
        if (sMes.length() == 1)
            sMes = "0" + sMes;

        String fecha=sAnio+"-"+sMes+"-"+sDia;

        //  int finalAuxDia = auxDia;
        builder.setTitle("Seleccione una Actividad")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                          /*   if(finalAuxDia ==1)
                            {}*/
                            //int valor = getIntent().getExtras().getInt("dia");    //getStringExtra("dia");

                            /*Toast toast2 =
                                    Toast.makeText(getApplicationContext(),
                                            fecha, Toast.LENGTH_SHORT);

                            //toast2.setGravity(Gravity.CENTER|Gravity.LEFT,0,0);

                            toast2.show();*/

                            Intent intent=new Intent(getApplication(),AgregarTurnosActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("fecha",fecha);
                            //bundle.putInt("mes",mes);
                            //bundle.putInt("anio",anio);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else if (i == 1) {
                            Intent intent=new Intent(getApplication(),TurnosProgramadosActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putInt("dia",dia);
                            bundle.putInt("mes",mes);
                            bundle.putInt("anio",anio);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return super.onCreateOptionsMenu(menu);
    }

}