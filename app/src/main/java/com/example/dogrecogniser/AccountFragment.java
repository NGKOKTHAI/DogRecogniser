package com.example.dogrecogniser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AccountFragment extends Fragment {


    public AccountFragment() {
        // Required empty public constructor
    }

    FirebaseUser user;
    DatabaseReference reference;
    String userID;

    TextView tv_greeting, tv_username, tv_emailaddress;
    Button btn_delete, btn_logout,btn_more;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account, container, false);

        tv_greeting = view.findViewById(R.id.tv_greeting);
        tv_username = view.findViewById(R.id.tv_username);
        tv_emailaddress = view.findViewById(R.id.tv_emailaddress);
        btn_delete = view.findViewById(R.id.btn_delete);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_more = view.findViewById(R.id.btn_more);

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

                Toast.makeText(getActivity(),"Something wrong! Try again!",Toast.LENGTH_LONG).show();

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
                                    Toast.makeText(getActivity(),"Account Deleted",Toast.LENGTH_LONG).show();

                                    Intent intent3 = new Intent(getActivity(), Login.class);
                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent3);
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), task.getException().getMessage(),Toast.LENGTH_LONG).show();
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

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Log Out");
                dialog.setMessage("Are you sure to log out?");
                dialog.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();

                        Intent intent3 = new Intent(getActivity(), Login.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent3);

                        Toast.makeText(getActivity(),"Logout successful!",Toast.LENGTH_LONG).show();
                        finish();
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

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(),More.class));
            }
        });

        return view;

    }

    private void finish()
    {
        finish();
    }


}