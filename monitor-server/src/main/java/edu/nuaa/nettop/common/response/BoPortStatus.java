package edu.nuaa.nettop.common.response;


import edu.nuaa.nettop.common.obj.PortStatusObj;

import java.io.Serializable;


public class BoPortStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5800734370333468077L;

    private String nm;  //接口名称

    private Short st;    //接口状态

    private long br;  //字节接收量

    private long bs;  //字节发送量

    private long pr;  //数据包接收量

    private long ps;  //数据包发送量

    public BoPortStatus() {
        // TODO Auto-generated constructor stub
    }

    public BoPortStatus(PortStatusObj obj) {
        // TODO Auto-generated constructor stub
        this.setBr(obj.getBr());
        this.setBs(obj.getBs());
        this.setNm(obj.getNm());
        this.setPr(obj.getPr());
        this.setPs(obj.getPs());
        this.setSt(obj.getSt());
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public Short getSt() {
        return st;
    }

    public void setSt(Short st) {
        this.st = st;
    }

    public long getBr() {
        return br;
    }

    public void setBr(long br) {
        this.br = br;
    }

    public long getBs() {
        return bs;
    }

    public void setBs(long bs) {
        this.bs = bs;
    }

    public long getPr() {
        return pr;
    }

    public void setPr(long pr) {
        this.pr = pr;
    }

    public long getPs() {
        return ps;
    }

    public void setPs(long ps) {
        this.ps = ps;
    }

}
