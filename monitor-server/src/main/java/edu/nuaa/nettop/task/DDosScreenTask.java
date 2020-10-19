package edu.nuaa.nettop.task;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.obj.LinkStatusObj;
import edu.nuaa.nettop.common.response.ddos.BoDdosScreenStatus;
import edu.nuaa.nettop.common.response.BoLinkStatus;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.common.response.ddos.BoServerStatus;
import edu.nuaa.nettop.common.response.ddos.BoTdStatus;
import edu.nuaa.nettop.common.response.ddos.BoVictimStatus;
import edu.nuaa.nettop.utils.CommonUtils;
import edu.nuaa.nettop.utils.ProxyUtil;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.vo.DDosScreenRequest;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;
import lombok.SneakyThrows;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-18
 * Time: 16:46
 */
@Slf4j
public class DDosScreenTask implements Job {

    private static final String URL = "http://" + StaticConfig.WEB_IP + ":" + StaticConfig.PORT + "/v2/netMonitorData/screen/ddos";

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        String wlid = jobDataMap.getString("wlid");
        DDosScreenRequest.Attacker attacker = (DDosScreenRequest.Attacker) jobDataMap.get("attacker");
        DDosScreenRequest.Victim victim = (DDosScreenRequest.Victim) jobDataMap.get("victim");
        List<DDosScreenRequest.Td> tds = (List<DDosScreenRequest.Td>) jobDataMap.get("tds");
        List<String> serverIps = (List<String>) jobDataMap.get("servers");
        BoDdosScreenStatus ddosScreenStatus = new BoDdosScreenStatus();

