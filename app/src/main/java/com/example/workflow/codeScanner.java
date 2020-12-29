package com.example.workflow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

public class codeScanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private MaterialButton noQrBtn;
    private static final int RC_PERMISSION = 10;
    private boolean mPermissionGranted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_SHORT).show();
                        checkOrderNoValid(result.getText());
                    }
                });
            }
        });


        mCodeScanner.setErrorCallback(error -> runOnUiThread(
                () -> Toast.makeText(this, "getString(R.string.scanner_error, error)", Toast.LENGTH_LONG).show()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false;
                requestPermissions(new String[] {Manifest.permission.CAMERA}, RC_PERMISSION);
            } else {
                mPermissionGranted = true;
            }
        } else {
            mPermissionGranted = true;
        }

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        noQrBtnListerer();

    }

    private void noQrBtnListerer() {
        noQrBtn=findViewById(R.id.noQrBtn);
        noQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enterOrderNo=new Intent(getApplicationContext(),enterOrderNo.class);
                startActivity(enterOrderNo);


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    private void checkOrderNoValid(String orderNo) {

        DatabaseReference orderNoActive= FirebaseDatabase.getInstance().
                getReference("orderNoActive");

        orderNoActive.child(orderNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {

                    passValueToActivity(orderNo);
                }
                else
                {
                    Toast.makeText(codeScanner.this, orderNo+" is not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void passValueToActivity(String orderNo) {

        Intent selectItemActivity=new Intent(getApplicationContext(),selectItemActivity.class);
        selectItemActivity.putExtra("orderNo",orderNo);
        startActivity(selectItemActivity);

    }
}