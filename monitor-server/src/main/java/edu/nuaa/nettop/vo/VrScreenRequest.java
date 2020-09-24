package edu.nuaa.nettop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 18:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VrScreenRequest {

    List<String> serverIps = new ArrayList<>();
    List<VrPort> ports = new ArrayList<>();
    private String wlid;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VrPort implements Serializable {
        private String serverIp;
        private String deviceIp;
        private String vPort;
        private String pPort;
    }
}
