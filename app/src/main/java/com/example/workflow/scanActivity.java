package com.example.workflow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.workflow.common.BarcodeScanningProcessor;
import com.example.workflow.common.CameraSource;
import com.example.workflow.common.CameraSourcePreview;
import com.example.workflow.common.FrameMetadata;
import com.example.workflow.common.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.workflow.common.BarcodeScanner.Constants.KEY_CAMERA_PERMISSION_GRANTED;
import static com.example.workflow.common.BarcodeScanner.Constants.PERMISSION_REQUEST_CAMERA;

public class scanActivity extends AppCompatActivity {

    String TAG="scanActivity";
    Boolean isCalled;
    GraphicOverlay barcodeOverlay;
    CameraSourcePreview preview;



    BarcodeScanningProcessor barcodeScanningProcessor;
    private CameraSource mCameraSource=null;
   static boolean alreadyRunned=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        barcodeOverlay=findViewById(R.id.barcodeOverlay);
        preview=findViewById(R.id.preview);




        if (preview != null)
            if (preview.isPermissionGranted(true, mMessageSender))
                new Thread(mMessageSender).start();
    }


    private void createCameraSource() {
        FirebaseVisionBarcodeDetectorOptions options=new FirebaseVisionBarcodeDetectorOptions.
                Builder().setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();
        FirebaseVisionBarcodeDetector detector= FirebaseVision.getInstance().
                getVisionBarcodeDetector(options);
        mCameraSource=new CameraSource(this,barcodeOverlay);
        mCameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);

        barcodeScanningProcessor=new BarcodeScanningProcessor(detector);
        barcodeScanningProcessor.setBarcodeResultListener(getBarcodeResultLister());


        mCameraSource.setMachineLearningFrameProcessor(barcodeScanningProcessor);
        startCameraSource();
    }



    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());

        Log.d(TAG, "startCameraSource: " + code);

        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, PERMISSION_REQUEST_CAMERA);
            dlg.show();
        }

        if (mCameraSource != null && preview != null && barcodeOverlay != null) {
            try {
                Log.d(TAG, "startCameraSource: ");
                preview.start(mCameraSource, barcodeOverlay);
            } catch (IOException e) {
                Log.d(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        } else
            Log.d(TAG, "startCameraSource: not started");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        preview.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /***
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /***
     * Stops the camera.
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (preview!=null)
            preview.stop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isCalled=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource!=null)
        {
            mCameraSource.release();
        }
    }
    @SuppressLint("HandlerLeak")
    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG,"handleMessage: ");
            if (preview!=null)
                createCameraSource();
        }
    };
    private final Runnable mMessageSender= () ->{
        Log.d(TAG,"mMessageSender: ");
        Message msg=mHandler.obtainMessage();
        Bundle bundle=new Bundle();
        bundle.putBoolean(KEY_CAMERA_PERMISSION_GRANTED,false);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    };


    private BarcodeScanningProcessor.BarcodeResultListener getBarcodeResultLister() {
        return new BarcodeScanningProcessor.BarcodeResultListener() {
            @Override
            public void onSuccess(@Nullable Bitmap originalCameraImage, @NonNull
                    List<FirebaseVisionBarcode> barcodes, @NonNull FrameMetadata frameMetadata,
                                  @NonNull GraphicOverlay graphicOverlay) {
                String orderNo="";
                Log.d(TAG,"onSuccess:barcodeSize "+barcodes.size());
                for (FirebaseVisionBarcode barcode:barcodes){
                    Log.d(TAG,"onSuccess:RawValue "+barcode.getRawValue());
                    orderNo=barcode.getRawValue();

                    Log.d(TAG,"onSuccess:Format "+barcode.getFormat());
                    Log.d(TAG,"onSuccess:ValueType "+barcode.getValueType());




                    }


                if (barcodes.size()>=1&& alreadyRunned) {
                    alreadyRunned=false;
                    barcodeScanningProcessor.stop();
                    mCameraSource.stop();
                    checkOrderNoValid(orderNo);

                }



            }

            @Override
            public void onFailure(@NonNull Exception e) {

            }
        };
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
                    Toast.makeText(scanActivity.this, orderNo+" is not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void passValueToActivity(String orderNo) {
        Log.d(TAG,"passValueToActivity:runned");
        Intent selectItemActivity=new Intent(getApplicationContext(),selectItemActivity.class);
        selectItemActivity.putExtra("orderNo",orderNo);
        startActivity(selectItemActivity);

    }


}

