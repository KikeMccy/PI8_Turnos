package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pi8_turnos.Adaptadores.AdapterInstituciones;
import com.example.pi8_turnos.Adaptadores.AdapterInstitucionesUser;
import com.example.pi8_turnos.Model.Institucion;
import com.example.pi8_turnos.Model.InstitucionUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InstitucionesUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    AdapterInstitucionesUser adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instituciones_user);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_instituciones_user);
        setSupportActionBar(toolbar);
        nombre = getIntent().getExtras().getString("nombre");
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
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Instituciones").orderByChild("nombreusuario").equalTo(nombre), InstitucionUser.class)
                        .build();
        FirebaseRecyclerAdapter<InstitucionUser, myviewholder> adapter =
                new FirebaseRecyclerAdapter<InstitucionUser, myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull myviewholder holder, final int position, @NonNull InstitucionUser model)
                    {
                        holder.nombreinstitucion.setText(model.getNombreinstitucion());
                        Glide.with(holder.urlimagen.getContext()).load(model.getUrlimagen()).into(holder.urlimagen);

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

    class myviewholder extends RecyclerView.ViewHolder{
        ImageView urlimagen;
        TextView nombreinstitucion, nombreusuario;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            urlimagen=(ImageView) itemView.findViewById(R.id.imgPortada_user);
            nombreinstitucion=(TextView) itemView.findViewById(R.id.txtNombre_user);

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            startActivity(new Intent(InstitucionesUserActivity.this,PrincipalActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }
}