package com.example.dogrecogniser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.UserNotAuthenticatedException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileTemp extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference reference;
    String userID;

    TextView tv_greeting, tv_username, tv_emailaddress;
    Button btn_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_temp);

        tv_greeting = findViewById(R.id.tv_greeting);
        tv_username = findViewById(R.id.tv_username);
        tv_emailaddress = findViewById(R.id.tv_emailaddress);
        btn_delete = findViewById(R.id.btn_delete);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null)
                {
                    String username = userProfile.username;
                    String email = userProfile.email;

                    tv_greeting.setText("Welcome, "+ username+" !");
                    tv_username.setText(username);
                    tv_emailaddress.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ProfileTemp.this,"Something wrong! Try again!",Toast.LENGTH_LONG).show();

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileTemp.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your account from the application and you won't be able to access the application!");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    deleteUser();
                                    Toast.makeText(ProfileTemp.this,"Account Deleted",Toast.LENGTH_LONG).show();

                                    Intent intent3 = new Intent(ProfileTemp.this, Login.class);
                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent3);
                                }
                                else
                                {
                                    Toast.makeText(ProfileTemp.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }

                            private void deleteUser()
                            {
                                    DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                                    user.removeValue();
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

    }




}