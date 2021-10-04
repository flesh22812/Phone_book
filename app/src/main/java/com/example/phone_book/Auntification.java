package com.example.phone_book;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Auntification extends AppCompatActivity {
 public String userPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auntification);
        Button button= findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText= findViewById(R.id.editTextPhone);
                userPhone =editText.getText().toString();
                Intent intent = new Intent(Auntification.this, MainActivity.class);
                intent.putExtra("userPhone", userPhone);
                startActivity(intent);
            }
        });
    }
}

