package edu.nuaa.nettop.controller;

import edu.nuaa.nettop.model.ServMem;
import edu.nuaa.nettop.service.SnmpService;
import edu.nuaa.nettop.vo.snmp.SnmpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-08-06
 * Time: 19:09
 */
@RestController
@RequestMapping("snmp")
public class SnmpController {
    private static final Logger log = LoggerFactory.getLogger(LxdController.class);

    @Autowired
    private SnmpService snmpService;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "ok";
    }

    @RequestMapping(value = "cpu/{deviceIp}", method = RequestMethod.GET)
    @ResponseBody
    public String getCpu(@PathVariable String deviceIp) {
        log.info("Get {} cpu", deviceIp);
        SnmpRequest request = new SnmpRequest();
        request.setDeviceIp(deviceIp);
        request.setCommunityName("public");
        request.setPort(161);
        request.setVersion(1);
        return snmpService.getServerCpuUtilization(request);
    }

    @RequestMapping(value = "mem/{deviceIp}", method = RequestMethod.GET)
    @ResponseBody
    public String getMem(@PathVariable String deviceIp) {
        log.info("Get {} memory", deviceIp);
        SnmpRequest request = new SnmpRequest();
        request.setDeviceIp(deviceIp);
        request.setCommunityName("public");
        request.setPort(161);
        request.setVersion(1);
        return snmpService.getServerMemoryUtilization(request);
    }

    @RequestMapping(value = "mem/all/{deviceIp}", method = RequestMethod.GET)
    @ResponseBody
    public ServMem getAllMem(@PathVariable String deviceIp) {
        log.info("Get {} all memory", deviceIp);
        SnmpRequest request = new SnmpRequest();
        request.setDeviceIp(deviceIp);
        request.setCommunityName("public");
        request.setPort(161);
        request.setVersion(1);
        return snmpService.getServerAllMem(request);
    }

    @RequestMapping(value = "tcp/out/{deviceIp}", method = RequestMethod.GET)
    @ResponseBody
    public Long getTcpOut(@PathVariable String deviceIp) {
        log.info("Get {} tcpOut", deviceIp);
        SnmpRequest snmpModel = new SnmpRequest();
        snmpModel.setDeviceIp(deviceIp);
        snmpModel.setCommunityName("public");
        snmpModel.setPort(161);
        snmpModel.setVersion(1);
        return snmpService.getTcpOut(snmpModel);
    }

    @RequestMapping(value = "udp/out/{deviceIp}", method = RequestMethod.GET)
    @ResponseBody
    public long getUdpOut(@PathVariable String deviceIp) {
        log.info("Get {} udpOut", deviceIp);
        SnmpRequest snmpModel = new SnmpRequest();
        snmpModel.setDeviceIp(deviceIp);
        snmpModel.setCommunityName("public");
        snmpModel.setPort(161);
        snmpModel.setVersion(1);
        return snmpService.getUdpOut(snmpModel);
    }
}
