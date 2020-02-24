package vn.vhc.live;

import java.util.Vector;
public class UtilMemory 
{
	
	
	public static Vector<DataObject> lstData= new Vector<DataObject>();
	public static int currentIndexProcessing=-1;
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
				currentIndexProcessing=lstData.size()-1;
				DataObject o=lstData.get(currentIndexProcessing);
				return o;
			}		
		}
		return null;
	}
	public static void removeData()
	{
		if(currentIndexProcessing==-1) return;
		synchronized (lstData) {
			if(lstData.size()>currentIndexProcessing)
			{
				lstData.remove(currentIndexProcessing);
			}
		}		
	}
	public static boolean flushToFileIfNeed()
	{
		UtilGame.checkSDCard();
		if(!UtilGame.isSdCard) return false;
		synchronized (lstData) 
		{
			if(lstData.size()>=2 || (BatteryLog.levelBattery>0 && BatteryLog.levelBattery<=5))
			{					
				//Vector<DataObject> lstTemp=new Vector<DataObject>();
				StringBuilder sb =  new StringBuilder();
				for(int jx=0;jx<=lstData.size()-1;jx++)
				{
					DataObject oData=lstData.get(jx);
					sb.append(oData.toFileString()+"\n");			
				}
				/*
				if(lstTemp.size()==0) return false;
				for(int jx=0;jx<=lstTemp.size()-1;jx++)
				{
					DataObject oData=lstTemp.get(jx);						
					sb.append(oData.toFileString()+"\n");
					lstData.remove(oData);
				}
				//currentIndexProcessing=-1;
				*/				
				boolean isFlush=FileUtil.saveTextToTempFile(sb.toString());
				if(isFlush)lstData= new Vector<DataObject>();
				return true;
			}
		}	
		return false;
	}
}
