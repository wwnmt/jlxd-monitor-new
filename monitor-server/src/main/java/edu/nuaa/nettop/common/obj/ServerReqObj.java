/*
 * 物理设备监控请求对象类
 */
package edu.nuaa.nettop.common.obj;

import java.io.Serializable;

public class ServerReqObj implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3273833000063386481L;
    private String smc;    //服务器id
    private String pmc;   //端口名

    public ServerReqObj() {
        // TODO Auto-generated constructor stub
    }

    public String getSmc() {
        return smc;
    }

    public void setSmc(String smc) {
        this.smc = smc;
    }

    public String getPmc() {
        return pmc;
    }

    public void setPmc(String pmc) {
        this.pmc = pmc;
    }


}
