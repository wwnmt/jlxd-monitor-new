/*
 * 集群的总体服务器资源利用情况
 */
package edu.nuaa.nettop.common.response;

import java.io.Serializable;

public class BoNetServStatus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6761369173362121228L;

	public BoNetServStatus() {
		// TODO Auto-generated constructor stub
	}
	
	private int servnum;  //运行服务器数量
	
	private double cpu;  //cpu使用量GB
	
	private double cpul; //cpu使用率
	
	private double mem; //内存使用量GB
	
	private double meml; //内存使用率
	
    public double getCpu() {
		return cpu;
	}

	public void setCpu(double cpu) {
		this.cpu = cpu;
	}

	public double getMem() {
		return mem;
	}

	public void setMem(double mem) {
		this.mem = mem;
	}

	public double getCpul() {
		return cpul;
	}

	public void setCpul(double cpul) {
		this.cpul = cpul;
	}

	public double getMeml() {
		return meml;
	}

	public void setMeml(double meml) {
		this.meml = meml;
	}

	public int getServnum() {
		return servnum;
	}

	public void setServnum(int servnum) {
		this.servnum = servnum;
	}

    
}
