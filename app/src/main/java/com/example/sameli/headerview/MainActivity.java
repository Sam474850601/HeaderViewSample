package com.example.sameli.headerview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SuspensionLinearLayout suspensionLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        suspensionLinearLayout = findViewById(R.id.headerlayout);
        recyclerView = findViewById(R.id.recyclerview);
        linearLayoutManager  = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new MyAdapter());
        suspensionLinearLayout.setOnSuspensionListener(new SuspensionLinearLayout.OnSuspensionListener() {
            @Override
            public void onScroll(float persent) {
                Log.e("SuspensionHeaderLayout", "persent:"+persent);
            }
        });
       // suspensionLinearLayout.setExternalHeight(100);

    }




    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MyAdapter extends RecyclerView.Adapter{


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }


    public void onTest(View view ){
        suspensionLinearLayout.hide();
    }

}
