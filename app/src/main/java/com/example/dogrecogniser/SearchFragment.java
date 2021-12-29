package com.example.dogrecogniser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    EditText ed_search;
    ListView result_list;
    Button btn_search;
    ArrayList<String> breedlist;
    ArrayList<String> urllist;




    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =inflater.inflate(R.layout.fragment_search, container, false);

        ed_search = view.findViewById(R.id.ed_search);
        btn_search = view.findViewById(R.id.btn_search);
        result_list = view.findViewById(R.id.result_list);



        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_search.getText().toString().matches("")){
                    Toast.makeText(getActivity(), "Please input some characters!",Toast.LENGTH_LONG).show();
                }else{
                    searchBreed();
                }
            }
        });



        return view;
    }

    private void searchBreed() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Breeds");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String searchKey = ed_search.getText().toString();
                breedlist = new ArrayList<>();
                urllist = new ArrayList<>();

                for (DataSnapshot breed:snapshot.getChildren()){
                   if(breed.getKey().toLowerCase().contains(searchKey.toLowerCase())){
                       breedlist.add(breed.getKey());
                       urllist.add(breed.child("Url").getValue().toString());
                   }
                }

                ArrayAdapter adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.listview_layout,breedlist);
                result_list.setAdapter(adapter);

                result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String breed = breedlist.get(position);

                        String url = urllist.get(position);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + uid);
                        databaseReference.child("history").child(breed).child("Url").setValue(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}