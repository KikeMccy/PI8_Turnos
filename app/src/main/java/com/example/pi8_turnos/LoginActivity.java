package com.example.pi8_turnos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pi8_turnos.Model.Entidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private TextView lb_password;
    private EditText txt_email, txt_password;
    private Button btn_login, btn_registro;
    private String email="",password="";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog mDialog;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linearLayout=(LinearLayout) findViewById(R.id.login);
        firebaseAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);
        txt_email=(EditText) findViewById(R.id.txt_email_login);
        txt_password=(EditText) findViewById(R.id.txt_password_login);
        lb_password=(TextView) findViewById(R.id.lb_rec_pass);
        lb_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RecuperarPasswordActivity.class));
            }
        });

        btn_registro=(Button) findViewById(R.id.btn_registrarse);
        btn_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistroActivity.class));
            }
        });

        btn_login=(Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email=txt_email.getText().toString().trim();
                password=txt_password.getText().toString().trim();
                if(!email.isEmpty() && !password.isEmpty()){
                    mDialog.setMessage("Iniciando sesión...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    loginUsuario();
                }else {
                    if(email.equals("")){
                        txt_email.setError("Ingrese email");
                    }
                    if(password.equals("")) {
                        txt_password.setError("Ingrese contraseña");
                    }
                    Snackbar snackbar=Snackbar.make(linearLayout, "Complete los casilleros", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //Toast.makeText(LoginActivity.this,"Complete los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loginUsuario() {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
                }else{
                    Snackbar snackbar=Snackbar.make(linearLayout, "Datos incorrectos", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //Toast.makeText(LoginActivity.this,"Datos incorrectos", Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this,PrincipalActivity.class));
            finish();
        }
    }


}