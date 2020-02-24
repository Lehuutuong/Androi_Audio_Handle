package vn.vhc.live.erp;

public class DataUI {
	private String _id;
	private String _title;
	private String _titleToCompare;	
	private String _time;
	private String _placefullname;
	private String _placeaddress;
	public DataUI(String id, String title, String time, String placefullname,
			String placeaddress, String extra1) {
		super();
		_id = id;
		_title = title;
		_titleToCompare=UtilErp.TrimVietnameseMark(title);
		_time = time;
		_placefullname = placefullname;
		_placeaddress = placeaddress;
		_extra1 = extra1;
	}
	public String get_extra1() {
		return _extra1;
	}
	public void set_extra1(String extra1) {
		_extra1 = extra1;
	}
	private String _extra1;
	
	public DataUI(String id, String title) {
		super();
		_id = id;
		_title = title;
	}
	public DataUI(String id, String title,String time,String placefullname,String placeaddress) {
		super();
		_id = id;
		_title = title;
		_time = time;		
		_placefullname=placefullname;
		_placeaddress=placeaddress;
		
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String id) {
		_id = id;
	}
	public String get_title() {
		return _title;
	}
	public void set_title(String title) {
		_title = title;
	}
	public String get_time() {
		//if(_time!=null) 
		//	return UtilGame.getInstance().GetDateToView(_time);
		return _time;
	}
	public void set_time(String time) {
		_time = time;
	}
	public String get_placefullname() {
		return _placefullname;
	}
	public void set_placefullname(String placefullname) {
		_placefullname = placefullname;
	}
	public String get_placeaddress() {
		return _placeaddress;
	}
	public void set_placeaddress(String placeaddress) {
		_placeaddress = placeaddress;
	}
	public String toString(){return _title;}
	public String get_titleToCompare() {
		return _titleToCompare;
	}
	public void set_titleToCompare(String titleToCompare) {
		_titleToCompare = titleToCompare;
	}
}
