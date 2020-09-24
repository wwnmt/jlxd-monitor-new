/*
 * 物理服务器信息
 */
package edu.nuaa.nettop.common.obj;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ServerObj implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1089276480856237632L;

    private String mc;

    private List<NetPortObj> ports = new LinkedList<>();

    public ServerObj() {
        // TODO Auto-generated constructor stub
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public List<NetPortObj> getPorts() {
        return ports;
    }

    public void addPort(NetPortObj netPortObj) {
        this.ports.add(netPortObj);
    }

    public void setPorts(List<NetPortObj> ports) {
        this.ports = ports;
    }
}
