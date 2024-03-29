package edu.nuaa.nettop.utils;

import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.model.PktStatistics;
import edu.nuaa.nettop.model.RoutingTable;
import edu.nuaa.nettop.model.ServMem;
import edu.nuaa.nettop.model.ServPort;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-08-05
 * Time: 11:21
 */
@Slf4j
@SuppressWarnings("all")
public class ProxyUtil {

    public static RoutingTable getRoutingTable(String serverIp, String nodeName) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("name", nodeName);
        String url = "http://" + serverIp + ":2081/lxd/rt/{name}";
        ResponseEntity<RoutingTable> responseEntity = restTemplate.getForEntity(url, RoutingTable.class, params);
        return responseEntity.getBody();
    }

    public static Long snmpGetTcpOut(String serverIp, String deviceIp) {

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIp", deviceIp);
        String url = "http://" + serverIp + ":2081/snmp/tcp/out/{deviceIp}";
        ResponseEntity<Long> responseEntity = restTemplate.getForEntity(url, Long.class, params);
        return responseEntity.getBody();
    }

    public static Long snmpGetUdpOut(String serverIp, String deviceIp) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIp", deviceIp);
        String url = "http://" + serverIp + ":2081/snmp/udp/out/{deviceIp}";
        ResponseEntity<Long> responseEntity = restTemplate.getForEntity(url, Long.class, params);
        return responseEntity.getBody();
    }

    public static String getCpu(String serverIp, String deviceIp) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIp", deviceIp);
        String url = "http://" + serverIp + ":2081/snmp/cpu/{deviceIp}";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, params);
        return responseEntity.getBody();
    }

    public static String getMem(String serverIp, String deviceIp) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIp", deviceIp);
        String url = "http://" + serverIp + ":2081/snmp/mem/{deviceIp}";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, params);
        return responseEntity.getBody();
    }

    public static ServMem getAllMem(String serverIp, String deviceIp) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIp", deviceIp);
        String url = "http://" + serverIp + ":2081/snmp/mem/all/{deviceIp}";
        ResponseEntity<ServMem> responseEntity = restTemplate.getForEntity(url, ServMem.class, params);
        return responseEntity.getBody();
    }

    public static ServPort getServPort(String serverIp, String deviceIp) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIp", deviceIp);
        String url = "http://" + serverIp + ":2081/snmp/serv/port/{deviceIp}";
        ResponseEntity<ServPort> responseEntity = restTemplate.getForEntity(url, ServPort.class, params);
        return responseEntity.getBody();
    }

    public static LxdResponse getLxdStatus(String serverIp, LxdRequest request) throws MonitorException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LxdRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + serverIp + ":2081/lxd/status";
        LxdResponse response = restTemplate.postForObject(url, entity, LxdResponse.class);
        return response;
    }

    public static void runTcpdumpCapUdp(String serverIp, String nodeName) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeName", nodeName);
        String url = "http://" + serverIp + ":2081/lxd/cap/udp/{nodeName}";
        ResponseEntity<Void> responseEntity = restTemplate.getForEntity(url, Void.class, params);
    }

    public static void runTcpdumpCapTcp(String serverIp, String nodeName) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeName", nodeName);
        String url = "http://" + serverIp + ":2081/lxd/cap/tcp/{nodeName}";
        ResponseEntity<Void> responseEntity = restTemplate.getForEntity(url, Void.class, params);
    }

    public static void cancelTcpdump(String serverIp, String nodeName) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeName", nodeName);
        String url = "http://" + serverIp + ":2081/lxd/cap/cancel/{nodeName}";
        ResponseEntity<Void> responseEntity = restTemplate.getForEntity(url, Void.class, params);
    }

    public static Long getUdpRate(String serverIp, String nodeName) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeName", nodeName);
        String url = "http://" + serverIp + ":2081/lxd/udp/{nodeName}";
        ResponseEntity<Long> responseEntity = restTemplate.getForEntity(url, Long.class, params);
        return responseEntity.getBody();
    }

    public static Long getTcpRate(String serverIp, String nodeName) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeName", nodeName);
        String url = "http://" + serverIp + ":2081/lxd/tcp/{nodeName}";
        ResponseEntity<Long> responseEntity = restTemplate.getForEntity(url, Long.class, params);
        return responseEntity.getBody();
    }

    public static String getDDosRate(String serverIp, String nodeName, String ip) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeName", nodeName);
        params.put("ip", ip);
        String url = "http://" + serverIp + ":2081/lxd/ddos/rate/{nodeName}/{ip}";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, params);
        return responseEntity.getBody();
    }

    public static void runMultiServ(String serverIp, String nodeName) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeName", nodeName);
        String url = "http://" + serverIp + ":2081/lxd/ddos/serv/{nodeName}";
        ResponseEntity<Void> responseEntity = restTemplate.getForEntity(url, Void.class, params);
    }

    public static PktStatistics getDevPktSta(String serverIp, String deviceIp) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIp", deviceIp);
        String url = "http://" + serverIp + ":2081/snmp/dev/pkt/{deviceIp}";
        ResponseEntity<PktStatistics> responseEntity = restTemplate.getForEntity(url, PktStatistics.class, params);
        return responseEntity.getBody();
    }

    public static void main(String[] args) {
//        runTcpdumpCapTcp("192.168.31.13", "n4td1");
//        runTcpdumpCapUdp("192.168.31.13", "n4td1");
//        cancelTcpdump("192.168.31.13", "n4td1");
        System.out.println(getDevPktSta("192.168.31.13", "1.20.0.8"));
    }
}
