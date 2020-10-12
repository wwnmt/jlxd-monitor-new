package edu.nuaa.nettop.task;

import com.alibaba.fastjson.JSON;
import com.mchange.v1.util.ArrayUtils;
import edu.nuaa.nettop.common.constant.Constants;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.common.response.ddos.BoDdosScreenStatus;
import edu.nuaa.nettop.common.response.ospf.BoOSPFAttackPack;
import edu.nuaa.nettop.common.response.ospf.BoOSPFPackage;
import edu.nuaa.nettop.common.response.ospf.BoRouterAttackScreenStatus;
import edu.nuaa.nettop.common.response.ospf.BoVictimRouterItem;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.model.RoutingTable;
import edu.nuaa.nettop.utils.CommonUtils;
import edu.nuaa.nettop.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-30
 * Time: 11:06
 */
@Slf4j
public class OspfScreenTask implements Job {

    private static final String URL = "http://" + StaticConfig.WEB_IP + ":" + StaticConfig.PORT + "/v2/netMonitorData/screen/routerattack";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //读取参数
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String wlid = jobDataMap.getString("wlid");
        String attacker;
        String attServerIp;
        String victim;
        String vimServerIp;

        log.info("Run ospf screen task-> {}", wlid);

        BoRouterAttackScreenStatus screenStatus = new BoRouterAttackScreenStatus();
        screenStatus.setWlid(wlid);
        //被攻击路由器信息
        if (Constants.vimRouter.containsKey(wlid)) {
            //读取参数
            String[] vals = Constants.vimRouter.get(wlid).split(":");
            victim = vals[0];
            vimServerIp = vals[1];
            screenStatus.setVictim(victim);
            //获取初始路由表
            RoutingTable routingTable = null;
            if (Constants.podRoutings.containsKey(wlid)) {
                routingTable = Constants.podRoutings.get(wlid).get(victim);
                List<BoVictimRouterItem> originrouters = new ArrayList<>();
                if (routingTable != null) {
                    for (RoutingTable.RouteContent content : routingTable.getContents()) {
                        BoVictimRouterItem item = new BoVictimRouterItem();
                        item.setItem(content.briefString());
                        item.setStatus(0);
                        originrouters.add(item);
                    }
                } else {
                    log.error("no history routing tables->{}", victim);
                }
                screenStatus.setOldrouters(originrouters);
            } else {
                log.error("no history routing tables->{}", victim);
            }
            //获取最新路由表
            List<BoVictimRouterItem> routers = new ArrayList<>();
            RoutingTable routingTable2 = ProxyUtil.getRoutingTable(vimServerIp, victim);
            for (RoutingTable.RouteContent content : routingTable2.getContents()) {
                BoVictimRouterItem item = new BoVictimRouterItem();
                item.setItem(content.briefString());
                if (routingTable != null && routingTable.getContents().contains(content)) {
                    item.setStatus(0);
                } else {
                    item.setStatus(1);
                }
                routers.add(item);
            }
            screenStatus.setRouters(routers);
        } else { //测试数据
            screenStatus.setVictim("示例数据");
            List<BoVictimRouterItem> originrouters = new ArrayList<>();
            List<BoVictimRouterItem> routers = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                BoVictimRouterItem item = new BoVictimRouterItem();
                item.setItem("network 192.168." + i + ".0/24 area 0");
                item.setStatus(0);
                originrouters.add(item);
            }
            screenStatus.setOldrouters(originrouters);
            for (int i = 0; i < 3; i++) {
                BoVictimRouterItem item = new BoVictimRouterItem();
                item.setItem("network 192.168." + i + ".0/24 area 0");
                item.setStatus(i);
                routers.add(item);
            }
            screenStatus.setRouters(routers);
        }

        //攻击路由器路由表信息
        if (Constants.attRouter.containsKey(wlid)) {
            //读取参数
            String[] vals = Constants.attRouter.get(wlid).split(":");
            attacker = vals[0];
            attServerIp = vals[1];
            screenStatus.setAttacker(attacker);
            //获取最新路由表
            //攻击路由器的攻击报文 读取redis
            List<BoOSPFAttackPack> attackpacks = new ArrayList<>();
            String firstAttack = CommonUtils.getFromRedis("trigger_lsa");
            String secondAttack = CommonUtils.getFromRedis("disguised_lsa");
            if (firstAttack != null && !firstAttack.isEmpty()) {
                BoOSPFAttackPack pack = new BoOSPFAttackPack();
                pack.setTm("1");
                pack.setSmip(attacker);
                pack.setPack(firstAttack);
                attackpacks.add(pack);
            }
            if (secondAttack != null && !secondAttack.isEmpty()) {
                BoOSPFAttackPack pack = new BoOSPFAttackPack();
                pack.setTm("2");
                pack.setSmip(attacker);
                pack.setPack(secondAttack);
                attackpacks.add(pack);
            }
            screenStatus.setAttackpacks(attackpacks);
        } else {
            //返回空
            screenStatus.setAttacker("无");
            screenStatus.setAttackpacks(null);
        }

        //所有网络报文 从李梓铜程序获取
        List<BoOSPFPackage> packs = new ArrayList<>();
        int i = 1;
        for (String packet : CommonUtils.getListFromRedis("lsa_from_attack_router")) {
            BoOSPFPackage pack = new BoOSPFPackage();
            pack.setTm(String.valueOf(i));
            pack.setSeqno(String.valueOf(i+3));
            pack.setPtype("OSPF");
            pack.setPack(packet);
            packs.add(pack);
            i++;
        }
        screenStatus.setPacks(packs);
        log.info("OSPF Data-> {}", JSON.toJSONString(screenStatus));
        sendToWeb(screenStatus);
    }

    private void sendToWeb(BoRouterAttackScreenStatus screenStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoRouterAttackScreenStatus> entity = new HttpEntity<>(screenStatus, headers);

        RestTemplate restTemplate = new RestTemplate();
        BoRestResObj boRestResObj = restTemplate.postForObject(URL, entity, BoRestResObj.class);
        assert boRestResObj != null;
        if (boRestResObj.getOptres() == 1) {
            log.info("POST OSPF Data to " + URL + " success");
        } else {
            log.error("POST failed:" + boRestResObj.getMsg());
        }
    }
}
