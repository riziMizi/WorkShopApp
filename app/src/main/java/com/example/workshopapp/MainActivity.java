package com.example.workshopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private TextView txtRegister;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.LoginEmail);
        editTextPassword = (EditText) findViewById(R.id.LoginPassword);
        txtRegister = (TextView) findViewById(R.id.txtRegister);

        txtRegister.setPaintFlags(txtRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
    }

    public void Register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void Login(View view) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        Intent intentPostaro = new Intent(this, PostaroLiceActivity.class);
        Intent intentVolonter = new Intent(this, VolonterActivity.class);
        Intent intentOrganizator = new Intent(this, OrganizatorActivity.class);

        if(email.equals("")) {
            editTextEmail.setError("Задолжително поле!");
            editTextEmail.requestFocus();
            return;
        } else {
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Внесете валидна е-маил адреса!");
                editTextEmail.requestFocus();
                return;
            }
        }

        if(password.equals("")) {
            editTextPassword.setError("Задолжително поле!");
            editTextPassword.requestFocus();
            return;
        } else {
            if(password.length() < 6) {
                editTextPassword.setError("Внесете повеќе од 6 карактери!");
                editTextPassword.requestFocus();
                return;
            }
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    String uid = user.getUid();

                    reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User korisnik = snapshot.getValue(User.class);

                            if(korisnik != null) {
                                String tip = korisnik.getTip();

                                if(tip.equals("PostaroLice")) {
                                    startActivity(intentPostaro);
                                } else if(tip.equals("Volonter")) {
                                    startActivity(intentVolonter);
                                } else if(tip.equals("Organizator")) {
                                    startActivity(intentOrganizator);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();

                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, "Неуспешна најава.Погрешен е-маил или лозинка!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}