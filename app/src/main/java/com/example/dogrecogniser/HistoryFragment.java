package com.example.dogrecogniser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    ArrayList<String> breedlist;
    ArrayList<String> urllist;
    ListView historylist;

    public HistoryFragment() {
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
        View view =inflater.inflate(R.layout.fragment_history, container, false);

        historylist = view.findViewById(R.id.historylist);

        searchHistory();

        return view;
    }

    private void searchHistory() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users/" + uid + "/history");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                breedlist = new ArrayList<>();
                urllist = new ArrayList<>();

                historylist.setAdapter(null);
                for (DataSnapshot breed:snapshot.getChildren()){
                        breedlist.add(breed.getKey());
                        urllist.add(breed.child("Url").getValue().toString());
                }

                ArrayAdapter adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.listview_layout,breedlist);
                historylist.setAdapter(adapter);

                historylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String url = urllist.get(position);
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