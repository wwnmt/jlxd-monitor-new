package edu.nuaa.nettop.common.response;


import edu.nuaa.nettop.common.obj.LinkStatusObj;
import edu.nuaa.nettop.common.obj.NetStatusObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoNetStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1523520452761539861L;

    private String wlid;  //网络ID

    private long tm;

    private List<BoLinkStatus> links = new ArrayList<>();  //网络链路状态

    private List<String> errordevs = new ArrayList<>();   //异常设备ID列表（状态为0或者端口有状态为0的）

    public BoNetStatus() {
        // TODO Auto-generated constructor stub
    }

    public BoNetStatus(NetStatusObj obj) {
        // TODO Auto-generated constructor stub
        this.setWlid(obj.getWlid());
        this.setTm(obj.getTm());
        List<LinkStatusObj> olinks = obj.getLinks();
        if (null != olinks && 0 < olinks.size()) {
            for (LinkStatusObj olink : olinks) {
                BoLinkStatus link = new BoLinkStatus(olink);
                this.getLinks().add(link);
            }
        }

        List<String> errors = obj.getErrordevs();
        if (null != errors && 0 < errors.size()) {
            for (String error : errors) {
                this.getErrordevs().add(error);
            }
        }
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

    public List<BoLinkStatus> getLinks() {
        return links;
    }

    public void setLinks(List<BoLinkStatus> links) {
        this.links = links;
    }

    public List<String> getErrordevs() {
        return errordevs;
    }

    public void setErrordevs(List<String> errordevs) {
        this.errordevs = errordevs;
    }

}
