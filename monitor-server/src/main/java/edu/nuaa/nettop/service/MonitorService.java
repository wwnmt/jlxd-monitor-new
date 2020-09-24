package edu.nuaa.nettop.service;

import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.vo.NetRequest;
import edu.nuaa.nettop.vo.NodeRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 16:20
 */
public interface MonitorService {

    NetRequest createNetMonitorTask(String wlid) throws MonitorException;

    NodeRequest createNodeMonitorTask(String wlid, String sbid) throws MonitorException;

    void addNetMonitorTask(NetRequest request) throws MonitorException;

    void addNodeMonitorTask(NodeRequest request) throws MonitorException;

    void cancelTask(String jobName, String jobGroup) throws MonitorException;

    void deleteTask(String jobName, String jobGroup) throws MonitorException;


}
