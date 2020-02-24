package vn.vhc.live.erp;

import java.util.ArrayList;

public class Products {
	public String ProductName;
	public String ProductNumber;
	public Boolean ProductActive;
	public String ID;
	public String ProductNameE;
	public String UnitID;
	public String UnitTtile;
	public String[] DatasourceId ;//= new Unit[] {};
	public String[] DatasourceText;
	public String TimeOrder;
	public String Code;
	public String Stt;
	public String Address;
	public String Note;
	ArrayList<Products> prdoucts;
	//public int UnitID;
	  public Products(String ProductName,String ProductNumber,String ID,String[] DatasourceId,String[] DatasourceText ){
			this.ProductName=ProductName;
			this.ProductNumber=ProductNumber;
			this.ProductNameE= UtilErp.TrimVietnameseMark(ProductName);
			this.ID=ID;
			//this.DATASOURCE1=unit;
			
			UnitID="-1";
			if(DatasourceId.length>0){
				
				this.UnitID=DatasourceId[0];
			}
			else{
				DatasourceId = new String[1];
				DatasourceId[0]="-1";
			}
			UnitTtile="Đơn vị";
			if(DatasourceText.length>0){
				this.UnitTtile=DatasourceText[0];
			}
			else{
				DatasourceText = new String[1];
				DatasourceText[0]="Đơn vị";
			}
			this.DatasourceId=DatasourceId;
			this.DatasourceText=DatasourceText;
		}
	  public Products(String ProductName,String ID,String TimeOrder,String Code,ArrayList<Products> products,String stt){
		  this.ProductName=ProductName;
		  this.ID=ID;
		  this.TimeOrder=TimeOrder;
		  this.Code=Code;
		  this.prdoucts=products;
		  this.ProductNameE= UtilErp.TrimVietnameseMark(ProductName);
		  this.Stt=stt;
	  }
	  public Products(){
			
		}

}
