// 차트 전체보기.

package org.androidtown.dietapp.Chart;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.LineGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.linegraph.LineGraph;
import com.handstudio.android.hzgrapherlib.vo.linegraph.LineGraphVO;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zidru on 2017-09-27.
 */

// 전체데이터를 꺾은선 그래프로 표현
public class ViewAllCalendarActivity extends android.support.v4.app.Fragment{
    // 뷰
    private ViewGroup layoutGraphView;
    private ViewGroup GraphView;
    TextView textView;

    // 데이터 리스트들
   List<FoodItem> datas = new ArrayList<>();
    int sum_of_calorie[];

    //파이어베이스관련
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference historyRef;
    DatabaseReference userRef;
    DatabaseReference RootRef;

    Bundle bundle;

    // 유저칼로리, 날짜
    int user_calorie;
    int dates;

    // 프래그먼트 구조상 분리될때 컨텍스트를 null로 반환해서 여러 에러가 생김;
    // 이를 해결하기 위해 onAttach에서 액티비티와 연결
    private Activity activity;

    TextView testtext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            activity = (Activity) context;
        }
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //초기화
        dates=0;
        sum_of_calorie = new int[30];

        //뷰 선언
        layoutGraphView = (ViewGroup) inflater.inflate(R.layout.activity_view_all_calendar, container, false);
        GraphView = (ViewGroup) layoutGraphView.findViewById((R.id.view_all_calendar_byline));
        textView = (TextView)layoutGraphView.findViewById(R.id.text_int_viewCalendar_by_line);

        // 데이터베이스레퍼런스
        RootRef = FirebaseDatabase.getInstance().getReference();
        userRef = RootRef.child("user").child(uid).child("basicCalorie");
        historyRef = RootRef.child("userHistory").child(uid);

        get_datas_and_makeChart();

        return layoutGraphView;
    }

    private void get_datas_and_makeChart(){
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int j=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int i=0; datas.clear();
                    for(DataSnapshot snapshot2 : snapshot.getChildren()){
                        FoodItem data = snapshot2.getValue(FoodItem.class);
                        datas.add(data);
                        // 히스토리안의 각각의 날짜에 있는 음식들의 칼로리를 합쳐 리스트에 추가.
                        setSum_of_calorie(j,getSum_of_calorie(j)+datas.get(i).getCalorie());
                        i++;
                    }
                    //30일까지만
                    if(j>=30) {
                        break;
                    }else j++;
                    setDates(getDates()+1);
                }
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // 유저칼로리 셋팅후 그래프출력.
                        int u_cal = dataSnapshot.getValue(int.class);
                        setUser_calorie(u_cal);
                        setLineGraph();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // make line graph
    private LineGraphVO makeLineGraphAllSetting() {

        int paddingBottom 	= LineGraphVO.DEFAULT_PADDING;
        int paddingTop 		= LineGraphVO.DEFAULT_PADDING;
        int paddingLeft 	= LineGraphVO.DEFAULT_PADDING;
        int paddingRight 	= LineGraphVO.DEFAULT_PADDING;

        //graph margin
        int marginTop 		= LineGraphVO.DEFAULT_MARGIN_TOP;
        int marginRight 	= LineGraphVO.DEFAULT_MARGIN_RIGHT;

        //max value
        int maxValue 		= LineGraphVO.DEFAULT_MAX_VALUE;

        //increment
        int increment 		= LineGraphVO.DEFAULT_INCREMENT;

        //GRAPH SETTING

        ViewAllCalendarActivity users = new ViewAllCalendarActivity();
        String[] legendArr = new String[getDates()];
        int[] graph1 = new int[getDates()];
        int[] graph2 = new int[getDates()];
        for(int i=0; i<getDates();i++){
            legendArr[i] = String.valueOf(i+1)+"일차";
            graph1[i] = sum_of_calorie[i];
            graph2[i] = getUser_calorie();
        }
        List<LineGraph> arrGraph = new ArrayList<LineGraph>();

        arrGraph.add(new LineGraph("Calorie", Color.RED, graph1));
        arrGraph.add(new LineGraph("user_calorie", Color.BLACK, graph2));

        LineGraphVO vo = new LineGraphVO(
                paddingBottom, paddingTop, paddingLeft, paddingRight,
                marginTop, marginRight, maxValue, increment, legendArr, arrGraph);

        //set animation
        vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, GraphAnimation.DEFAULT_DURATION));
        //set graph name box
        vo.setGraphNameBox(new GraphNameBox());

        return vo;
    }

    //getter and setter
    private void setUser_calorie(int u_cal){
        this.user_calorie = u_cal;
    }
    private void setDates(int dates){
        this.dates = dates;
    }
    private int getUser_calorie(){
        return user_calorie;
    }
    private int getDates(){
        return dates;
    }
    private int getSum_of_calorie(int index) {
        return sum_of_calorie[index];
    }
    private void setSum_of_calorie(int index, int value){
        sum_of_calorie[index] = value;
    }

    //set drawing graph
    private void setLineGraph() {
        LineGraphVO vo = makeLineGraphAllSetting();

        GraphView.addView(new LineGraphView(activity, vo));
    }
}
