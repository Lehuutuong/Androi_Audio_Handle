package vn.vhc.live.erp;

import java.util.List;
import java.util.Vector;

public class UtilTaxi 
{
	public static Vector<DataObject> lstData= new Vector<DataObject>();
	public static void addTo(DataObject o)
	{
		synchronized (lstData) {
			lstData.add(o);
		}
	}
	public static DataObject getData()
	{
		synchronized (lstData) {
			if(lstData.size()>0)
			{
				DataObject o=lstData.get(0);
				return o;
			}
		}
		return null;
	}
	public static void removeData()
	{
		synchronized (lstData) {
			if(lstData.size()>0)
			{
				lstData.remove(0);
			}
		}		
	}
}
