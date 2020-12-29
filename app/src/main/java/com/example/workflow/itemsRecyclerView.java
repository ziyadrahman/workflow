package com.example.workflow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.workflow.common.item;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.yashovardhan99.timeit.Stopwatch;

import java.util.Queue;

public class itemsRecyclerView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseRecyclerOptions<item> options;
    private TextView timerText;
    private  Boolean isStart = true;

    Stopwatch stopwatch = new Stopwatch();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_recycler_view);
        recyclerView=findViewById(R.id.itemsRecyclerView);
        timerText=findViewById(R.id.timerText);
        int numberOfColumns=2;
        recyclerView.setLayoutManager
                (new GridLayoutManager(this,numberOfColumns));
        fetchData();
        fetchDataToAdapter();
    }

    private void fetchData() {
        final Query query= FirebaseDatabase.getInstance().getReference()
                .child("workFlow").child("productionItems").orderByKey();

        options =new FirebaseRecyclerOptions.Builder<item>().setQuery(
                query, new SnapshotParser<item>() {
                    @NonNull
                    @Override
                    public item parseSnapshot(@NonNull DataSnapshot snapshot) {
                        String itemNo=snapshot.getKey();
                        String orderNo=(String) snapshot.child("orderNo").getValue();
                        String itemName=(String) snapshot.child("itemName").getValue();
                        return new item(itemNo,orderNo,itemName);
                    }
                }).build();
    }

    private void fetchDataToAdapter() {
        adapter=new FirebaseRecyclerAdapter<item,ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull item item) {
               viewHolder.itemNo.setText(item.getItemNo());
               viewHolder.itemName.setText(item.getItemName());
               viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (isStart)
                       {
                         startTimer();
                         isStart=false;
                       }
                       else
                       {
                           pauseTimer();
                           isStart=true;
                       }
                   }
               });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_card,
                        parent, false);
                return new ViewHolder(view);
            }

        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView itemNo;
        private TextView orderNo;
        private TextView itemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNo=itemView.findViewById(R.id.itemsCardOrderNo);
            itemName=itemView.findViewById(R.id.itemName);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    private void startTimer() {
        stopwatch.setTextView(timerText);
        stopwatch.start();
    }
    private void pauseTimer() {
        stopwatch.pause();
    }
    private void resumeTimer() {
        stopwatch.resume();
    }

}