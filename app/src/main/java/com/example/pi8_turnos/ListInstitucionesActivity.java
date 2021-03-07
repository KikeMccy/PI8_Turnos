package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pi8_turnos.Adaptadores.AdapterInstituciones;
import com.example.pi8_turnos.Model.Institucion;
import com.example.pi8_turnos.Model.InstitucionUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListInstitucionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recyclerView;
    //AdapterInstituciones adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog mDialog;
    private NavigationView navView;
    DrawerLayout drawerLayout;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_instituciones);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mDialog=new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_buscar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_list_instituciones);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icono_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = (NavigationView)findViewById(R.id.nav_view_list_instituciones);
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);

        getInfoUser();
        recyclerView=(RecyclerView) findViewById(R.id.rcl_listado_instituciones);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());

        mDialog.setMessage("Cargando instituciones...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        /*FirebaseRecyclerOptions<Institucion> options =
                new FirebaseRecyclerOptions.Builder<Institucion>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones"), Institucion.class)
                        .build();
        adapter=new AdapterInstituciones(options);
        recyclerView.setAdapter(adapter);*/
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Institucion> options =
                new FirebaseRecyclerOptions.Builder<Institucion>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones"), Institucion.class)
                        .build();
        FirebaseRecyclerAdapter<Institucion, ListInstitucionesActivity.myviewholder> adapter =
                new FirebaseRecyclerAdapter<Institucion, ListInstitucionesActivity.myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListInstitucionesActivity.myviewholder holder, final int position, @NonNull Institucion model)
                    {
                        holder.nombreusuario.setText(model.getNombreusuario());
                        holder.nombreinstitucion.setText(model.getNombreinstitucion());
                        Glide.with(holder.urlimagen.getContext()).load(model.getUrlimagen()).into(holder.urlimagen);
                        String nombre_institucion= model.getNombreinstitucion();
                        mDialog.dismiss();
                        holder.ubicacion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id = getRef(position).getKey();
                                databaseReference.child("Instituciones").child(id.toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            String la=snapshot.child("latitud").getValue().toString();
                                            String lo=snapshot.child("longitud").getValue().toString();
                                            String nombre=snapshot.child("nombreinstitucion").getValue().toString();
                                            Intent intent=new Intent(ListInstitucionesActivity.this,MapsActivity.class);
                                            Bundle bundle=new Bundle();
                                            bundle.putString("id",id);
                                            bundle.putString("latitud",la);
                                            bundle.putString("longitud",lo);
                                            bundle.putString("nombre", nombre);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {


                                    }
                                });

                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String id = getRef(position).getKey();
                                Intent intent=new Intent(ListInstitucionesActivity.this,TurnosInstitucionesActivity.class);
                                intent.putExtra("id_institucion", id);
                                intent.putExtra("nombreinstitucion", nombre_institucion);
                                startActivity(intent);

                            }
                        });

                        holder.calificacion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id = getRef(position).getKey();
                                Intent intent = new Intent(ListInstitucionesActivity.this, SugerenciasActivity.class);
                                intent.putExtra("id_institucion",id);
                                //Toast.makeText(ListInstitucionesActivity.this, id, Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ListInstitucionesActivity.myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_empresas, viewGroup, false);
                        ListInstitucionesActivity.myviewholder viewHolder = new ListInstitucionesActivity.myviewholder(view);
                        return viewHolder;
                    }
                };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        //int resId = R.anim.layout_animation_down_to_up;
        //LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        //recyclerView.setLayoutAnimation(animation);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_inicio:
                startActivity(new Intent(ListInstitucionesActivity.this,PrincipalActivity.class));
                break;
            case R.id.item_perfil:
                startActivity(new Intent(ListInstitucionesActivity.this,ModificarUserActivity.class));
                break;
            case R.id.item_informacion:
                startActivity(new Intent(ListInstitucionesActivity.this,AboutActivity.class));
                break;
            case R.id.item_escanear:
                startActivity(new Intent(ListInstitucionesActivity.this,LeerQRActivity.class));
                break;
            case R.id.item_generar_turno:
                startActivity(new Intent(ListInstitucionesActivity.this,GenerarTurnosActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    class myviewholder extends RecyclerView.ViewHolder{
        ImageView urlimagen;
        TextView nombreinstitucion, nombreusuario;
        ImageView ubicacion, calificacion;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            urlimagen=(ImageView) itemView.findViewById(R.id.imgPortada);
            nombreinstitucion=(TextView) itemView.findViewById(R.id.txtNombre);
            nombreusuario=(TextView)itemView.findViewById(R.id.txt_usuario_item);
            ubicacion=(ImageView)itemView.findViewById(R.id.img_ubicacion_institucion);
            calificacion=(ImageView) itemView.findViewById(R.id.img_calificar_atencion);

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            startActivity(new Intent(ListInstitucionesActivity.this,PrincipalActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buscar_instituciones,menu);
        MenuItem item=menu.findItem(R.id.buscar);

        SearchView searchView=(SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                prosesssearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                prosesssearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void prosesssearch(String s) {

        FirebaseRecyclerOptions<Institucion> options =
                new FirebaseRecyclerOptions.Builder<Institucion>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones").orderByChild("nombreinstitucion").startAt(s).endAt(s+"\uf8ff"), Institucion.class)
                        .build();
        FirebaseRecyclerAdapter<Institucion, ListInstitucionesActivity.myviewholder> adapter =
                new FirebaseRecyclerAdapter<Institucion, ListInstitucionesActivity.myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListInstitucionesActivity.myviewholder holder, final int position, @NonNull Institucion model)
                    {
                        holder.nombreusuario.setText(model.getNombreusuario());
                        holder.nombreinstitucion.setText(model.getNombreinstitucion());
                        Glide.with(holder.urlimagen.getContext()).load(model.getUrlimagen()).into(holder.urlimagen);
                        String nombre_institucion= model.getNombreinstitucion();
                        holder.ubicacion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id = getRef(position).getKey();
                                databaseReference.child("Instituciones").child(id.toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            String la=snapshot.child("latitud").getValue().toString();
                                            String lo=snapshot.child("longitud").getValue().toString();
                                            String nombre=snapshot.child("nombreinstitucion").getValue().toString();
                                            Intent intent=new Intent(ListInstitucionesActivity.this,MapsActivity.class);
                                            Bundle bundle=new Bundle();
                                            bundle.putString("id",id);
                                            bundle.putString("latitud",la);
                                            bundle.putString("longitud",lo);
                                            bundle.putString("nombre", nombre);
                                            intent.putExtras(bundle);
                                            startActivity(intent);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {


                                    }
                                });

                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String id = getRef(position).getKey();
                                Intent intent=new Intent(ListInstitucionesActivity.this,TurnosInstitucionesActivity.class);
                                intent.putExtra("id_institucion", id);
                                intent.putExtra("nombreinstitucion", nombre_institucion);
                                startActivity(intent);
                            }
                        });

                        holder.calificacion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id = getRef(position).getKey();
                                Intent intent = new Intent(ListInstitucionesActivity.this, SugerenciasActivity.class);
                                intent.putExtra("id_institucion",id);
                                //Toast.makeText(ListInstitucionesActivity.this, id, Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ListInstitucionesActivity.myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_empresas, viewGroup, false);
                        ListInstitucionesActivity.myviewholder viewHolder = new ListInstitucionesActivity.myviewholder(view);
                        return viewHolder;
                    }
                };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
        int resId = R.anim.layout_animation_down_to_up;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        recyclerView.setLayoutAnimation(animation);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void getInfoUser(){
        String id=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre_user=snapshot.child("nombre").getValue().toString();
                    String email_user=snapshot.child("email").getValue().toString();
                    View header = ((NavigationView)findViewById(R.id.nav_view_list_instituciones)).getHeaderView(0);
                    ((TextView) header.findViewById(R.id.txt_nav_usuario)).setText(nombre_user);
                    ((TextView) header.findViewById(R.id.txt_nav_correo)).setText(email_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }
}