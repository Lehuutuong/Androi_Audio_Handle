package vn.vhc.live.erp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.util.Base64;
import android.widget.Toast;



public class UtilErp {
	public static String PARENT_CHAMCONG = "1";
    public static String PARENT_DSDAILY = "2";//old version is 2, new version is 3
    public static String PARENT_KHLV = "4";
    public static String PARENT_MESSAGE = "10";
    
    public static String PARENT_KIEMHANG_DAILY = "11";//
    
	public static boolean isDebug=false;
	public static int totalScreen=0;
	public static int totalScreenLive=0;
	public static boolean compressData=true;
	public static String GetCompressUrl()
	{
		if(compressData) return "&compress=1";
		return "";
	}
	public static String TrimVietnameseMark(String str)
    {
        str = str.replace(".", " ");
        str = str.replace(",", " ");
        str = str.replace("_", " ");
        str = str.replace(";", " ");
        str = str.replace("?", " ");
        str = str.replace("(", "");
        str = str.replace(")", "");
        str = str.replace("[", "");
        str = str.replace("]", "");
        str = str.replace("{", "");
        str = str.replace("}", "");
        str = str.replace("<", "");
        str = str.replace(">", "");
        str = str.replace("'", "");

        //while (str.indexOf("  ") > 0)
        //{
        str = str.replace("  ", " ");
        //}
            
        str = str.replace("ấ", "a");
        str = str.replace("ầ", "a");
        str = str.replace("ẩ", "a");
        str = str.replace("ẫ", "a");
        str = str.replace("ậ", "a");
        
        str = str.replace("Ấ", "A");
        str = str.replace("Ầ", "A");
        str = str.replace("Ẩ", "A");
        str = str.replace("Ẫ", "A");
        str = str.replace("Ậ", "A");
        
        str = str.replace("ắ", "a");
        str = str.replace("ằ", "a");
        str = str.replace("ẳ", "a");
        str = str.replace("ẵ", "a");
        str = str.replace("ặ", "a");
        
        str = str.replace("Ắ", "A");
        str = str.replace("Ằ", "A");
        str = str.replace("Ẳ", "A");
        str = str.replace("Ẵ", "A");
        str = str.replace("Ặ", "A");
        
        str = str.replace("á", "a");
        str = str.replace("à", "a");
        str = str.replace("ả", "a");
        str = str.replace("ã", "a");
        str = str.replace("ạ", "a");
        str = str.replace("â", "a");
        str = str.replace("ă", "a");
        
        str = str.replace("Á", "A");
        str = str.replace("À", "A");
        str = str.replace("Ả", "A");
        str = str.replace("Ã", "A");
        str = str.replace("Ạ", "A");
        str = str.replace("Â", "A");
        str = str.replace("Ă", "A");
        
        str = str.replace("ế", "e");
        str = str.replace("ề", "e");
        str = str.replace("ể", "e");
        str = str.replace("ễ", "e");
        str = str.replace("ệ", "e");
        
        str = str.replace("Ế", "E");
        str = str.replace("Ề", "E");
        str = str.replace("Ể", "E");
        str = str.replace("Ễ", "E");
        str = str.replace("Ệ", "E");
        
        str = str.replace("é", "e");
        str = str.replace("è", "e");
        str = str.replace("ẻ", "e");
        str = str.replace("ẽ", "e");
        str = str.replace("ẹ", "e");
        str = str.replace("ê", "e");
        
        str = str.replace("É", "E");
        str = str.replace("È", "E");
        str = str.replace("Ẻ", "E");
        str = str.replace("Ẽ", "E");
        str = str.replace("Ẹ", "E");
        str = str.replace("Ê", "E");
        
        str = str.replace("í", "i");
        str = str.replace("ì", "i");
        str = str.replace("ỉ", "i");
        str = str.replace("ĩ", "i");
        str = str.replace("ị", "i");
        
        str = str.replace("Í", "I");
        str = str.replace("Ì", "I");
        str = str.replace("Ỉ", "I");
        str = str.replace("Ĩ", "I");
        str = str.replace("Ị", "I");
        
        str = str.replace("ố", "o");
        str = str.replace("ồ", "o");
        str = str.replace("ổ", "o");
        str = str.replace("ỗ", "o");
        str = str.replace("ộ", "o");
        
        str = str.replace("Ố", "O");
        str = str.replace("Ồ", "O");
        str = str.replace("Ổ", "O");
        str = str.replace("Ô", "O");
        str = str.replace("Ộ", "O");
        
        str = str.replace("ớ", "o");
        str = str.replace("ờ", "o");
        str = str.replace("ở", "o");
        str = str.replace("ỡ", "o");
        str = str.replace("ợ", "o");
        
        str = str.replace("Ớ", "O");
        str = str.replace("Ờ", "O");
        str = str.replace("Ở", "O");
        str = str.replace("Ỡ", "O");
        str = str.replace("Ợ", "O");
        
        str = str.replace("ứ", "u");
        str = str.replace("ừ", "u");
        str = str.replace("ử", "u");
        str = str.replace("ữ", "u");
        str = str.replace("ự", "u");
        
        str = str.replace("Ứ", "U");
        str = str.replace("Ừ", "U");
        str = str.replace("Ử", "U");
        str = str.replace("Ữ", "U");
        str = str.replace("Ự", "U");
        
        str = str.replace("ý", "y");
        str = str.replace("ỳ", "y");
        str = str.replace("ỷ", "y");
        str = str.replace("ỹ", "y");
        str = str.replace("ỵ", "y");
        
        str = str.replace("Ý", "Y");
        str = str.replace("Ỳ", "Y");
        str = str.replace("Ỷ", "Y");
        str = str.replace("Ỹ", "Y");
        str = str.replace("Ỵ", "Y");
        
        str = str.replace("Đ", "D");
        str = str.replace("Đ", "D");
        str = str.replace("đ", "d");
        
        str = str.replace("ó", "o");
        str = str.replace("ò", "o");
        str = str.replace("ỏ", "o");
        str = str.replace("õ", "o");
        str = str.replace("ọ", "o");
        str = str.replace("ô", "o");
        str = str.replace("ơ", "o");
        
        str = str.replace("Ó", "O");
        str = str.replace("Ò", "O");
        str = str.replace("Ỏ", "O");
        str = str.replace("Õ", "O");
        str = str.replace("Ọ", "O");
        str = str.replace("Ô", "O");
        str = str.replace("Ơ", "O");
        
        str = str.replace("ú", "u");
        str = str.replace("ù", "u");
        str = str.replace("ủ", "u");
        str = str.replace("ũ", "u");
        str = str.replace("ụ", "u");
        str = str.replace("ư", "u");

        str = str.replace("Ú", "U");
        str = str.replace("Ù", "U");
        str = str.replace("Ủ", "U");
        str = str.replace("Ũ", "U");
        str = str.replace("Ụ", "U");
        str = str.replace("Ư", "U");

       // lê thanh lâm
       //str = str.ToLower();

        return str;
    }
	public static String decompress(String zipText)  
	{
		try
		{
			if(!compressData) return zipText;
			if (zipText.equals("[]")) return zipText;
			if (zipText.equals("")) return zipText;
			
		    byte[] compressed = Base64.decode(zipText, Base64.DEFAULT);
		    if (compressed.length > 4)
		    {
		        GZIPInputStream gzipInputStream = new GZIPInputStream(
		                new ByteArrayInputStream(compressed, 4,
		                        compressed.length - 4));
	
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        for (int value = 0; value != -1;) {
		            value = gzipInputStream.read();
		            if (value != -1) {
		                baos.write(value);
		            }
		        }
		        gzipInputStream.close();
		        baos.close();
		        String sReturn = new String(baos.toByteArray(), "UTF-8");
		        return sReturn;
		    }
		    else
		    {
		        return "";
		    }
		}catch(Exception ex)
		{
			return "";
		}
	}
	public static String decompressData(String zipText)  
	{
		try
		{
			if (zipText == "[]") return zipText;
			if (zipText.equals("")) return zipText;
			
		    byte[] compressed = Base64.decode(zipText, Base64.DEFAULT);
		    if (compressed.length > 4)
		    {
		        GZIPInputStream gzipInputStream = new GZIPInputStream(
		                new ByteArrayInputStream(compressed, 4,
		                        compressed.length - 4));
	
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        for (int value = 0; value != -1;) {
		            value = gzipInputStream.read();
		            if (value != -1) {
		                baos.write(value);
		            }
		        }
		        gzipInputStream.close();
		        baos.close();
		        String sReturn = new String(baos.toByteArray(), "UTF-8");
		        return sReturn;
		    }
		    else
		    {
		        return "";
		    }
		}catch(Exception ex)
		{
			return "";
		}
	}
	public static String compressString(String str)
	{
		try
		{
	        if (str == null || str.length() == 0) {
	            return str;
	        }
	        //System.out.println("String length : " + str.length());
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        GZIPOutputStream gzip = new GZIPOutputStream(out);
	        gzip.write(str.getBytes());
	        gzip.close();
	        
	        String outStr =Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
	        //System.out.println("Output String lenght : " + outStr.length());
	        return outStr;
		}catch(Exception ex)
		{
			return "";
		}
     }
	public static String ReadFromFile(String nameFile) 
	{
		// TODO Auto-generated method stub
		try 
        {
        	File myFile = new File("/sdcard/tmpb/"+nameFile+".txt");
        	if(!myFile.exists()) return "";
        	Reader myReader= new BufferedReader(new InputStreamReader(new FileInputStream(myFile), "UTF-8"));
        
        	StringBuilder fileContent = new StringBuilder();
        	int c;
            while((c=myReader.read())!=-1)
            {
        		fileContent.append((char)c);
            }
            myReader.close();        	
            return fileContent.toString();
        } catch (Exception e) {
            UIManager.getInstance().showMsg("Exception when ReadFromFile:"+e.toString());
            return "";
        }              
	}
	public static void SaveToFile(String filename, String string) 
	{
        try
        {
        	 if(string.equals("")) return;
         	 if(string.toLowerCase().startsWith("error:")) return;
        	 File myFile = new File("/sdcard/tmpb/"+filename+".txt");
             FileOutputStream fOut = new FileOutputStream(myFile,false);
             Writer myWriter= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myFile), "UTF-8"));

             myWriter.append(string);
             myWriter.close();
             fOut.close();
            //Toast.makeText(getBaseContext(), "Done writing SD 'mysdfile.txt'",Toast.LENGTH_SHORT).show();
        } catch (Exception e) 
        {
        	UIManager.getInstance().showMsg("Exception when ReadFromFile:"+e.toString());
                    }
	}
}
