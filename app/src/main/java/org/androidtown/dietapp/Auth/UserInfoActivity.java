package org.androidtown.dietapp.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidtown.dietapp.DTO.UsersItem;
import org.androidtown.dietapp.R;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextWeight;
    private EditText editTextBasicCalorie;

    private Button buttonSubmit;
    private Button buttonSignOut;

    private RadioButton radioButtonFemale;
    private RadioButton radioButtonMale;
    private CheckBox checkBox;

    private DatabaseReference mRoofRef;
    private DatabaseReference mUserRef;
    private String uid;
    private FirebaseUser user;
    private BackPressCloseHandler backPressCloseHandler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        init();

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UsersItem curUsersItem = dataSnapshot.getValue(UsersItem.class);
                if(curUsersItem!=null) {
                    editTextName.setText(curUsersItem.getName());
                    editTextAge.setText(curUsersItem.getAge() + "");
                    editTextWeight.setText(curUsersItem.getWeight() + "");
                    editTextBasicCalorie.setText(curUsersItem.getBasicCalorie() + "");
                    if(curUsersItem.getGender().equals("Male")){
                        radioButtonMale.setChecked(true);
                    }else{
                        radioButtonFemale.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        buttonSubmit.setOnClickListener(this);
        buttonSignOut.setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);

    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }



    boolean canISubmit(){
        if(editTextName.getText().toString()==null){
            Toast.makeText(getApplicationContext(), "이름이 제대로 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
            return false;
        }else if(radioButtonFemale.isChecked()==false&&radioButtonMale.isChecked()==false){
            Toast.makeText(getApplicationContext(), "성별이 제대로 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
            return false;
        }else if(checkBox.isChecked()==false){
            Toast.makeText(getApplicationContext(), "체크를 하지 않았습니다.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    void init(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("UserInfo",user.getClass().toString());
        uid=user.getUid();
        mRoofRef = FirebaseDatabase.getInstance().getReference();
        mUserRef=mRoofRef.child("user").child(uid);

        editTextName=(EditText) findViewById(R.id.editTextName);
        editTextAge=(EditText)findViewById(R.id.editTextAge);
        editTextWeight=(EditText)findViewById(R.id.editTextWeight);
        editTextBasicCalorie=(EditText)findViewById(R.id.editTextBasicCalorie);

        radioButtonMale=(RadioButton)findViewById(R.id.radioButtonMale);
        radioButtonFemale=(RadioButton)findViewById(R.id.radioButtonFemale);

        buttonSubmit=(Button)findViewById(R.id.buttonSubmit);
        buttonSignOut=(Button)findViewById(R.id.buttonSignOut);
        checkBox=(CheckBox)findViewById(R.id.checkBox);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSubmit:
                if(canISubmit()==true) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String email = user.getEmail();
                    String name = editTextName.getText().toString();
                    String gender;
                    if (radioButtonFemale.isChecked()) {
                        gender = "Female";
                    } else {
                        gender = "Male";
                    }try{
                        int age = Integer.parseInt(editTextAge.getText().toString());
                        int weight = Integer.parseInt(editTextWeight.getText().toString());
                        int basicCalorie = Integer.parseInt(editTextBasicCalorie.getText().toString());
                        mUserRef.setValue(new UsersItem(email, uid, name, age, weight, basicCalorie, gender));
                        finish();
                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(), "숫자를 입력할 곳에 숫자를 입력하지 않았습니다.", Toast.LENGTH_LONG).show();
                    }

                }else{
                    //한글이 딸림 한글패치좀
                    Toast.makeText(getApplicationContext(), "제대로 완료되지 못한 부분이 있습니다.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonSignOut:
                Intent AuthIntent = new Intent(UserInfoActivity.this,AuthMainActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(AuthIntent);
                finish();
                break;
        }

    }
}
