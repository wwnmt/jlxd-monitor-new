package edu.nuaa.nettop.common.response;

import edu.nuaa.nettop.common.obj.LinkStatusObj;

import java.io.Serializable;
import java.util.StringJoiner;


public class BoLinkStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6368590652476309380L;

    private String id;

    private byte st;    //链路状态

    private String mc;

    private String tp;  //链路流量吞吐量 Byte

    public BoLinkStatus() {
        // TODO Auto-generated constructor stub
    }

    public BoLinkStatus(LinkStatusObj obj) {
        // TODO Auto-generated constructor stub
        this.setId(obj.getId());
        this.setSt(obj.getSt());
        this.setTp(String.valueOf(obj.getTp()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte getSt() {
        return st;
    }

    public void setSt(byte st) {
        this.st = st;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BoLinkStatus.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("st=" + st)
                .add("tp='" + tp + "'")
                .toString();
    }
}
