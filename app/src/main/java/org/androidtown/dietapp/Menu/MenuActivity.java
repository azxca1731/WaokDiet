package org.androidtown.dietapp.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.UUID;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher{
    private FirebaseDatabase database;
    private DatabaseReference userHistoryRef;
    private DatabaseReference foodRef;
    private View.OnClickListener listener;
    private Button buttonSearch;
    private Button buttonAddMenu;
    private EditText edit;
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private FirebaseUser user;
    private String dateStr;
    public ArrayList<FoodItem> foodItemList;
    public ArrayList<FoodItem> barcodeItemList;



    //자료구조 데모 선언 시작
    private DataStructure datastructure;
    private ArrayList<FoodItem> searchedItemList;
    //자료구조 데모 선언 끝

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();
    }

    private void init(){
        //firebase init
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        dateStr = getIntent().getStringExtra("dateStr");
        database = FirebaseDatabase.getInstance();
        userHistoryRef =database.getReference().child("userHistory").child(user.getUid()).child(dateStr);
        foodRef = database.getReference().child("food");
        //firebase init end
        barcodeItemList= new ArrayList<FoodItem>();
        foodItemList = new ArrayList<FoodItem>();


        //자료구조 데모 init
        datastructure=DataStructure.getInstance();
        datastructure.setFoodList(foodItemList);
        datastructure.setBarcodeList(barcodeItemList);
        //자료구조 데모 init 끝

        recyclerView=(RecyclerView)findViewById(R.id.user_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lim);
        adapter = new FoodAdapter(foodItemList);
        adapter.setHistoryRef(userHistoryRef);
        recyclerView.setAdapter(adapter);
        updateFoodList();

        //button
        buttonSearch=(Button)findViewById(R.id.buttonSearch);
        buttonAddMenu=(Button)findViewById(R.id.buttonAddMenu);
        edit=(EditText)findViewById(R.id.edit);

        buttonAddMenu.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);
        edit.addTextChangedListener(this);
    }

    private  void updateFoodList(){
        if(foodRef == null){
            return;
        }
        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                foodItemList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FoodItem foodItem = snapshot.getValue(FoodItem.class);
                    if(foodItem!= null) {
                        foodItemList.add(foodItem);
                         if(foodItem.getBarcode()!=""){
                             //바코드가 있는 음식의 경우 받아온다.
                            barcodeItemList.add(foodItem);
                        }

                    }
                }
                //sort부분 시작
                //data의 수정이 일어날때 마다 sorting 함
                datastructure.sort();
                datastructure.barcodeSort();
                //sort부분 끝
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            // QR코드/ 바코드를 스캔한 결과
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            // result.getFormatName() : 바코드 종류
            // result.getContents() : 바코드 값
            if (result.getContents() == null) {
                return;
            }

            final FoodItem selectedItem = datastructure.binarySearch(result.getContents());
            Log.d("TAG", "onActivityResult: "+selectedItem+"한국말");
            if (selectedItem == null) {
                Toast.makeText(getApplicationContext(), "찾지 못하였습니다", Toast.LENGTH_LONG).show();
            }

            //스낵바 부분
            Snackbar.make(this.getWindow().getDecorView(), selectedItem.getBarcode(), Snackbar.LENGTH_LONG)
                    .setAction("add to history", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (userHistoryRef != null) {
                                String key;
                                key = UUID.randomUUID().toString();
                                selectedItem.setKey(key);
                                userHistoryRef.child(key).setValue(selectedItem);
                            }
                        }
                    }).show();
        }
    }




    /*
        IMPLEMENTS METHOD ON IT
     */

    ///Watcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        searchedItemList=datastructure.search(s.toString());
        adapter.setFoodList(searchedItemList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    //OnClickListener
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonSearch:
                new IntentIntegrator(MenuActivity.this).initiateScan();
                break;
            case R.id.buttonAddMenu:
                Intent addIntent = new Intent(MenuActivity.this,ItemAddActivity.class);
                startActivity(addIntent);
            case R.id.user_list:
        }

    }
}