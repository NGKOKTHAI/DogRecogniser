package com.example.dogrecogniser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {


    TextView tv_forgetpassword;
    EditText ed_loginemail, ed_loginpassword;
    Button btn_login, btn_signup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tv_forgetpassword = findViewById(R.id.tv_forgetpassword);
        ed_loginemail = findViewById(R.id.ed_loginemail);
        ed_loginpassword = findViewById(R.id.ed_loginpassword);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                userLogin();
            }
        });



        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        tv_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,ForgetPassword.class);
                startActivity(intent);
            }
        });

    }

    private void userLogin()
    {
        String email = ed_loginemail.getText().toString().trim();
        String password = ed_loginpassword.getText().toString().trim();

        if (email.isEmpty()) {
            ed_loginemail.setError("Please fill in the username!");
            ed_loginemail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed_loginemail.setError("Please enter a valid email");
            ed_loginemail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            ed_loginpassword.setError("Password not filled in");
            ed_loginpassword.requestFocus();
            return;

        }


        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified())
                    {
                        //redirect User
                        startActivity(new Intent(Login.this,MainActivity.class));
                        finish();
                    }
                    else
                    {
                        user.sendEmailVerification();
                        Toast.makeText(Login.this, "Check your email to verify your account!",Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(Login.this,"Fail to login! Something went wrong!z",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //maintain the session
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null)
        {
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }
    }
}
