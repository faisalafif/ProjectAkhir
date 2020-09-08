package com.faislll.projectakhir.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faislll.projectakhir.R;
import com.faislll.projectakhir.aktivitas.MainActivity;
import com.faislll.projectakhir.fragment.FragmentProfile;
import com.faislll.projectakhir.model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    private List<UserData> users;

    public UserAdapter(Context context, List<UserData> users) {
        this.context = context;
        this.users = users;
    }

    //    firebase
    private FirebaseUser user;

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.MyViewHolder(view);
    }

    private void addNotifikasi(String userid) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("notifikasi")
                .document(userid);
        Map<String, Object> dataNotifikasi = new HashMap<>();
        dataNotifikasi.put("id_user", user.getUid());
        dataNotifikasi.put("text", "mulai mengikuti");
        dataNotifikasi.put("idupload", "");
        dataNotifikasi.put("ispost", false);
        reference.set(dataNotifikasi);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.MyViewHolder holder, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        final UserData userData = users.get(position);
        holder.buttonFollow.setVisibility(View.VISIBLE);

        holder.textViewUsername.setText(userData.getUsername());
        holder.textViewFullName.setText(userData.getFullname());
        Glide.with(context).load(userData.getImageUrl()).into(holder.imageViewPp);

        isFollowing(userData.getId_user(), holder.buttonFollow, user.getUid());

        if (userData.getId_user().equals(user.getUid())) {
            holder.buttonFollow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(MainActivity.KEY, userData.getId_user());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new FragmentProfile()).commit();
            }
        });

        holder.buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.buttonFollow.getText().toString().equals("ikuti")) {
                    Map<String, Object> dataFollowing = new HashMap<>();
                    dataFollowing.put(userData.getId_user(), true);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("follow").document(user.getUid())
                            .collection("following").document(userData.getId_user()).set(dataFollowing);

                    Map<String, Object> dataFollower = new HashMap<>();
                    dataFollower.put(user.getUid(), true);
                    db.collection("follow").document(userData.getId_user())
                            .collection("followers").document(user.getUid()).set(dataFollowing);

                    addNotifikasi(userData.getId_user());
                } else {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("follow").document(user.getUid())
                            .collection("following").document(userData.getId_user()).delete();
                    db.collection("follow").document(userData.getId_user())
                            .collection("followers").document(user.getUid()).delete();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewUsername, textViewFullName;
        public CircleImageView imageViewPp;
        public Button buttonFollow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUsername = itemView.findViewById(R.id.t_view_username);
            textViewFullName = itemView.findViewById(R.id.t_view_fullnama);
            imageViewPp = itemView.findViewById(R.id.pp);
            buttonFollow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userId, final Button button, final String following) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("follow").document(following).collection("following").document(userId);
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    button.setText("berhenti mengikuti");
                    button.setBackground(context.getResources().getDrawable(R.drawable.custom_bg_button_unfoll));
                } else {
                    button.setText("ikuti");
                    button.setBackground(context.getResources().getDrawable(R.drawable.custom_bg_button));
                }
            }
        });
    }

}
