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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ListadoTurnosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String id_turno,id_usuario,nombre,date,id_institucion,nombre_institucion,correo;
    private ProgressDialog mDialog;
    DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_turnos);

        drawerLayout=(DrawerLayout) findViewById(R.id.listado_turnos);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mDialog=new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_turnos);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icono_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = (NavigationView)findViewById(R.id.nav_view_listado_turnos);
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);

        id_turno=getIntent().getStringExtra("id_turno");
        date=getIntent().getStringExtra("fecha");
        id_institucion=getIntent().getStringExtra("id_institucion");
        nombre_institucion=getIntent().getStringExtra("nombreinstitucion");
        recyclerView=(RecyclerView) findViewById(R.id.rcl_horario);
        getInfoUser();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        //Toast.makeText(ListadoTurnosActivity.this, id_turno, Toast.LENGTH_SHORT).show();
        mDialog.setMessage("Cargando horarios...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

    }
    private void getInfoUser(){

        id_usuario=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id_usuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nombre=snapshot.child("nombre").getValue().toString();
                    correo=snapshot.child("email").getValue().toString();
                    View header = ((NavigationView)findViewById(R.id.nav_view_listado_turnos)).getHeaderView(0);
                    ((TextView) header.findViewById(R.id.txt_nav_usuario)).setText(nombre);
                    ((TextView) header.findViewById(R.id.txt_nav_correo)).setText(correo);
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
                        mDialog.dismiss();
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
                                                                mapinsert.put("estado", "pendiente");

                                                                databaseReference.child("TurnosAsignados").push().setValue(mapinsert).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task2) {
                                                                        if(task2.isSuccessful()){
                                                                            databaseReference.child("Instituciones").child(id_institucion).addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    if(snapshot.exists()){
                                                                                        String nombre_usuario=snapshot.child("nombreusuario").getValue().toString();
                                                                                        final String recipientEmail = "pi8semestre@gmail.com";
                                                                                        final String recipientPassword = "8SemestrePI";
                                                                                        sendEmailWithGmail(recipientEmail, recipientPassword, correo, "Turno Movil", "Ust tiene un turno pendiente en la institución: "+nombre_institucion+" Con el usuario: "+nombre_usuario+" el: "+date+", la hora de inicio es: "+h_inicio+" y la hora de finalización es: "+h_fin+". Recuerde estar pendiente al turno.");
                                                                                        //Snackbar snackbar=Snackbar.make(drawerLayout, "Turno Asignado", Snackbar.LENGTH_SHORT);
                                                                                        //snackbar.show();
                                                                                        Intent intent=new Intent(ListadoTurnosActivity.this,MisTurnosActivity.class);
                                                                                        startActivity(intent);
                                                                                        Toast.makeText(ListadoTurnosActivity.this, "Turno Asignado", Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                }
                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                                }
                                                                            });

                                                                        }else {
                                                                            //Snackbar snackbar=Snackbar.make(drawerLayout, "No se pudo crear los datos correctamente", Snackbar.LENGTH_SHORT);
                                                                            //snackbar.show();
                                                                            Toast.makeText(ListadoTurnosActivity.this,"No se pudo crear los datos correctamente", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            } else {
                                                                //Snackbar snackbar=Snackbar.make(drawerLayout, "Error al asignar turno, intente nuevamente", Snackbar.LENGTH_SHORT);
                                                                //snackbar.show();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_inicio:
                startActivity(new Intent(ListadoTurnosActivity.this,PrincipalActivity.class));
                break;
            case R.id.item_perfil:
                startActivity(new Intent(ListadoTurnosActivity.this,ModificarUserActivity.class));
                break;
            case R.id.item_informacion:
                startActivity(new Intent(ListadoTurnosActivity.this,AboutActivity.class));
                break;
            case R.id.item_escanear:
                startActivity(new Intent(ListadoTurnosActivity.this,LeerQRActivity.class));
                break;
            case R.id.item_generar_turno:
                startActivity(new Intent(ListadoTurnosActivity.this,GenerarTurnosActivity.class));
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
                startActivity(new Intent(ListadoTurnosActivity.this,MainActivity.class));
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
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
    private void sendEmailWithGmail(final String recipientEmail, final String recipientPassword,
                                    String to, String subject, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(recipientEmail, recipientPassword);
            }
        });

        SenderAsyncTask task = new SenderAsyncTask(session, recipientEmail, to, subject, message);
        task.execute();
    }

    private class SenderAsyncTask extends AsyncTask<String, String, String> {

        private String from, to, subject, message;
        private ProgressDialog progressDialog;
        private Session session;

        public SenderAsyncTask(Session session, String from, String to, String subject, String message) {
            this.session = session;
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ListadoTurnosActivity.this, "Éxito", "Mensaje enviado", true);
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Message mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(from));
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                mimeMessage.setSubject(subject);
                mimeMessage.setContent(message, "text/html; charset=utf-8");
                Transport.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }
}