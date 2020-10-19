package edu.nuaa.nettop.service;


import edu.nuaa.nettop.exception.ProxyException;
import edu.nuaa.nettop.model.RoutingTable;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-08-05
 * Time: 10:06
 */
public interface LxdService {

    LxdResponse getLxdStatus(LxdRequest request) throws ProxyException;

    RoutingTable getRoutingTable(String nodeName) throws ProxyException;

    void runTcpdumpCapUdp(String nodeName) throws ProxyException;

    void runTcpdumpCapTcp(String nodeName) throws ProxyException;

    void cancelTcpdump(String nodeName) throws ProxyException;

    Long getUdpRate(String nodeName) throws ProxyException;

    Long getTcpRate(String nodeName) throws ProxyException;

    void runMultiTcpServer(String nodeName) throws ProxyException;

    void cancelMultiTcpServer(String nodeName) throws ProxyException;

    String getDDosRate(String nodeName, String ip) throws ProxyException;
}
