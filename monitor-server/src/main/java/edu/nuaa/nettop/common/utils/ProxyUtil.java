package edu.nuaa.nettop.common.utils;

import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.model.LxdStatus;
import edu.nuaa.nettop.model.ServMem;
import edu.nuaa.nettop.model.ServPort;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
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

    public static Long getTcpOut(String serverIp, String deviceIp) {

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIp", deviceIp);
        String url = "http://" + serverIp + ":2081/snmp/tcp/out/{deviceIp}";
        ResponseEntity<Long> responseEntity = restTemplate.getForEntity(url, Long.class, params);
        return responseEntity.getBody();
    }

    public static Long getUdpOut(String serverIp, String deviceIp) {
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
}
