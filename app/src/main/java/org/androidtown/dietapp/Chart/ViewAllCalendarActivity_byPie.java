// 차트 전체보기.

package org.androidtown.dietapp.Chart;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ThemedSpinnerAdapter;
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
import com.handstudio.android.hzgrapherlib.graphview.CircleGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.circlegraph.CircleGraph;
import com.handstudio.android.hzgrapherlib.vo.circlegraph.CircleGraphVO;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zidru on 2017-09-27.
 */

public class ViewAllCalendarActivity_byPie extends android.support.v4.app.Fragment{
    // 뷰
    private ViewGroup layoutGraphView;
    private ViewGroup GraphView;

    // 리스트
    ArrayList<FoodItem> foods = new ArrayList<FoodItem>();

    // 파이어베이스관련
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    //탄단지
    int carbo,protein,fat;
    TextView textView;

    // 프래그먼트 구조상 분리될때 컨텍스트를 null로 반환해서 여러 에러가 생김;
    // 이를 해결하기 위해 onAttach에서 액티비티와 연결
    private Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            activity = (Activity) context;
        }
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        layoutGraphView = (ViewGroup) inflater.inflate(R.layout.activity_view_all_calendar_bypie, container, false);
        GraphView = (ViewGroup) layoutGraphView.findViewById((R.id.view_all_calendar_bypie));
        textView = (TextView)layoutGraphView.findViewById(R.id.text_int_viewCalendar_by_pie);


        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = RootRef.child("user").child(uid).child("basicCalorie");
        final DatabaseReference historyRef = RootRef.child("userHistory").child(uid);

        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setCarbo(0);
                setProtein(0);
                setFat(0);
                int j=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    foods.clear();
                    for(DataSnapshot snapshot_food : snapshot.getChildren()){
                        FoodItem data = snapshot_food.getValue(FoodItem.class);
                        foods.add(data);
                        setCarbo(getCarbo() + foods.get(j).getCarbohydrate());
                        setProtein(getProtein() + foods.get(j).getProtein());
                        setFat(getFat() + foods.get(j).getFat());
                        j++;
                    }
                    j=0;
                }
                setCircleGraph();
                theAdvise();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return layoutGraphView;
    }


    public void theAdvise(){

        int sum = getCarbo() + getProtein() + getFat();
        float rat_carbo =( (float)getCarbo()/ (float)sum)*100;
        float rat_fat = ( (float)getFat()/ (float)sum)*100;
        float rat_protein = ( (float)getProtein()/ (float)sum)*100;
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
    // drawing circle graph
    private void setCircleGraph() {
        CircleGraphVO vo = makeCircleGraphAllSetting();
        GraphView.addView(new CircleGraphView(activity,vo));

    }

    // make circle graph
    private CircleGraphVO makeCircleGraphAllSetting() {
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
        vo.setLineColor(Color.BLACK);

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

    // getter and setter
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
