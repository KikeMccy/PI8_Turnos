package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pi8_turnos.Model.PreTurnos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class AgregarTurnosActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    DatabaseReference mDataBase;
    String hour = "";
    String minute = "";
    String fecha = "";
    String idInstitucion = "";
    private int horas, minutos;
    String horasInicioEntretiempo = "", minutosInicioEntretiempo = "";
    String horasFinEntretiempo = "", minutosFinEntretiempo = "";
    private int horasEntretiempo1, minutosEntretiempo1;
    private int horasEntretiempo2, minutosEntretiempo2;
    String dia = "", mes = "", anio = "", horasD = "", minutosD = "";
    TextView tvDescripcionTurnos, tvCantidadTurnos, tvDuracionTurnos, tvHoraInicio, tvFecha, tvEntretiempo, tvTurnosGenerados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_turnos);

        firebaseAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_add_turnos);
        setSupportActionBar(toolbar);

        //INICIAR VARIABLES TEXT VIEW
        tvDescripcionTurnos = (TextView) findViewById(R.id.descripcionTurnos);
        tvCantidadTurnos = (TextView) findViewById(R.id.cantidadTurnos);
        tvDuracionTurnos = (TextView) findViewById(R.id.duracionTurnos);
        tvHoraInicio = (TextView) findViewById(R.id.horaInicio);
        tvFecha = (TextView) findViewById(R.id.fecha);
        tvEntretiempo = (TextView) findViewById(R.id.entretiempo);

        idInstitucion = getIntent().getStringExtra("id_institucion");
        fecha = getIntent().getStringExtra("fecha");
        tvFecha.setText(fecha);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void agregarEntretiempo(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AgregarTurnosActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_entretiempo, null);
        mBuilder.setTitle("Enter data");
        mBuilder.setCancelable(false);
        TextView inicioEntretiempo = (TextView) mView.findViewById(R.id.inicioEntretiempo);
        TextView finEntretiempo = (TextView) mView.findViewById(R.id.finEntretiempo);
        mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (horasInicioEntretiempo.length() > 0 && horasFinEntretiempo.length() > 0) {
                    tvEntretiempo.setText(horasInicioEntretiempo + ":" + minutosInicioEntretiempo + " - " + horasFinEntretiempo + ":" + minutosFinEntretiempo);
                    dialogInterface.dismiss();
                }
                else
                {
                    Toast.makeText(mView.getContext(), "Enter values", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

        if (!horasInicioEntretiempo.equals("")) {
            horasEntretiempo1 = Integer.valueOf(horasInicioEntretiempo);
            minutosEntretiempo1 = Integer.valueOf(minutosInicioEntretiempo);
            inicioEntretiempo.setText(horasInicioEntretiempo + ":" + minutosInicioEntretiempo);
        }


        if (!horasFinEntretiempo.equals("")) {
            horasEntretiempo2 = Integer.valueOf(horasFinEntretiempo);
            minutosEntretiempo2 = Integer.valueOf(minutosFinEntretiempo);
            finEntretiempo.setText(horasFinEntretiempo + ":" + minutosFinEntretiempo);
        }

        // Toast toast = Toast.makeText(getApplicationContext(), "MO", Toast.LENGTH_SHORT);
        inicioEntretiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast toast = Toast.makeText(getApplicationContext(), "Ingrese datos correctamente", Toast.LENGTH_SHORT);
                if (!horasInicioEntretiempo.equals("")) {
                    horasEntretiempo1 = Integer.valueOf(horasInicioEntretiempo);
                    minutosEntretiempo1 = Integer.valueOf(minutosInicioEntretiempo);
                }
                TimePickerDialog ingresarHoraInicio = new TimePickerDialog(AgregarTurnosActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        //Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT);
                        horasInicioEntretiempo = Integer.toString(i);
                        minutosInicioEntretiempo = Integer.toString(i1);
                        if (horasInicioEntretiempo.length() == 1)
                            horasInicioEntretiempo = "0" + horasInicioEntretiempo;
                        if (minutosInicioEntretiempo.length() == 1)
                            minutosInicioEntretiempo = "0" + minutosInicioEntretiempo;
                        inicioEntretiempo.setText(horasInicioEntretiempo + ":" + minutosInicioEntretiempo);
                    }
                }, horasEntretiempo1, minutosEntretiempo1, false);
                ingresarHoraInicio.setTitle("Seleccione hora de inicio");
                ingresarHoraInicio.show();
            }
        });

        finEntretiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!horasFinEntretiempo.equals("")) {
                    horasEntretiempo2 = Integer.valueOf(horasFinEntretiempo);
                    minutosEntretiempo2 = Integer.valueOf(minutosFinEntretiempo);
                }
                TimePickerDialog ingresarHoraInicio = new TimePickerDialog(AgregarTurnosActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        horasFinEntretiempo = Integer.toString(i);
                        minutosFinEntretiempo = Integer.toString(i1);
                        if (horasFinEntretiempo.length() == 1)
                            horasFinEntretiempo = "0" + horasFinEntretiempo;
                        if (minutosFinEntretiempo.length() == 1)
                            minutosFinEntretiempo = "0" + minutosFinEntretiempo;
                        finEntretiempo.setText(horasFinEntretiempo + ":" + minutosFinEntretiempo);

                    }
                },
                        horasEntretiempo2, minutosEntretiempo2, false);
                ingresarHoraInicio.setTitle("Seleccione hora de inicio");
                ingresarHoraInicio.show();
            }
        });
    }


    public void clickTextView(View view) {
        final Calendar c = Calendar.getInstance();
        if (hour.equals("")) {
            horas = c.get(Calendar.HOUR);
            minutos = c.get(Calendar.MINUTE);
            int am_pm = c.get(Calendar.AM_PM);
            if (am_pm == 1)
                horas = horas + 12;
            //Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(am_pm), Toast.LENGTH_SHORT);
        } else {
            horas = Integer.parseInt(hour);
            minutos = Integer.parseInt(minute);
        }

        TimePickerDialog ingresarHoraInicio = new TimePickerDialog(AgregarTurnosActivity.this,
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
                false);
        ingresarHoraInicio.setTitle("Seleccione hora de inicio");
        ingresarHoraInicio.show();
    }

    public void agregarTurnos(View view) {
        try {
            int cantidadTurnos = Integer.valueOf(tvCantidadTurnos.getText().toString());
            int duracionTurnos = Integer.valueOf(tvDuracionTurnos.getText().toString());
            String hora = tvHoraInicio.getText().toString();

            if (tvDescripcionTurnos.getText().toString().length() > 0 && cantidadTurnos > 0 && duracionTurnos > 0 && hora.length() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
                String dateInString = fecha + " " + hora + ":00";
                Date date = sdf.parse(dateInString);

                //INICIAR VARIABLES DEL ALERT DIALOG
                AlertDialog.Builder mBuilder = null;
                View mView = null;
                String content = "";
                for (int i = 0; i < cantidadTurnos; i++) {

                    descomponerDate(date);
                    String auxHora = horasD + ":" + minutosD;
                    date = sumarRestarHorasFecha(date, duracionTurnos);
                    descomponerDate(date);


                    mBuilder = new AlertDialog.Builder(AgregarTurnosActivity.this);
                    mView = getLayoutInflater().inflate(R.layout.lista_creacion_turnos, null);
                    mBuilder.setTitle("¿Desea agregar los turnos?");

                    tvTurnosGenerados = (TextView) mView.findViewById(R.id.turnosGenerados);

                    content += "Turno " + (i + 1) + ": " + auxHora + "-" + horasD + ":" + minutosD + "\n";
                    tvTurnosGenerados.append(content);

                }
                mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ingresarTurnos();
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();


            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Ingrese todos los datos", Toast.LENGTH_SHORT);
                toast.show();
            }


        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Ingrese datos correctamente", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void ingresarTurnos() {
        String id = mDataBase.child("posts").push().getKey();
        mDataBase.child("Instituciones").child(idInstitucion).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String us = snapshot.child("idusuario").getValue().toString();
                    Toast.makeText(AgregarTurnosActivity.this, us, Toast.LENGTH_SHORT).show();
                    mDataBase.child("Turnos").child(id).child("id_usuario").setValue(us);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDataBase.child("Turnos").child(id).child("fecha").setValue(fecha);
        mDataBase.child("Turnos").child(id).child("id_institucion").setValue(idInstitucion);
        mDataBase.child("Turnos").child(id).child("descripcion").setValue(tvDescripcionTurnos.getText().toString());


        try {
            int cantidadTurnos = Integer.valueOf(tvCantidadTurnos.getText().toString());
            int duracionTurnos = Integer.valueOf(tvDuracionTurnos.getText().toString());
            String hora = tvHoraInicio.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String dateInString = fecha + " " + hora + ":00";
            Date date = sdf.parse(dateInString);

            for (int i = 0; i < cantidadTurnos; i++) {
                descomponerDate(date);
                String auxHora = horasD + ":" + minutosD;
                date = sumarRestarHorasFecha(date, duracionTurnos);
                descomponerDate(date);

                Map<String, Object> turnosHorario = new HashMap<>();
                turnosHorario.put("estado", "libre");
                turnosHorario.put("hora_inicio", auxHora);
                turnosHorario.put("hora_fin", horasD + ":" + minutosD);
                turnosHorario.put("nombre_usuario", "ninguno");
                turnosHorario.put("numero", String.valueOf(i+1));

                mDataBase.child("Turnos").child(id).child("horario").push().setValue(turnosHorario);
            }
            //mDataBase.child("turnos").child("horario").push().setValue(turnosMap);


            Toast toast = Toast.makeText(getApplicationContext(), "Turnos agregados correctamente", Toast.LENGTH_SHORT);
            toast.show();
            super.onBackPressed();
        } catch (Exception e) {
        }
    }

    public void descomponerDate(Date date) {
        dia = String.valueOf(date.getDate());
        mes = String.valueOf(date.getMonth() + 1);
        anio = String.valueOf(date.getYear());
        horasD = String.valueOf(date.getHours());
        minutosD = String.valueOf(date.getMinutes());

        String aux = anio.substring(0, 1);
        anio = anio.substring(1, 3);

        if (aux.equals("0"))
            anio = "19" + anio;
        else if (aux.equals("1"))
            anio = "20" + anio;
        else anio = "21" + anio;

        if (dia.length() == 1)
            dia = "0" + dia;
        if (mes.length() == 1)
            mes = "0" + mes;
        if (horasD.length() == 1)
            horasD = "0" + horasD;
        if (minutosD.length() == 1)
            minutosD = "0" + minutosD;
    }


    // SUMA MINUTOS RECIBIDOS A LA FECHA
    public Date sumarRestarHorasFecha(Date fecha, int minutos) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.MINUTE, minutos);  // numero de horas a añadir, o restar en caso de horas<0
        return calendar.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas
    }
}