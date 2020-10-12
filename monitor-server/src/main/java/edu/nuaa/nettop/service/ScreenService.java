package edu.nuaa.nettop.service;

import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.obj.ServerObj;
import edu.nuaa.nettop.model.RoutingTable;
import edu.nuaa.nettop.vo.DDosScreenRequest;
import edu.nuaa.nettop.vo.OspfScreenRequest;
import edu.nuaa.nettop.vo.PerfScreenRequest;
import edu.nuaa.nettop.vo.VrScreenRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 16:19
 */
public interface ScreenService {

    //ddos
    DDosScreenRequest createDDosScreen(String wlid) throws MonitorException;

    void addDDosScreen(DDosScreenRequest request) throws MonitorException;

    //vr
    VrScreenRequest createVrScreen(String wlid) throws MonitorException;

    void addVrScreen(VrScreenRequest request) throws MonitorException;

    //perf
    PerfScreenRequest createPerfScreen(String wlid) throws MonitorException;

    void addPerformanceScreen(PerfScreenRequest request) throws MonitorException;

    //ospf
    OspfScreenRequest createOspfScreen(String wlid) throws MonitorException;

    void addOspfScreen(OspfScreenRequest request) throws MonitorException;

    void addRt(String wlid, String sbid, boolean isVictim) throws MonitorException;

    void removeRt(String wlid, boolean isVictim) throws MonitorException;

    void cancelScreen(String jobName, String jobGroup) throws MonitorException;

    void deleteScreen(String jobName, String jobGroup) throws MonitorException;

    ServerObj getPhysicalInterfaceInfo(String wlid, String sbid) throws MonitorException;
}
