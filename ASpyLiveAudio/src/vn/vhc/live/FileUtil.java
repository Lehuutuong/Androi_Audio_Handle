package vn.vhc.live;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

public class FileUtil {
	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;
	
	public static String FILE_SAVE =UtilGame.FILE_SAVE;
	public static String KEY_PTRACKER =UtilGame.KEY_PTRACKER;
	public static String DIR_SAVE =UtilGame.DIR_SAVE;
	public static String FILE_OFFLINE =DIR_SAVE+FILE_SAVE+".txt";
	
	public void setUpExternalStorage()
	{
		String state = Environment.getExternalStorageState();
	
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}
	/**
     * Save the given text to a file for printing.
     */
	public static boolean isExistFileOffline()  
	{
		try 
		{
			File myFile = new File(FILE_OFFLINE);
			return myFile.exists();
			
		} catch (Exception e) {			
			//Toast.makeText(ContextManager.getInstance().getCurrentContext(), e.toString(),
			//		Toast.LENGTH_LONG).show();
			return false;
		}		
	}
	public static String[] getFileToUpload(String filter)  
	{
		try 
		{
			File myFile = new File(DIR_SAVE);
			FilenameFilter m1= new OnlyPtracker(filter);
			
			return myFile.list(m1);
			
		} catch (Exception e) {			
			//Toast.makeText(ContextManager.getInstance().getCurrentContext(), e.toString(),
			//		Toast.LENGTH_LONG).show();
			return new String[]{};
		}		
	}
	public static void deleteFileAfterDownload(String filter)  
	{
		try 
		{	
			File myFile = new File("/sdcard/download/");
			FilenameFilter m1= new OnlyPtrackerDownload();			
			String[] arrFiles= myFile.list(m1);
			for(String f:arrFiles)
			{
				//Toast.makeText(ContextManagerCore.getInstance().getCurrentContext(),"Delete file:"+f,Toast.LENGTH_LONG).show();
				deleteFile("/sdcard/download/"+f);
			}			
		} catch (Exception e) {			
			//Toast.makeText(ContextManagerCore.getInstance().getCurrentContext(), e.toString(),Toast.LENGTH_LONG).show();
		}		
	}
	public static void renameFile(String file1,String file2)  
	{
		try
		{
			File from = new File(DIR_SAVE,file1);
			File to = new File(DIR_SAVE,file2);
			from.renameTo(to);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
    public static boolean saveTextToTempFile(String text)  
    {
    	try {
    		text=FileUtil.encryptString(text);
			File myFile = new File(DIR_SAVE+FILE_SAVE+".txt");
			//if(!myFile.exists())
			myFile.createNewFile();
			FileWriter fw= new FileWriter(myFile,true);
			fw.write(text+"\n");
			fw.flush();
			fw.close();
			return true;
				
		} catch (Exception e) {	
			e.printStackTrace();
			//Toast.makeText(ContextManager.getInstance().getCurrentContext(), e.toString(),
			//		Toast.LENGTH_LONG).show();
			return false;
		}
		
    }
    public static boolean saveTextToXMLFile(String text,String file)  
    {
    	try 
    	{
			File myFile = new File(DIR_SAVE+file);
			//if(!myFile.exists())
			myFile.createNewFile();
			FileWriter fw= new FileWriter(myFile,true);
			fw.write(text+"");
			fw.flush();
			fw.close();
			return true;			
		} catch (Exception e) {			
			//Toast.makeText(ContextManager.getInstance().getCurrentContext(), e.toString(),
			//		Toast.LENGTH_LONG).show();
			return false;
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
    public static void  deleteFile(String pathFile)
	{
		File dir = new File(pathFile);// Environment.getExternalStorageDirectory(), "temp" );
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
    		  if(!UtilGame.modeEncrypt) return;
    		  
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
    		  
    		  rand.seek(rand.length()-arrFirst.length-1);
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
    public static String encryptStringPassword(String sString)
    {
    	return SecBase64.encode(sString);    	
    }
}
class OnlyPtracker implements FilenameFilter {
	private String _filter="";
	public OnlyPtracker(String filter) 
	{			
		_filter=filter;
	}
	public boolean accept(File dir, String name) 
	{
		if(_filter.equals(""))
		{
			return (name.startsWith("ok_") && name.toLowerCase().endsWith(".jpg"))  ||(name.startsWith("ok_") && name.toLowerCase().endsWith(".3gp")) || name.toLowerCase().endsWith(".txt");
		}
		else if(_filter.equals(".txt"))
		{
			return name.endsWith(".txt");
		}
		else return name.toLowerCase().startsWith("ok_") && name.toLowerCase().endsWith(_filter);
	}
}
class OnlyPtrackerDownload implements FilenameFilter {
	public OnlyPtrackerDownload() 
	{			
		
	}
	public boolean accept(File dir, String name) 
	{
		return (name.toLowerCase().startsWith("ptracker") && name.toLowerCase().endsWith(".apk"))  ;
	}
}