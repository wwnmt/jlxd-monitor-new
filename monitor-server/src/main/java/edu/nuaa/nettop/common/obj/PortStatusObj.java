package edu.nuaa.nettop.common.obj;

import java.io.Serializable;

public class PortStatusObj implements Serializable {
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

    public PortStatusObj() {
        // TODO Auto-generated constructor stub
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
