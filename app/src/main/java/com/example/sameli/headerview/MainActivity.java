package com.example.sameli.headerview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity  implements SuspensionHeaderLayout.OnTouchConflictListener{
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SuspensionHeaderLayout suspensionHeaderLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        suspensionHeaderLayout = findViewById(R.id.headerlayout);
        recyclerView = findViewById(R.id.recyclerview);
        linearLayoutManager  = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new MyAdapter());
        suspensionHeaderLayout.setTouchConflictListener(this);

    }

    @Override
    public boolean onChildScrollTop() {
        return 0 == linearLayoutManager.findFirstCompletelyVisibleItemPosition();
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
            return 100;
        }
    }

}
