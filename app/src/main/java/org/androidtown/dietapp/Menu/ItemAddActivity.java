package org.androidtown.dietapp.Menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.io.IOException;
import java.util.UUID;

public class ItemAddActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextName;
    private EditText editTextCategory;
    private EditText editTextCalorie;
    private EditText editTextCarb;
    private EditText editTextProtein;
    private EditText editTextFat;
    private EditText editTextSugar;
    private EditText editTextNatrium;
    private EditText editTextCholestreol;
    private EditText editTextSaturateFat;
    private EditText editTextTransFat;

    private Button buttonSubmit;
    private String uuid;
    private String barcode;
    private Button buttonBarcode;

    //image & firebase storage  start
    private ImageView imagePrev;
    private Button buttonChoose;
    private Button buttonUpload;
    private Uri filePath;
    private boolean uploadCheck;
    //image & firebase storage end

    DatabaseReference mFoodRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);

        init();
    }

    private void init(){
        //find id start
        editTextName=(EditText)findViewById(R.id.editTextName);
        editTextCategory=(EditText)findViewById(R.id.editTextCategory);
        editTextCalorie=(EditText)findViewById(R.id.editTextCalorie);
        editTextCarb=(EditText)findViewById(R.id.editTextCarb);
        editTextProtein=(EditText)findViewById(R.id.editTextProtein);
        editTextFat=(EditText)findViewById(R.id.editTextFat);
        editTextSugar=(EditText)findViewById(R.id.editTextSugar);
        editTextNatrium=(EditText)findViewById(R.id.editTextNatrium);
        editTextCholestreol=(EditText)findViewById(R.id.editTextCholestreol);
        editTextSaturateFat=(EditText)findViewById(R.id.editTextSaturateFat);
        editTextTransFat=(EditText)findViewById(R.id.editTextTransFat);
        buttonSubmit=(Button) findViewById(R.id.buttonSubmit);

        buttonChoose=(Button) findViewById(R.id.buttonChoose);
        buttonUpload=(Button) findViewById(R.id.buttonUpload);
        buttonBarcode=(Button) findViewById(R.id.buttonBarcode);
        imagePrev=(ImageView)findViewById(R.id.imageViewPrev);
        //find id end
        uploadCheck=false;
        barcode="0";

        uuid=UUID.randomUUID().toString();
        mFoodRef= FirebaseDatabase.getInstance().getReference().child("food").child(uuid);

        buttonBarcode.setOnClickListener(this);
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            barcode = result.getContents();
        }

        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d("TAG", "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imagePrev.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            String filename = uuid + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReference().child("foodImage/" + filename);
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                            uploadCheck=true;
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitFood(){
        if(uploadCheck==false){
            Toast.makeText(ItemAddActivity.this, "사진을 업로드 하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        String name=editTextName.getText().toString();
        String category=editTextCategory.getText().toString();
        int calorie=Integer.parseInt(editTextCalorie.getText().toString());
        int carb=Integer.parseInt(editTextCarb.getText().toString());
        int protein=Integer.parseInt(editTextProtein.getText().toString());
        int fat=Integer.parseInt(editTextFat.getText().toString());
        int sugar=Integer.parseInt(editTextSugar.getText().toString());
        int natrium=Integer.parseInt(editTextNatrium.getText().toString());
        int cholesterol=Integer.parseInt(editTextCholestreol.getText().toString());
        int saturatedFat=Integer.parseInt(editTextSaturateFat.getText().toString());
        int transFat=Integer.parseInt(editTextTransFat.getText().toString());
        FoodItem foodItem=new FoodItem(category,name, calorie, fat, carb, protein, sugar, natrium, cholesterol, saturatedFat, transFat, uuid);
        foodItem.setBarcode(barcode);
        mFoodRef.setValue(foodItem);
        finish();
    }

    /*
            IMPLEMENTS METHOD
     */

    //ONCLICKLISTNER

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBarcode:
                new IntentIntegrator(ItemAddActivity.this).initiateScan();
                break;
            case R.id.buttonChoose:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
                break;
            case R.id.buttonUpload:
                uploadFile();
                break;
            case R.id.buttonSubmit:
                submitFood();
                break;
        }
    }
}
