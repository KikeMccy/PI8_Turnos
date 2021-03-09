package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pi8_turnos.Model.Turno;
import com.example.pi8_turnos.Model.TurnoAsignado;
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
import java.util.Objects;

public class MisTurnosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    int count=0;
    boolean isActive, noti=false;
    DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_turnos);

        drawerLayout=(DrawerLayout) findViewById(R.id.mis_turnos);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_mis_turnos);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icono_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = (NavigationView)findViewById(R.id.nav_view_mis_turnos);
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);
        getInfoUser();

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

                        if (model.getEstado().equals("pendiente")) {
                            holder.nombreinstitucion.setText(model.getNombre_institucion());
                            holder.hora_inicio.setText(model.getHora_inicio());
                            holder.hora_fin.setText(model.getHora_fin());
                            holder.fecha.setText(model.getFecha());
                            String id_turno = model.getId_turno();
                            String id_horario = model.getId_horario();
                            String id_institucion = model.getId_institucion();

                            holder.qr_code.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    databaseReference.child("Instituciones").child(id_institucion).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String nombre_usuario = snapshot.child("nombreusuario").getValue().toString();
                                                Intent intent = new Intent(MisTurnosActivity.this, QRCodeActivity.class);
                                                String mensaje = "Ust tiene un turno en la institución: " + model.getNombre_institucion() + ".\n" + "Con el usuario: " + nombre_usuario + ".\n" + "En la fecha: " + model.getFecha() + ".\n" + "En el horario de : " + model.getHora_inicio() + " y " + model.getHora_fin();
                                                intent.putExtra("mensaje", mensaje);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });

                                }
                            });

                            holder.cancelar_turno.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MisTurnosActivity.this);
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
                                                                            //Snackbar snackbar=Snackbar.make(drawerLayout, "Turno cancelado", Snackbar.LENGTH_SHORT);
                                                                            //snackbar.show();
                                                                            Toast.makeText(MisTurnosActivity.this, "Turno cancelado", Toast.LENGTH_SHORT).show();
                                                                            //isActive=true;
                                                                            //content();
                                                                            startActivity(new Intent(MisTurnosActivity.this, PrincipalActivity.class));
                                                                        } else {
                                                                            //Snackbar snackbar=Snackbar.make(drawerLayout, "Error al cancelar el turno", Snackbar.LENGTH_SHORT);
                                                                            //snackbar.show();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_inicio:
                startActivity(new Intent(MisTurnosActivity.this,PrincipalActivity.class));
                break;
            case R.id.item_perfil:
                startActivity(new Intent(MisTurnosActivity.this,ModificarUserActivity.class));
                break;
            case R.id.item_informacion:
                startActivity(new Intent(MisTurnosActivity.this,AboutActivity.class));
                break;
            case R.id.item_escanear:
                startActivity(new Intent(MisTurnosActivity.this,LeerQRActivity.class));
                break;
            case R.id.item_generar_turno:
                startActivity(new Intent(MisTurnosActivity.this,GenerarTurnosActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:

                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.opc_cerrar_sesion:{
                firebaseAuth.signOut();
                startActivity(new Intent(MisTurnosActivity.this,MainActivity.class));
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        ImageView cancelar_turno,notificaciones,qr_code;
        TextView nombreinstitucion, hora_inicio,hora_fin, fecha;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            nombreinstitucion=(TextView) itemView.findViewById(R.id.txt_institucion_mis_turnos);
            hora_inicio=(TextView)itemView.findViewById(R.id.txt_hora_incio_mis_turnos);
            hora_fin=(TextView)itemView.findViewById(R.id.txt_hora_fin_mis_turnos);
            fecha=(TextView)itemView.findViewById(R.id.txt_fecha_mis_turno);
            cancelar_turno=(ImageView)itemView.findViewById(R.id.img_cancelar_turno);
            qr_code=(ImageView) itemView.findViewById(R.id.img_codigo_qr);

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
    private void getInfoUser(){
        String id=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre_user=snapshot.child("nombre").getValue().toString();
                    String email_user=snapshot.child("email").getValue().toString();
                    View header = ((NavigationView)findViewById(R.id.nav_view_mis_turnos)).getHeaderView(0);
                    ((TextView) header.findViewById(R.id.txt_nav_usuario)).setText(nombre_user);
                    ((TextView) header.findViewById(R.id.txt_nav_correo)).setText(email_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

}