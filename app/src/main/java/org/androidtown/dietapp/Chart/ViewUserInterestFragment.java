// 차트 전체보기.

package org.androidtown.dietapp.Chart;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zidru on 2017-09-27.
 */

public class ViewUserInterestFragment extends android.support.v4.app.Fragment{

    // 뷰들
    private ViewGroup layoutGraphView;
    private ViewGroup GraphView;
    private TextView textView;
    private TextView Whatisit;

    // 상세내용
    private ImageView image;
    private TextView rank_name;
    private TextView rank_cal;
    private TextView rank_carbo;
    private TextView rank_fat;
    private TextView rank_protein;

    //
    private int carbo, protein, fat;

    Bundle bundle;

    //파이어베이스
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference myHistoryRef;
    String uid ;

    // 리사이클러뷰 관련
    private RecyclerView recyclerView;
    private List<FoodItem> interestList;
    private InterestAdapter adapter;

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

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // 뷰들 선언
        layoutGraphView = (ViewGroup) inflater.inflate(R.layout.activity_view_user_interest, container, false);
        textView = (TextView)layoutGraphView.findViewById(R.id.text_in_viewUser_interest);
        Whatisit = (TextView)layoutGraphView.findViewById(R.id.Whatisit);
        GraphView  = (ViewGroup) layoutGraphView.findViewById(R.id.view_user_interest);

        // 음식상세내용 뷰들
        rank_name = (TextView) layoutGraphView.findViewById(R.id.Rank_Name);
        rank_cal = (TextView) layoutGraphView.findViewById(R.id.Rank_cal);
        rank_carbo = (TextView) layoutGraphView.findViewById(R.id.Rank_carbo);
        rank_fat = (TextView) layoutGraphView.findViewById(R.id.Rank_fat);
        rank_protein = (TextView) layoutGraphView.findViewById(R.id.Rank_protein);
        image = (ImageView) layoutGraphView.findViewById(R.id.image_in_interest);

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

        bundle = getArguments();
        uid = bundle.getString("uid");

        // 파이어베이스
        storageReference= FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
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
                interestList.clear(); set_zero();
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
                for(int i=0; i<interestList.size(); i++){
                    carbo = carbo + interestList.get(i).getCarbohydrate();
                    protein = protein + interestList.get(i).getProtein();
                    fat = fat + interestList.get(i).getFat();
                }

                set_rank_food();
                adapter.notifyDataSetChanged();
                advise();
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


    private void set_rank_food(){
        if(interestList.size()!=0){
            if(activity.isFinishing()) {return;}
            Glide.with(activity).using(new FirebaseImageLoader()).load(storageReference.child("foodImage/" + interestList.get(adapter.get_number()).getUid() + ".png")).into(image);
            rank_name.setText("이름 : " + interestList.get(adapter.get_number()).getName());
            rank_carbo.setText("탄수화물 : " + interestList.get(adapter.get_number()).getCarbohydrate() + "g");
            rank_cal.setText("칼로리 : " + interestList.get(adapter.get_number()).getCalorie() + "kcal");
            rank_protein.setText("단백질 : " + interestList.get(adapter.get_number()).getProtein() + "g");
            rank_fat.setText("지방 : " + interestList.get(adapter.get_number()).getFat() + "g");
        }else{
            return;
        }

    }

    private void advise(){
        if(interestList.size()!=0) {
            int sum = carbo + fat + protein;
            float rat_carbo = ((float) carbo / (float) sum) * 100;
            float rat_fat = ((float) fat / (float) sum) * 100;
            float rat_protein = ((float) protein / (float) sum) * 100;
            if(get_frequencies()<5){
                textView.setText("표본이 적지만 다음과 같네요.");
            } else if (rat_carbo >= 45) {
                textView.setText("대부분 고탄수화물 음식들을 드시고 계십니다. \n 탄수화물의 비율을 낮춰주세요.");
            } else if (rat_protein >= 60) {
                textView.setText("근육을 키우는게 아니시라면 단백질의 함량이 더 적은 \n 음식을 섭취하는것을 권해드립니다.");
            } else if (rat_fat >= 35) {
                textView.setText("고지방의 음식의 섭취가 너무 잦습니다. \n 현재보다 지방 섭취량을 줄이실 필요가 있습니다.");
            } else if (rat_protein < 40) {
                textView.setText("단백질의 함량이 적은 음식을 너무 많이 드시고 계십니다.");
            } else if (rat_fat < 15) {
                textView.setText("지방의 함량이 적은 음식을 너무 많이 드시고 계십니다..");
            } else if (rat_carbo < 25) {
                textView.setText("탄수화물의 함량이 적은 음식을 너무 많이 드시고 계십니다.");
            } else textView.setText("균형잡힌 식단을 선호하시는 군요!");
        }
        else{
            textView.setText("먹은 음식이 없습니다!");
        }
    }

    private int get_frequencies(){
        int sum=0;
        for(int i=0; i<interestList.size(); i++){
            sum = sum + interestList.get(i).getFrequency();
        }
        return sum;
    }

    // quick sort
    private static int partition(List<FoodItem> L, int left, int right) {
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
    private static void quickSort(List<FoodItem> L, int left, int right) {

        if (left < right) {
            int pivotNewIndex = partition(L, left, right);
            quickSort(L, left, pivotNewIndex - 1);
            quickSort(L, pivotNewIndex + 1, right);
        }

    }

    private void set_zero(){
        this.carbo = 0; this.protein = 0; this.fat = 0;
    }

}
