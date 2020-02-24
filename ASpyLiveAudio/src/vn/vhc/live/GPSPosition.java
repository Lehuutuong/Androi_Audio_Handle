package vn.vhc.live;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;



/**
 * @author lamlt
 *
 */
public class GPSPosition implements Runnable 
{
	
	private Context _app;

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 10000; // in Milliseconds
    
    protected LocationManager locationManager;
    protected LocationListener myListenter;
    protected LocationListener myListenterNetwork;
    
	//private Location currentLocation;
    public GPSPosition(Context app) 
	{
		_app=app;
		startLocationUpdate();
	}
    public  void removeGps() 
	{
    	try
    	{
	    	if(locationManager==null)return;
	    	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		    {
	    		locationManager.removeUpdates(myListenter);
		    }
	    	if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
	        {
	    		locationManager.removeUpdates(myListenterNetwork);
	        }
    	}catch(Exception ex123){}
	}
	private  void startLocationUpdate() 
	{
		
		locationManager = (LocationManager)_app.getSystemService(Context.LOCATION_SERVICE); 
		if(locationManager==null)return;
		
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	    {
			UtilGame.GPS_SAT=false;
			myListenter= new MyLocationListener();
			locationManager.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, 
	                MINIMUM_TIME_BETWEEN_UPDATES, 
	                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
	                myListenter
	        );			
			//currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		    //updateLocation (currentLocation);
	    }
		else UtilGame.GPS_SAT=false;
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
			UtilGame.GPS_NETWORK=true;
			myListenterNetwork= new MyLocationListenerNetwork();
			locationManager.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER, 
	                0, 
	                0,  
	                myListenterNetwork);	        
	        
        }
		else 
			UtilGame.GPS_NETWORK=false;
//		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//        {
//			myListenter= new MyLocationListener();
//			locationManager.requestLocationUpdates(
//	                LocationManager.NETWORK_PROVIDER, 
//	                MINIMUM_TIME_BETWEEN_UPDATES, 
//	                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,  
//	                myListenter);
//	        
//	        //currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//	        //updateLocation (currentLocation);
//        }
    
//        if (lm == null)
//    	{
//    		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);    
//        
//	        locationListener = new MyLocationListener();
//	        
//	        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
//	        {
//	        
//	        	Log.i(TAG, "starting up GPS location provider...");
//	        	
//		        lm.requestLocationUpdates(
//		            LocationManager.GPS_PROVIDER, 
//		            LOCATION_UPDATE_TIME, 
//		            LOCATION_UPDATE_DISTANCE, 
//		            locationListener);
//		       
//		        currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		        updateLocation (currentLocation);
//	        }
//	      
//	        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//	        {
//		    	Log.i(TAG, "starting up Network location provider...");
//		    	
//		        lm.requestLocationUpdates(
//		                LocationManager.NETWORK_PROVIDER, 
//		                LOCATION_UPDATE_TIME, 
//			            LOCATION_UPDATE_DISTANCE,  
//		                locationListener);
//		        
//		        currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		        updateLocation (currentLocation);
//	        }
//    	}
       
    }
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		startLocationUpdate();

	}
	 
	private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) 
        {
//            String message = String.format(
//                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
//                    location.getLongitude(), location.getLatitude()
//            );
            //Toast.makeText(LocationTracker.this, message, Toast.LENGTH_LONG).show();
        	 try
 			{
	            if (location != null)
		        {
	            	//updateLocation(location);
	            	
	            	LocationTracker.lat=String.valueOf(location.getLatitude());
	            	LocationTracker.lon=String.valueOf(location.getLongitude());
			        LocationTracker.accuracy=String.valueOf(location.getAccuracy());
			        LocationTracker.speed=String.valueOf(location.getSpeed());
			        
			        LocationTracker.lon = Double.toString(round(location.getLongitude(), 4));
		            LocationTracker.lat = Double.toString(round(location.getLatitude(), 4));
		            LocationTracker.alt=Double.toString(round(location.getAltitude(), 4));
		            LocationTracker.course=Float.toString((float) round(location.getAltitude(), 4));
		            LocationTracker.accuracy=Double.toString(round((double)location.getAccuracy(), 4));
		            try
		            {
		            	LocationTracker.time=UtilGame.getInstance().GetStringFromTimeStamp(location.getTime());
		            	LocationTracker.speed = Float.toString((float) round(location.getSpeed(), 2));
		            }
		            catch(Exception ex)
		            {LocationTracker.speed = "0";}
			         
		           
		        }
		        else 
		        {	
		        	LocationTracker.lat="";
			        LocationTracker.lon="";
			        LocationTracker.accuracy="";
			        LocationTracker.speed="";
			        LocationTracker.time="";
			        LocationTracker.speed="";
		        }
            }catch(Exception ex)
			{
				UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
						"GpsThread:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
	
			}           
        }
        public void onStatusChanged(String s, int i, Bundle b) 
        {
//            Toast.makeText(LocationTracker.this, "Provider status changed",
//                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
        	UtilGame.GPS_SAT=false;
        	LocationTracker.lat="";
	        LocationTracker.lon="";
	        LocationTracker.accuracy="";
	        LocationTracker.speed="";
	        LocationTracker.time="";
	        LocationTracker.speed="";
//            Toast.makeText(_app.getApplicationContext(),
//                    "Your GPS turned off,Please turn on GPS",
//                   Toast.LENGTH_LONG).show();            
            
//             AlertDialog.Builder builder = new AlertDialog.Builder(_app);  
//             builder.setMessage("Your GPS is disabled! Would you like to enable it?")  
//                  .setCancelable(false)  
//                  .setPositiveButton("Enable GPS",  
//                       new DialogInterface.OnClickListener(){  
//                       public void onClick(DialogInterface dialog, int id){  
//                            showGpsOptions();  
//                       }  
//                  });  
//                  builder.setNegativeButton("Do nothing",  
//                       new DialogInterface.OnClickListener(){  
//                       public void onClick(DialogInterface dialog, int id){  
//                            dialog.cancel();  
//                       }  
//                  });  
//             AlertDialog alert = builder.create();  
//             alert.show();  
        }
