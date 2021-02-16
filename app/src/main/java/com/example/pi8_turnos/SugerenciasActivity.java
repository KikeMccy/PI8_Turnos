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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pi8_turnos.Model.Comentario;
import com.example.pi8_turnos.Model.InstitucionUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SugerenciasActivity extends AppCompatActivity  {


    private TextView textView;
    private EditText txt_comentario;
    private Button btn_comentar;
    private RatingBar ratingBar;
    private float ratevalue;
    private String temp, comentario,nombre;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Boolean ban=false;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);

        Calendar calendar = Calendar.getInstance();
        String fecha = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calendar.getTime());

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_sugerencias);
        setSupportActionBar(toolbar);



        nombre = getIntent().getExtras().getString("nombre");
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        ban=false;
        ratingBar=(RatingBar) findViewById(R.id.ratingBar_valoracion);
        txt_comentario=(EditText) findViewById(R.id.txt_comentario);
        textView=(TextView) findViewById(R.id.txt_valoracion);
        btn_comentar=(Button) findViewById(R.id.btn_comentar);
        recyclerView=(RecyclerView) findViewById(R.id.rcl_sugerencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratevalue=ratingBar.getRating();
                if (ratevalue<=1 && ratevalue>=0){
                    textView.setText("Mala: "+ ratevalue+ "/5");
                    ban=true;

                }else if(ratevalue<=2 && ratevalue>1){
                    textView.setText("Regular: "+ ratevalue+ "/5");
                    ban=true;

                }else if(ratevalue<=3 && ratevalue>2){
                    textView.setText("Buena: "+ ratevalue+ "/5");
                    ban=true;

                }else if(ratevalue<=4 && ratevalue>3){
                    textView.setText("Muy buena: "+ ratevalue+ "/5");
                    ban=true;

                }else if(ratevalue<=5 && ratevalue>4){
                    textView.setText("Excelente: "+ ratevalue+ "/5");
                    ban=true;

                }
            }
        });
        btn_comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                comentario=txt_comentario.getText().toString().trim();
                if (ban==false){
                    Toast.makeText(SugerenciasActivity.this,"Falta la puntuación",Toast.LENGTH_SHORT).show();
                }else {
                    if (comentario.equals("")){
                        txt_comentario.setError("Escriba el comentario");
                    }else {


                                    Map<String, Object> map=new HashMap<>();
                                    map.put("nombre", nombre);
                                    map.put("puntuacion", String.valueOf(ratevalue));
                                    map.put("comentario", comentario);
                                    map.put("fecha", fecha);

                                    databaseReference.child("Sugerencias").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SugerenciasActivity.this, "Ust a comentado" , Toast.LENGTH_SHORT).show();
                                                ratingBar.setRating(0);
                                                textView.setText("");
                                                txt_comentario.setText("");
                                                ban=false;
                                            }else {
                                                Toast.makeText(SugerenciasActivity.this, "No se pudo registrar sugerencia" , Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                }
                            }

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comentario> options = new FirebaseRecyclerOptions.Builder<Comentario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Sugerencias"), Comentario.class)
                        .build();
        FirebaseRecyclerAdapter<Comentario, SugerenciasActivity.myviewholder> adapter = new FirebaseRecyclerAdapter<Comentario, SugerenciasActivity.myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SugerenciasActivity.myviewholder holder, final int position, @NonNull Comentario model)
                    {
                        holder.ratingBar2.setRating(Float.valueOf(model.getPuntuacion()));
                        holder.nombre.setText(model.getNombre());
                        holder.comentario.setText(model.getComentario());
                        holder.fecha.setText(model.getFecha());

                    }

                    @NonNull
                    @Override
                    public SugerenciasActivity.myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sugerencias, viewGroup, false);
                        SugerenciasActivity.myviewholder viewHolder = new SugerenciasActivity.myviewholder(view);
                        return viewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }




    class myviewholder extends RecyclerView.ViewHolder{
        RatingBar ratingBar2;
        TextView nombre, comentario, fecha;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            ratingBar2=(RatingBar) itemView.findViewById(R.id.ratingBar_commet_val);
            nombre=(TextView) itemView.findViewById(R.id.txt_usurio_commet_val);
            comentario=(TextView)itemView.findViewById(R.id.txt_commet_val);
            fecha=(TextView)itemView.findViewById(R.id.txt_fecha_commet_val);

        }
    }
}