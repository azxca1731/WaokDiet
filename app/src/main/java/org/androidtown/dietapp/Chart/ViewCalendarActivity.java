package org.androidtown.dietapp.Chart;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.CircleGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.circlegraph.CircleGraph;
import com.handstudio.android.hzgrapherlib.vo.circlegraph.CircleGraphVO;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zidru on 2017-09-18.
 */

public class ViewCalendarActivity extends AppCompatActivity {
    // 파이어베이스관련
    private DatabaseReference UserHistory;
    private DatabaseReference mDatabase;

    //탄단지
    private float rat_carbo, rat_protein, rat_fat;
    private int carbo,protein,fat;

    // 파이어베이스에 검색할 날짜
    private String date;

    // 탄단지 합계및, 초기화
    private int sum = 0;

    //  뷰
    private ViewGroup layoutGraphView;
    private TextView textView;

    // 리사이클러뷰 관련
    private ChartAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<FoodItem> theDayfoods;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // 뷰 설정
        setContentView(R.layout.activity_view_calender);
        textView = (TextView)findViewById(R.id.message_to_calender_viewer);
        layoutGraphView = (ViewGroup) findViewById(R.id.pie_chart);

        theDayfoods = new ArrayList<>();

        // 파이어베이스 설정
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // 레퍼런스설정
        DatabaseReference userHistoryRef = mDatabase.child("userHistory").child(user.getUid());

        // 인텐트로 넘어온 날짜값을 받아 문자열로 제작
        Intent intent = getIntent();
        int year = intent.getExtras().getInt("Year");
        int month = intent.getExtras().getInt("Month")+1;
        int day = intent.getExtras().getInt("Day");

        if(month<10 && day<10){
            date = String.valueOf(year)+"0"+String.valueOf(month)+"0"+String.valueOf(day);
        }else if(month<10 && day>=10){
            date = String.valueOf(year)+"0"+String.valueOf(month)+String.valueOf(day);
        }else if(day<10){
            date = String.valueOf(year)+String.valueOf(month)+"0"+String.valueOf(day);
        }else date = String.valueOf(year)+String.valueOf(month)+String.valueOf(day);

        // 완성된 날짜를 이용한 레퍼런스완성
        UserHistory = userHistoryRef.child(date);

        // 리사이클러뷰
        recyclerView = (RecyclerView)findViewById(R.id.calendar_list);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(lim);
        adapter = new ChartAdapter(theDayfoods);
        recyclerView.setAdapter(adapter);

        set_chart();
    }

    // 화면출력
    private void set_chart(){
        if(UserHistory == null){
        }else {
        UserHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 탄단지 초기화 및 리스트 초기화
                setzero(); theDayfoods.clear();
                // 해당날짜에 데이터가 있을경우에만 fooditem을 받아와 리스트에 등록.
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FoodItem foodItem = snapshot.getValue(FoodItem.class);
                        theDayfoods.add(foodItem);
                        setCarbo(getCarbo() + foodItem.getCarbohydrate());
                        setProtein(getProtein() + foodItem.getProtein());
                        setFat(getFat() + foodItem.getFat());
                        setSum(sum + getCarbo() + getFat() + getProtein());
                    }
                    setCircleGraph();
                    adapter.notifyDataSetChanged();

                    // 비율설정
                    rat_carbo =( (float)getCarbo()/ (float)getSum())*100;
                    rat_fat = ( (float)getFat()/ (float)getSum())*100;
                    rat_protein = ( (float)getProtein()/ (float)getSum())*100;

                    theAdvise();
                }else{
                    Toast.makeText(getApplicationContext(), "선택하신 날짜에는 먹은 음식이 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        }
    }

    // 조언
    private void theAdvise(){
        if(rat_carbo>=45){
            textView.setText("탄수화물을 너무 많이 섭취하고 있습니다!");
        }else if(rat_protein>=60){
            textView.setText("단백질을 너무 많이 섭취하고 있습니다!");
        }else if(rat_fat>=35){
            textView.setText("지방을 너무 많이 섭취하고 있습니다!");
        }else if(rat_protein<40){
            textView.setText("단백질의 섭취량이 너무 낮습니다.");
        }else if(rat_fat<15){
            textView.setText("지방의 섭취량이 너무 낮습니다.");
        }else if(rat_carbo<25) {
            textView.setText("탄수화물의 섭취량이 너무 낮습니다.");
        }else textView.setText("적당히 균형잡힌 식단이군요.");
    }

    // draw cicle graph
    private void setCircleGraph() {
        CircleGraphVO vo = makeLineGraphAllSetting();
        layoutGraphView.addView(new CircleGraphView(this,vo));
    }

    // make circle graph
    private CircleGraphVO makeLineGraphAllSetting() {
        //BASIC LAYOUT SETTING
        //padding
        int paddingBottom 	= CircleGraphVO.DEFAULT_PADDING;
        int paddingTop 		= CircleGraphVO.DEFAULT_PADDING;
        int paddingLeft 	= CircleGraphVO.DEFAULT_PADDING;
        int paddingRight 	= CircleGraphVO.DEFAULT_PADDING;


        //graph margin
        int marginTop 		= CircleGraphVO.DEFAULT_MARGIN_TOP;
        int marginRight 	= CircleGraphVO.DEFAULT_MARGIN_RIGHT;

        // radius setting
        int radius = 130;

        List<CircleGraph> arrGraph 	= new ArrayList<CircleGraph>();


        //GRAPH SETTING
        ViewAllCalendarActivity_byPie users = new ViewAllCalendarActivity_byPie();

        arrGraph.add(new CircleGraph("단백질", Color.GREEN, getCarbo()));
        arrGraph.add(new CircleGraph("탄수화물", Color.RED, getCarbo()));
        arrGraph.add(new CircleGraph("지방", Color.BLUE, getFat()));

        CircleGraphVO vo = new CircleGraphVO(paddingBottom, paddingTop, paddingLeft, paddingRight,marginTop, marginRight,radius, arrGraph);

        // circle Line
        vo.setLineColor(Color.WHITE);

        // set text setting
        vo.setTextColor(Color.BLACK);
        vo.setTextSize(40);

        // set circle center move X ,Y
        vo.setCenterX(0);
        vo.setCenterY(0);

        //set animation
        vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, 2000));
        //set graph name box

        vo.setPieChart(true);

        GraphNameBox graphNameBox = new GraphNameBox();

        // nameBox
        graphNameBox.setNameboxMarginTop(25);
        graphNameBox.setNameboxMarginRight(25);

        vo.setGraphNameBox(graphNameBox);

        return vo;
    }

    //탄단지 초기화
    public void setzero(){
        setCarbo(0);
        setProtein(0);
        setFat(0);
    }

    // getter and setter
    public int getSum() {return sum;}
    public void setSum(int sum) {this.sum = sum;}
    public int getCarbo() {
        return carbo;
    }
    public void setCarbo(int carbo) {
        this.carbo = carbo;
    }
    public int getProtein() {
        return protein;
    }
    public void setProtein(int protein) {
        this.protein = protein;
    }
    public int getFat() {
        return fat;
    }
    public void setFat(int fat) {
        this.fat = fat;
    }
}