package org.androidtown.dietapp.Main;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidtown.dietapp.Auth.AuthMainActivity;
import org.androidtown.dietapp.Auth.UserInfoActivity;
import org.androidtown.dietapp.Chart.ChartActivity;
import org.androidtown.dietapp.Chart.ViewFriendActivity;
import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.Menu.MenuActivity;
import org.androidtown.dietapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "!!!!!!!!!!MAIN!!!!!!!!";
    //날짜 관련
    private long now;
    private SimpleDateFormat dateFormat;
    private Date date;
    private String dateStr;

    //프로그레스바 관련
    private DatabaseReference basicCalRef;
    int todayCal;
    private int basicCal;
    private int progress;
    //프로그레스바 끝

    //리사이클러 뷰 시작
    private RecyclerView recyclerView;
    private List<FoodItem> historyList;
    private HistoryAdapter adapter;
    //리사이클러 뷰 끝

    //데이터베이스 시작
    private FirebaseDatabase database;
    private DatabaseReference myHistoryRef;
    private FirebaseUser user;
    //데이터베이스 끝

    //레이아웃
    private BottomNavigationView bottomNav;
    private ProgressBar calorie_pbar;
    private TextView percentage_view;
    private View.OnClickListener listener;
    //레이아웃 끝

    //기타 변수
    public static Context mainContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDate();


        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser(); //위로뺌;
        if(user==null) {
            Intent AuthIntent= new Intent(MainActivity.this, AuthMainActivity.class);
            startActivity(AuthIntent);
            user=mAuth.getCurrentUser();
        }else {
            initDatabase();
        }

        mainContext=this;



        //리사이클러뷰 시작
        historyList=new ArrayList<>();
        recyclerView=(RecyclerView)findViewById(R.id.user_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lim);
        //리사이클러뷰 끝\


        //데이터 베이스 시작
        if(user!=mAuth.getCurrentUser())initDatabase();
        //데이터 베이스 끝

        bottomNav = (BottomNavigationView)findViewById(R.id.bottom_nav);
        calorie_pbar=(ProgressBar)findViewById(R.id.pbar_calorie);
        percentage_view=(TextView)findViewById(R.id.view_percentage);

        adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);
        updateHistoryList();
        progress=0;
        setProgress();

        adapter.setHistoryRef(myHistoryRef);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_menu:

                        Intent menuIntent = new Intent(MainActivity.this,MenuActivity.class);
                        menuIntent.putExtra("dateStr",dateStr);
                        startActivity(menuIntent);
                        break;
                    case R.id.action_userInfo:
                        Intent AuthIntent = new Intent(MainActivity.this,UserInfoActivity.class);
                        startActivity(AuthIntent);
                        initDatabase();
                        break;
                    case R.id.action_chart:
                        Intent chartIntent = new Intent(MainActivity.this,ChartActivity.class);
                        startActivity(chartIntent);
                        break;
                    case R.id.action_friend:
                        Intent friendIntent = new Intent(MainActivity.this,ViewFriendActivity.class);
                        startActivity(friendIntent);
                }
                return true;
            }
        });
    }


    private void setProgress()
    {
        if(basicCalRef==null){
            return;
        }
        basicCalRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                basicCal = dataSnapshot.getValue(int.class);
                calculateTodayCal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void calculateTodayCal(){
        if(basicCal==0)return;
        todayCal=0;
        for(int i=0;i<historyList.size();i++){
            int cal = historyList.get(i).getCalorie();
            todayCal = todayCal+cal;
        }
        calorie_pbar.setMax(100);
        progress = todayCal*100;
        progress = progress/basicCal;


        calorie_pbar.setProgress(progress);
        percentage_view.setText(progress + "%");
        if(progress <= 100){
            calorie_pbar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFFACC35")));
        }else{
            calorie_pbar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FF1100")));
        }
    }

    private void updateHistoryList() {
        if(myHistoryRef==null){
            return;
        }
        myHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                historyList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodItem food = snapshot.getValue(FoodItem.class);
                    historyList.add(food);
                }
                calculateTodayCal();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initDatabase(){
        database = FirebaseDatabase.getInstance();
        basicCalRef = database.getReference().child("user").child(user.getUid()).child("basicCalorie");
        myHistoryRef = database.getReference().child("userHistory").child(user.getUid()).child(dateStr);
    }

    private void getDate(){
        now=System.currentTimeMillis();
        dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        date = new Date(now);
        dateStr =  dateFormat.format(date);
    }

    @Override
    protected void onPostResume() {
        if(user!=FirebaseAuth.getInstance().getCurrentUser()){
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(user==null) {
                Intent AuthIntent= new Intent(MainActivity.this, AuthMainActivity.class);
                startActivity(AuthIntent);
                user=FirebaseAuth.getInstance().getCurrentUser();
            }else {
                getDate();
                initDatabase();
                adapter.setHistoryRef(myHistoryRef);
                updateHistoryList();
                setProgress();
            }
        }

        super.onPostResume();
    }
}