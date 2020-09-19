package edu.nuaa.nettop.service;


import edu.nuaa.nettop.vo.snmp.SnmpRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-08-05
 * Time: 10:06
 */
public interface SnmpService {
    String getServerCpuUtilization(SnmpRequest request);

    String getServerMemoryUtilization(SnmpRequest request);

    Long getUdpIn(SnmpRequest request);

    Long getUdpOut(SnmpRequest request);

    Long getTcpIn(SnmpRequest request);

    Long getTcpOut(SnmpRequest request);
}
