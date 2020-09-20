package edu.nuaa.nettop.controller;

import edu.nuaa.nettop.common.constant.TaskType;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.service.MonitorService;
import edu.nuaa.nettop.vo.NodeRequest;
import edu.nuaa.nettop.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 18:51
 */
@Slf4j
@RestController
@RequestMapping("/v2/devMonitor")
public class NodeMonitorController {

    private final MonitorService monitorService;

    @Autowired
    public NodeMonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/start/{wlid}/{sbid}")
    @ResponseBody
    public Response startToMonitor(@PathVariable("wlid") String wlid,
                                   @PathVariable("sbid") String sbid) throws MonitorException {
        log.info("Recv request-> create node task for {}", sbid);
        NodeRequest request = monitorService.createNodeMonitorTask(wlid, sbid);
        monitorService.addNodeMonitorTask(request);
        return new Response("启动成功");
    }

    @GetMapping("/stop/{wlid}/{sbid}")
    @ResponseBody
    public Response stopToMonitor(@PathVariable("wlid") String wlid,
                                  @PathVariable("sbid") String sbid) throws MonitorException {
        log.info("Recv request-> stop node task for {}", sbid);
        monitorService.cancelTask(sbid, TaskType.NODE_TASK.getDesc());
        monitorService.deleteTask(sbid, TaskType.NODE_TASK.getDesc());
        return new Response("停止成功");
    }
}
