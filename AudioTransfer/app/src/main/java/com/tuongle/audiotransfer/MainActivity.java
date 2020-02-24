package com.tuongle.audiotransfer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MicrophoneInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tuongle.audiotransfer.AudioRecord.RecordAudioManager;
import com.tuongle.audiotransfer.AudioRecord.UploadToServerTask;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.devices.WOWZAudioDevice;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.mp4.WOWZMP4Writer;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;


public class MainActivity extends AppCompatActivity implements WOWZStatusCallback, View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private WowzaGoCoder goCoder;
    private WOWZAudioDevice goCoderAudioDevice;

    // The broadcast configuration settings
    private WOWZBroadcastConfig goCoderBroadcastConfig;
    private WOWZBroadcast goCoderBroadcaster;
    private  TextView label;
    private Button recodeAndUpload;
    private Chronometer chronometer;
    private Button broadcastButton;
    private Boolean isStreaming = false;
    private EditText lengthPackage;
    private Thread t;
    private int lengPackage ;
    protected WOWZMP4Writer mMP4Writer          = null;

    // Properties needed for Android 6+ permissions handling
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;
    private boolean mPermissionsGranted = true;
    private String[] mRequiredPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerAndInitializeSDK();
         broadcastButton = findViewById(R.id.broadcast_button);
        label = findViewById(R.id.live_label);
        recodeAndUpload = findViewById(R.id.btSendAudio);
        chronometer = findViewById(R.id.chronometer);
        lengthPackage = findViewById(R.id.edtLength);
        broadcastButton.setOnClickListener(this);
        recodeAndUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lengPackage = Integer.parseInt(lengthPackage.getText().toString());
                RecordAudioManager mRecordAudioManager = new RecordAudioManager();
                if(lengthPackage.getText().toString()==null){
                    recodeAndUpload.setEnabled(false);
                     Toast.makeText(MainActivity.this,"Input long of package audio before send to server!",Toast.LENGTH_LONG).show();
                }
                if(!(recodeAndUpload.getText()=="Recording......")){
                    try{
              //          lengthPackage.setEnabled(false);

                        chronometer.start();
                        recodeAndUpload.setText("Recording......");
                        mRecordAudioManager.startRecordAudio(1000*lengPackage);
                  //      uploadFileBase64(new File(Environment.getExternalStorageDirectory() + "/MyRecorder"));

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else if(recodeAndUpload.getText()=="Recording......"){
                     chronometer.stop();
                     chronometer.setBase(SystemClock.elapsedRealtime());

                     mRecordAudioManager.stopRecordAudio();
                     recodeAndUpload.setText("SEND AUDIO PACKAGE TO SERVER");
                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void registerAndInitializeSDK() {
        // Initialize the GoCoder SDK
        goCoder = WowzaGoCoder.init(getApplicationContext(), "GOSK-CF46-010C-DA42-7C80-565E");

        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WOWZError goCoderInitError = WowzaGoCoder.getLastError();
            Toast.makeText(this,
                    "GoCoder SDK error: " + goCoderInitError.getErrorDescription(),
                    Toast.LENGTH_LONG).show();
            return;
        }
        goCoderAudioDevice = new WOWZAudioDevice();
        goCoderAudioDevice.setAudioSource(AudioSource.MIC);
        //goCoderAudioDevice.setAudioSource(AudioSource.DEFAULT);
        configureBroadcastStream();
    }

    private void startClock(){
         label.setVisibility(View.VISIBLE);
          t = new Thread() {

            @Override
            public void run() {
                final long startedAt = System.currentTimeMillis();
                long updatedAt = System.currentTimeMillis();
                try {
                    while (!isInterrupted()) {
                        if (System.currentTimeMillis() - updatedAt > 1000) {
                            updatedAt = System.currentTimeMillis();


                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    long diff = System.currentTimeMillis() - startedAt;
                                    long second = diff / 1000 % 60;
                                    long min = diff / 1000 / 60;
                                    long hours = min/60;
                                    String timer = getString(R.string.publishing_label, hours, min,second);
                                    label.setText(timer); //change clock to your textview
                                }
                            });

                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }
    private void stopClock(){
        label.setText("");
        label.setVisibility(View.INVISIBLE);
        t.interrupt();

    }
    private void configureBroadcastStream() {
        // Create a broadcaster instance
        goCoderBroadcaster = new WOWZBroadcast();
        goCoderBroadcastConfig = new WOWZBroadcastConfig();
        goCoderBroadcastConfig.setAudioBitRate(64000);
        goCoderBroadcastConfig.setAudioSampleRate(44100);
        goCoderBroadcastConfig.setAudioChannels(WOWZMediaConfig.AUDIO_CHANNELS_STEREO);
        // Set the connection properties for the target Wowza Streaming Engine server or Wowza Cloud account
//        goCoderBroadcastConfig.setHostAddress("814723.entrypoint.cloud.wowza.com");
//        goCoderBroadcastConfig.setPortNumber(1935);
//        goCoderBroadcastConfig.setStreamName("8781ee05");
//        goCoderBroadcastConfig.setApplicationName("app-43bb ");
//        goCoderBroadcastConfig.setUsername("client40136");
//        goCoderBroadcastConfig.setPassword("6b4e7492");
        goCoderBroadcastConfig.setHostAddress("192.168.100.4");
        goCoderBroadcastConfig.setPortNumber(1935);
        goCoderBroadcastConfig.setStreamName("myStream");
        goCoderBroadcastConfig.setApplicationName("live");
        goCoderBroadcastConfig.setUsername("wowza");
        goCoderBroadcastConfig.setPassword("woza");
        goCoderBroadcastConfig.setAudioBroadcaster(goCoderAudioDevice);
    }
    /**
     * Enable Android's sticky immersive full-screen mode
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    /**
     * Called when an activity is brought to the foreground
     */
    @Override
    protected void onResume() {
        super.onResume();

        // If running on Android 6 (Marshmallow) or above, check to see if the necessary permissions
        // have been granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionsGranted = hasPermissions(this, mRequiredPermissions);
            if (!mPermissionsGranted)
                ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
        } else
            mPermissionsGranted = true;
    }

    /**
     * Callback invoked in response to a call to ActivityCompat.requestPermissions() to interpret
     * the results of the permissions request
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mPermissionsGranted = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // Check the result of each permission granted
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionsGranted = false;
                    }
                }
            }
        }
    }

    /**
     * Utility method to check the status of a permissions request for an array of permission identifiers
     *
     * @param context
     * @param permissions
     * @return
     */
    private static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions)
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }

    @Override
    public void onWZStatus(WOWZStatus wzStatus) {
        // A successful status transition has been reported by the GoCoder SDK
        final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (wzStatus.getState()) {
            case WOWZState.STARTING:
                statusMessage.append("Broadcast initialization");
                break;

            case WOWZState.READY:
                statusMessage.append("Ready to begin streaming");
                break;

            case WOWZState.RUNNING:
                statusMessage.append("Streaming is active");
                break;

            case WOWZState.STOPPING:
                statusMessage.append("Broadcast shutting down");
                break;

            case WOWZState.IDLE:
                statusMessage.append("The broadcast is stopped");
                break;

            default:
                return;
        }

        // Display the status message using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, statusMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onWZError(final WOWZStatus wzStatus) {
        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,
                        "Streaming error: " + wzStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onClick(View v) {
            broadcastButton.setText("Streaming.......");
            // return if the user hasn't granted the app the necessary permissions
            if (!mPermissionsGranted) return;
            // Ensure the minimum set of configuration settings have been specified necessary to
            // initiate a broadcast streaming session
               WOWZStreamingError configValidationError = goCoderBroadcastConfig.validateForBroadcast();
               if(configValidationError!=null){configValidationError=null;}

            if (configValidationError != null) {
                goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, this);
                startClock();
                isStreaming = true;

                  Toast.makeText(this, configValidationError.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else
                if (goCoderBroadcaster.getStatus().isRunning()) {
                // Stop the broadcast that is currently running
                goCoderBroadcaster.endBroadcast(this);
                broadcastButton.setText("LiveStream Audio");
                stopClock();
            } else {
                // Start streaming
                goCoderBroadcaster.startBroadcast(goCoderBroadcastConfig, this);
                startClock();
                isStreaming = true;
            }

    }
    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        //
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "GoCoderSDK MP4s");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
               WOWZLog.warn(TAG, "failed to create the directory in which to store the MP4");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator
                + timeStamp + ".mp4");
    }
    private void uploadFileBase64(File dir) throws IOException {

        String pdfPattern = ".3gp";
        File[] listFile = dir.listFiles();
        if (listFile != null) {
      //      for (File file : listFile){
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    uploadFileBase64(listFile[i]);
                } else {
                    if ((listFile[i].getName().endsWith(pdfPattern))&&(!listFile[i].getName().contains("OK_"))){
                          final File fileToRename = listFile[i];

                        //Do what ever u want
                        File file = new File(listFile[i].getAbsolutePath());

                        byte[] bytes = FileUtils.readFileToByteArray(file);

                        String encoded = Base64.encodeToString(bytes, 0);
                        String audioID =   listFile[i].getName().substring(0,19);
                        String _time = DateFormat.getDateTimeInstance().format(new Date());

                        Log.e("base64", "-----" + encoded);

                        // Upload hình  lên server
                        UploadToServerTask uploadToServer=new UploadToServerTask(MainActivity.this,encoded);
                        uploadToServer.execute();
//                        if(uploadToServer.getStatus()==AsyncTask.Status.FINISHED){
//                           String abPath = fileToRename.getAbsolutePath();
//                            String newPath = abPath.substring(0,abPath.lastIndexOf(fileToRename.separator));
//                            fileToRename.renameTo(new File(newPath + "/OK_" + fileToRename.getName()));
//
//                        }

                    }
                }
            }
        }
    }
}