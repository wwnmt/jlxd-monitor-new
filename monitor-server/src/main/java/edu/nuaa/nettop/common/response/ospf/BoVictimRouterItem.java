package edu.nuaa.nettop.common.response.ospf;

import java.io.Serializable;

public class BoVictimRouterItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4514000038653801729L;

	public BoVictimRouterItem() {
		// TODO Auto-generated constructor stub
	}
	
	private String item;   //路由表项
	
	private int status;   //0：正常；1：被篡改；2：被删除
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	

}
