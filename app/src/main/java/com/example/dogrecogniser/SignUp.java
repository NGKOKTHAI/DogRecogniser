package com.example.dogrecogniser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6}$");

    EditText ed_signupusername,ed_signupemail,ed_signuppassword,ed_signconpassword;
    Button btn_signup2,btn_login2;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        ed_signupusername = findViewById(R.id.ed_signupusername);
        ed_signupemail = findViewById(R.id.ed_signupemail);
        ed_signuppassword = findViewById(R.id.ed_signuppassword);
        ed_signconpassword = findViewById(R.id.ed_signconpassword);
        btn_signup2 = findViewById(R.id.btn_signup2);
        btn_login2 = findViewById(R.id.btn_login2);

        btn_signup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUser();
            }
        });

        btn_login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SignUp.this, Login.class);
                startActivity(intent1);
            }
        });


    }

    private void signupUser()
    {
        String username = ed_signupusername.getText().toString().trim();
        String email = ed_signupemail.getText().toString().trim();
        String password = ed_signuppassword.getText().toString().trim();
        String confirm = ed_signconpassword.getText().toString().trim();

        if (username.isEmpty()) {
            ed_signupusername.setError("Please fill in the username!");
            ed_signupusername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            ed_signupemail.setError("Please fill in the email address!");
            ed_signupemail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed_signupemail.setError("Please enter a valid email address!");
            ed_signupemail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            ed_signuppassword.setError("Please provide a password!");
            ed_signuppassword.requestFocus();
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            ed_signuppassword.setError("The password must be at least 6 characters containing at least 1 digit, 1 lower case, 1 upper case and 1 special character!");
            ed_signuppassword.requestFocus();
        }

        if (confirm.isEmpty()) {
            ed_signconpassword.setError("Please confirm your password!");
            ed_signconpassword.requestFocus();
            return;
        }

        if (!confirm.equals(password)) {
            ed_signconpassword.setError("The password is not the same!");
            ed_signconpassword.requestFocus();
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            User user = new User (username, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SignUp.this, "Sign Up successful!",Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(SignUp.this, "Sign Up failed! Try again!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(SignUp.this, "Sign Up failed! Try again lalalalaa!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}