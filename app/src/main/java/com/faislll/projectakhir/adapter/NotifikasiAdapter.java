package com.faislll.projectakhir.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faislll.projectakhir.R;
import com.faislll.projectakhir.aktivitas.ComentAktivitas;
import com.faislll.projectakhir.aktivitas.MainActivity;
import com.faislll.projectakhir.fragment.FragmentProfile;
import com.faislll.projectakhir.fragment.PostDetailFragment;
import com.faislll.projectakhir.model.Notifikasi;
import com.faislll.projectakhir.model.Postingan;
import com.faislll.projectakhir.model.UserData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class NotifikasiAdapter extends RecyclerView.Adapter<NotifikasiAdapter.MyViewHolder> {

    private static final String TAG = "NotifikasiAdapter";
    private Context context;
    private List<Notifikasi> notifikasiList;

    public NotifikasiAdapter(Context context, List<Notifikasi> notifikasiList) {
        this.context = context;
        this.notifikasiList = notifikasiList;
    }

    @NonNull
    @Override
    public NotifikasiAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_notif, parent, false);

        return new NotifikasiAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifikasiAdapter.MyViewHolder holder, int position) {
        final Notifikasi notifikasi = notifikasiList.get(position);
        Log.d(TAG, "onBindViewHolder: notif pengapload" + notifikasi);

        holder.textViewText.setText(notifikasi.getText());
        getUserInfo(holder.imageViewPp, holder.textViewUsername, notifikasi.getId_user());

        if (notifikasi.isIspost()) {
            holder.imageViewPostingan.setVisibility(View.VISIBLE);
            getPostGambar(holder.imageViewPostingan, notifikasi.getIdupload());
        } else {
            holder.imageViewPostingan.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                if (notifikasi.isIspost()) {
                    editor.putString(ComentAktivitas.ID_POST, notifikasi.getIdupload());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new PostDetailFragment()).commit();
                } else {
                    editor.putString(MainActivity.KEY, notifikasi.getId_user());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new FragmentProfile()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifikasiList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewPp, imageViewPostingan;
        public TextView textViewUsername, textViewText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPostingan = itemView.findViewById(R.id.post_image);
            imageViewPp = itemView.findViewById(R.id.image_view_pp_notif);
            textViewText = itemView.findViewById(R.id.text_view_commenttar_notif);
            textViewUsername = itemView.findViewById(R.id.text_view_username_notif);
        }
    }

    private void getUserInfo(final ImageView imageViewPp, final TextView textViewUsernaame, String pengapload) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users")
                .document(pengapload);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                assert value != null;
                UserData userData = value.toObject(UserData.class);
                Log.d(TAG, "getUserInfo: " + userData);

                assert userData != null;
                Glide.with(context).load(userData.getImageUrl()).into(imageViewPp);
                textViewUsernaame.setText(userData.getUsername());
            }
        });
    }

    private void getPostGambar(final ImageView imageViewGambar, String idupload) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("photos")
                .document(idupload);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                Postingan postingan = value.toObject(Postingan.class);
                assert postingan != null;
                Glide.with(context).load(postingan.getGambar()).into(imageViewGambar);
            }
        });
    }
}
