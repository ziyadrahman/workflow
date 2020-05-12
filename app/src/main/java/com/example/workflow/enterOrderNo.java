package com.example.workflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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
                passValueToActivity(orderNo);
            }
            catch (NullPointerException e)
            {
                Log.d(TAG,e.getMessage());
            }
        });
    }
    private void passValueToActivity(String orderNo) {
        Log.d(TAG,"passValueToActivity:runned");
        Intent controlActivity=new Intent(getApplicationContext(),timeCounterActivity.class);
        controlActivity.putExtra("orderNo",orderNo);
        startActivity(controlActivity);

    }
}
