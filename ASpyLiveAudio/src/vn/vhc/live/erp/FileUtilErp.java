package vn.vhc.live.erp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileUtilErp {
	public static String FILE_SAVE ="metadata";
	public static String DIR_SAVE ="/sdcard/tmpb/";
	public static String FILE_OFFLINE =DIR_SAVE+FILE_SAVE+".txt";
	
	public static boolean saveTextToMetaData(String nameFile,String time,String note,String data)  
    {
    	try {
    		note=note.replace(",", "");
			File myFile = new File(DIR_SAVE+FILE_SAVE+".txt");
			if(!myFile.exists()) myFile.createNewFile();			
			FileWriter fw= new FileWriter(myFile,true);
			String text=nameFile+","+time+","+note+","+data;
			fw.write(text+"\n");
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {			
			//Toast.makeText(ContextManager.getInstance().getCurrentContext(), e.toString(),
			//		Toast.LENGTH_LONG).show();
			return false;
		}		
    }
	public static HashMap<String, DataMetaData> loadTextFromMetaData()  
    {
		try 
		{
			File myFile = new File(DIR_SAVE+FILE_SAVE+".txt");			
			HashMap<String,DataMetaData> map=new HashMap<String, DataMetaData>();
			FileReader fw= new FileReader(myFile);
		
		    BufferedReader br = new BufferedReader(fw);
		    String line;
		    while ((line = br.readLine()) != null)
		    {
		    	String[] arrLine=line.split(",");		
		    	DataMetaData m= new DataMetaData(arrLine[0], arrLine[2], arrLine[1], arrLine[3]);
		    	map.put(arrLine[0],m);			       
		    }
			fw.close();
			return map;
			
	    } catch (Exception e) {			
			
	    	Log.v("lamdaica1", "Expcetion when loadTextFromMetaData:"+e.toString());
	    	return new HashMap<String, DataMetaData>();			
		}	
    }
    public static File getTempDir()
	{
		File dir = new File(DIR_SAVE);// Environment.getExternalStorageDirectory(), "temp" );
		//dir.listFiles()
		if( dir.exists() == false && dir.mkdirs() == false )
		{
			//Log.e( TAG, "failed to get/create temp directory" );
			return null;
		}
		return dir;
	}
    
    public static void  deleteFileMetaData()
	{
		File dir = new File(DIR_SAVE+FILE_SAVE+".txt");// Environment.getExternalStorageDirectory(), "temp" );
		if( dir.exists() )
		{
			dir.delete();
		}		
	}
    public static File createDir(String pathDir)
	{
		File dir = new File(pathDir);// Environment.getExternalStorageDirectory(), "temp" );
		if( dir.exists() == false && dir.mkdirs() == false )
		{
			//Log.e( TAG, "failed to get/create temp directory" );
			return null;
		}
		return dir;
	}
    /**
     * Helper method to close given output stream ignoring any exceptions.
     */
    public static void close( OutputStream os )
    {
        if( os != null )
        {
            try
            {
                os.close();
            }
            catch( IOException e ) {}
        }
    }
    public static void encryptFile(String aOutputFileName)
    {
    	
    	 try
    	 {
    		  
    		  byte[] arrLast= new byte[10];
     		  byte[] arrFirst= new byte[10];
     		 
    		  //Open the file for both reading and writing
    		  RandomAccessFile rand = new RandomAccessFile(aOutputFileName,"rw"); 
    		  
    		  //tracking first
    		  rand.seek(0);  //Seek to start point of file
    		  rand.read(arrLast);
    		  
    		  //tracking last
    		  rand.seek(rand.length()-arrFirst.length-1);
    		  rand.read(arrFirst); 
    		  
    		  rand.seek(0);
    		  rand.write(arrFirst);
    		  
    		  rand.seek(rand.length()-arrLast.length-1);
    		  rand.write(arrLast);
    		  
    		  rand.close();
    	  }
		  catch(IOException e)
		  {
			  System.out.println(e.getMessage());
		  }
		  
    }
    public static String encryptString(String sString)
    {
    	return sString;
    	/*
    	if(!UtilGame.modeEncrypt) return sString;
    	return SecBase64.encode(sString);    	
    	*/
    }
}
