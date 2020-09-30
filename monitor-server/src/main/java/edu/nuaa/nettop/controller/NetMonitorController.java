package edu.nuaa.nettop.controller;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.constant.Constants;
import edu.nuaa.nettop.common.constant.TaskType;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.obj.ServerObj;
import edu.nuaa.nettop.common.obj.ServerReqObj;
import edu.nuaa.nettop.common.obj.ServerReqParam;
import edu.nuaa.nettop.common.response.Response;
import edu.nuaa.nettop.common.utils.CommonUtils;
import edu.nuaa.nettop.service.MonitorService;
import edu.nuaa.nettop.service.ScreenService;
import edu.nuaa.nettop.vo.DDosScreenRequest;
import edu.nuaa.nettop.vo.NetRequest;
import edu.nuaa.nettop.vo.OspfScreenRequest;
import edu.nuaa.nettop.vo.PerfScreenRequest;
import edu.nuaa.nettop.vo.VrScreenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/test")
    @ResponseBody
    public Response test() throws MonitorException {
        return new Response("ok");
    }

    /**
     * 启动网络监控
     */
    @GetMapping("/start/{wlid}")
    @ResponseBody
    public Response startToMonitor(@PathVariable("wlid") String wlid) throws MonitorException {
        NetRequest request = monitorService.createNetMonitorTask(wlid);
        log.info("Recv request-> create net task for {}", wlid);
        monitorService.addNetMonitorTask(request);
        return new Response("启动成功");
    }

    /**
     * 停止网络监控
     */
    @GetMapping("/stop/{wlid}")
    @ResponseBody
    public Response stopToMonitor(@PathVariable("wlid") String wlid) throws MonitorException {
        log.info("Recv request-> stop net task for {}", wlid);
        monitorService.cancelTask(wlid, TaskType.NET_TASK.getDesc());
        monitorService.deleteTask(wlid, TaskType.NET_TASK.getDesc());
        return new Response("停止成功");
    }

    /**
     * 启动DDoS数字大屏
     */
    @GetMapping("/screen/ddos/start/{wlid}")
    @ResponseBody
    public Response runDDosScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        DDosScreenRequest request = screenService.createDDosScreen(wlid);
        screenService.addDDosScreen(request);
        return new Response("ok");
    }

    /**
     * 停止DDoS数字大屏
     */
    @GetMapping("/screen/ddos/stop/{wlid}")
    @ResponseBody
    public Response cancelDDosScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        screenService.cancelScreen(wlid, TaskType.DDOS_SCREEN.getDesc());
        screenService.deleteScreen(wlid, TaskType.DDOS_SCREEN.getDesc());
        return new Response("ok");
    }

    /**
     * 启动虚实数字大屏
     */
    @GetMapping("/screen/virtrealconn/start/{wlid}")
    @ResponseBody
    public Response runVrScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        VrScreenRequest request = screenService.createVrScreen(wlid);
        screenService.addVrScreen(request);
        return new Response("ok");
    }

    /**
     * 停止虚实数字大屏
     */
    @GetMapping("/screen/virtrealconn/stop/{wlid}")
    @ResponseBody
    public Response cancelVrScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        screenService.cancelScreen(wlid, TaskType.VR_SCREEN.getDesc());
        screenService.deleteScreen(wlid, TaskType.VR_SCREEN.getDesc());
        return new Response("ok");
    }

    /**
     * 虚实数字大屏 用户指定监控端口
     */
    @PostMapping("/virtreal/monitorserver")
    @ResponseBody
    public Response addIntf(@RequestBody ServerReqParam param) throws MonitorException {
        String wlid = param.getWlid();
        List<ServerReqObj> serverReqObjs = CommonUtils.getOrCreate(wlid,
                                                                   Constants.servIntfMap,
                                                                   ArrayList::new);
        serverReqObjs.clear();
        serverReqObjs.addAll(param.getSports());
        log.info("Recv new vr port->{}", JSON.toJSONString(serverReqObjs));
        return new Response("ok");
    }

    /**
     * 虚实数字大屏 获取虚实互联接口列表
     */
    @GetMapping("/virtreal/getserver/{wlid}/{sbid}")
    @ResponseBody
    public ServerObj getServer(@PathVariable("wlid") String wlid,
                               @PathVariable String sbid) throws MonitorException {
        log.info("Recv getServer-> {}-{}", wlid, sbid);
        ServerObj serverObj = screenService.getPhysicalInterfaceInfo(wlid, sbid);
        log.info("getServer data-> {}", JSON.toJSONString(serverObj));
        return serverObj;
    }

    /**
     * 启动性能优化数字大屏
     */
    @GetMapping("/screen/perfopt/start/{wlid}")
    @ResponseBody
    public Response runPerfScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        log.info("Recv perf screen-> {}", wlid);
        PerfScreenRequest request = screenService.createPerfScreen(wlid);
        screenService.addPerformanceScreen(request);
        return new Response("ok");
    }

    /**
     * 停止性能优化数字大屏
     */
    @GetMapping("/screen/perfopt/stop/{wlid}")
    @ResponseBody
    public Response cancelPerfScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        screenService.cancelScreen(wlid, TaskType.PRO_SCREEN.getDesc());
        screenService.deleteScreen(wlid, TaskType.PRO_SCREEN.getDesc());
        return new Response("ok");
    }

    /**
     * 性能优化数字大屏 用户指定监控路由器
     */
    @GetMapping("perfopt/monitorrouter/{wlid}/{sbid}")
    @ResponseBody
    public Response adddev(@PathVariable("wlid") String wlid,
                           @PathVariable String sbid) throws MonitorException {
        Constants.perfDevMap.put(wlid, sbid);
        cancelPerfScreen(wlid);
        runPerfScreen(wlid);
        return new Response("ok");
    }

    /**
     * 启动OSPF数字大屏
     */
    @GetMapping("/screen/routerattack/start/{wlid}")
    @ResponseBody
    public Response runOspfScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        log.info("Recv router attack screen-> {}", wlid);
        OspfScreenRequest request = screenService.createOspfScreen(wlid);
        screenService.addOspfScreen(request);
        return new Response("ok");
    }

    /**
     * 停止OSPF数字大屏
     */
    @GetMapping("/screen/routerattack/stop/{wlid}")
    @ResponseBody
    public Response cancelOspfScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        screenService.cancelScreen(wlid, TaskType.ROUTER_ATTACK_SCREEN.getDesc());
        screenService.deleteScreen(wlid, TaskType.ROUTER_ATTACK_SCREEN.getDesc());
        return new Response("ok");
    }
}
