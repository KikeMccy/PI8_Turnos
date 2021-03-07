package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.pi8_turnos.Adaptadores.AdapterInstituciones;
import com.example.pi8_turnos.Adaptadores.AdapterInstitucionesUser;
import com.example.pi8_turnos.Model.Institucion;
import com.example.pi8_turnos.Model.InstitucionUser;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class InstitucionesUserActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    AdapterInstitucionesUser adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String id_user;
    private NavigationView navView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instituciones_user);
        drawerLayout = (DrawerLayout)findViewById(R.id.mis_instituciones);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_instituciones_user);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icono_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = (NavigationView)findViewById(R.id.nav_view_instituciones_user);
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);

        getInfoUser();

        //nombre = getIntent().getExtras().getString("nombre");
        recyclerView=(RecyclerView) findViewById(R.id.rcl_instituciones_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        /*FirebaseRecyclerOptions<InstitucionUser> options =
                new FirebaseRecyclerOptions.Builder<InstitucionUser>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones").orderByChild("nombreusuario").equalTo(nombre), InstitucionUser.class)
                        .build();
        adapter=new AdapterInstitucionesUser(options);
        recyclerView.setAdapter(adapter);*/

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<InstitucionUser> options =
                new FirebaseRecyclerOptions.Builder<InstitucionUser>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones").orderByChild("idusuario").equalTo(id_user), InstitucionUser.class)
                        .build();
        FirebaseRecyclerAdapter<InstitucionUser, myviewholder> adapter =
                new FirebaseRecyclerAdapter<InstitucionUser, myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myviewholder holder, final int position, @NonNull InstitucionUser model)
                    {
                        holder.nombreinstitucion.setText(model.getNombreinstitucion());
                        Glide.with(holder.urlimagen.getContext()).load(model.getUrlimagen()).into(holder.urlimagen);

                        holder.eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id = getRef(position).getKey();
                                AlertDialog.Builder builder=new AlertDialog.Builder(InstitucionesUserActivity.this);
                                builder.setTitle("!Alerta¡");
                                builder.setMessage("¿Desea eliminar la institución?")
                                        .setCancelable(false)
                                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        databaseReference.child("Instituciones").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Query query=FirebaseDatabase.getInstance().getReference("TurnosAsignados").orderByChild("id_institucion").equalTo(id);
                                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            for (DataSnapshot snapshot1:snapshot.getChildren()){
                                                                                snapshot1.getRef().removeValue();

                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
                                                                    //Snackbar snackbar=Snackbar.make(drawerLayout, "Institución eliminada", Snackbar.LENGTH_SHORT);
                                                                    //snackbar.show();
                                                                    Toast.makeText(InstitucionesUserActivity.this, "Eliminado", Toast.LENGTH_SHORT).show();
                                                                }else {
                                                                    //Snackbar snackbar=Snackbar.make(drawerLayout, "Error al eliminar", Snackbar.LENGTH_SHORT);
                                                                    //snackbar.show();
                                                                    Toast.makeText(InstitucionesUserActivity.this, "No se pudo eliminar la institución...", Toast.LENGTH_SHORT).show();
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

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String id = getRef(position).getKey();
                                Intent intent = new Intent(InstitucionesUserActivity.this, Calendario_Activity.class);
                                intent.putExtra("id_institucion", id);
                                startActivity(intent);
                                //Toast.makeText(InstitucionesUserActivity.this,id,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_instituciones_user, viewGroup, false);
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
                    startActivity(new Intent(InstitucionesUserActivity.this,PrincipalActivity.class));
                    break;
                case R.id.item_perfil:
                    startActivity(new Intent(InstitucionesUserActivity.this,ModificarUserActivity.class));
                    break;
                case R.id.item_informacion:
                    startActivity(new Intent(InstitucionesUserActivity.this,AboutActivity.class));
                    break;
                case R.id.item_escanear:
                    startActivity(new Intent(InstitucionesUserActivity.this,LeerQRActivity.class));
                    break;
                case R.id.item_generar_turno:
                    startActivity(new Intent(InstitucionesUserActivity.this,GenerarTurnosActivity.class));
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
                startActivity(new Intent(InstitucionesUserActivity.this,MainActivity.class));
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ImageView urlimagen, eliminar;
        TextView nombreinstitucion, nombreusuario;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            urlimagen=(ImageView) itemView.findViewById(R.id.imgPortada_user);
            nombreinstitucion=(TextView) itemView.findViewById(R.id.txtNombre_user);
            eliminar=(ImageView) itemView.findViewById(R.id.img_eliminar_institucion);

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            startActivity(new Intent(InstitucionesUserActivity.this,GenerarTurnosActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getInfoUser(){
        id_user=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre_user=snapshot.child("nombre").getValue().toString();
                    String email_user=snapshot.child("email").getValue().toString();
                    View header = ((NavigationView)findViewById(R.id.nav_view_instituciones_user)).getHeaderView(0);
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