package edu.nuaa.nettop.lxd;

import com.alibaba.fastjson.JSONObject;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.model.LxdStatus;
import edu.nuaa.nettop.model.PortStatus;
import jnr.unixsocket.UnixSocket;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
public class LxdMonitorHandle
        extends java.util.concurrent.RecursiveTask<Set<LxdStatus>> {

    private static final int THRESHOLD = StaticConfig.THRESHOLD;

    private final List<String> nodeList;

    public LxdMonitorHandle(List<String> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    protected Set<LxdStatus> compute() {
        Set<LxdStatus> lxdStatusSet = null;
        if (nodeList.size() <= THRESHOLD) {
            lxdStatusSet = new HashSet<>(updateLxdStatus());
        } else {
            int head = 0;
            List<String> leftList = nodeList.subList(head, head + THRESHOLD);
//            System.out.println(leftList);
            LxdMonitorHandle leftTask = new LxdMonitorHandle(leftList);
            head = head + THRESHOLD;
            List<String> rightList = nodeList.subList(head, nodeList.size());
//            System.out.println(rightList);
            LxdMonitorHandle rightTask = new LxdMonitorHandle(rightList);
            invokeAll(leftTask, rightTask);
            try {
                lxdStatusSet = groupResult(leftTask.get(), rightTask.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return lxdStatusSet;
    }

    private Set<LxdStatus> groupResult(Set<LxdStatus> set1, Set<LxdStatus> set2) {
        Set<LxdStatus> lxdStatusSet = new HashSet<>();
        lxdStatusSet.addAll(set1);
        lxdStatusSet.addAll(set2);
        return lxdStatusSet;
    }

    public Set<LxdStatus> updateLxdStatus() {
        Set<LxdStatus> lxdStatusSet = new HashSet<>();
        for (String lxdName : nodeList) {
            try {
                LxdStatus lxdStatus = new LxdStatus();
                lxdStatus.setName(lxdName);
                String result = getContainerState(lxdName);
                JSONObject lxdStateJson = JSONObject.parseObject(result);
                if (lxdStateJson.get("type").equals("error")){
                    lxdStatus.setStatus(0);
                    lxdStatusSet.add(lxdStatus);
                    log.info("Fail to get {} status data failed", lxdName);
                    continue;
                }
                int requestStatus = (int) lxdStateJson.get("status_code");
                if (requestStatus != 200) {
                    lxdStatus.setStatus(0);
                    lxdStatusSet.add(lxdStatus);
                    log.info("Fail to get {} status data failed", lxdName);
                    continue;
                }
                JSONObject metadata = lxdStateJson.getJSONObject("metadata");

                //更新启动状态
                if ((int) metadata.get("status_code") == 103) {
                    lxdStatus.setStatus(1);
                } else {
                    lxdStatus.setStatus(0);
                    lxdStatusSet.add(lxdStatus);
                    continue;
                }
                //更新磁盘数据
                JSONObject diskJson = metadata.getJSONObject("disk");
                if (diskJson != null && diskJson.getJSONObject("root") != null) {
                    lxdStatus.setDiskUsage(diskJson.getJSONObject("root").getLongValue("usage"));
                }
                //更新内存数据
                JSONObject memory = metadata.getJSONObject("memory");
                lxdStatus.setMemUsage(memory.getLongValue("usage"));
                lxdStatus.setMemUsagePeak(memory.getLongValue("usage_peak"));

                //更新接口数据
                JSONObject network = metadata.getJSONObject("network");
                for (Map.Entry<String, Object> entry : network.entrySet()) {
                    String interfaceName = entry.getKey();
                    if (interfaceName.contains("manage"))
                        continue;
                    JSONObject interfaceJson = (JSONObject) entry.getValue();
                    JSONObject counters = interfaceJson.getJSONObject("counters");

                    PortStatus port = new PortStatus();
                    lxdStatus.addInterface(port);
                    String state = (String) interfaceJson.get("state");
                    port.setName(interfaceName);
                    port.setStatus(state.equals("up") ? 1 : 0);
                    port.setByteRecv(counters.getLongValue("bytes_received"));
                    port.setByteSent(counters.getLongValue("bytes_sent"));
                    port.setPktRecv(counters.getLongValue("packets_received"));
                    port.setPktSent(counters.getLongValue("packets_sent"));
                }
                //更新CPU时间
                JSONObject cpu = metadata.getJSONObject("cpu");
                lxdStatus.setCpuTime(cpu.getLongValue("usage"));
                lxdStatusSet.add(lxdStatus);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lxdStatusSet;
    }

    private String getContainerState(String name) throws IOException {
        // 建立 Unix Socket 连接
        File sockFile = new File("/var/lib/lxd/unix.socket");
        UnixSocketAddress address = new UnixSocketAddress(sockFile);
        UnixSocketChannel channel = UnixSocketChannel.open(address);
        UnixSocket unixSocket = new UnixSocket(channel);

        PrintWriter w = new PrintWriter(unixSocket.getOutputStream());
        w.println("GET /1.0/containers/" + name + "/state HTTP/1.1");
        w.println("Host: lxd");
        w.println("Accept: */*");
        w.println("Content-Type: application/json");
        w.println(""); //一定要加一个空行
        w.flush();
        // 关闭 Output，否则会导致下面的 read 操作一直阻塞
        unixSocket.shutdownOutput();
        // 获取返回结果

        StringBuilder resultBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(unixSocket.getInputStream()));
        String line;
        boolean flag = false;
        while ((line = br.readLine()) != null) {
            if (flag) {
                resultBuilder.append(line);
            }
            if (line.equals("")) {
                flag = true;
            }
        }
        unixSocket.close();
        return resultBuilder.substring(resultBuilder.indexOf("{"), resultBuilder.lastIndexOf("}") + 1);
    }
}
