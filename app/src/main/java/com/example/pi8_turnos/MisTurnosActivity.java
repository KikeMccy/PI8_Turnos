package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pi8_turnos.Model.Turno;
import com.example.pi8_turnos.Model.TurnoAsignado;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MisTurnosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    int count=0;
    boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_turnos);
        
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_mis_turnos);
        setSupportActionBar(toolbar);
        recyclerView=(RecyclerView) findViewById(R.id.rcl_mis_turnos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

    }

    @Override
    protected void onStart() {
        super.onStart();
        String id_usuario=firebaseAuth.getCurrentUser().getUid();
        FirebaseRecyclerOptions<TurnoAsignado> options =
                new FirebaseRecyclerOptions.Builder<TurnoAsignado>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("TurnosAsignados").orderByChild("id_usuario").equalTo(id_usuario), TurnoAsignado.class)
                        .build();
        FirebaseRecyclerAdapter<TurnoAsignado, myviewholder> adapter =
                new FirebaseRecyclerAdapter<TurnoAsignado, myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myviewholder holder, final int position, @NonNull TurnoAsignado model) {
                        holder.nombreinstitucion.setText(model.getNombre_institucion());
                        holder.hora_inicio.setText(model.getHora_inicio());
                        holder.hora_fin.setText(model.getHora_fin());
                        holder.fecha.setText(model.getFecha());
                        String id_turno = model.getId_turno();
                        String id_horario = model.getId_horario();
                        holder.notificaciones.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        holder.cancelar_turno.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder=new AlertDialog.Builder(MisTurnosActivity.this);
                                builder.setTitle("!Alerta¡");
                                builder.setMessage("¿Desea cancelar el turno?")
                                        .setCancelable(false)
                                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String id = getRef(position).getKey();
                                                databaseReference.child("TurnosAsignados").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Object> map = new HashMap<>();
                                                            map.put("estado", "libre");
                                                            map.put("nombre_usuario", "ninguno");
                                                            databaseReference.child("Turnos").child(id_turno).child("horario").child(id_horario).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task2) {
                                                                    if (task2.isSuccessful()) {
                                                                        Toast.makeText(MisTurnosActivity.this, "A cancelado el turno", Toast.LENGTH_SHORT).show();
                                                                        //isActive=true;
                                                                        //content();
                                                                        startActivity(new Intent(MisTurnosActivity.this,PrincipalActivity.class));
                                                                    } else {
                                                                        Toast.makeText(MisTurnosActivity.this, "Error al cancelar turno", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                }
                                                            });

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
                    }


                    @NonNull
                    @Override
                    public myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mis_turnos, viewGroup, false);
                        myviewholder viewHolder = new myviewholder(view);
                        return viewHolder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    class myviewholder extends RecyclerView.ViewHolder{

        ImageView cancelar_turno,notificaciones;
        TextView nombreinstitucion, hora_inicio,hora_fin, fecha;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            nombreinstitucion=(TextView) itemView.findViewById(R.id.txt_institucion_mis_turnos);
            hora_inicio=(TextView)itemView.findViewById(R.id.txt_hora_incio_mis_turnos);
            hora_fin=(TextView)itemView.findViewById(R.id.txt_hora_fin_mis_turnos);
            fecha=(TextView)itemView.findViewById(R.id.txt_fecha_mis_turno);
            cancelar_turno=(ImageView)itemView.findViewById(R.id.img_cancelar_turno);
            notificaciones=(ImageView)itemView.findViewById(R.id.img_notificar_turno);

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            startActivity(new Intent(MisTurnosActivity.this,PrincipalActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }
public void content(){
        count++;
        if(isActive){
            refresh(500);
        }
}

    private void refresh(int i) {
        final Handler handler=new Handler();
        final Runnable runnable=new Runnable() {
            @Override
            public void run() {
                content();
            }
        };
        handler.postDelayed(runnable,i);
    }

}