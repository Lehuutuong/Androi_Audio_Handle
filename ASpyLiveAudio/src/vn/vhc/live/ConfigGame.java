package vn.vhc.live;

import android.content.Context;

public class ConfigGame {
	public String getIdsms() {
		return idsms;
	}
	public void setIdsms(String idsms) {
		this.idsms = idsms;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIdclient() {
		return idclient;
	}
	public void setIdclient(String idclient) {
		this.idclient = idclient;
	}
	
	public String getUrkSocket() {
		
		return urkSocket;
	}
	
	public void setUrkSocket(String urkSocket) {
		this.urkSocket = urkSocket;
	}
	public ConfigGame(Context parent) 
	{
		/*
		urkSocket="socket:"+"//"+parent.getAppProperty("Url-sk")+":"+parent.getAppProperty("Port-sk");		
		imsi=System.getProperty("IMEI");
		if(imsi==null)imsi=parent.getAppProperty("imsi");
		activeKey=parent.getAppProperty("activeKey");
		try
		{
			reqInterval=Integer.parseInt(parent.getAppProperty("reqinterval"));
			if(reqInterval<10000)reqInterval=10000;
		}catch(Exception ex)
		{
			reqInterval=30000;
		}
		
		try
		{
			isBackground=(parent.getAppProperty("isbackground").equals("1"));			
		}catch(Exception ex)
		{
			isBackground=false;
		}
		//activeKey="0912905827";
		//urkSocket="socket://210.245.81.205:9991";
		*/
	}
	public int getReqInterval() {
		return reqInterval;
	}
	public void setReqInterval(int reqInterval) {
		this.reqInterval = reqInterval;
	}
	private static ConfigGame _instance;
	public static ConfigGame getInstance(Context parent)
	{		
		if(_instance==null)_instance= new ConfigGame(parent);
		return _instance;
	}	
	private String urkSocket="210.245.81.205";
	private String username=getActiveKey();
	private String idclient="3";
	private String idsms;//key session for java
	private int typeGame;
	private int port=9991;
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	private int diffDegree=3;
	private int reqInterval=30000;
	private boolean isBackground=false;
	public boolean isBackground() {
		return isBackground;
	}
	public void setBackground(boolean isBackground) {
		this.isBackground = isBackground;
	}
	
	public String getActiveKey() {
		//return "0912905827";
		return LocationUtil.IMEI;
		//if(activeKey==null)return LocationUtil.IMEI;
		//return activeKey;
	}
	public void setActiveKey(String activeKey) {
		this.activeKey = activeKey;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	private String activeKey;
	private String imsi;
	public int getTypeGame() {
		return typeGame;
	}
	public int getDiffDegree() {
		return diffDegree;
	}
	public void setDiffDegree(int diffDegree) {
		this.diffDegree = diffDegree;
	}
	public void setTypeGame(int typeGame) {
		this.typeGame = typeGame;
	}
	
}
