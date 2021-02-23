package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListInstitucionesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    //AdapterInstituciones adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_instituciones);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_buscar);
        setSupportActionBar(toolbar);
        recyclerView=(RecyclerView) findViewById(R.id.rcl_listado_instituciones);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

    class myviewholder extends RecyclerView.ViewHolder{
        ImageView urlimagen;
        TextView nombreinstitucion, nombreusuario;
        ImageView ubicacion;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            urlimagen=(ImageView) itemView.findViewById(R.id.imgPortada);
            nombreinstitucion=(TextView) itemView.findViewById(R.id.txtNombre);
            nombreusuario=(TextView)itemView.findViewById(R.id.txt_usuario_item);
            ubicacion=(ImageView)itemView.findViewById(R.id.img_ubicacion_institucion);

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

        /*FirebaseRecyclerOptions<Institucion> options =
                new FirebaseRecyclerOptions.Builder<Institucion>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones").orderByChild("nombreinstitucion").startAt(s).endAt(s+"\uf8ff"), Institucion.class)
                        .build();

        adapter=new AdapterInstituciones(options);*/
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

        return super.onOptionsItemSelected(item);
    }
}