package com.faislll.projectakhir.aktivitas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faislll.projectakhir.R;
import com.faislll.projectakhir.fragment.FragmentProfile;

public class AktivitasLihatPp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktivitas_lihat_pp);
        Intent intent = getIntent();
        String image = intent.getStringExtra(FragmentProfile.KEY_IMAGE);
        String username = intent.getStringExtra(FragmentProfile.USERNAME);

        Toolbar toolbar = findViewById(R.id.toolbar_detail_pp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(username);
        toolbar.setTitleTextColor(getResources().getColor(R.color.warnaPutih));


        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

        ImageView imageViewPp = findViewById(R.id.image_view_pp_detail_pp);


        Glide.with(getApplicationContext()).load(image).apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(imageViewPp);

        ImageView close = findViewById(R.id.image_view_close_detail_pp);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}