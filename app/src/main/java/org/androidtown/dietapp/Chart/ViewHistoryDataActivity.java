package org.androidtown.dietapp.Chart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.androidtown.dietapp.R;
import org.w3c.dom.Text;


/**
 * Created by zidru on 2017-10-07.
 */


public class ViewHistoryDataActivity extends AppCompatActivity {

    // 이동할 화면들
    ViewAllCalendarActivity view_line;
    ViewAllCalendarActivity_byPie view_pie;
    ViewUserInterestActivity view_interst;

    // bottomnav
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history_of_user);

        // 프래그먼트들
        view_line = new ViewAllCalendarActivity();
        view_pie = new ViewAllCalendarActivity_byPie();
        view_interst = new ViewUserInterestActivity();

        // 초기화면
        getSupportFragmentManager().beginTransaction().replace(R.id.container_of_historyview, view_line).commit();

        // bottomnavi 설정
        bottomNav = (BottomNavigationView)findViewById(R.id.bottom_nav_in_chartview);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_viewCalorie:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_of_historyview, view_line).commit();
                        break;
                    case R.id.action_viewPie:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_of_historyview, view_pie).commit();
                        break;
                    case R.id.action_viewInterest:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_of_historyview, view_interst).commit();
                        break;
                }
                return true;
            }
        });

    }
}
