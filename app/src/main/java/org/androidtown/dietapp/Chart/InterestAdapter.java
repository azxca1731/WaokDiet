package org.androidtown.dietapp.Chart;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.Menu.FoodAdapter;
import org.androidtown.dietapp.R;

import java.util.List;

// ViewUserInterestActivity 리사이클려뷰 어댑터
public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.FoodViewHolder>{

    private List<FoodItem> historyList;
    private Context context;
    private StorageReference storageReference;
    private static int number;

    public InterestAdapter(List<FoodItem> historyList) {
        this.historyList=historyList;
        storageReference= FirebaseStorage.getInstance().getReference();
    }

    public void setUidList(List<FoodItem> historyList) {
        this.historyList = historyList;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new FoodViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_interest,parent,false));
    }

    @Override
    public void onBindViewHolder(final FoodViewHolder holder, int position) {

        FoodItem food = historyList.get(position);

        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(storageReference.child("foodImage/" +  food.getUid() + ".png"))
                .override(50,50)
                .into(holder.imgView);

        holder.textName.setText(food.getName());
        holder.frequency.setText(food.getFrequency()+"회");
        holder.key = food.getUid();
    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder{
        ImageView imgView;
        TextView textName;
        TextView frequency;
        String key;

        public FoodViewHolder(final View itemView){
            super(itemView);
            imgView=(ImageView)itemView.findViewById((R.id.image_view_inInterest));
            textName=(TextView)itemView.findViewById(R.id.text_name_inInterest);
            frequency = (TextView)itemView.findViewById((R.id.text_cal_inInterest));



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v,textName.getText()+"의 내용보기 ",Snackbar.LENGTH_LONG).setAction("선택", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            for(int i=0; i<5; i++){
                                    if(historyList.get(i).getUid().equals(key)){
                                        set_number(i);
                                        break;
                                    }
                                }
                        }
                    }).show();
                }
            });
        }

    }
    public void set_number(int a){ this.number = a; }
    public int get_number(){return number;}

}
