package org.androidtown.dietapp.Chart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.androidtown.dietapp.R;


/**
 * Created by zidru on 2017-10-07.
 */


public class ViewHistoryDataActivity extends AppCompatActivity {

    // 이동할 화면들
    ViewAllCalendarFragment view_line;
    ViewAllCalendarFragment_byPie view_pie;
    ViewUserInterestFragment view_interst;

    Bundle bundle = new Bundle(1);
    Intent intent;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference myHistoryRef;

    // bottomnav
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        String uid = intent.getExtras().getString("uid");

        myHistoryRef = database.getReference().child("userHistory").child(uid);

        if(myHistoryRef != null){
        }
        else{
            Toast.makeText(getApplicationContext(), "음식을 섭취한 내역이 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        setContentView(R.layout.activity_view_history_of_user);

        bundle.putString("uid", uid);

        // 프래그먼트들
        view_line = new ViewAllCalendarFragment();
        view_pie = new ViewAllCalendarFragment_byPie();
        view_interst = new ViewUserInterestFragment();

        // 초기화면
        view_line.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_of_historyview, view_line).commit();

        // bottomnavi 설정
        bottomNav = (BottomNavigationView)findViewById(R.id.bottom_nav_in_chartview);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_viewCalorie:
                        view_line.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_of_historyview, view_line).commit();
                        break;
                    case R.id.action_viewPie:
                        view_pie.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_of_historyview, view_pie).commit();
                        break;
                    case R.id.action_viewInterest:
                        view_interst.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_of_historyview, view_interst).commit();
                        break;
                }
                return true;
            }
        });

    }
}
