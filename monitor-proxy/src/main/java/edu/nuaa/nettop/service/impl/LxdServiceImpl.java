package edu.nuaa.nettop.service.impl;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.exception.ProxyException;
import edu.nuaa.nettop.lxd.LxdMonitorHandle;
import edu.nuaa.nettop.model.LxdStatus;
import edu.nuaa.nettop.model.RoutingTable;
import edu.nuaa.nettop.service.LxdService;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 15:30
 */
@Slf4j
@Service
public class LxdServiceImpl implements LxdService {

    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();

    @Override
    public LxdResponse getLxdStatus(LxdRequest request) throws ProxyException {

        LxdMonitorHandle task = new LxdMonitorHandle(request.getDeviceList());
        forkJoinPool.invoke(task);
        Set<LxdStatus> result = task.getRawResult();
        if (result == null || result.size() == 0) {
            throw new ProxyException("无法获取状态数据");
        }
        LxdResponse response = new LxdResponse();
        for (LxdStatus lxdStatus : result) {
            if (lxdStatus.getStatus() == 1) {
                response.getLxdStatuses().add(lxdStatus);
            } else {
                response.getErrDevs().add(lxdStatus.getName());
            }
        }
        return response;
    }

    @Override
    public RoutingTable getRoutingTable(String nodeName) throws ProxyException {
        try {
            String[] cmd = new String[] { "/bin/sh", "-c", "lxc exec " + nodeName + " route -n" };
            Process ps = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line;
            int count = 0;
            RoutingTable routingTable = new RoutingTable();
            routingTable.setNodeName(nodeName);
            List<RoutingTable.RouteContent> contents = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (count < 2) {
                    count++;
                    continue;
                }
                line = line.replaceAll("\\s+", " ");
                String[] lines = line.split(" ");
                if (lines[0].startsWith("1."))
                    continue;
                RoutingTable.RouteContent content = new RoutingTable.RouteContent(
                        lines[0], lines[1], lines[2], lines[3],
                        lines[4], lines[5], lines[7]
                );
                contents.add(content);
            }
            routingTable.setContents(contents);
            log.info("{} routing table-> {}", nodeName, JSON.toJSONString(routingTable));
            return routingTable;
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }

    @Override
    public void runTcpdumpCapUdp(String nodeName) throws ProxyException {
        try {
            String[] cmd = new String[] {
                    "/bin/sh",
                    "-c",
                    "lxc exec " + nodeName + " -- bash -c 'tcpdump -nn -i eth0 udp >udp.log'" };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }

    @Override
    public void runTcpdumpCapTcp(String nodeName) throws ProxyException {
        try {
            String[] cmd = new String[] {
                    "/bin/sh",
                    "-c",
                    "lxc exec " + nodeName + " -- bash -c 'tcpdump -nn -i eth0 tcp >tcp.log'" };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }

    @Override
    public void cancelTcpdump(String nodeName) throws ProxyException {
        try {
            String[] cmd = new String[] {
                    "/bin/sh",
                    "-c",
                    "lxc exec " + nodeName + " pkill tcpdump" };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }

    @Override
    public Long getUdpRate(String nodeName) throws ProxyException {
        try {
            String[] cmd = new String[] {
                    "/bin/sh",
                    "-c",
                    "lxc exec " + nodeName + " -- wc -l udp.log" };
            Process ps = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line;
            if  ((line = br.readLine()) != null) {
                String result = line.split(" ")[0];
                return Long.valueOf(result);
            } else
                return 0L;
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }

    @Override
    public Long getTcpRate(String nodeName) throws ProxyException {
        try {
            String[] cmd = new String[] {
                    "/bin/sh",
                    "-c",
                    "lxc exec " + nodeName + " -- wc -l tcp.log" };
            Process ps = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line;
            if  ((line = br.readLine()) != null) {
                String result = line.split(" ")[0];
                return Long.valueOf(result);
            } else
                return 0L;
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }

    @Override
    public void runMultiTcpServer(String nodeName) throws ProxyException {
        try {
            String[] cmd = new String[] {
                    "/bin/sh",
                    "-c",
                    "lxc exec " + nodeName + " /usr/local/bin/multitcp_server 0.0.0.0:80" };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }

    @Override
    public void cancelMultiTcpServer(String nodeName) throws ProxyException {
        try {
            String[] cmd = new String[] {
                    "/bin/sh",
                    "-c",
                    "lxc exec " + nodeName + " pkill multitcp_server" };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }

    @Override
    public String getDDosRate(String nodeName, String ip) throws ProxyException {

        try {
            String[] cmd = new String[] {
                    "/bin/sh",
                    "-c",
                    "lxc exec " + nodeName + " -- bash -c '/usr/local/bin/multitcp --addr " + ip + ":80 --num 100'" };
            Process ps = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line;
            if  ((line = br.readLine()) != null) {
                return line;
            } else
                return null;
        } catch (Exception e) {
            throw new ProxyException(e.getMessage());
        }
    }
}
