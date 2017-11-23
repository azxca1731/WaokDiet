package org.androidtown.dietapp.Menu;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.util.UUID;

/**
 * Created by Tae-Hyun on 2017-11-21.
 */

public class CustomDialog extends Dialog {

    private View.OnClickListener clickListener;
    private ImageView img;
    private TextView foodName;
    private TextView foodCal;
    private TextView foodCarbo;
    private TextView foodProtein;
    private TextView foodFat;
    private String key;
    private Context context;
    private StorageReference storageReference;

    /*
    private TextView foodName;
    private TextView foodName;
    private TextView foodName;
    private TextView foodName;
    */
    private Button add;
    private Button cancel;
    private FoodItem food;
    private DatabaseReference historyRef;

    public CustomDialog(@NonNull Context context, FoodItem food, DatabaseReference historyRef) {
        super(context);
        this.context=context;
        this.food = food;
        this.historyRef = historyRef;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);
        img=(ImageView)findViewById(R.id.foodImg);
        foodName=(TextView)findViewById(R.id.foodName);
        foodCal=(TextView)findViewById(R.id.foodCal);
        foodCarbo=(TextView)findViewById(R.id.foodCarbohydrate);
        foodProtein=(TextView)findViewById(R.id.foodProtein);
        foodFat=(TextView)findViewById(R.id.foodFat);
        add=(Button)findViewById((R.id.add));
        cancel=(Button)findViewById((R.id.cancel));

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.add:
                        if (historyRef != null) {
                            String key = UUID.randomUUID().toString();
                            food.setKey(key);
                            historyRef.child(key).setValue(food);
                        }
                        cancel();
                        break;
                    case R.id.cancel:
                        cancel();
                        break;
                }

            }
        };
        add.setOnClickListener(clickListener);
        cancel.setOnClickListener(clickListener);

        init();
    }

    public void init(){
      foodName.setText(food.getName());
      foodCal.setText("칼로리: " + String.valueOf(food.getCalorie())+" Kcal");
      foodProtein.setText("단백질: " + String.valueOf(food.getProtein())+ " g");
      foodCarbo.setText("탄수화물: " + String.valueOf(food.getCarbohydrate())+" g");
      foodFat.setText("지방: " + String.valueOf(food.getFat()) +" g");
      Glide.with(context)
              .using(new FirebaseImageLoader())
              .load(storageReference.child("foodImage/" + food.getUid() + ".png"))
              .override(145,75)
              .into(img);
    }

}