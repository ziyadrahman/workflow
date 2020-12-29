package com.example.workflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class enterOrderNo extends AppCompatActivity {
    String TAG="enterOrderNoActivity";
    TextInputEditText orderNoEditText;
    MaterialButton okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_order_no);
        orderNoEditText=findViewById(R.id.orderNoEditText);
        okBtn=findViewById(R.id.okBtn);
        okBtnListener();
    }
    private void okBtnListener() {
        okBtn.setOnClickListener(v -> {
            try {

                String orderNo= orderNoEditText.getText().toString().trim();
//                passValueToActivity(orderNo);
                checkOrderNoValid(orderNo);
            }
            catch (NullPointerException e)
            {
                Log.d(TAG,e.getMessage());
            }
        });
    }
    private void passValueToActivity(String orderNo) {
        Log.d(TAG,"passValueToActivity:runned");
        Intent selectItemActivity=new Intent(getApplicationContext(),selectItemActivity.class);
        selectItemActivity.putExtra("orderNo",orderNo);
        startActivity(selectItemActivity);

    }
    private void checkOrderNoValid(String orderNo) {
        Log.d(TAG,"orderno="+ orderNo);
        Log.d(TAG,"checkOrderNoValid:runned");
        DatabaseReference orderNoActive= FirebaseDatabase.getInstance().
                getReference("orderNoActive");

        orderNoActive.child(orderNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,"onChildAdded:runned");
                if (dataSnapshot.exists())
                {
                    Log.d(TAG,"if:runned");
                    Log.d(TAG,"checkOrderNoValid:runned"+dataSnapshot.getKey());
                    passValueToActivity(orderNo);
                }
                else
                {
                    Toast.makeText(enterOrderNo.this, orderNo+" is not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
