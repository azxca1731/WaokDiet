package org.androidtown.dietapp.Chart;

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

public class InterestDialog extends Dialog {

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
    private FoodItem food;
    //데이터 베이스 끝
    public InterestDialog(@NonNull Context context, FoodItem food) {
        super(context);
        this.context=context;
        this.food = food;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest_dialog);
        img=(ImageView)findViewById(R.id.i_foodImg);
        foodName=(TextView)findViewById(R.id.i_foodName);
        foodCategory=(TextView)findViewById(R.id.i_foodCategory);
        foodCal=(TextView)findViewById(R.id.i_foodCal);
        foodCarbo=(TextView)findViewById(R.id.i_foodCarbohydrate);
        foodProtein=(TextView)findViewById(R.id.i_foodProtein);
        foodFat=(TextView)findViewById(R.id.i_foodFat);
        foodSugar=(TextView)findViewById(R.id.i_foodSugar);
        foodNatrium=(TextView)findViewById(R.id.i_foodNatrium);
        foodCholesterol=(TextView)findViewById(R.id.i_foodCholesterol);
        foodSaturatedFat=(TextView)findViewById(R.id.i_foodSaturatedFat);
        foodTransFat=(TextView)findViewById(R.id.i_foodTransFat);
        cancel=(Button)findViewById((R.id.i_cancel));

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.i_cancel:
                        cancel();
                        break;
                }
            }
        };
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
      foodSaturatedFat.setText("지방: " + String.valueOf(food.getSaturatedFat()) +" g");
      foodTransFat.setText("지방: " + String.valueOf(food.getTransFat()) +" g");
      Glide.with(context)
              .using(new FirebaseImageLoader())
              .load(storageReference.child("foodImage/" + food.getUid() + ".png"))
              .override(145,75)
              .into(img);
    }

}