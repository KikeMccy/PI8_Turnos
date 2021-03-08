package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pi8_turnos.Model.PreTurnos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TurnosProgramadosActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String fecha, idInstitucion;
    String horasInicioT = "", minutosInicioT = "";
    String horasFinT = "", minutosFinT = "";
    private int horasInicio, minutosInicio;
    private int horasFin, minutosFin;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos_programados);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_turnos_programados);
        setSupportActionBar(toolbar);

        fecha = getIntent().getStringExtra("fecha");
        idInstitucion = getIntent().getStringExtra("id_institucion");
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) findViewById(R.id.rcl_turnosCalendar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //OBTENER EL ID DEL TURNO CREADO POR LA INSTITUCION EN ESA FECHA
        databaseReference.child("Turnos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String fechaObtenida = ds.child("fecha").getValue().toString();
                        String idInstitucionObtenida = ds.child("id_institucion").getValue().toString();
                        //Toast.makeText(TurnosProgramadosActivity.this,idInstitucion,Toast.LENGTH_SHORT).show();
                        if (fechaObtenida.equals(fecha) && idInstitucionObtenida.equals(idInstitucion)) {
                            id = ds.getKey();
                            //Toast.makeText(TurnosProgramadosActivity.this,id,Toast.LENGTH_SHORT).show();
                            FirebaseRecyclerOptions<PreTurnos> options = new FirebaseRecyclerOptions.Builder<PreTurnos>()
                                    //.setQuery(FirebaseDatabase.getInstance().getReference().child("Turnos").orderByChild("fecha").equalTo(fecha).orderByChild("id_institucion").equalTo(idInstitucion), PreTurnos.class)
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Turnos").child(id).child("horario"), PreTurnos.class)
                                    .build();
                            FirebaseRecyclerAdapter<PreTurnos, TurnosProgramadosActivity.myviewholder> adapter =
                                    new FirebaseRecyclerAdapter<PreTurnos, myviewholder>(options) {
                                        @Override
                                        protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull PreTurnos model) {
                                            holder.estadoTurno.setText(model.getEstado());
                                            holder.horaInicioTurno.setText("Inicio: " + model.getHora_inicio());
                                            holder.horaFinTurno.setText("Fin: " + model.getHora_fin());
                                            holder.numeroTurno.setText("Turno Número: " + model.getNumero());
                                            if (model.getNombre_usuario().equals("ninguno"))
                                                holder.nombreTurno.setText("");
                                            else
                                                holder.nombreTurno.setText(model.getNombre_usuario());
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    if (model.getEstado().equals("libre")) {
                                                        String idTurno = getRef(position).getKey();
                                                        //Toast.makeText(ListaRecordatoriosActivity.this,id, Toast.LENGTH_SHORT).show();
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(TurnosProgramadosActivity.this);
                                                        CharSequence[] items = new CharSequence[3];
                                                        items[0] = "Eliminar Turno";
                                                        items[1] = "Modificar";
                                                        items[2] = "Cancelar";
                                                        builder.setItems(items, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                if (i == 0) {
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("Turnos").child(id).child("horario").child(idTurno).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(TurnosProgramadosActivity.this, "Turno Eliminado", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                Toast.makeText(TurnosProgramadosActivity.this, "Remove Failed", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                } else if (i == 1) {
                                                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(TurnosProgramadosActivity.this);
                                                                    View mView = getLayoutInflater().inflate(R.layout.dialog_entretiempo, null);
                                                                    mBuilder.setTitle("Modificar turno");
                                                                    mBuilder.setCancelable(false);
                                                                    TextView inicioEntretiempo = (TextView) mView.findViewById(R.id.inicioEntretiempo);
                                                                    TextView finEntretiempo = (TextView) mView.findViewById(R.id.finEntretiempo);
                                                                    inicioEntretiempo.setText(model.getHora_inicio());
                                                                    finEntretiempo.setText(model.getHora_fin());

                                                                    /*if (!horasInicioT.equals("")) {
                                                                        horasInicio = Integer.valueOf(horasInicioT);
                                                                        minutosInicio = Integer.valueOf(minutosInicioT);
                                                                        inicioEntretiempo.setText(horasInicioT + ":" + minutosInicioT);
                                                                    }


                                                                    if (!horasFinT.equals("")) {
                                                                        horasFin = Integer.valueOf(horasFinT);
                                                                        minutosFin = Integer.valueOf(minutosFinT);
                                                                        finEntretiempo.setText(horasFinT + ":" + minutosFinT);
                                                                    }*/

                                                                    mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                                            try {
                                                                                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
                                                                                String inicioInString = fecha + " " + inicioEntretiempo.getText().toString() + ":00";
                                                                                Date inicio = sdf.parse(inicioInString);

                                                                                String finInString = fecha + " " + finEntretiempo.getText().toString() + ":00";
                                                                                Date fin=sdf.parse(finInString);

                                                                                String com = String.valueOf(fin.compareTo(inicio));

                                                                                if (com.equals("1")) {

                                                                                    FirebaseDatabase.getInstance().getReference().child("Turnos").child(id).child("horario").child(idTurno).child("hora_inicio").setValue(inicioEntretiempo.getText().toString());
                                                                                    FirebaseDatabase.getInstance().getReference().child("Turnos").child(id).child("horario").child(idTurno).child("hora_fin").setValue(finEntretiempo.getText().toString());
                                                                                    Toast.makeText(TurnosProgramadosActivity.this, "Turno Modificado", Toast.LENGTH_SHORT).show();


                                                                                }else {
                                                                                        Toast.makeText(TurnosProgramadosActivity.this, "La hora de fin debe ser mayor a la de inicio", Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                            }catch(Exception e){

                                                                            }

                                                                            /*FirebaseDatabase.getInstance().getReference()
                                                                                    .child("Turnos").child(id).child("horario").child(idTurno).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(TurnosProgramadosActivity.this, "Turno Eliminado", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(TurnosProgramadosActivity.this, "Remove Failed", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });*/




                                                                          /*if (horasInicioEntretiempo.length() > 0 && horasFinEntretiempo.length() > 0) {
                                                                                tvEntretiempo.setText(horasInicioEntretiempo + ":" + minutosInicioEntretiempo + " - " + horasFinEntretiempo + ":" + minutosFinEntretiempo);
                                                                                dialogInterface.dismiss();
                                                                            } else {
                                                                                Toast.makeText(mView.getContext(), "Enter values", Toast.LENGTH_SHORT).show();
                                                                            }*/
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

                                                                   /* if (!horasInicioT.equals("")) {
                                                                        horasInicio = Integer.valueOf(horasInicioT);
                                                                        minutosInicio= Integer.valueOf(minutosInicioT);
                                                                        inicioEntretiempo.setText(horasInicioT + ":" + minutosInicioT);
                                                                    }


                                                                    if (!horasFinT.equals("")) {
                                                                        horasFin = Integer.valueOf(horasFinT);
                                                                        minutosFin = Integer.valueOf(minutosFinT);
                                                                        finEntretiempo.setText(horasFinT + ":" + minutosFinT);
                                                                    }*/

                                                                    horasInicio=Integer.valueOf(inicioEntretiempo.getText().toString().substring(0,2));
                                                                    minutosInicio=Integer.valueOf(inicioEntretiempo.getText().toString().substring(3,5));

                                                                    horasFin=Integer.valueOf(finEntretiempo.getText().toString().substring(0,2));
                                                                    minutosFin=Integer.valueOf(finEntretiempo.getText().toString().substring(3,5));

                                                                    inicioEntretiempo.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            TimePickerDialog ingresarHoraInicio = new TimePickerDialog(TurnosProgramadosActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                                                                @Override
                                                                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                                                    //Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT);
                                                                                    horasInicioT = Integer.toString(i);
                                                                                    minutosInicioT = Integer.toString(i1);
                                                                                    if (horasInicioT.length() == 1)
                                                                                        horasInicioT = "0" + horasInicioT;
                                                                                    if (minutosInicioT.length() == 1)
                                                                                        minutosInicioT = "0" + minutosInicioT;

                                                                                    inicioEntretiempo.setText(horasInicioT + ":" + minutosInicioT);
                                                                                }
                                                                            }, horasInicio, minutosInicio, false);
                                                                            ingresarHoraInicio.setTitle("Seleccione hora de inicio");
                                                                            ingresarHoraInicio.show();
                                                                        }
                                                                    });

                                                                    finEntretiempo.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            TimePickerDialog ingresarHoraInicio = new TimePickerDialog(TurnosProgramadosActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                                                                @Override
                                                                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                                                    //Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(i), Toast.LENGTH_SHORT);
                                                                                    horasFinT = Integer.toString(i);
                                                                                    minutosFinT = Integer.toString(i1);
                                                                                    if (horasFinT.length() == 1)
                                                                                        horasFinT = "0" + horasFinT;
                                                                                    if (minutosFinT.length() == 1)
                                                                                        minutosFinT = "0" + minutosFinT;

                                                                                    finEntretiempo.setText(horasFinT + ":" + minutosFinT);
                                                                                }
                                                                            }, horasFin, minutosFin, false);
                                                                            ingresarHoraInicio.setTitle("Seleccione hora de inicio");
                                                                            ingresarHoraInicio.show();
                                                                        }
                                                                    });

                                                                }
                                                            }
                                                        });
                                                        builder.setCancelable(false);
                                                        AlertDialog dialog = builder.create();
                                                        dialog.show();
                                                    }
                                                }
                                            });
                                        }

                                        @NonNull
                                        @Override
                                        public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_lista_turnos, parent, false);
                                            TurnosProgramadosActivity.myviewholder viewholder = new TurnosProgramadosActivity.myviewholder(view);
                                            return viewholder;
                                        }
                                    };
                            recyclerView.setAdapter(adapter);
                            adapter.startListening();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        FirebaseRecyclerOptions<PreTurnos> options=new FirebaseRecyclerOptions.Builder<PreTurnos>()
                //.setQuery(FirebaseDatabase.getInstance().getReference().child("Turnos").orderByChild("fecha").equalTo(fecha).orderByChild("id_institucion").equalTo(idInstitucion), PreTurnos.class)
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Turnos").orderByChild(id), PreTurnos.class)
                .build();
        FirebaseRecyclerAdapter<PreTurnos,TurnosProgramadosActivity.myviewholder> adapter=
                new FirebaseRecyclerAdapter<PreTurnos, myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull PreTurnos model) {
                    holder.estadoTurno.setText(model.getEstado());
                    holder.horaInicioTurno.setText(model.getHoraInicio());
                    holder.horaFinTurno.setText(model.getHoraFin());

                    }

                    @NonNull
                    @Override
                    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_lista_turnos,parent,false);
                        TurnosProgramadosActivity.myviewholder viewholder=new TurnosProgramadosActivity.myviewholder(view);
                        return viewholder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();*/
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView horaInicioTurno, horaFinTurno, estadoTurno, nombreTurno, numeroTurno;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            // urlimagen=(ImageView) itemView.findViewById(R.id.imgPortada_user);
            estadoTurno = (TextView) itemView.findViewById(R.id.rvTituloDescripcionTurno);
            horaInicioTurno = (TextView) itemView.findViewById(R.id.rvHoraInicioTurno);
            horaFinTurno = (TextView) itemView.findViewById(R.id.rvHoraFinTurno);
            nombreTurno = (TextView) itemView.findViewById(R.id.rvNombreTurno);
            numeroTurno = (TextView) itemView.findViewById(R.id.rvNumeroTurno);
            //estadoRecordatorio=(TextView) itemView.findViewById(R.id.rvEstadoListaRecordatorios);*/
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }
}