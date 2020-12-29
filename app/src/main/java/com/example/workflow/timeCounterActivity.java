package com.example.workflow;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yashovardhan99.timeit.Stopwatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import me.zhanghai.android.materialplaypausedrawable.MaterialPlayPauseButton;
import me.zhanghai.android.materialplaypausedrawable.MaterialPlayPauseDrawable;

import static com.example.workflow.scanActivity.alreadyRunned;
import static java.lang.Boolean.getBoolean;

public class timeCounterActivity extends AppCompatActivity {
    String TAG="timerActivity";
    MaterialPlayPauseButton materialPlayPauseButton;
    MaterialPlayPauseDrawable.State currentState;
    MaterialPlayPauseDrawable.State changeState;

    LinearLayout controlLayout;

    MaterialButton startBtn;
    MaterialButton completeBtn;
    MaterialButton backBtn;

    TextView itemNameText;
    TextView timerText;
    TextView designerNameText;
    TextView orderNoText;

    String orderNo;
    String itemName;
    String to;
    String newOrAlteration="new";
    Boolean alterationStatus=false;
    static String operatorName="tajammul";
    static String operation="handwork";



    Stopwatch stopwatch = new Stopwatch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null)
        {
           to= savedInstanceState.getString("mCurrentTrail");
           goneStartBtn();
           showTimerText();
           showControls();
        }
        setContentView(R.layout.activity_time_counter);
        isAllowedUser();

        materialPlayPauseButton = findViewById(R.id.play_pause_btn);
        completeBtn=findViewById(R.id.completeBtn);
        backBtn=findViewById(R.id.backBtn);
        designerNameText=findViewById(R.id.designerNameText);
        itemNameText=findViewById(R.id.itemNameText);
        orderNoText=findViewById(R.id.orderNoText);
        timerText=findViewById(R.id.timerText);
        Intent intent=getIntent();
        orderNo=intent.getStringExtra("orderNo");
        itemName=intent.getStringExtra("itemName");
        alterationStatus = Objects.requireNonNull(intent.getExtras()).getBoolean("alterationStatus");
        if (alterationStatus)
        {
            newOrAlteration="alteration";
        }
//        fetchData();
        controlBtnListener();
        startBtnListener();
        completeBtnListener();

        Log.d(TAG,"orderNo"+orderNo);
        orderNoText.setText(orderNo);
        itemNameText.setText(itemName);
        showItemNameText();
