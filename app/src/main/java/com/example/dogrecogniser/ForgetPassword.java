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
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    EditText ed_forgetemail;
    Button btn_forget;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ed_forgetemail = findViewById(R.id.ed_forgetemail);
        btn_forget = findViewById(R.id.btn_forget);

        auth = FirebaseAuth.getInstance();

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                forgetPassword();
            }
        });
    }

    private void forgetPassword()
    {
        String email = ed_forgetemail.getText().toString().trim();

        if (email.isEmpty()) {
            ed_forgetemail.setError("Please fill in the username!");
            ed_forgetemail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed_forgetemail.setError("Please enter a valid email");
            ed_forgetemail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            ed_forgetemail.setText("");
                            Intent intent = new Intent(ForgetPassword.this, Login.class);
                            startActivity(intent);
                            Toast.makeText(ForgetPassword.this,"Check your email to reset password!",Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            Toast.makeText(ForgetPassword.this,"Email not registered! Try again!",Toast.LENGTH_LONG).show();
                        }


                    }
                });

    }

}