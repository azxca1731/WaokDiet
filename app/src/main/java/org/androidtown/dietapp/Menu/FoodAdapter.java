package org.androidtown.dietapp.Menu;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodList;
    private StorageReference storageReference;
    private Context context;

    public void setFoodList(List<FoodItem> foodList) {
        this.foodList = foodList;
    }

    public void setHistoryRef(DatabaseReference historyRef) {
        this.historyRef = historyRef;
    }

    private DatabaseReference historyRef;

    public FoodAdapter(ArrayList<FoodItem> foodList) {
        this.foodList = foodList;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void setUidList(ArrayList<FoodItem> foodList) {
        this.foodList = foodList;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new FoodViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final FoodViewHolder holder, int position) {
        FoodItem foodItem = foodList.get(position);

        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(storageReference.child("foodImage/" + foodItem.getUid() + ".png"))
                .override(50, 50)
                .into(holder.imageViewItems);

        holder.textName.setText(foodItem.getName());
        holder.textCal.setText("칼로리: " + String.valueOf(foodItem.getCalorie())+"Kcal");
        holder.food = foodItem;

    }


    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textCal;
        ImageView imageViewItems;
        FoodItem food;

        public FoodViewHolder(View itemView) {
            super(itemView);

            textName = (TextView) itemView.findViewById(R.id.foodName);
            textCal = (TextView) itemView.findViewById(R.id.foodCal);
            imageViewItems = (ImageView) itemView.findViewById(R.id.image_view);
            food = new FoodItem();


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog dialog = new CustomDialog(context,food,historyRef);
                    dialog.show();
                }
            });
        }

    }
}


