package edu.nuaa.nettop.common.response.ospf;

import java.io.Serializable;

public class BoOSPFPackage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5427469885763766239L;

	public BoOSPFPackage() {
		// TODO Auto-generated constructor stub
	}
	
	private String tm;    //发送时间
	
	private String seqno;  //报文序号
	
	private String ptype;  //报文类型
	
	private String pack;  //报文详细内容

	public String getTm() {
		return tm;
	}

	public void setTm(String tm) {
		this.tm = tm;
	}

	public String getSeqno() {
		return seqno;
	}

	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

}
