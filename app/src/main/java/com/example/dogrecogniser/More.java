package com.example.dogrecogniser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class More extends AppCompatActivity {

    TextView tv_fci, tv_akc, tv_link1, tv_link2, tv_link3, tv_link4, tv_link5, tv_link6, tv_link7, tv_link8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        tv_fci = findViewById(R.id.tv_fci);
        tv_akc = findViewById(R.id.tv_akc);
        tv_link1 = findViewById(R.id.tv_link1);
        tv_link2 = findViewById(R.id.tv_link2);
        tv_link3 = findViewById(R.id.tv_link3);
        tv_link4 = findViewById(R.id.tv_link4);
        tv_link5 = findViewById(R.id.tv_link5);
        tv_link6 = findViewById(R.id.tv_link6);
        tv_link7 = findViewById(R.id.tv_link7);
        tv_link8 = findViewById(R.id.tv_link8);

        tv_fci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fci.be/en/"));
                startActivity(intent);
            }
        });

        tv_akc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.akc.org/"));
                startActivity(intent);
            }
        });

        tv_link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/U-QYSof6x48"));
                startActivity(intent);
            }
        });

        tv_link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=peUVLEUj-AM"));
                startActivity(intent);
            }
        });

        tv_link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Ro0KH6hAnHA"));
                startActivity(intent);
            }
        });

        tv_link4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=-M7Z7Ri_Xis"));
                startActivity(intent);
            }
        });

        tv_link5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/5mj7xjtlOGI"));
                startActivity(intent);
            }
        });

        tv_link6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/5C39A9UU720"));
                startActivity(intent);
            }
        });

        tv_link7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=1oDGa2yPb2g"));
                startActivity(intent);
            }
        });

        tv_link8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=gbYrn9Q09wc"));
                startActivity(intent);
            }
        });


    }
}