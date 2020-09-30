package edu.nuaa.nettop.common.response.ospf;

import java.io.Serializable;

public class BoOSPFAttackPack implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1555872511507572440L;

	public BoOSPFAttackPack() {
		// TODO Auto-generated constructor stub
	}
	
	private String tm;  //攻击时间
	
	private String smip;  //声明的IP
	
	private String pack;  //报文详细信息

	public String getTm() {
		return tm;
	}

	public void setTm(String tm) {
		this.tm = tm;
	}

	public String getSmip() {
		return smip;
	}

	public void setSmip(String smip) {
		this.smip = smip;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}
}
