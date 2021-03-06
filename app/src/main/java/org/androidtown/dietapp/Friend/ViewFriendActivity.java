package org.androidtown.dietapp.Friend;

import android.content.Context;
import android.net.Uri;
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
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import org.androidtown.dietapp.DTO.FriendItem;
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

    private static final String TAG="FRIENDACTIVITY";
    ///카카오 링크 시작
    private final Context context=this;

    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    private String friendName;
    private String myName;
    //카카오 링크 끝

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    String uid = user.getUid();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent()!=null){
            Uri uri = getIntent().getData();
            if(uri!=null){
                String friendUid = uri.getQueryParameter("uid");
                Log.d(TAG, "onCreate: ");
                //settingFriend(friendUid);
                getNames(friendUid);
            }
        }

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
                        addMyfriend();
                        break;
                    case R.id.action_add_friend:
                        addMyfriend();
                        break;
                    case R.id.action_friend_right:
                        addMyfriend();
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
                    if(snapshot!=null) {
                        FriendItem friendItem = snapshot.getValue(FriendItem.class);
                        friendList.add(friendItem);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void addMyfriend(){
       try {
            kakaoLink = KakaoLink.getKakaoLink(this);
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
            kakaoTalkLinkMessageBuilder
                   .addText("친구추가를 수락하려면 이 버튼을 누르세요!")
                   .addAppLink("자세히 보기", new AppActionBuilder()
                           .addActionInfo(AppActionInfoBuilder
                                   .createAndroidActionInfoBuilder()
                                   .setExecuteParam("uid="+uid)
                                   .setMarketParam("referrer=kakaotalklink")
                                   .build())
                           .build());
           kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, context);
           Log.d("FRIENDACTIVITY","들어오긴함");
        } catch (KakaoParameterException e) {
            e.printStackTrace();
           Log.d("FRIENDACTIVITY",e.getMessage());
        }
    }

    private void getNames(final String friendUid){
        DatabaseReference myInfoRef=FirebaseDatabase.getInstance().getReference().child("user").child(uid);
        DatabaseReference myfriendInfoRef=FirebaseDatabase.getInstance().getReference().child("user").child(friendUid);
        myInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FriendItem myItem=dataSnapshot.getValue(FriendItem.class);
                myName=myItem.getName();
                settingMe(friendUid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //친구 정보 세팅
        myfriendInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FriendItem myItem=dataSnapshot.getValue(FriendItem.class);
                friendName=myItem.getName();
                settingFriend(friendUid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void settingFriend(String friendUid){

        DatabaseReference myfriendRef=FirebaseDatabase.getInstance().getReference().child("friends").child(uid).child(friendUid);
        //내 정보 세팅

        Log.d(TAG, "settingFriend: "+friendName);
        Log.d(TAG, "settingFriend: "+friendUid);
        Log.d(TAG, "settingFriend: "+myName);
        //내 친구로 등록
        myfriendRef.setValue(new FriendItem(friendUid,friendName));

    }

    private void settingMe(String friendUid){

        DatabaseReference myfriendfriendRef=FirebaseDatabase.getInstance().getReference().child("friends").child(friendUid).child(uid);
        //내 정보 세팅

        Log.d(TAG, "settingFriend: "+friendName);
        Log.d(TAG, "settingFriend: "+friendUid);
        Log.d(TAG, "settingFriend: "+myName);
        //친구 나를 친구로 등록
        myfriendfriendRef.setValue(new FriendItem(uid,myName));
    }

}
