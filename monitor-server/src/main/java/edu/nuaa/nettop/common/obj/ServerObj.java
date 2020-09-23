/*
 * 物理服务器信息
 */
package edu.nuaa.nettop.common.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerObj   implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1089276480856237632L;

	private String mc;
	
	private NetPortObj port;

	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public NetPortObj getPort() {
		return port;
	}

	public void setPort(NetPortObj port) {
		this.port = port;
	}

	public ServerObj() {
		// TODO Auto-generated constructor stub
	}

}
