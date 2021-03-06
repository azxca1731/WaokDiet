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
    //음식 정보
    private TextView foodName;
    private TextView foodCategory;
    private TextView foodCal;
    private TextView foodCarbo;
    private TextView foodProtein;
    private TextView foodFat;

    private TextView foodSugar;
    private TextView foodNatrium;
    private TextView foodCholesterol;
    private TextView foodSaturatedFat;
    private TextView foodTransFat;
    //음식 정보 끝

    //xml 버튼 이미지
    private ImageView img;
    private Button add;
    private Button cancel;
    //버튼 이미지 끝

    //데이터 베이스 관련
    private Context context;
    private StorageReference storageReference;
    private DatabaseReference historyRef;
    private FoodItem food;
    //데이터 베이스 끝
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
        foodCategory=(TextView)findViewById(R.id.foodCategory);
        foodCal=(TextView)findViewById(R.id.foodCal);
        foodCarbo=(TextView)findViewById(R.id.foodCarbohydrate);
        foodProtein=(TextView)findViewById(R.id.foodProtein);
        foodFat=(TextView)findViewById(R.id.foodFat);
        foodSugar=(TextView)findViewById(R.id.foodSugar);
        foodNatrium=(TextView)findViewById(R.id.foodNatrium);
        foodCholesterol=(TextView)findViewById(R.id.foodCholesterol);
        foodSaturatedFat=(TextView)findViewById(R.id.foodSaturatedFat);
        foodTransFat=(TextView)findViewById(R.id.foodTransFat);
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
      foodCategory.setText(food.getCategory());
      foodCal.setText("칼로리: " + String.valueOf(food.getCalorie())+" Kcal");
      foodProtein.setText("단백질: " + String.valueOf(food.getProtein())+ " g");
      foodCarbo.setText("탄수화물: " + String.valueOf(food.getCarbohydrate())+" g");
      foodFat.setText("지방: " + String.valueOf(food.getFat()) +" g");
      foodSugar.setText("당: " + String.valueOf(food.getSugar()) +" g");
      foodNatrium.setText("나트륨: " + String.valueOf(food.getNatrium()) +" mg");
      foodCholesterol.setText("콜레스테롤: " + String.valueOf(food.getCholesterol()) +" mg");
      foodSaturatedFat.setText("포화지방: " + String.valueOf(food.getSaturatedFat()) +" g");
      foodTransFat.setText("트랜스지방: " + String.valueOf(food.getTransFat()) +" g");
      Glide.with(context)
              .using(new FirebaseImageLoader())
              .load(storageReference.child("foodImage/" + food.getUid() + ".png"))
              .override(145,75)
              .into(img);
    }

}