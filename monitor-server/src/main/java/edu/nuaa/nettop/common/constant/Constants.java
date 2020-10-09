package edu.nuaa.nettop.common.constant;

import edu.nuaa.nettop.common.obj.ServerReqObj;
import edu.nuaa.nettop.common.response.ospf.BoVictimRouterItem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 18:21
 */
public final class Constants {

    //Vr监控端口
    public static final Map<String, List<ServerReqObj>> servIntfMap = new ConcurrentHashMap<>();
    //Perf路由器监控集合
    //key:wlid value:sbid
    public static final Map<String, String> perfDevMap = new ConcurrentHashMap<>();

    // Rt监控路由器
    public static final Map<String, String> routingRouter = new ConcurrentHashMap<>();

    //错误信息常量
    public static class ErrMsg {

    }
}
