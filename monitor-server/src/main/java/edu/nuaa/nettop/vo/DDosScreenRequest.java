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
 * Time: 18:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DDosScreenRequest implements Serializable {

    private String wlid;

    private String rwid;

    private Attacker attacker;

    private Victim victim;

    private List<Td> tds = new ArrayList<>();

    private List<String> serverIps = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attacker implements Serializable {
        private String name;
        private String fullName;
        private String serverIp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Victim implements Serializable {
        private String name;
        private String fullName;
        private String ip;
        private String manageIp;
        private String serverIp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Td implements Serializable {
        private String name;
        private String fullName;
        private String manageIp;
        private String serverIp;
    }
}
