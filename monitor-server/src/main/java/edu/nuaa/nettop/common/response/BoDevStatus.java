package edu.nuaa.nettop.common.response;

import edu.nuaa.nettop.common.obj.DevStatusObj;
import edu.nuaa.nettop.common.obj.PortStatusObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BoDevStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -8751056936417643839L;

    private String id;  //节点名称

    private Short st;    //节点启动状态

    private double cpu; //CPU使用率

    private long dk;  //磁盘占用量

    private long mem; //内存占用量

    private long memPk;   //内存占用峰值

    private long tm;

    private List<BoPortStatus> ports = new ArrayList<BoPortStatus>();  //端口状态列表

    public BoDevStatus() {
        // TODO Auto-generated constructor stub
    }

    public BoDevStatus(DevStatusObj obj) {
        // TODO Auto-generated constructor stub
        this.setCpu(obj.getCpu());
        this.setDk(obj.getDk());
        this.setMem(obj.getMem());
        this.setMemPk(obj.getMemPk());
        this.setId(obj.getId());
        this.setSt(obj.getSt());
        this.setTm(obj.getTm());

        List<PortStatusObj> oports = obj.getPorts();//BoPortStatus
        if (null != oports && 0 < oports.size()) {
            for (PortStatusObj oport : oports) {
                BoPortStatus port = new BoPortStatus(oport);
                this.getPorts().add(port);
            }
        }
    }

    public long getTm() {
        return tm;
    }

    public void setTm(long tm) {
        this.tm = tm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Short getSt() {
        return st;
    }

    public void setSt(Short st) {
        this.st = st;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public long getDk() {
        return dk;
    }

    public void setDk(long dk) {
        this.dk = dk;
    }

    public long getMem() {
        return mem;
    }

    public void setMem(long mem) {
        this.mem = mem;
    }

    public long getMemPk() {
        return memPk;
    }

    public void setMemPk(long memPk) {
        this.memPk = memPk;
    }

    public List<BoPortStatus> getPorts() {
        return ports;
    }

    public void setPorts(List<BoPortStatus> ports) {
        this.ports = ports;
    }

}
