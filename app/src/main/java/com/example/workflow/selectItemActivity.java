package com.example.workflow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.workflow.scanActivity.alreadyRunned;

public class selectItemActivity extends AppCompatActivity {
    String orderNo;
    ListView itemsListView;
    List<String> itemsList = new ArrayList<>();

    TextView orderNoView;
    TextView designerNameText;

    CheckBox alterationCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_item);
        orderNoView=findViewById(R.id.orderNoText);
        designerNameText=findViewById(R.id.designerNameText);
        alterationCheckBox=findViewById(R.id.alterationCheckBox);
        backBtnListener();
        Intent intent=getIntent();
        orderNo=intent.getStringExtra("orderNo");
        fetchItems();
        fetchDesignerId();
        orderNoView.setText(orderNo);



    }
    public Boolean checkAlteration()
    {


        return alterationCheckBox.isChecked();

    }

    public void passValueThroughIntent( Intent intent,String key, String value)
    {

        intent.putExtra(key,value);
    }
    public void passValueThroughIntent(Intent intent ,String key, boolean value)
    {

        intent.putExtra(key,value);
    }
    public void fetchItems()
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
    public void fetchDesignerId()
    {
        DatabaseReference databaseOrderDesignerId= FirebaseDatabase.getInstance().getReference("orders").child(orderNo).child("designerId");
        databaseOrderDesignerId.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String designerId=dataSnapshot.getValue().toString();
                fetchDesignerName(designerId);

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
    public void fetchDesignerName(String designerId)
    {
        DatabaseReference databaseUsers=FirebaseDatabase.getInstance().getReference("users").child(designerId);
        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String designerName= Objects.requireNonNull(dataSnapshot.getValue()).toString();
                setDesignerNameText("designerName");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setDesignerNameText(String designerName)
    {
        this.designerNameText.setText(designerName);
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
            Intent timerActivity=new Intent(getApplicationContext(),timeCounterActivity.class);
            String itemName=parent.getItemAtPosition(position).toString();
            if (checkAlteration())
            {
                passValueThroughIntent(timerActivity,"alterationStatus",true);
                alterationCheckBox.setChecked(false);

            }

            passValueThroughIntent(timerActivity,"itemName",itemName);
            passValueThroughIntent(timerActivity,"orderNo",orderNo);
            startActivity(timerActivity);
        });


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