        ddosScreenStatus.setWlid(wlid);
        //被攻击节点数据
        long start = System.currentTimeMillis();
        ddosScreenStatus.setVicSt(setVictimData(victim, tds.get(0)));
        log.debug("victim times: {}", System.currentTimeMillis() - start);
        //傀儡机数据
        start = System.currentTimeMillis();
        ddosScreenStatus.setTd(setTdData(tds));
        log.debug("tds times: {}", System.currentTimeMillis() - start);
        //服务器资源数据
        start = System.currentTimeMillis();
        for (String serverIp : serverIps) {
            if (serverIp.equals("192.168.31.12")) {
                continue;
            }
            ddosScreenStatus.addServer(setServerData(serverIp));
        }
        log.debug("server times: {}", System.currentTimeMillis() - start);
        //链路数据
        //从redis读取数据
        start = System.currentTimeMillis();
        List<LinkStatusObj> linkStatusObjList = JSON.parseArray(
                CommonUtils.getFromRedis(wlid),
                LinkStatusObj.class);
        linkStatusObjList.sort(Comparator.comparingLong(LinkStatusObj::getTp));
        List<BoLinkStatus> linkStatuses = new ArrayList<>();
        int length = linkStatusObjList.size();
        for (int i = 0; i < 5; i++) {
            BoLinkStatus boLinkStatus = new BoLinkStatus();
            boLinkStatus.setMc(linkStatusObjList.get(length - 1).getMc());
            boLinkStatus.setSt((byte) 1);
            boLinkStatus.setTp(String.valueOf(linkStatusObjList.get(length - 1).getTp()));
            linkStatuses.add(boLinkStatus);
            length--;
        }
        ddosScreenStatus.setLinks(linkStatuses);
        log.debug("links times: {}", System.currentTimeMillis() - start);
        //set TM
        String tmString;
        if ((tmString = CommonUtils.getFromRedis(wlid + "ddostm")) == null) {
            CommonUtils.storeToRedis(wlid + "ddostm", "5");
            ddosScreenStatus.setTm(0);
        } else {
            int tm = Integer.parseInt(tmString);
            ddosScreenStatus.setTm(tm);
            CommonUtils.storeToRedis(wlid + "ddostm", String.valueOf(tm + 5));
        }
        log.info("Create data-> {}", JSON.toJSONString(ddosScreenStatus));
        sendToWeb(ddosScreenStatus);
    }

    private void sendToWeb(BoDdosScreenStatus ddosScreenStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoDdosScreenStatus> entity = new HttpEntity<>(ddosScreenStatus, headers);

        RestTemplate restTemplate = new RestTemplate();
        BoRestResObj boRestResObj = restTemplate.postForObject(URL, entity, BoRestResObj.class);
        assert boRestResObj != null;
        if (boRestResObj.getOptres() == 1) {
            log.info("POST DDosScreen Data to " + URL + " success");
        } else {
            log.error("POST failed:" + boRestResObj.getMsg());
        }
    }

    private BoServerStatus setServerData(String serverIp) {
        String cpu = ProxyUtil.getCpu(serverIp, serverIp);
        String mem = ProxyUtil.getMem(serverIp, serverIp);
        BoServerStatus boServerStatus = new BoServerStatus();
        boServerStatus.setCpu(Double.parseDouble(cpu));
        boServerStatus.setMem(Double.parseDouble(mem));
        boServerStatus.setServerId(serverIp);
        return boServerStatus;
    }

    private BoTdStatus setTdData(List<DDosScreenRequest.Td> tds) throws InterruptedException {
        BoTdStatus tdStatus = new BoTdStatus();
        List<Long> tcpList = new ArrayList<>();
        List<Long> udpList = new ArrayList<>();
        //启动tcpdump监听udp tcp报文发送数量
        for (DDosScreenRequest.Td td : tds) {
            ProxyUtil.runTcpdumpCapUdp(td.getServerIp(), td.getFullName());
            ProxyUtil.runTcpdumpCapTcp(td.getServerIp(), td.getFullName());
        }
        //等待3秒
        TimeUnit.SECONDS.sleep(3);
        for (DDosScreenRequest.Td td : tds) {
            long tcpOutRatio = ProxyUtil.getTcpRate(td.getServerIp(), td.getFullName());
            long udpOutRatio = ProxyUtil.getUdpRate(td.getServerIp(), td.getFullName());
            tcpList.add(tcpOutRatio / 3);
            udpList.add(udpOutRatio / 3);
            ProxyUtil.cancelTcpdump(td.getServerIp(), td.getFullName());
        }
        tdStatus.setMaxtcp(Collections.max(tcpList));
        tdStatus.setMintcp(Collections.min(tcpList));
        tdStatus.setMaxudp(Collections.max(udpList));
        tdStatus.setMinudp(Collections.min(udpList));
        tdStatus.setTcp(tcpList.stream().mapToInt(Math::toIntExact).sum() / tcpList.size());
        tdStatus.setUdp(udpList.stream().mapToInt(Math::toIntExact).sum() / udpList.size());
        return tdStatus;
    }

    private BoVictimStatus setVictimData(DDosScreenRequest.Victim victim, DDosScreenRequest.Td td) throws MonitorException {
        BoVictimStatus victimStatus = new BoVictimStatus();
        victimStatus.setMc(victim.getName());
        //SYN_FLOOD攻击效果
        //启动multi_server
        ProxyUtil.runMultiServ(victim.getServerIp(), victim.getFullName());
        String rate = ProxyUtil.getDDosRate(td.getServerIp(), td.getFullName(), victim.getIp());
        if (rate == null)
            victimStatus.setRate(0);
        else
            victimStatus.setRate(Double.parseDouble(rate));
        //停止multi_server
        ProxyUtil.cancelTcpdump(victim.getServerIp(), victim.getFullName());
        /*
            用wc -l进行统计即可得出某个端口的连接数
            snmpwalk -v2c -c snmp_community_string 192.168.0.1 .1.3.6.1.2.1.6.13.1.3.192.168.0.1.80 | wc -l
         */
        victimStatus.setSyn_recv(0);
        //CPU MEM
        LxdRequest request = new LxdRequest();
        request.getDeviceList().add(victim.getFullName());
        LxdResponse response = ProxyUtil.getLxdStatus(victim.getServerIp(), request);
        if (CollectionUtils.isNotEmpty(response.getErrDevs())) {
            victimStatus.setCpu(0);
            victimStatus.setMem(0);
        } else {
            response.getLxdStatuses().forEach(lxdStatus -> {
                //cpu
                //读取redis数据
                String cache = CommonUtils.getFromRedis(victim.getFullName());
                if (cache == null || cache.equals("")) {
                    victimStatus.setCpu(0);
                } else {
                    long oldCpuTime = Long.parseLong(cache);
                    long newCpuTime = lxdStatus.getCpuTime();
                    if (newCpuTime < oldCpuTime) {
                        victimStatus.setCpu((Long.MAX_VALUE - oldCpuTime + newCpuTime) / 1_000_000f / 10_200f * 100);
                    } else {
                        double cpu = (newCpuTime - oldCpuTime) / 1_000_000f / 10_200f * 100;
                        victimStatus.setCpu(cpu);
                    }
                    //存入redis
                    CommonUtils.storeToRedis(victim.getFullName(), String.valueOf(lxdStatus.getCpuTime()));
                }
                //mem
                victimStatus.setMem(lxdStatus.getMemUsage());

            });
        }
        return victimStatus;
    }
}
