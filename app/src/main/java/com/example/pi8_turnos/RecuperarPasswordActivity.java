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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarPasswordActivity extends AppCompatActivity {

    private EditText txt_email;
    private Button btn_recuperar;
    private String email="";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mDialog;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);

        linearLayout=(LinearLayout) findViewById(R.id.recuperar_password);
        firebaseAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);
        txt_email=(EditText)findViewById(R.id.txt_email_pass);
        btn_recuperar=(Button) findViewById(R.id.btn_recuperar_pass);
        btn_recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=txt_email.getText().toString();
                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mDialog.setMessage("Espere un momento...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    recuperarPassword();
                }else {
                    if(email.equals("")){
                     txt_email.setError("Ingrese email");
                    }
                    Snackbar snackbar=Snackbar.make(linearLayout, "Email incorrecto", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //Toast.makeText(RecuperarPasswordActivity.this,"Email incorrecto",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void recuperarPassword() {
        firebaseAuth.setLanguageCode("es");
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    txt_email.setText("");
                    startActivity(new Intent(RecuperarPasswordActivity.this,LoginActivity.class));
                    Snackbar snackbar=Snackbar.make(linearLayout, "Se ha enviado un correo para restablecer la contraseña", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //Toast.makeText(RecuperarPasswordActivity.this,"Se ha enviado un correo para restablecer la contraseña",Toast.LENGTH_SHORT).show();
                }else {
                    Snackbar snackbar=Snackbar.make(linearLayout, "No se pudo restablecer, email no registrado", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //Toast.makeText(RecuperarPasswordActivity.this,"No se pudo restablecer, email no registrado",Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }
}