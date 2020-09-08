package com.faislll.projectakhir.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faislll.projectakhir.R;
import com.faislll.projectakhir.adapter.NotifikasiAdapter;
import com.faislll.projectakhir.model.Notifikasi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentSuka extends Fragment {
    private static final String TAG = "FragmentSuka";
    private RecyclerView recyclerView;
    private NotifikasiAdapter adapter;
    private List<Notifikasi> notifikasiList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suka, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_notif);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayout);
        notifikasiList = new ArrayList<>();
        adapter = new NotifikasiAdapter(getContext(), notifikasiList);
        recyclerView.setAdapter(adapter);

        bacaNotifikasi();

        return view;
    }

    private void bacaNotifikasi() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection("notifikasi")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
//                        notifikasiList.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            Notifikasi notifikasi = snapshot.toObject(Notifikasi.class);
                            notifikasiList.add(notifikasi);
                        }
                        Collections.reverse(notifikasiList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}