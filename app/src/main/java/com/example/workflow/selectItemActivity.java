package com.example.workflow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.workflow.scanActivity.alreadyRunned;

public class selectItemActivity extends AppCompatActivity {
    String orderNo;
    ListView itemsListView;
    List<String> itemsList = new ArrayList<>();

    TextView orderNoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_item);
        orderNoView=findViewById(R.id.orderNoText);
        backBtnListener();
        Intent intent=getIntent();
        orderNo=intent.getStringExtra("orderNo");
        fetchItems(orderNo);
        orderNoView.setText(orderNo);
    }
    public void fetchItems(String orderNo)
    {
        DatabaseReference databaseOrderItems= FirebaseDatabase.getInstance().getReference("orders").child(orderNo).child("items");
        databaseOrderItems.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                setItemsList(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                setAdapter();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setItemsList(String itemName) {
        itemsList.add(itemName);
    }

    private void setAdapter() {
        itemsListView = findViewById(R.id.itemsListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                itemsList);

        itemsListView.setAdapter(arrayAdapter);
        itemsListView.setOnItemClickListener((parent, view, position, id) -> {
            String itemName=parent.getItemAtPosition(position).toString();
            toTimerActivity(itemName);
        });


    }
    private void toTimerActivity(String itemName)
    {
        Intent timerActivity=new Intent(getApplicationContext(),timeCounterActivity.class);
        timerActivity.putExtra("orderNo",orderNo);
        timerActivity.putExtra("itemName",itemName);
        startActivity(timerActivity);
    }
    private void backToLastActivity() {
        alreadyRunned=true; //Setting scanActivity
        selectItemActivity.super.finish();
    }
    private void backBtnListener()
    {
        MaterialButton backBtn=findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
        backToLastActivity();
        });
    }
}