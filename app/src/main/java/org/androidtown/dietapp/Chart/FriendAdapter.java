package org.androidtown.dietapp.Chart;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.androidtown.dietapp.DTO.FoodItem;
import org.androidtown.dietapp.DTO.FriendItem;
import org.androidtown.dietapp.DTO.UsersItem;
import org.androidtown.dietapp.R;

import java.util.List;

// ViewUserInterestActivity 리사이클려뷰 어댑터
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder>{

    private List<FriendItem> friendList;
    private Context context;
    private StorageReference storageReference;
    private DatabaseReference historyRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public FriendAdapter(List<FriendItem> friendList) {
        this.friendList= friendList;
        storageReference= FirebaseStorage.getInstance().getReference();
    }

    public void setHistoryRef(DatabaseReference historyRef){
        this.historyRef=historyRef;
    }

    public void setUidList(List<FriendItem> friendList) {
        this.friendList = friendList;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new FriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friend,parent,false));
    }

    @Override
    public void onBindViewHolder(final FriendViewHolder holder, int position) {
        FriendItem friendItem = friendList.get(position);

        holder.textName.setText(friendItem.getName());
        holder.key = friendItem.getUid();
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder{
        TextView textName;
        String key;

        public FriendViewHolder(View itemView){
            super(itemView);

            textName=(TextView)itemView.findViewById(R.id.text_name_friend);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v,textName.getText()+" 선택",Snackbar.LENGTH_LONG).setAction("delete", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(historyRef!=null){
                                historyRef.child(key).removeValue();
                            }
                        }
                    }).show();
                }
            });
        }
    }
}
