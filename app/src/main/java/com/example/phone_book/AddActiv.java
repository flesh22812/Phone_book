package com.example.phone_book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddActiv extends AppCompatActivity {
    private EditText subscriberName, phoneNumber;
    private CheckBox editFlag;
    private Button button;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    ;
    private StorageReference storageReference;


    private Button btnChoose;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    String pandaUrl = "https://firebasestorage.googleapis.com/v0/b/phonebook-cefb8.appspot.com/o/images%2F1593988868_180_0_2911_2048_1920x0_80_0_0_ba53ac996ef36e96e26a80caa04dc656.jpg?alt=media&token=bb26a04d-6888-4cf9-a138-3a2c3131551c";
    private String USER_KEY = "User";
    private String message = "Этот номер уже у вас записан";

    @Override             ///////// initialization of AddView
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setTitle("Новый контакт");
        init();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void init() {

        subscriberName = findViewById(R.id.editSubscriber_name);
        phoneNumber = findViewById(R.id.editPhone_Number);
        editFlag = findViewById(R.id.checkBox);
        button = findViewById(R.id.button);
        mDatabase = FirebaseDatabase.getInstance().getReference(USER_KEY);
        button.setEnabled(false);
        EditText[] edList = {subscriberName, phoneNumber};
        BlockEditText textWatcher = new BlockEditText(edList, button, phoneNumber);
        for (EditText editText : edList) editText.addTextChangedListener(textWatcher);
        storageReference = storage.getReference();
        onclickChoose();

    }

    public void onclickChoose() {
        btnChoose = (Button) findViewById(R.id.btnChoose);
        imageView = (ImageView) findViewById(R.id.imgView);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }

        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите"), PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /////// Saves data to firebase
    public void onClickSave(View view) {

        String name = subscriberName.getText().toString();
        String phone = phoneNumber.getText().toString();
        String url = "empty";
        boolean boolFlag = editFlag.isChecked();
        int flag = boolFlag ? 1 : 0;
        User newUser = new User(UUID.randomUUID().toString(), name, phone, flag, url);

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Query queryToGetData = dbRef.child("User").child("User")

                .orderByChild("number").equalTo(phone);

        queryToGetData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {

                    mDatabase.child("User").child(newUser.getId()).setValue(newUser);


                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        uploadImage(newUser);
        //finishActivity(AddActiv);

    }

    private void uploadImage(User newUser) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + newUser.id);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddActiv.this, "Uploaded", Toast.LENGTH_SHORT).show();


                            //submitted sucessfully
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newUser.setUrl(uri.toString());

                                    mDatabase.child("User").child(newUser.getId()).setValue(newUser);

                                }
                            });
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                            Toast.makeText(AddActiv.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });

        } else {
            newUser.setUrl(pandaUrl);
            mDatabase.child("User").child(newUser.getId()).setValue(newUser);
            finish();
        }
    }


}
