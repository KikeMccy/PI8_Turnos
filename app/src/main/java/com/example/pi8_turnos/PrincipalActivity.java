package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrincipalActivity extends AppCompatActivity {

    private TextView lb_nombre,lb_email;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String nombre,email, id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        lb_nombre=(TextView) findViewById(R.id.txt_nombre_user);
        lb_email=(TextView) findViewById(R.id.txt_email_user);

        getInfoUser();


        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        final DrawerLayout drawerLayout;
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();*/

    }
    private void getInfoUser(){
         id=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.exists()){
                 nombre=snapshot.child("nombre").getValue().toString();
                 email=snapshot.child("email").getValue().toString();
                 lb_nombre.setText(nombre);
                 lb_email.setText(email);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.opc_cerrar_sesion:{
                firebaseAuth.signOut();
                startActivity(new Intent(PrincipalActivity.this,MainActivity.class));
                finish();
                break;
            }
            case R.id.opc_modificar_user:{
                startActivity(new Intent(PrincipalActivity.this,ModificarUserActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void abrirCalendarioTurnos(View view){
        startActivity(new Intent(PrincipalActivity.this,Calendario_Activity.class));
    }

    public void abririmagen(View view){

        Intent intent=new Intent(PrincipalActivity.this, InstitucionesActivity.class);
        intent.putExtra("nombre",nombre);
        intent.putExtra("email", email);
        intent.putExtra("id", id);
        startActivity(intent);
        //startActivity(new Intent(PrincipalActivity.this, InstitucionesActivity.class));
    }
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Desea salir de Turno movil")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.show();
        }
        return super.onKeyDown(keyCode, event);
    }*/

}