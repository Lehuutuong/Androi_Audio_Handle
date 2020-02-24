package vn.vhc.live;

import java.net.URLEncoder;

public class DataObject 
{
	private String _id;
	private int _type;//type gps log :1=gps,2=incominglog or incominglog,3:battery log,7: sms log
	
	private String _data;
	private String _levelBattery;
	private String _tel;
	private String _mode;
	private String _extradata="";
	
	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String id) {
		_id = id;
	}
	public int get_type() {
		return _type;
	}
	
	private String _time="";
	public String get_time() {
		return _time;
	}
	public void set_time(String time) {
		_time = time;
	}
	
	public void set_type(int type) {
		_type = type;
	}
	public String get_data() {
		return _data;
	}
	public void set_data(String data) {
		_data = data;
	}
	public String get_levelBattery() {
		return _levelBattery;
	}
	public void set_levelBattery(String levelBattery) {
		_levelBattery = levelBattery;
	}
	public String get_tel() {
		return _tel;
	}
	public void set_tel(String tel) {
		_tel = tel;
	}
	public String get_mode() {
		return _mode;
	}
	public void set_mode(String mode) {
		_mode = mode;
	}
	public void set_extradata(String extradata) {
		_extradata = extradata;
	}
	public DataObject(String id, int type, String data, String levelBattery,
			String tel, String mode) {
		super();
		_id = id;
		_type = type;
		_data = data;
		_levelBattery = levelBattery;
		_tel = tel;
		_mode = mode;
	}
	
	public DataObject(String id, int type, String data, String levelBattery,
			String tel, String mode,String time) {
		super();
		_id = id;
		_type = type;
		_data = data;
		_levelBattery = levelBattery;
		_tel = tel;
		_mode = mode;
		_time=time;
	}
	
	public String toUrlString()
	{
		//gps log
		if(_type==TypeData.GPS_LOG)
		{
			String xurl=HttpData.prefixUrlData()+"handlePositionXXX.aspx?data="+_data
			+"&iddevice="+_id+"&batery="+String.valueOf(_levelBattery)+"&type="+_type;
			return xurl;
		}
		//calling log
		if(_type==TypeData.CALL_LOG)
		{
			//String Url="http://all.viethonggps.vn:12411/services/m/handleLogCall.aspx?"+
			String xurl=HttpData.prefixUrlData()+"handlePositionXXX.aspx?data="+_data
			+"&iddevice="+_id+"&tel="+_tel
		 	+"&mode="+_mode+"&batery="+String.valueOf(_levelBattery)
		 	+"&time="+_time	
		 	+"&type="+_type;						
		    return xurl;
		}	
		if(_type==TypeData.VIDEO_LOG|| _type==TypeData.AUDIO_LOG|| _type==TypeData.IMG_LOG)
		{
			String xurl=HttpData.prefixUrlData()+"handlePositionXXX.aspx?iddevice="+_id
		    +"&batery="+String.valueOf(_levelBattery)+"&type="+_type
			+"&data="+_data
			+"&time="+_time;
			return xurl;
		}
		if(_type==TypeData.SMS_LOG)
		{
			String xurl=HttpData.prefixUrlData()+"handlePositionXXX.aspx?iddevice="+_id
		    +"&batery="+String.valueOf(_levelBattery)+"&type="+_type
		    +"&content="+URLEncoder.encode(_extradata)+"&mode="+_mode
		    +"&tel="+_tel
		    +"&time="+_time		    
			+"&data="+_data;
			
		    return xurl;
		}
		if(_type==TypeData.WEB_LOG)
		{
			String xurl=HttpData.prefixUrlData()+"handlePositionXXX.aspx?iddevice="+_id
		    +"&batery="+String.valueOf(_levelBattery)+"&type="+_type+"&content="+_extradata+"&mode="+_mode
		    +"&tel="+_tel
		    +"&time="+_time		    
			+"&data="+_data;			
		    return xurl;
		}
		if(_type==TypeData.ERROR_LOG)
		{
			String xurl=HttpData.prefixUrlData()+"handlePositionXXX.aspx?data="+_data
			+"&iddevice="+_id+"&batery="+String.valueOf(_levelBattery)+"&type="+_type
			+"&time="+_time;
			
		    return xurl;
		}
		return "";
	}
	public String toFileString()
	{
		//gps log
		if(_type==TypeData.GPS_LOG)
		{
			String xurl="data="+_data
			+"&iddevice="+_id+"&batery="+String.valueOf(_levelBattery)+"&type="+_type;
			return xurl;
		}
		
		//calling log
		if(_type==TypeData.CALL_LOG)
		{
			//String Url="http://all.viethonggps.vn:12411/services/m/handleLogCall.aspx?"+
			String xurl="data="+_data
			+"&iddevice="+_id+"&tel="+_tel
		 	+"&mode="+_mode+"&batery="+String.valueOf(_levelBattery)
		 	+"&time="+_time	
		 	+"&type="+_type;			
		    return xurl;
		}	
		if(_type==TypeData.VIDEO_LOG|| _type==TypeData.AUDIO_LOG|| _type==TypeData.IMG_LOG)
		{
			String xurl="iddevice="+_id
		    +"&batery="+String.valueOf(_levelBattery)+"&type="+_type+"&ldc=offline"		    
			//+"&batery="+String.valueOf(_levelBattery)+"&type=10&ldc=offline"		    
			+"&data="+_data
			+"&time="+_time;
		    return xurl;
		}
		if(_type==TypeData.SMS_LOG)
		{
			String xurl="iddevice="+_id
		    +"&batery="+String.valueOf(_levelBattery)+"&type="+_type
		    +"&content="+URLEncoder.encode(_extradata)+"&mode="+_mode
		    +"&tel="+_tel
		    +"&time="+_time		    
			+"&data="+_data;
		    return xurl;
		}	
		if(_type==TypeData.WEB_LOG)
		{
			String xurl="iddevice="+_id
		    +"&batery="+String.valueOf(_levelBattery)+"&type="+_type+"&content="+_extradata+"&mode="+_mode
		    +"&tel="+ _tel
		    +"&time="+_time		    
			+"&data="+_data;
		    return xurl;
		}	
		//error log
		if(_type==TypeData.ERROR_LOG)
		{
			String xurl="data="+_data
			+"&iddevice="+_id+"&batery="+String.valueOf(_levelBattery)
			+"&type="+_type+"&time="+_time;
		    return xurl;
		}
		return "";
	}
	public boolean isCouldBeFlush()
	{		
		return true;
		//return (_type==TypeData.GPS_LOG || _type==TypeData.CALL_LOG ||_type==TypeData.SMS_LOG );
	}
	//http://localhost:42588/services/m/handlePositionX.aspx?351824057359414%40351824057359414%40107025209%40452%4004%4041023%40CID%4020120907070536%4094&iddevice=351824057359414&batery=94&type=1
}
