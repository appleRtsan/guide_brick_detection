package com.example.projec4;
import  com.example.projec4.Scp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import ch.ethz.ssh2.Connection;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements socket.AsyncTaskResult<Integer> {

    MediaPlayer mediaPlayer;

    private EditText et_Name;
    private TextView tv_Show;
    private Button btn_Show;
    private TextView connect_succeed;
    private ImageView click_image_id;
    private TextView recvmsg;
    public static final int photo_id=1;
    public Bitmap lesimg;
    public socket client = new socket();
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_Name = findViewById(R.id.et_Name);
        tv_Show = findViewById(R.id.tv_Show);
        btn_Show = findViewById(R.id.btn_Show);
        connect_succeed = findViewById(R.id.connect_succeed);
        TextView connect_failed = findViewById(R.id.connect_failed);
        recvmsg = findViewById(R.id.recvmsg);
        //connect_succeed.setVisibility(View.GONE);
        click_image_id = (ImageView)findViewById(R.id.click_image);
        //socket.connectionTestResult = this;
        context = getApplicationContext();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;

        setobj();
        try {
                startActivityForResult(takePictureIntent, photo_id);
                Log.i("Photo", "picture capturing");
            }catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

        btn_Show.setOnClickListener(new View.OnClickListener()
        {
            public void testlog()
            {
                Log.i("On click","set on click");
            }
            @Override
            public void onClick(View v)
            {
                tv_Show.setText((et_Name.getText().toString()));

                Log.i("On click","is on click");
                //client.execute(tv_Show.getText().toString());
                boolean F = true;


            }

        });
    }

    private void setobj() {
        client.connectionTestResult = this;
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {
        if(resultCode != RESULT_CANCELED) {
            // Match the request 'pic id with requestCode
            if (requestCode == photo_id) {
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "photo.jpg");
                Uri uri = Uri.fromFile(file);
                Log.i("OnActivity","test");
                // BitMap is data structure of image file
                // which stor the image in memory
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                lesimg=photo;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                lesimg.compress(Bitmap.CompressFormat.JPEG, 100, out);
                click_image_id.setImageBitmap(lesimg);
                byte[] array = out.toByteArray();
                Byte[] byteObjects = new Byte[array.length];
                int i=0;
//                // Associating Byte array values with bytes. (byte[] to Byte[])
                for(byte b: array)
                    byteObjects[i++] = b;  // Autoboxing.
                client.execute(byteObjects);
            }
        }
    }


    @Override
    public void taskFinish(Integer result) {
        if (result.toString().equals("3")) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.forward);
            mediaPlayer.start();
        }
        else if (result.toString().equals("4")) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
            mediaPlayer.start();
        }
        else if (result.toString().equals("2")) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.right);
            mediaPlayer.start();
        }
        else if (result.toString().equals("1")) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.left);
            mediaPlayer.start();
        }
        else if (result.toString().equals("5")) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.forward_brick);
            mediaPlayer.start();
        }
        try {
            Thread.sleep(5000);
        }catch(InterruptedException e)
        {
            Log.i("a","a");
        }
        doRestart(context);
        Log.i("d","do not restart");
    }

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want

                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.i("restart error", "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e("restart error", "Was not able to restart application, PM null");
                }
            } else {
                Log.e("restart error", "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e("restart error", "Was not able to restart application");
        }
    }


}