// 차트 전체보기.

package org.androidtown.dietapp.Chart;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zidru on 2017-09-27.
 */

public class ViewUserInterestActivity extends android.support.v4.app.Fragment{

    // 뷰들
    private ViewGroup layoutGraphView;
    private ViewGroup GraphView;
    TextView textView;
    TextView Whatisit;

    //파이어베이스
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database;
    private DatabaseReference myHistoryRef;
    String uid = user.getUid();

    // 리사이클러뷰 관련
    private RecyclerView recyclerView;
    private List<FoodItem> interestList;
    private InterestAdapter adapter;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // 뷰들 선언
        layoutGraphView = (ViewGroup) inflater.inflate(R.layout.activity_view_user_interest, container, false);
        GraphView = (ViewGroup) layoutGraphView.findViewById((R.id.view_user_interest));
        textView = (TextView)layoutGraphView.findViewById(R.id.text_in_viewUser_interest);
        Whatisit = (TextView)layoutGraphView.findViewById(R.id.Whatisit);

        // 리스트 객체생성
        interestList=new ArrayList<>();

        // 리사이클러뷰
        recyclerView = (RecyclerView)layoutGraphView.findViewById(R.id.interest_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lim = new LinearLayoutManager(getActivity());
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lim);
        adapter = new InterestAdapter(interestList);
        recyclerView.setAdapter(adapter);

        // 파이어베이스
        database = FirebaseDatabase.getInstance();
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        myHistoryRef = database.getReference().child("userHistory").child(uid);

        // 목록이름
        Whatisit.setText("먹은 음식 TOP5");

        updateHistoryList();

        return layoutGraphView;
    }

    // 먹은음식 top5 추리고 정렬. 리스트뷰에 표현.
    private void updateHistoryList() {
        if (myHistoryRef == null) {
            return;
        }
        myHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                interestList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot snapshot2 : snapshot.getChildren()) {
                        FoodItem foodItem = snapshot2.getValue(FoodItem.class);
                        if(interestList.size()>0){
                            if(searching_food_uid(interestList,foodItem)){
                             interestList.get(searching_food_uid_i(interestList,foodItem)).plusFrequency();
                            } else {
                                foodItem.setFrequency(1);
                                interestList.add(foodItem);
                            }
                        }else { interestList.add(foodItem);interestList.get(0).setFrequency(1);}
                    }
                }

                quickSort(interestList, 0, interestList.size()-1);

                while(interestList.size()>5)  interestList.remove(interestList.size()-1);

                 adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // 같은 "이름"의 음식이 존재하는지 판정 및 반환
    private boolean searching_food_uid(List<FoodItem> L, FoodItem f){
        for(int i=0; i<L.size(); i++){
            if(L.get(i).getName().equals(f.getName())){
                return true;
            }
        }
        return false;
    }
    private int searching_food_uid_i(List<FoodItem> L, FoodItem f){
        int the_charge=0;
        for(int i=0; i<L.size(); i++){
            if(L.get(i).getName().equals(f.getName())){
                the_charge = i;
                break;
            }
        }
        return the_charge;
    }


    // quick sort
    public static int partition(List<FoodItem> L, int left, int right) {
        int pivot = L.get((left + right) / 2).getFrequency();

        while (left < right) {
            while ((L.get(left).getFrequency() > pivot) && (left < right))
                left++;
            while ((L.get(right).getFrequency() < pivot) && (left < right))
                right--;

            if (left < right) {
                FoodItem temp = L.get(left);
                L.set(left,L.get(right));
                L.set(right,temp);
                left++; right--;
            }
        }
        return left;
    }
    public static void quickSort(List<FoodItem> L, int left, int right) {

        if (left < right) {
            int pivotNewIndex = partition(L, left, right);
            quickSort(L, left, pivotNewIndex - 1);
            quickSort(L, pivotNewIndex + 1, right);
        }

    }
}
