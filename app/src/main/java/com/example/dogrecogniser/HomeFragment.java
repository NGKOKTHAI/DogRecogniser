package com.example.dogrecogniser;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CaptureResult;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogrecogniser.ml.TfliteModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.dogrecogniser.R.*;


public class HomeFragment extends Fragment {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    Button btn_take,btn_gallery,btn_upload;
    ImageView imageView;
    TextView breed;
    String currentPhotoPath;
    Bitmap img;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(layout.fragment_home, container, false);

        btn_take = view.findViewById(id.btn_take);
        btn_gallery = view.findViewById(id.btn_gallery);
        btn_upload = view.findViewById(id.btn_upload);
        imageView = view.findViewById(id.imageView);
        breed = view.findViewById(id.breed);



        //button take photo
        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breed.setVisibility(View.GONE);
                askCameraPermissions();
            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });


        //button upload
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(img != null){
                    img = Bitmap.createScaledBitmap(img, 7,7,true);

                    try {
                        TfliteModel model = TfliteModel.newInstance(getActivity().getApplicationContext());

                        // Creates inputs for reference.
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 7, 7, 512}, DataType.FLOAT32);

                        Log.d("shape", inputFeature0.getBuffer().toString());
                        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                        tensorImage.load(img);
                        ByteBuffer byteBuffer = tensorImage.getBuffer();

                        Log.d("shape", byteBuffer.toString());

                        Log.d("shape", inputFeature0.getBuffer().toString());
                        inputFeature0.loadBuffer(byteBuffer);

                        // Runs model inference and gets result.
                        TfliteModel.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                        // Releases model resources if no longer used.
                        model.close();

                        breed.setVisibility(View.VISIBLE);
                        breed.setText("This dog breed looks like " + outputFeature0);


                    } catch (IOException e) {
                        // TODO Handle the exception
                    }
                }else{
                    Toast.makeText(getActivity(),"Please insert image!",Toast.LENGTH_LONG).show();
                }

            }

        });


        //breed result
        breed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Ref = breed.getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Breeds");
                databaseReference.child(Ref).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String URL = snapshot.child("Url").getValue().toString();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
            }
        });

        return view;
    }



    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            dispatchTakePictureIntent();
        }
    }


    //Request camera permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Check permission
        if(requestCode == CAMERA_PERM_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(getActivity(),"Camera Permission is required",Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(f));
                Log.d("tag","Absolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);

                //convert to bitmap
                try{
                    img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),contentUri);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag","onActivityResult: Gallery Image Uri: " + imageFileName);
                imageView.setImageURI(contentUri);

                try{
                    img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),contentUri);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    //create a collision-resistant file name
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   /*prefix*/
                ".jpg",          /*suffix*/
                storageDir       /*directory*/
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //method available to create a file for the photo
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }

}