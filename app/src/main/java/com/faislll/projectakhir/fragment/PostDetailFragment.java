package com.faislll.projectakhir.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faislll.projectakhir.R;
import com.faislll.projectakhir.adapter.PostinganAdapter;
import com.faislll.projectakhir.aktivitas.ComentAktivitas;
import com.faislll.projectakhir.aktivitas.MainActivity;
import com.faislll.projectakhir.model.Postingan;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {
    private RecyclerView recyclerViewPostingan;
    private PostinganAdapter adapter;
    private List<Postingan> list;
    private String idupload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.DATA_UID, Context.MODE_PRIVATE);
        idupload = preferences.getString(ComentAktivitas.ID_POST, "none");
        recyclerViewPostingan = view.findViewById(R.id.recycler_view_post_detail);
        recyclerViewPostingan.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewPostingan.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        adapter = new PostinganAdapter(getContext(), list);
        recyclerViewPostingan.setAdapter(adapter);
        bacaPostingan();

        return view;
    }

    private void bacaPostingan() {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("photos")
                .document(idupload);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();
                Postingan postingan = value.toObject(Postingan.class);
                list.add(postingan);
                adapter.notifyDataSetChanged();
            }
        });
    }
}