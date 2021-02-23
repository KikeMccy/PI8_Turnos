package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pi8_turnos.Model.Horario;
import com.example.pi8_turnos.Model.Turno;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ListadoTurnosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String id_turno,id_usuario,nombre,date,id_institucion,nombre_institucion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_turnos);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_turnos);
        setSupportActionBar(toolbar);
        id_turno=getIntent().getStringExtra("id_turno");
        date=getIntent().getStringExtra("fecha");
        id_institucion=getIntent().getStringExtra("id_institucion");
        nombre_institucion=getIntent().getStringExtra("nombreinstitucion");
        recyclerView=(RecyclerView) findViewById(R.id.rcl_horario);
        getInfoUser();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        //Toast.makeText(ListadoTurnosActivity.this, id_turno, Toast.LENGTH_SHORT).show();

    }
    private void getInfoUser(){

        id_usuario=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id_usuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nombre=snapshot.child("nombre").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Horario> options =
                new FirebaseRecyclerOptions.Builder<Horario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Turnos").child(id_turno).child("horario"), Horario.class)
                        .build();
        FirebaseRecyclerAdapter<Horario, myviewholder> adapter =
                new FirebaseRecyclerAdapter<Horario, myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Horario model) {
                        holder.hora_inicio.setText(model.getHora_inicio());
                        holder.hora_fin.setText(model.getHora_fin());
                        holder.estado.setText(model.getEstado());
                        holder.numero.setText("Turno Nº"+model.getNumero());
                        String h_inicio=model.getHora_inicio();
                        String h_fin=model.getHora_fin();
                        String status = model.getEstado();
                        if (status.equals("libre")){
                            holder.estado.setTextColor(Color.BLUE);
                            holder.asignar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    AlertDialog.Builder builder=new AlertDialog.Builder(ListadoTurnosActivity.this);
                                    builder.setTitle("!Alerta¡");
                                    builder.setMessage("¿Desea asignar turno en el horario de "+h_inicio+" a " +h_fin+" ?")
                                            .setCancelable(false)
                                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String id = getRef(position).getKey();

                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("estado", "ocupado");
                                                    //map.put("id_usuario", id_usuario);
                                                    map.put("nombre_usuario", nombre);

                                                    databaseReference.child("Turnos").child(id_turno).child("horario").child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Map<String, Object> mapinsert=new HashMap<>();
                                                                mapinsert.put("id_usuario", id_usuario);
                                                                mapinsert.put("id_turno",id_turno);
                                                                mapinsert.put("hora_inicio",h_inicio);
                                                                mapinsert.put("hora_fin",h_fin);
                                                                mapinsert.put("fecha", date);
                                                                mapinsert.put("id_institucion",id_institucion);
                                                                mapinsert.put("nombre_institucion",nombre_institucion);
                                                                mapinsert.put("id_horario", id);

                                                                databaseReference.child("TurnosAsignados").push().setValue(mapinsert).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task2) {
                                                                        if(task2.isSuccessful()){
                                                                            startActivity(new Intent(ListadoTurnosActivity.this,MisTurnosActivity.class));
                                                                            Toast.makeText(ListadoTurnosActivity.this, "Turno Asignado", Toast.LENGTH_SHORT).show();
                                                                        }else {
                                                                            Toast.makeText(ListadoTurnosActivity.this,"No se pudo crear los datos correctamente", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                Toast.makeText(ListadoTurnosActivity.this, "Error al asignar turno, intente nuevamente", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                }
                                            }).show();
                                }
                            });
                    }else {
                            holder.estado.setTextColor(Color.RED);
                            holder.asignar.setVisibility(View.INVISIBLE);
                        }

                    }

                    @NonNull
                    @Override
                    public myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_horario, viewGroup, false);
                        myviewholder viewHolder = new myviewholder(view);
                        return viewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView hora_inicio, hora_fin, estado,numero;
        ImageView asignar;
        public myviewholder(@NonNull View itemView) {
            super(itemView);

            hora_inicio=(TextView) itemView.findViewById(R.id.txt_hora_incio);
            hora_fin=(TextView)itemView.findViewById(R.id.txt_hora_fin);
            estado=(TextView)itemView.findViewById(R.id.txt_estado_turno);
            numero=(TextView)itemView.findViewById(R.id.txt_titulo_turno);
            asignar=(ImageView)itemView.findViewById(R.id.img_solicitar_turno);

        }
    }

}