package com.example.dogrecogniser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    EditText ed_loginusername, ed_loginpassword;
    Button btn_login, btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_loginusername = findViewById(R.id.ed_loginusername);
        ed_loginpassword = findViewById(R.id.ed_loginpassword);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ed_loginusername.getText().toString().trim();
                String password = ed_loginpassword.getText().toString().trim();
                if (email.isEmpty()) {
                    ed_loginusername.setError("Please fill in the username!");
                    ed_loginusername.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    ed_loginpassword.setError("Password not filled in");
                    ed_loginpassword.requestFocus();
                    return;

                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    ed_loginusername.setError("Please neter a valid email");
                    ed_loginusername.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    ed_loginpassword.setError("Min password length is 6 characters");
                    ed_loginpassword.requestFocus();
                    return;
                }
            }
        });

        

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Login.this, SignUp.class);
                startActivity(intent2);
            }
        });

    }
}
