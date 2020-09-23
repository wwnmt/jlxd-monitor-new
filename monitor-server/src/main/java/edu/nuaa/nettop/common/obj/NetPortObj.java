package edu.nuaa.nettop.common.obj;

import java.io.Serializable;

public class NetPortObj  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5175539200305256518L;

	private String mc;
	
	private String ip;
	
	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}


	public NetPortObj() {
		// TODO Auto-generated constructor stub
	}

}
