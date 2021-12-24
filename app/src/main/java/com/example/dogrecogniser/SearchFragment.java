package com.example.dogrecogniser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    AutoCompleteTextView ed_search;
    Button btn_search;
    ListView result_list;
    DatabaseReference breed;

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

         breed = FirebaseDatabase.getInstance().getReference("Breeds");

        ed_search = view.findViewById(R.id.ed_search);
        btn_search = view.findViewById(R.id.btn_search);
        result_list = view.findViewById(R.id.result_list);

        ValueEventListener event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        breed.addListenerForSingleValueEvent(event);

        return view;
    }

    private void populateSearch(DataSnapshot snapshot){
        ArrayList<String> names = new ArrayList<>();
        if(snapshot.exists())
        {
            for(DataSnapshot ds:snapshot.getChildren())
            {
                String name = ds.child("Url").getValue(String.class);
                names.add(name);
            }

            ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,names);
            ed_search.setAdapter(adapter);
            ed_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String name = ed_search.getText().toString();
                    searchUser(name);
                }
            });

        }else{
            Toast.makeText(getActivity(),"No data found!",Toast.LENGTH_LONG).show();
        }
    }

    private void searchUser(String name) {
    }

}