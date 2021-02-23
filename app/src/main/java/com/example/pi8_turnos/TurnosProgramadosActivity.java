package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class TurnosProgramadosActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String fecha, idInstitucion;
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
                                                        CharSequence[] items = new CharSequence[2];
                                                        items[0] = "Eliminar Turno";
                                                        items[1] = "Cancelar";
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