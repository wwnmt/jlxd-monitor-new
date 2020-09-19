package edu.nuaa.nettop.common.obj;

import java.io.Serializable;
import java.util.List;

public class NetStatusObj implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1523520452761539861L;

    private String wlid;  //网络ID

    private long tm;

    private List<LinkStatusObj> links;  //网络链路状态

    private List<String> errordevs;  //异常设备ID列表（状态为0）

    public NetStatusObj() {
        // TODO Auto-generated constructor stub
    }

    public long getTm() {
        return tm;
    }

    public void setTm(long tm) {
        this.tm = tm;
    }

    public String getWlid() {
        return wlid;
    }

    public void setWlid(String wlid) {
        this.wlid = wlid;
    }

    public List<LinkStatusObj> getLinks() {
        return links;
    }

    public void setLinks(List<LinkStatusObj> links) {
        this.links = links;
    }

    public List<String> getErrordevs() {
        return errordevs;
    }

    public void setErrordevs(List<String> errordevs) {
        this.errordevs = errordevs;
    }

}
