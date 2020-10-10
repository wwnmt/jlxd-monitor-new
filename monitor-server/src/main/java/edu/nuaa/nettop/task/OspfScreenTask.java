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
        String attacker = jobDataMap.getString("attacker");
        String attServerIp = jobDataMap.getString("attServerIp");
        String victim = jobDataMap.getString("victim");
        String vimServerIp = jobDataMap.getString("vimServerIp");

        String selected = jobDataMap.getString("selected");
        String selectedServerIp = jobDataMap.getString("selectedServerIp");

        log.info("Run ospf screen task-> {}", wlid);

        //测试数据
        BoRouterAttackScreenStatus screenStatus = new BoRouterAttackScreenStatus();
        screenStatus.setWlid(wlid);
        //攻击路由器名称
        screenStatus.setAttacker(attacker);
        //受害者路由器名称
        screenStatus.setVictim(victim);
        //受害者路由器初始路由表信息 SNMP
        List<BoVictimRouterItem> originrouters = new ArrayList<>();
        //受害者路由器路由表信息 SNMP
        List<BoVictimRouterItem> routers = new ArrayList<>();
        if (!selected.equals("") && !selectedServerIp.equals("")) {
            RoutingTable routingTable = null;
            if (Constants.podRoutings.containsKey(wlid)) {
                routingTable = Constants.podRoutings.get(wlid).get(selected);

                if (routingTable != null) {
                    for (RoutingTable.RouteContent content : routingTable.getContents()) {
                        BoVictimRouterItem item = new BoVictimRouterItem();
                        item.setItem(content.briefString());
                        item.setStatus(0);
                        originrouters.add(item);
                    }
                } else {
                    log.info(selected);
                }
            }

            screenStatus.setOldrouters(originrouters);

            RoutingTable routingTable2 = ProxyUtil.getRoutingTable(selectedServerIp, selected);
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
        } else {
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
        //攻击路由器的攻击报文 读取redis
        List<BoOSPFAttackPack> attackpacks = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            BoOSPFAttackPack pack = new BoOSPFAttackPack();
//            pack.setTm(String.valueOf(i + 1));
//            pack.setSmip("10.0.1." + i);
//            pack.setPack("attack pack test" + i);
//            attackpacks.add(pack);
//        }
        String firstAttack = CommonUtils.getFromRedis("trigger_lsa"),
                secondAttack = CommonUtils.getFromRedis("disguised_lsa");
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
        //所有网络报文 从李梓铜程序获取
        List<BoOSPFPackage> packs = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            BoOSPFPackage pack = new BoOSPFPackage();
//            pack.setTm(String.valueOf(i));
//            pack.setSeqno(String.valueOf(i+3));
//            pack.setPtype("Hello");
//            pack.setPack("OSPF test pack" + i);
//            packs.add(pack);
//        }
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