//        onDisconnect();






    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("mCurrentTrail",to);


    }

    private void showItemNameText() {
        itemNameText.setVisibility(View.VISIBLE);
    }

    private void isAllowedUser() {
        DatabaseReference userState=FirebaseDatabase.getInstance().getReference("activeUsers").child(operatorName);
        userState.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isAllowsUser=false;
                if (dataSnapshot.exists())
                {
                    isAllowsUser=(Boolean)dataSnapshot.getValue();
                    if (isAllowsUser)
                    {
                        showStartBtn();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void completeBtnListener() {
        completeBtn=findViewById(R.id.completeBtn);
        completeBtn.setOnClickListener(v -> {
            updateTo();
            updateOperationStatus();
            backToLastActivity();

        });
    }

    private void updateOperationStatus() {
        DatabaseReference operationStatusDb=FirebaseDatabase.getInstance().getReference("workFlow")
                .child("operationStatus");
        operationStatusDb.child(orderNo).child(itemName).child(operation).setValue(true);

    }

    private void fetchData() {

        DatabaseReference workFlow= FirebaseDatabase.getInstance().getReference("workFlow")
                .child("orders").child(orderNo);
        workFlow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String designerName;
                String itemName;
                if (dataSnapshot.exists()) {

                    designerName = Objects.requireNonNull(dataSnapshot.child("designerName").
                            getValue()).toString();
                    itemName = Objects.requireNonNull(dataSnapshot.child("itemName").
                            getValue()).toString();
                    Log.d(TAG, "designerName: " + designerName);
                    Log.d(TAG, "itemName: " + itemName);
                    designerNameText.setText(designerName);
                    itemNameText.setText(itemName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startBtnListener() {
        startBtn=findViewById(R.id.startBtn);
        startBtn.setOnClickListener(v -> {
            goneStartBtn();
            showTimerText();
            startTimer();
            showControls();
            updateFrom();

        });

    }

    private void startTimer() {
        stopwatch.setTextView(timerText);
        stopwatch.start();
    }

    private void updateFrom() {

        updateTimeInDb("from");
        updateStatus(true);
    }
    private void updateTo() {
        DatabaseReference workFlowOrders=FirebaseDatabase.getInstance().
                getReference("workFlow").child("workFlowOrders").child(orderNo).child(newOrAlteration).child(itemName).child(operation)
                .child(operatorName);
        workFlowOrders.child("to"+to).setValue(getDateTime());
        updateStatus(false);


    }
    private void updateStatus(Boolean isRunning)
    {
        DatabaseReference updateStatus=FirebaseDatabase.getInstance().
                getReference("workFlow").child("productionStatus").child(operatorName);
               updateStatus.setValue(isRunning);
    }
    private void onDisconnect(){
        DatabaseReference updateStatus=FirebaseDatabase.getInstance().
                getReference("workFlow").child("productionStatus").child(operatorName);
        updateStatus.onDisconnect().setValue(false);


    }
    private void showControls() {
        controlLayout=findViewById(R.id.controlLayout);
        controlLayout.setVisibility(View.VISIBLE);
    }

    private void showTimerText() {
        timerText.setVisibility(View.VISIBLE);

    }

    private void goneStartBtn() {
        startBtn=findViewById(R.id.startBtn);
        startBtn.setVisibility(View.GONE);
    }
    private void showStartBtn()
    {

        startBtn=findViewById(R.id.startBtn);
        startBtn.setVisibility(View.VISIBLE);
    }

    private void controlBtnListener()
    {
        materialPlayPauseButton.setOnClickListener(v -> {
            checkCurrentState();
            updateState();

            materialPlayPauseButton.jumpToState(changeState);

        });

    }
    private void updateState() {
        if (currentState.equals(MaterialPlayPauseDrawable.State.Play))
        {
            changeState=MaterialPlayPauseDrawable.State.Pause;
            updateTo();
            goneCompleteBtn();
            showBackBtn();
            pauseTimer();


        }
        else if (currentState.equals(MaterialPlayPauseDrawable.State.Pause))
        {
            changeState=MaterialPlayPauseDrawable.State.Play;
            updateFrom();
            resumeTimer();
            goneBackBtn();
            showCompleteBtn();
        }
    }

    private void showBackBtn() {
     backBtn.setVisibility(View.VISIBLE);
     backBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             backToLastActivity();
         }
     });

    }
    private void goneBackBtn() {
        backBtn.setVisibility(View.GONE);

    }

    private void resumeTimer() {
        stopwatch.resume();
    }

    private void pauseTimer() {
        stopwatch.pause();
    }

    private void hideCompleteBtn() {
        completeBtn.setVisibility(View.INVISIBLE);
    }
    private void goneCompleteBtn(){completeBtn.setVisibility(View.GONE);}

    private void showCompleteBtn() {
        completeBtn.setVisibility(View.VISIBLE);
    }




    public void checkCurrentState() {
        currentState = materialPlayPauseButton.getState();
    }


    public void updateTimeInDb(String fromORto)
    {


        DatabaseReference workFlowOrders=FirebaseDatabase.getInstance().
                getReference("workFlow").child("workFlowOrders").child(orderNo).child(newOrAlteration).child(itemName).child(operation)
                .child(operatorName);
        workFlowOrders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String trailCount=String.valueOf(dataSnapshot.getChildrenCount()/2+1);
                    to=trailCount;
                    Log.d(TAG,"trailCount "+trailCount);
                    workFlowOrders.child(fromORto+trailCount).setValue(getDateTime());
                }
                else
                {
                    workFlowOrders.child(fromORto+1).setValue(getDateTime());
                    to="1";

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getDateTime() {

        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        Calendar dateTime=Calendar.getInstance();
        return dateFormat.format(dateTime.getTime());
    }

    private void backToLastActivity() {
        alreadyRunned=true;
        timeCounterActivity.super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateStatus(false);
    }
}

