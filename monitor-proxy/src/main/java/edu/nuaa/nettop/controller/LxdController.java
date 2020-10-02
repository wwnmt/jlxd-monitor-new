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
}
