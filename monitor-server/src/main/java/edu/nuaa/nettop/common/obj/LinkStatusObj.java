package edu.nuaa.nettop.common.obj;

import java.io.Serializable;
import java.util.StringJoiner;

public class LinkStatusObj implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6368590652476309380L;

    private String llid;  //链路id

    private byte st;    //链路状态

    private long tp;  //链路流量吞吐量

    public LinkStatusObj() {
        // TODO Auto-generated constructor stub
    }

    public String getLlid() {
        return llid;
    }

    public void setLlid(String llid) {
        this.llid = llid;
    }

    public byte getSt() {
        return st;
    }

    public void setSt(byte st) {
        this.st = st;
    }

    public long getTp() {
        return tp;
    }

    public void setTp(long tp) {
        this.tp = tp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LinkStatusObj.class.getSimpleName() + "[", "]")
                .add("tp=" + tp)
                .toString();
    }
}
