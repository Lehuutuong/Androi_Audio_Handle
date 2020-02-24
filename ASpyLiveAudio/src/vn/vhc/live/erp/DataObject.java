package vn.vhc.live.erp;

import java.io.Serializable;

public class DataObject implements Serializable{
	private String _id;
	private String _data;
	private String _time;
	
	public String get_id() {
		return _id;
	}
	public DataObject(String id, String data,String time) {
		super();
		_id = id;
		_data = data;
		_time=time;
	}
	
	public String get_time() {
		return _time;
	}
	public void set_time(String time) {
		_time = time;
	}
	public void set_id(String id) {
		_id = id;
	}	
	public String get_data() {
		return _data;
	}
	public void set_data(String data) {
		_data = data;
	}
	public String toQueryString()
	{
		UrlParamEncoder encoder=(new UrlParamEncoder());
		encoder.addParam("id", _id);	
		encoder.addParam("data",_data);
		encoder.addParam("time",_time);
		return  encoder.toString();
	}
}