//        private void showGpsOptions(){  
//        	         Intent gpsOptionsIntent = new Intent(  
//        	                 android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
//        	         _app.getApplicationContext().startActivity(gpsOptionsIntent);  
//        }  
        public void onProviderEnabled(String s) {
        	
//            Toast.makeText(LocationTracker.this,
//                    "Provider enabled by the user. GPS turned on",
//                    Toast.LENGTH_LONG).show();
        }
        private  double round(double d, int decimal) {
            double powerOfTen = 1;

            while (decimal-- > 0) {
                powerOfTen *= 10.0;
            }

            double d1 = d * powerOfTen;
            int d1asint = (int) d1; // Clip the decimal portion away and cache the cast, this is a costly transformation.

            double d2 = d1 - d1asint; // Get the remainder of the double.

            // Is the remainder > 0.5? if so, round up, otherwise round down (lump in .5 with > case for simplicity).
            return (d2 >= 0.5 ? (d1asint + 1) / powerOfTen : (d1asint) / powerOfTen);
        }
       
    }
	private class MyLocationListenerNetwork implements LocationListener {

        public void onLocationChanged(Location location) 
        {
        	try
 			{
	            if (location != null)
		        {
	            	//updateLocation(location);
	            	
	            	LocationTracker.lat=String.valueOf(location.getLatitude());
	            	LocationTracker.lon=String.valueOf(location.getLongitude());
			        LocationTracker.accuracy=String.valueOf(location.getAccuracy());
			        LocationTracker.speed=String.valueOf(location.getSpeed());
			        
			        LocationTracker.lon = Double.toString(round(location.getLongitude(), 4));
		            LocationTracker.lat = Double.toString(round(location.getLatitude(), 4));
		            LocationTracker.alt=Double.toString(round(location.getAltitude(), 4));
		            LocationTracker.course=Float.toString((float) round(location.getAltitude(), 4));
		            LocationTracker.accuracy=Double.toString(round((double)location.getAccuracy(), 4));
		            try
		            {
		            	LocationTracker.time=UtilGame.getInstance().GetStringFromTimeStamp(location.getTime());
		            	LocationTracker.speed = Float.toString((float) round(location.getSpeed(), 2));
		            }
		            catch(Exception ex)
		            {LocationTracker.speed = "0";}
			         
		           
		        }
		        else 
		        {	
		        	LocationTracker.lat="";
			        LocationTracker.lon="";
			        LocationTracker.accuracy="";
			        LocationTracker.speed="";
			        LocationTracker.time="";
			        LocationTracker.speed="";
			        
		        }
            }catch(Exception ex)
			{
				UtilMemory.addTo(new DataObject(LocationUtil.IMEI, TypeData.ERROR_LOG, 
						"GpsThreadNetWork:"+ex.toString(),  String.valueOf(BatteryLog.levelBattery), "", "", UtilGame.getInstance().GetStringNow()));
	
			}           
        }
        public void onStatusChanged(String s, int i, Bundle b) 
        {

        }
        public void onProviderDisabled(String s) {
        	
        	UtilGame.GPS_NETWORK=false;
        	LocationTracker.lat="";
        	LocationTracker.lon="";
	        LocationTracker.accuracy="";
	        LocationTracker.speed="";
	        LocationTracker.time="";
	        LocationTracker.speed="";
	        
        }
        public void onProviderEnabled(String s) {
        	

        }
        private  double round(double d, int decimal) {
            double powerOfTen = 1;

            while (decimal-- > 0) {
                powerOfTen *= 10.0;
            }

            double d1 = d * powerOfTen;
            int d1asint = (int) d1; // Clip the decimal portion away and cache the cast, this is a costly transformation.

            double d2 = d1 - d1asint; // Get the remainder of the double.

            // Is the remainder > 0.5? if so, round up, otherwise round down (lump in .5 with > case for simplicity).
            return (d2 >= 0.5 ? (d1asint + 1) / powerOfTen : (d1asint) / powerOfTen);
        }
       
    }
}


 
 
 
