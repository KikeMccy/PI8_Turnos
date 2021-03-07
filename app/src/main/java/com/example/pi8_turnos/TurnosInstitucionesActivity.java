package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pi8_turnos.Adaptadores.AdapterInstitucionesUser;
import com.example.pi8_turnos.Model.InstitucionUser;
import com.example.pi8_turnos.Model.Turno;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TurnosInstitucionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String idInstitucion, usuario, nombre, institucion,nombre_institucion;
    DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos_instituciones);


        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_turnos_instituciones);
        setSupportActionBar(toolbar);

        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout_turnos_instituciones);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icono_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = (NavigationView)findViewById(R.id.nav_view_turnos_instituciones);
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);
        getInfoUser();
        idInstitucion=getIntent().getStringExtra("id_institucion");
        nombre_institucion=getIntent().getStringExtra("nombreinstitucion");
        //Toast.makeText(TurnosInstitucionesActivity.this, idInstitucion, Toast.LENGTH_SHORT).show();

        recyclerView=(RecyclerView) findViewById(R.id.rcl_turnos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Turno> options =
                new FirebaseRecyclerOptions.Builder<Turno>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Turnos").orderByChild("id_institucion").equalTo(idInstitucion), Turno.class)
                        .build();
        FirebaseRecyclerAdapter<Turno, myviewholder> adapter =
                new FirebaseRecyclerAdapter<Turno, myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myviewholder holder, final int position, @NonNull Turno model)
                    {
                        String id_usuario= model.getId_usuario();
                        databaseReference.child("Usuarios").child(id_usuario).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    nombre=snapshot.child("nombre").getValue().toString();
                                    databaseReference.child("Instituciones").child(idInstitucion).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                institucion=snapshot.child("nombreinstitucion").getValue().toString();
                                                //Toast.makeText(TurnosInstitucionesActivity.this, nombre, Toast.LENGTH_SHORT).show();
                                                holder.nombreinstitucion.setText(model.getDescripcion());
                                                holder.fecha.setText(model.getFecha());
                                                String date=model.getFecha();
                                                holder.turno.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        String id_turno = getRef(position).getKey();
                                                        Intent intent = new Intent(TurnosInstitucionesActivity.this,ListadoTurnosActivity.class);
                                                        intent.putExtra("id_turno", id_turno);
                                                        intent.putExtra("fecha", date);
                                                        intent.putExtra("id_institucion",idInstitucion);
                                                        intent.putExtra("nombreinstitucion",nombre_institucion);
                                                        startActivity(intent);
                                                        //Toast.makeText(TurnosInstitucionesActivity.this, id_turno, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_turnos, viewGroup, false);
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
                startActivity(new Intent(TurnosInstitucionesActivity.this,PrincipalActivity.class));
                break;
            case R.id.item_informacion:
                startActivity(new Intent(TurnosInstitucionesActivity.this,AboutActivity.class));
                break;
            case R.id.item_escanear:
                startActivity(new Intent(TurnosInstitucionesActivity.this,LeerQRActivity.class));
                break;
            case R.id.item_generar_turno:
                startActivity(new Intent(TurnosInstitucionesActivity.this,GenerarTurnosActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:

                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.opc_cerrar_sesion:{
                firebaseAuth.signOut();
                startActivity(new Intent(TurnosInstitucionesActivity.this,MainActivity.class));
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView nombreinstitucion, fecha;
        ImageView turno;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            turno=(ImageView) itemView.findViewById(R.id.img_tickets);
            nombreinstitucion=(TextView) itemView.findViewById(R.id.txt_institucion_turno);
            fecha=(TextView)itemView.findViewById(R.id.txt_fecha_turno);

        }
    }

    private void getInfoUser(){
        String id=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre_user=snapshot.child("nombre").getValue().toString();
                    String email_user=snapshot.child("email").getValue().toString();
                    View header = ((NavigationView)findViewById(R.id.nav_view_turnos_instituciones)).getHeaderView(0);
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