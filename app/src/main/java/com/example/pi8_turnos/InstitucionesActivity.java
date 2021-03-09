package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class InstitucionesActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private ImageView buscar,ver_ubicacion;
    private Button subir,seleccionar,ubicacion;
    private EditText txt_nombre;
    private ImageView foto;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog cargado;
    private Bitmap bitmap=null;
    private FirebaseAuth firebaseAuth;
    private String nombre,email,id_user;
    private Double latitud, longitud;
    private TextView text_ubi;
    DrawerLayout drawerLayout;
    NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instituciones);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar_crear_instituciones);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icono_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = (NavigationView)findViewById(R.id.nav_view_instituciones);
        navView.setNavigationItemSelectedListener(this);
        navView.setItemIconTintList(null);
        getInfoUser();

        int permisionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }


         //nombre = getIntent().getExtras().getString("nombre");
         //email = getIntent().getExtras().getString("email");
         //id_user=getIntent().getExtras().getString("id");

        txt_nombre=(EditText) findViewById(R.id.txt_nombre_institucion);
            foto=findViewById(R.id.img_foto);
            subir=findViewById(R.id.btn_cargar_foto);
            //seleccionar=findViewById(R.id.btn_seleccionar_foto);
            //ubicacion=(Button) findViewById(R.id.btn_seleccionar_ubicacion);
            buscar=(ImageView)findViewById(R.id.img_buscar_logo);
            ver_ubicacion=(ImageView) findViewById(R.id.img_seleccionar_ubicacion);
            text_ubi=(TextView) findViewById(R.id.txt_ubicacion);
            //databaseReference= FirebaseDatabase.getInstance().getReference().child("Instituciones");
            storageReference= FirebaseStorage.getInstance().getReference().child("img_comprimidas");

            cargado=new ProgressDialog(this);
            buscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CropImage.startPickImageActivity(InstitucionesActivity.this);
                }
            });
            ver_ubicacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LocationManager locationManager=(LocationManager) InstitucionesActivity.this.getSystemService(Context.LOCATION_SERVICE);
                    LocationListener locationListener=new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            text_ubi.setText(""+location.getLatitude()+" "+location.getLongitude());
                            latitud=location.getLatitude();
                            longitud=location.getLongitude();

                        }

                    };
                    int permisionCheck= ContextCompat.checkSelfPermission(InstitucionesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri imagen_url=CropImage.getPickImageResultUri(this,data);

            CropImage.activity(imagen_url)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(500,500)
                    .setAspectRatio(2, 2).start(InstitucionesActivity.this);

        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri=result.getUri();
                File url= new File(resultUri.getPath());
                Picasso.with(this).load(url).into(foto);
                try {
                    bitmap=new Compressor(this)
                            .setMaxWidth(500)
                            .setMaxHeight(500)
                            .setQuality(90)
                            .compressToBitmap(url);
                }catch (IOException e){
                     e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);

                int p=(int) (Math.random()*25+1); int s=(int) (Math.random()*25+1);
                int t=(int)(Math.random()*25+1); int c=(int) (Math.random()*25+1);
                int numero1 =(int) (Math.random()* 1012+2111);
                int numero2 =(int) (Math.random()* 1012+2111);

                final byte [] bytes= byteArrayOutputStream.toByteArray();
                String[] elementos={"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
                final String aleatorio=elementos[p]+elementos[s]+numero1+elementos[t]+elementos[c]+numero2+"Comprimido.jpeg";
                subir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder=new AlertDialog.Builder(InstitucionesActivity.this);
                        builder.setTitle("!Alerta¡");
                        builder.setMessage("¿Desea asignar turno en el horario de ")
                                .setCancelable(false)
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String nombre_ins = txt_nombre.getText().toString().trim();
                                                if (nombre_ins.equals("")) {
                                                    txt_nombre.setError("Requiere nombre");
                                                }else {
                                                    cargado.setTitle("Creando institución...");
                                                    cargado.setMessage("Espere por favor...");
                                                    cargado.show();
                                                    StorageReference ref = storageReference.child(aleatorio);
                                                    UploadTask uploadTask = ref.putBytes(bytes);
                                                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                        @Override
                                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                            if (!task.isSuccessful()) {
                                                                throw Objects.requireNonNull(task.getException());
                                                            }
                                                            return ref.getDownloadUrl();

                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                            Uri downloaduri = task.getResult();

                                                            Map<String, Object> map = new HashMap<>();
                                                            map.put("idusuario", id_user);
                                                            map.put("nombreusuario", nombre);
                                                            map.put("nombreinstitucion", nombre_ins);
                                                            map.put("urlimagen", downloaduri.toString());
                                                            map.put("latitud", latitud.toString());
                                                            map.put("longitud", longitud.toString());

                                                            databaseReference.child("Instituciones").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task2) {
                                                                    if (task2.isSuccessful()) {
                                                                        startActivity(new Intent(InstitucionesActivity.this, InstitucionesUserActivity.class));
                                                                    } else {
                                                                        Toast.makeText(InstitucionesActivity.this, "No se pudo crear los datos correctamente", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                            cargado.dismiss();
                                                            Toast.makeText(InstitucionesActivity.this, "Institucion cargada con exito", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();


                        ///
                    }
                });


            }
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            startActivity(new Intent(InstitucionesActivity.this,GenerarTurnosActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_inicio:
                startActivity(new Intent(InstitucionesActivity.this,PrincipalActivity.class));
                break;
            case R.id.item_generar_turno:
                startActivity(new Intent(InstitucionesActivity.this,GenerarTurnosActivity.class));
                break;
            case R.id.item_perfil:
                startActivity(new Intent(InstitucionesActivity.this,ModificarUserActivity.class));
                break;
            case R.id.item_informacion:
                startActivity(new Intent(InstitucionesActivity.this,AboutActivity.class));
                break;
            case R.id.item_escanear:
                startActivity(new Intent(InstitucionesActivity.this,LeerQRActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_instituciones);
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.opc_cerrar_sesion:{
                firebaseAuth.signOut();
                startActivity(new Intent(InstitucionesActivity.this,MainActivity.class));
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void getInfoUser(){
        id_user=firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Usuarios").child(id_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nombre=snapshot.child("nombre").getValue().toString();
                    email=snapshot.child("email").getValue().toString();
                    View header = ((NavigationView)findViewById(R.id.nav_view_instituciones)).getHeaderView(0);
                    ((TextView) header.findViewById(R.id.txt_nav_usuario)).setText(nombre);
                    ((TextView) header.findViewById(R.id.txt_nav_correo)).setText(email);
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