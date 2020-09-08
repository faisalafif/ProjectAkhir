package com.faislll.projectakhir.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faislll.projectakhir.R;
import com.faislll.projectakhir.aktivitas.ComentAktivitas;
import com.faislll.projectakhir.aktivitas.MainActivity;
import com.faislll.projectakhir.fragment.PostDetailFragment;
import com.faislll.projectakhir.model.Postingan;

import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.MyViewHolder> {
    public FotoAdapter(Context context, List<Postingan> postinganList) {
        this.context = context;
        this.postinganList = postinganList;
    }

    private Context context;
    private List<Postingan> postinganList;

    @NonNull
    @Override
    public FotoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoAdapter.MyViewHolder holder, int position) {
        final Postingan postingan = postinganList.get(position);
        Glide.with(context).load(postingan.getGambar()).into(holder.imageViewPostFoto);

        holder.imageViewPostFoto.setOnClickListener(new View.OnClickListener() {
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
        return postinganList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewPostFoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewPostFoto = itemView.findViewById(R.id.image_view_post);
        }
    }
}
