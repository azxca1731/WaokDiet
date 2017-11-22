package org.androidtown.dietapp.Chart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.Menu.FoodAdapter;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zidru on 2017-09-18.
 */

public class ChartActivity extends Activity {
    private BottomNavigationView bottomNav;
    Intent AuthIntent;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);



        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);

        //리스너 선택된 날짜의 정보 전송
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent(ChartActivity.this, ViewCalendarActivity.class);
                intent.putExtra("Year", year);
                intent.putExtra("Month", month);
                intent.putExtra("Day", dayOfMonth);

                startActivity(intent);
            }
        });

        bottomNav = (BottomNavigationView)findViewById(R.id.bottom_nav_in_chart);

        // 현재 같이 쓰고있는 바텀내비게이션은 1개의 메뉴만 만들면 너무 휑해서
        // 야매로 아래와 같이 조치함.
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_to_all_view:
                        AuthIntent = new Intent(ChartActivity.this, ViewHistoryDataActivity.class);
                        startActivity(AuthIntent);
                        break;
                    case R.id.action_to_all_view_left:
                        AuthIntent = new Intent(ChartActivity.this, ViewHistoryDataActivity.class);
                        startActivity(AuthIntent);
                        break;
                    case R.id.action_to_all_view_right:
                        AuthIntent = new Intent(ChartActivity.this, ViewHistoryDataActivity.class);
                        startActivity(AuthIntent);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); finish();
    }
}
