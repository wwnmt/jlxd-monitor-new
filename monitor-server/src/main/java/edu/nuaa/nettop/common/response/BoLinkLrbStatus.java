/*
 * 链路流量容量比信息
 */
package edu.nuaa.nettop.common.response;

import java.io.Serializable;


public class BoLinkLrbStatus implements Serializable{	
    /**
     *
     */
    private static final long serialVersionUID = 5525266719213525297L;

    private String llid;  //链路id

    private String mc;  //链路名称

    private double rate;  //流量容量比
    
    public String getLlid() {
        return llid;
    }

    public void setLlid(String llid) {
        this.llid = llid;
    }


    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public BoLinkLrbStatus() {
        // TODO Auto-generated constructor stub
    }

//    public BoLinkLrbStatus(LinkLrbStatusObj obj) {
//        // TODO Auto-generated constructor stub
//        this.setLlid(obj.getId());
//        this.setRate(obj.getRate());
//        this.setMc(obj.getMc());
//        //this.setTpstr(obj.getTpstr());
//    }


}
