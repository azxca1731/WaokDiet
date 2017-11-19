package org.androidtown.dietapp.Chart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidtown.dietapp.Auth.AuthMainActivity;
import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.DTO.FriendItem;
import org.androidtown.dietapp.DTO.UsersItem;
import org.androidtown.dietapp.Main.MainActivity;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zidru on 2017-11-14.
 */

public class ViewFriendActivity extends AppCompatActivity{
    private BottomNavigationView bottomNav;

    private RecyclerView recyclerView;
    private List<FriendItem> friendList;
    private FriendAdapter adapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    String uid = user.getUid();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //뷰
        setContentView(R.layout.activity_view_friends);

        // 리스트 초기화
        friendList = new ArrayList<>();

        // recycler view
        recyclerView = (RecyclerView)findViewById(R.id.friend_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lim);
        adapter = new FriendAdapter(friendList);
        recyclerView.setAdapter(adapter);

        // 파이어베이스
        database = FirebaseDatabase.getInstance();
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        userRef = database.getReference().child("friends").child(uid);

        // 어댑터의 레퍼런스설정
        adapter.setHistoryRef(userRef);


        bottomNav = (BottomNavigationView)findViewById(R.id.bottom_nav_in_friendview);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_friend_left:
                        break;
                    case R.id.action_add_friend:
                        break;
                    case R.id.action_friend_right:
                        break;
                }
                return true;
            }
        });

        updateHistoryList();

    }

    private void updateHistoryList() {
        if(userRef==null){
            return ;
        }
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                   FriendItem friendItem = snapshot.getValue(FriendItem.class);
                   friendList.add(friendItem);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
