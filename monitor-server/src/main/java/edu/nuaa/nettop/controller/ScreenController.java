package edu.nuaa.nettop.controller;

import edu.nuaa.nettop.common.constant.TaskType;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.response.Response;
import edu.nuaa.nettop.service.ScreenService;
import edu.nuaa.nettop.vo.DDosScreenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 18:44
 */
@Slf4j
@RestController
@RequestMapping("/v2/screen")
public class ScreenController {

    private final ScreenService screenService;

    @Autowired
    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @GetMapping("/test/{wlid}")
    public Response test(@PathVariable("wlid") String wlid) {
        return new Response(wlid);
    }

    @GetMapping("/start/{wlid}")
    public Response runDDosScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        DDosScreenRequest request = screenService.createDDosScreen(wlid);
        screenService.addDDosScreen(request);
        return new Response("ok");
    }

    @GetMapping("/stop/{wlid}")
    public Response cancelDDosScreen(@PathVariable("wlid") String wlid) throws MonitorException {
        screenService.cancelScreen(wlid, TaskType.DDOS_SCREEN.getDesc());
        screenService.deleteScreen(wlid, TaskType.DDOS_SCREEN.getDesc());
        return new Response("ok");
    }
}
