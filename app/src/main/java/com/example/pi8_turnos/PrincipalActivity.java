package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private TextView lb_nombre,lb_email;
    DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String nombre,email, id;
    private ProgressDialog mDialog;
    private NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mDialog=new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icono_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = (NavigationView)findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);
        navView.setCheckedItem(R.id.item_sugerencias);

        //lb_nombre=(TextView) findViewById(R.id.txt_nombre_user);
        //lb_email=(TextView) findViewById(R.id.txt_email_user);



        mDialog.setMessage("Espere por favor...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        getInfoUser();


        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        */

    }
    private void getInfoUser(){
         id=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.exists()){
                 nombre=snapshot.child("nombre").getValue().toString();
                 email=snapshot.child("email").getValue().toString();
                 //lb_nombre.setText(nombre);
                 //lb_email.setText(email);
                 View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
                 ((TextView) header.findViewById(R.id.txt_nav_usuario)).setText(nombre);
                 ((TextView) header.findViewById(R.id.txt_nav_correo)).setText(email);
                 mDialog.dismiss();
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
            case android.R.id.home:
                drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
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

    public void verinstitucionesuser(View view){
        Intent intent=new Intent(PrincipalActivity.this, InstitucionesUserActivity.class);
        intent.putExtra("nombre",nombre);
        startActivity(intent);
    }
    public void abrirInstituciones(View view){
        startActivity(new Intent(PrincipalActivity.this,ListInstitucionesActivity.class));
    }

    public void abririmagen(View view){

        Intent intent=new Intent(PrincipalActivity.this, InstitucionesActivity.class);
        intent.putExtra("nombre",nombre);
        intent.putExtra("email", email);
        intent.putExtra("id", id);
        startActivity(intent);
        //startActivity(new Intent(PrincipalActivity.this, InstitucionesActivity.class));
    }
    public void abrirMisTurnos(View view){
        startActivity(new Intent(PrincipalActivity.this,MisTurnosActivity.class));
    }


    // BOTON ATRAS
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            startActivity(new Intent(PrincipalActivity.this,MainActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_sugerencias:
                Intent intent=new Intent(PrincipalActivity.this, SugerenciasActivity.class);
                intent.putExtra("nombre",nombre);
                startActivity(intent);
                break;
            case R.id.item_informacion:
                startActivity(new Intent(PrincipalActivity.this,AboutActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}