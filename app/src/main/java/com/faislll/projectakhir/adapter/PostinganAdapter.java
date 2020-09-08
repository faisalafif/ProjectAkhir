package com.faislll.projectakhir.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bumptech.glide.request.RequestOptions;

import com.faislll.projectakhir.R;
import com.faislll.projectakhir.aktivitas.ComentAktivitas;
import com.faislll.projectakhir.aktivitas.MainActivity;
import com.faislll.projectakhir.fragment.FragmentProfile;
import com.faislll.projectakhir.fragment.PostDetailFragment;
import com.faislll.projectakhir.model.Postingan;
import com.faislll.projectakhir.model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostinganAdapter extends RecyclerView.Adapter<PostinganAdapter.MyViewHolder> {
    public Context context;
    public List<Postingan> listPostingan;
    private static final String TAG = "PostinganAdapter";
    public static final String ID_UPLOAD = "idupload";

    public PostinganAdapter(Context context, List<Postingan> listPostingan) {
        this.context = context;
        this.listPostingan = listPostingan;
    }

    private FirebaseUser user;


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_postingan, parent, false);

        return new PostinganAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        final Postingan postingan = listPostingan.get(position);
        Glide.with(context).load(postingan.getGambar())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.imageViewPostinganGambar);

        if (postingan.getDeskripsi().equals("")) {
            holder.textViewDeskripsi.setVisibility(View.GONE);
        } else {
            holder.textViewDeskripsi.setVisibility(View.VISIBLE);
            holder.textViewDeskripsi.setText(postingan.getDeskripsi());
        }

        infoPengapload(holder.imageViewPp, holder.textViewUsername, holder.textViewPengapload, postingan.getPengapload());
        disukai(postingan.getIdupload(), holder.imageViewSuka);
        penyuka(holder.textViewJumlahSuka, postingan.getIdupload());
        getComments(postingan.getIdupload(), holder.textViewComment);

        holder.imageViewSuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.imageViewSuka.getTag().equals("sukai")) {
                    final Map<String, Object> data = new HashMap<>();
                    data.put(user.getUid(), true);
                    FirebaseFirestore.getInstance().collection("suka")
                            .document(postingan.getIdupload()).set(data, SetOptions.merge());

                    addNotifikasi(postingan.getPengapload(), postingan.getIdupload());
                } else {
                    final Map<String, Object> data = new HashMap<>();
                    data.put(user.getUid(), FieldValue.delete());
                    final DocumentReference document = FirebaseFirestore.getInstance().collection("suka")
                            .document(postingan.getIdupload());
                    document.update(data);
                }
            }
        });

        holder.imageViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ComentAktivitas.class);
                intent.putExtra(ComentAktivitas.ID_POST, postingan.getIdupload());
                intent.putExtra(CommentAdapter.ID_PUBRISHER, postingan.getPengapload());
                context.startActivity(intent);
            }
        });
        holder.textViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ComentAktivitas.class);
                intent.putExtra(ComentAktivitas.ID_POST, postingan.getIdupload());
                intent.putExtra(CommentAdapter.ID_PUBRISHER, postingan.getPengapload());
                context.startActivity(intent);
            }
        });

        holder.imageViewPp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(MainActivity.KEY, postingan.getPengapload());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new FragmentProfile()).commit();
            }
        });

        holder.textViewUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(MainActivity.KEY, postingan.getPengapload());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new FragmentProfile()).commit();
            }
        });

        holder.textViewPengapload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(MainActivity.KEY, postingan.getPengapload());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new FragmentProfile()).commit();
            }
        });

        holder.imageViewPostinganGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE).edit();
                editor.putString(ComentAktivitas.ID_POST, postingan.getIdupload());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new PostDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPostingan.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewPp, imageViewPostinganGambar, imageViewSuka, imageViewComment, imageViewSimpan;
        public TextView textViewUsername, textViewJumlahSuka, textViewPengapload, textViewDeskripsi, textViewComment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewComment = itemView.findViewById(R.id.image_view_comment);
            imageViewPp = itemView.findViewById(R.id.image_view_pp_postingan);
            imageViewPostinganGambar = itemView.findViewById(R.id.image_view_postingan_gambar);
            imageViewSuka = itemView.findViewById(R.id.image_view_suka);
            imageViewSimpan = itemView.findViewById(R.id.image_view_simpan_postingan);
            textViewComment = itemView.findViewById(R.id.text_view_commentar);
            textViewUsername = itemView.findViewById(R.id.text_view_usename_home);
            textViewJumlahSuka = itemView.findViewById(R.id.text_view_jumlah_suka);
            textViewPengapload = itemView.findViewById(R.id.text_view_pengapload);
            textViewDeskripsi = itemView.findViewById(R.id.text_view_deskripsi_postingan);
        }
    }

    private void infoPengapload(final ImageView pp, final TextView username, final TextView pengapload, String idUser) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(idUser);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                UserData userData = value.toObject(UserData.class);
                assert userData != null;
                Glide.with(context).load(userData.getImageUrl()).into(pp);
                username.setText(userData.getUsername());
                pengapload.setText(userData.getUsername());
            }
        });
    }

    private void disukai(final String idpost, final ImageView imageViewPostinganGambar) {
        final DocumentReference reference = FirebaseFirestore.getInstance().collection("suka")
                .document(idpost);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String id = user.getUid();
                if (value.exists()) {
                    if (value.get(id) != null && value.getBoolean(id)) {
                        imageViewPostinganGambar.setTag("disukai");
                        imageViewPostinganGambar.setImageResource(R.drawable.ic_suka_pink);
                    } else {
                        imageViewPostinganGambar.setTag("sukai");
                        imageViewPostinganGambar.setImageResource(R.drawable.ic_suka_hitam);
                    }
                } else {
                    imageViewPostinganGambar.setTag("sukai");
                }

            }
        });
    }

    private void penyuka(final TextView textViewSukai, final String idPost) {
        FirebaseFirestore.getInstance().collection("suka").document(idPost)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        int jumlah = 0;
                        String template = "0 menyukai";
                        if (value.exists()) {
                            jumlah = value.getData().size();
                            template = jumlah + " menyukai";
                        }
                        textViewSukai.setText(template);
                    }
                });
    }

    private void getComments(final String idupload, final TextView komentar) {
        final DocumentReference reference = FirebaseFirestore.getInstance().collection("comments").document(idupload);
        reference.collection(idupload).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int jumlah = 0;
                String template = "0 berkomentar";
                for (DocumentSnapshot snapshot : value) {
                    if (snapshot.exists()) {
                        jumlah++;
                        template = jumlah + " berkomentar";
                    }
                }
                komentar.setText(template);
            }
        });


    }

    private void addNotifikasi(String userid, String idupload) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("notifikasi")
                .document(userid);
        Map<String, Object> dataNotifikasi = new HashMap<>();
        dataNotifikasi.put("id_user", user.getUid());
        dataNotifikasi.put("text", "menyukai postingan");
        dataNotifikasi.put("idupload", idupload);
        dataNotifikasi.put("ispost", true);
        reference.set(dataNotifikasi);
    }
}
