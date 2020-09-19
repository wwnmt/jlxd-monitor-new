package edu.nuaa.nettop.service.impl;

import edu.nuaa.nettop.service.SnmpService;
import edu.nuaa.nettop.snmp.SnmpUtils;
import edu.nuaa.nettop.vo.snmp.SnmpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 15:50
 */
@Slf4j
@Service
public class SnmpServiceImpl implements SnmpService {

    private static final DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public String getServerCpuUtilization(SnmpRequest request) {
        long start = System.currentTimeMillis();
        int count = 0;
        List<String> result = null;
        while (result == null || result.size() == 0) {
            count++;
            result = SnmpUtils.walk(".1.3.6.1.2.1.25.3.3.1.2", request);
        }
        double sum = 0;
        for (String s : result) {
            sum += Double.parseDouble(s);
        }
        long end = System.currentTimeMillis();
        log.info("Get CPU times: {} ms, count: {}", end - start, count);
        return df.format(sum / result.size());
    }

    /**
     * memTotal：1.3.6.1.4.1.2021.4.5
     * memAvailable：1.3.6.1.4.1.2021.4.6
     * memShared：1.3.6.1.4.1.2021.4.13
     * memBuffer：1.3.6.1.4.1.2021.4.14
     * memCached：1.3.6.1.4.1.2021.4.15
     * 1）当 memShared + memBuffer + memCached > memTotal 时，
     * 内存使用率 memUsage = (memTotal - memAvailable - memBuffer - memCached + memShared )/memTotal * 100
     * 2） 当 memShared + memBuffer + memCached <= memTotal 时，
     * 内存使用率 memUsage = (memTotal - memAvailable - memBuffer - memCached )/memTotal * 100
     */
    @Override
    public String getServerMemoryUtilization(SnmpRequest request) {
        long start = System.currentTimeMillis();
        List<String> memTotalList = null, memAvailableList = null,
                memSharedList = null, memBufferList = null, memCachedList = null;
        // CAS
        while (memTotalList == null || memAvailableList == null || memSharedList == null ||
                memBufferList == null || memCachedList == null || memAvailableList.size() == 0 ||
                memTotalList.size() == 0 || memSharedList.size() == 0 || memBufferList.size() == 0 ||
                memCachedList.size() == 0) {
            memTotalList = SnmpUtils.walk("1.3.6.1.4.1.2021.4.5", request);
            memAvailableList = SnmpUtils.walk("1.3.6.1.4.1.2021.4.6", request);
            memSharedList = SnmpUtils.walk("1.3.6.1.4.1.2021.4.13", request);
            memBufferList = SnmpUtils.walk("1.3.6.1.4.1.2021.4.14", request);
            memCachedList = SnmpUtils.walk("1.3.6.1.4.1.2021.4.15", request);
        }
        double memTotal = Double.parseDouble(memTotalList.get(0));
        double memAvailable = Double.parseDouble(memAvailableList.get(0));
        double memShared = Double.parseDouble(memSharedList.get(0));
        double memBuffer = Double.parseDouble(memBufferList.get(0));
        double memCached = Double.parseDouble(memCachedList.get(0));
        long end = System.currentTimeMillis();
        log.info("Get MEM times: {} ms", end - start);
        if (memShared + memBuffer + memCached > memTotal)
            return df.format((memTotal - memAvailable - memBuffer - memCached + memShared )/memTotal * 100);
        else
            return df.format((memTotal - memAvailable - memBuffer - memCached) / memTotal * 100);
    }

    @Override
    public Long getUdpIn(SnmpRequest request) {
        List<String> udpInList = null, udpNoPortList = null, udpInErrList = null;
        // CAS
        while (udpInList == null || udpInList.size() == 0 ||
                udpNoPortList == null || udpNoPortList.size() == 0 ||
                udpInErrList == null || udpInErrList.size() == 0) {
            udpInList = SnmpUtils.walk("1.3.6.1.2.1.7.1", request);
            udpNoPortList = SnmpUtils.walk("1.3.6.1.2.1.7.2", request);
            udpInErrList = SnmpUtils.walk("1.3.6.1.2.1.7.3", request);
        }
        long udpIn = Long.parseLong(udpInList.get(0));
        long udpNoPort = Long.parseLong(udpNoPortList.get(0));
        long udpErr = Long.parseLong(udpInErrList.get(0));
        return udpIn + udpNoPort + udpErr;
    }

    @Override
    public Long getUdpOut(SnmpRequest request) {
        List<String> udpOutList = null;
        // CAS
        while (udpOutList == null || udpOutList.size() == 0) {
            udpOutList = SnmpUtils.walk("1.3.6.1.2.1.7.4", request);
        }
        return Long.valueOf(udpOutList.get(0));
    }

    @Override
    public Long getTcpIn(SnmpRequest request) {
        List<String> tcpInList = null;
        // CAS
        while (tcpInList == null || tcpInList.size() == 0) {
            tcpInList = SnmpUtils.walk("1.3.6.1.2.1.6.10", request);
        }
        return Long.valueOf(tcpInList.get(0));
    }

    @Override
    public Long getTcpOut(SnmpRequest request) {
        List<String> tcpOutList = null;
        // CAS
        while (tcpOutList == null || tcpOutList.size() == 0) {
            tcpOutList = SnmpUtils.walk("1.3.6.1.2.1.6.11", request);
        }
        return Long.valueOf(tcpOutList.get(0));
    }
}
