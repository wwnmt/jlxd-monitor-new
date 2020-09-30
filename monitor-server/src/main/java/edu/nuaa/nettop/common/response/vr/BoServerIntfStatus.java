/*
 * 物理服务器链路流量信息
 */
package edu.nuaa.nettop.common.response.vr;

import java.io.Serializable;

public class BoServerIntfStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7673980574891914040L;

    private String mc;   //服务器名称

    private String dkmc;  //端口名

    private String ip;   //端口IP

    private Long tp;  //端口流量 kbps

    private int zt;   //状态

    public BoServerIntfStatus() {
        // TODO Auto-generated constructor stub
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getDkmc() {
        return dkmc;
    }

    public void setDkmc(String dkmc) {
        this.dkmc = dkmc;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getTp() {
        return tp;
    }

    public void setTp(Long tp) {
        this.tp = tp;
    }

    public int getZt() {
        return zt;
    }

    public void setZt(int zt) {
        this.zt = zt;
    }


}
