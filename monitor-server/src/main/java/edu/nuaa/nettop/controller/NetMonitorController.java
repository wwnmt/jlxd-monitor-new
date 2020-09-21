package edu.nuaa.nettop.controller;

import edu.nuaa.nettop.common.constant.TaskType;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.service.MonitorService;
import edu.nuaa.nettop.service.ScreenService;
import edu.nuaa.nettop.vo.DDosScreenRequest;
import edu.nuaa.nettop.vo.NetRequest;
import edu.nuaa.nettop.vo.PerfScreenRequest;
import edu.nuaa.nettop.common.response.Response;
import edu.nuaa.nettop.vo.VrScreenRequest;
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
 * Time: 18:42
 */
@Slf4j
@RestController
@RequestMapping("/v2/netMonitor/")
public class NetMonitorController {

    private final MonitorService monitorService;
    private final ScreenService screenService;

    @Autowired
    public NetMonitorController(MonitorService monitorService, ScreenService screenService) {
        this.monitorService = monitorService;
        this.screenService = screenService;
    }

    @GetMapping("/start/{wlid}")
    @ResponseBody
    public Response startToMonitor(@PathVariable("wlid") String wlid) throws MonitorException {
        NetRequest request = monitorService.createNetMonitorTask(wlid);
        log.info("Recv request-> create net task for {}", wlid);
        monitorService.addNetMonitorTask(request);
        return new Response("启动成功");
    }

    @GetMapping("/stop/{wlid}")
    @ResponseBody
    public Response stopToMonitor(@PathVariable("wlid") String wlid) throws MonitorException {
        log.info("Recv request-> stop net task for {}", wlid);
        monitorService.cancelTask(wlid, TaskType.NET_TASK.getDesc());
        monitorService.deleteTask(wlid, TaskType.NET_TASK.getDesc());
        return new Response("停止成功");
    }

    @GetMapping("/screen/ddos/start/{wlid}")
    public Response runDDosScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        DDosScreenRequest request = screenService.createDDosScreen(wlid);
        screenService.addDDosScreen(request);
        return new Response("ok");
    }

    @GetMapping("/screen/ddos/stop/{wlid}")
    public Response cancelDDosScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        screenService.cancelScreen(wlid, TaskType.DDOS_SCREEN.getDesc());
        screenService.deleteScreen(wlid, TaskType.DDOS_SCREEN.getDesc());
        return new Response("ok");
    }

    @GetMapping("/screen/virtrealconn/start/{wlid}")
    public Response runVrScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        VrScreenRequest request = screenService.createVrScreen(wlid);
        screenService.addVrScreen(request);
        return new Response("ok");
    }

    @GetMapping("/screen/virtrealconn/stop/{wlid}")
    public Response cancelVrScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        screenService.cancelScreen(wlid, TaskType.VR_SCREEN.getDesc());
        screenService.deleteScreen(wlid, TaskType.VR_SCREEN.getDesc());
        return new Response("ok");
    }

    @GetMapping("/screen/perfopt/start/{wlid}")
    public Response runPerfScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        PerfScreenRequest request = screenService.createPerfScreen(wlid);
        screenService.addPerformanceScreen(request);
        return new Response("ok");
    }

    @GetMapping("/screen/perfopt/stop/{wlid}")
    public Response cancelPerfScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        screenService.cancelScreen(wlid, TaskType.PRO_SCREEN.getDesc());
        screenService.deleteScreen(wlid, TaskType.PRO_SCREEN.getDesc());
        return new Response("ok");
    }
}
