package edu.nuaa.nettop.controller;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.exception.ProxyException;
import edu.nuaa.nettop.model.RoutingTable;
import edu.nuaa.nettop.service.LxdService;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("lxd")
public class LxdController {

    @Autowired
    private LxdService lxdService;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "ok";
    }

    @RequestMapping(value = "status", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public LxdResponse getLxdsStatus(@RequestBody LxdRequest request) throws ProxyException {
        log.info("Recv task: {}", JSON.toJSONString(request));
        return lxdService.getLxdStatus(request);
    }

    @RequestMapping(value = "/rt/{name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public RoutingTable getRoutingTable(@PathVariable("name") String nodeName) throws ProxyException {
        log.info("Recv task to get routing table: {}", JSON.toJSONString(nodeName));
        return lxdService.getRoutingTable(nodeName);
    }

    @RequestMapping(value = "/cap/udp/{name}", method = RequestMethod.GET)
    @ResponseBody
    public void capUdp(@PathVariable("name") String nodeName) throws ProxyException {
        log.info("Recv task to run tcpdump cap udp: {}", JSON.toJSONString(nodeName));
        lxdService.runTcpdumpCapUdp(nodeName);
    }

    @RequestMapping(value = "/cap/tcp/{name}", method = RequestMethod.GET)
    @ResponseBody
    public void capTcp(@PathVariable("name") String nodeName) throws ProxyException {
        log.info("Recv task to run tcpdump cap tcp: {}", JSON.toJSONString(nodeName));
        lxdService.runTcpdumpCapTcp(nodeName);
    }

    @RequestMapping(value = "/cap/cancel/{name}", method = RequestMethod.GET)
    @ResponseBody
    public void cancelTcpdump(@PathVariable("name") String nodeName) throws ProxyException {
        log.info("Recv task to cancel tcpdump: {}", JSON.toJSONString(nodeName));
        lxdService.cancelTcpdump(nodeName);
    }

    @RequestMapping(value = "/udp/{name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Long getUdpRate(@PathVariable("name") String nodeName) throws ProxyException {
        log.info("Recv task to get udp rate: {}", JSON.toJSONString(nodeName));
        return lxdService.getUdpRate(nodeName);
    }

    @RequestMapping(value = "/tcp/{name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Long getTcpRate(@PathVariable("name") String nodeName) throws ProxyException {
        log.info("Recv task to get tcp rate: {}", JSON.toJSONString(nodeName));
        return lxdService.getTcpRate(nodeName);
    }

    @RequestMapping(value = "/ddos/serv/{name}", method = RequestMethod.GET)
    @ResponseBody
    public void runMultiTcpServer(@PathVariable("name") String nodeName) throws ProxyException {
        log.info("Recv task to run multitcp_server: {}", JSON.toJSONString(nodeName));
        lxdService.runMultiTcpServer(nodeName);
    }

    @RequestMapping(value = "/ddos/serv/cancel/{name}", method = RequestMethod.GET)
    @ResponseBody
    public void cancelMultiTcpServer(@PathVariable("name") String nodeName) throws ProxyException {
        log.info("Recv task to cancel multitcp_server: {}", JSON.toJSONString(nodeName));
        lxdService.cancelMultiTcpServer(nodeName);
    }

    @RequestMapping(value = "/ddos/rate/{name}/{ip}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getDDosRate(@PathVariable("name") String nodeName,
                              @PathVariable("ip") String ip) throws ProxyException {
        log.info("Recv task to get ddos rate: {}", JSON.toJSONString(nodeName));
        return lxdService.getDDosRate(nodeName, ip);
    }
}
