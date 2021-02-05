package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    private EditText txt_nombre,txt_email,txt_password,txt_cedula;
    private Button button_registrar;
    private ProgressDialog mDialog;
    private String nombre="",email="",password="",cedula;


    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mDialog=new ProgressDialog(this);
        txt_cedula=(EditText) findViewById(R.id.txt_cedula);
        txt_nombre=(EditText)findViewById(R.id.txt_nombre);
        txt_email=(EditText)findViewById(R.id.txt_email);
        txt_password=(EditText)findViewById(R.id.txt_password);
        button_registrar=(Button)findViewById(R.id.btn_registrar);
        button_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cedula=txt_cedula.getText().toString();
                nombre=txt_nombre.getText().toString();
                email=txt_email.getText().toString();
                password=txt_password.getText().toString();

                if(!nombre.isEmpty() && !email.isEmpty() && !password.isEmpty() && !cedula.isEmpty()){
                    if (cedula.length()==10){
                        if(password.length()>=6){
                            mDialog.setMessage("Espere un momento...");
                            mDialog.setCanceledOnTouchOutside(false);
                            mDialog.show();
                            registrar_Usuario();
                        }else {
                            txt_password.setError("Minimo 6 carateres");
                            //Toast.makeText(RegistroActivity.this,"La contraseña debe ser mayor a 6 caracteres", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        txt_cedula.setError("Cédula incorrecta");
                    }



                }else{
                    if(cedula.equals("") | cedula.length()>10 | cedula.length()<10){
                        txt_cedula.setError("Cedula incorrecta");
                    }
                    if(nombre.equals("")){
                        txt_nombre.setError("Ingrese el nombre");
                    }
                    if (email.equals("")){
                        txt_email.setError("Ingrese email");
                    }
                    if (password.equals("")){
                        txt_password.setError("Ingrese contraseña");
                    }
                    Toast.makeText(RegistroActivity.this,"Debe completar los campos", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void registrar_Usuario() {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<String, Object> map=new HashMap<>();
                    map.put("cedula", cedula);
                    map.put("nombre", nombre);
                    map.put("email", email);
                    map.put("password", password);

                    String id=firebaseAuth.getCurrentUser().getUid();
                    databaseReference.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                startActivity(new Intent(RegistroActivity.this,PrincipalActivity.class));
                            }else {
                                Toast.makeText(RegistroActivity.this,"No se pudo crear los datos correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(RegistroActivity.this,"No se pudo registrar usuario", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(RegistroActivity.this,PrincipalActivity.class));
            finish();
        }
    }
}