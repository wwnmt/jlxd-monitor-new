package edu.nuaa.nettop.common.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerReqParam implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -39406652689694731L;

    public ServerReqParam() {
        // TODO Auto-generated constructor stub
    }

    private String wlid;

    private List<ServerReqObj> sports = new ArrayList<ServerReqObj>();

    public String getWlid() {
        return wlid;
    }

    public void setWlid(String wlid) {
        this.wlid = wlid;
    }

    public List<ServerReqObj> getSports() {
        return sports;
    }

    public void setSports(List<ServerReqObj> sports) {
        this.sports = sports;
    }


}
