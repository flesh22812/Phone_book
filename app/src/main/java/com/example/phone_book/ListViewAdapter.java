package com.example.phone_book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    private String messageAdd = "Контакт добавлен в избранное";
    private String messageDelete = "Контакт убран из избранного";
    Activity activity;
    List<User> listUsers;
    LayoutInflater inflater;
    View   itemView ;

    private FirebaseStorage storage= FirebaseStorage.getInstance();;

    public ListViewAdapter(Activity activity, List<User> listUsers) {
        this.activity = activity;
        this.listUsers = listUsers;
    }
    DatabaseReference  mDatabase= FirebaseDatabase.getInstance().getReference("User").child("User");;

    @Override
    public int getCount() {
        return listUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return listUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override                                      ////////// creating listView+
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) activity
                .getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

         itemView = inflater.inflate(R.layout.list_item,null);

      ImageView    avatar= (ImageView)  itemView.findViewById(R.id.imageView3);
        TextView txtUser = (TextView) itemView.findViewById(R.id.txt_name);
        TextView txtNumber = (TextView) itemView.findViewById(R.id.txt_number);
        ImageView    del= (ImageView)  itemView.findViewById(R.id.imageView2);
        ImageView    star= (ImageView)  itemView.findViewById(R.id.imageView);



        itemView.setPadding(0,0,0,0);



avatar.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+listUsers.get(i).number));
activity.startActivity(intent2);
    }
});

        ////////////////////Delete supporting
        del.setImageResource(R.mipmap.ic_delete );

del.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        User user = (User) listUsers.get(i);
        mDatabase
                .child(user.getId())
                .removeValue();

        storage.getReference().child("images/"+user.getId()).delete();
    }
});
///////////////////////Sorting
        listUsers.sort(Comparator.comparing(User::getName));
        listUsers.sort(Comparator.comparing(User::isFlag).reversed());



///////////////////////Star supporting
    star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = (User) listUsers.get(i);
                if(listUsers.get(i).flag==0){
                    mDatabase
                            .child(user.getId())
                            .child("flag")
                            .setValue(1);
                    Toast.makeText(activity, messageAdd, Toast.LENGTH_SHORT).show();
                }
                else {   mDatabase
                        .child(user.getId())
                        .child("flag")
                        .setValue(0);
                    Toast.makeText(activity, messageDelete, Toast.LENGTH_SHORT).show();}
            }

        }
        );


if(listUsers.get(i).isFlag()==1)
{
    star.setImageResource(R.mipmap.ic_star_full );
}
else{ star.setImageResource(R.mipmap.ic_star_empty );}
        txtUser.setText(listUsers.get(i).getName());
        txtNumber.setText(listUsers.get(i).getNumber());
        itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Glide.with(activity).load(listUsers.get(i).url).into(avatar);
        return  itemView;
    }


}


