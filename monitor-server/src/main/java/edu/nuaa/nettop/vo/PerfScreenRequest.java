package edu.nuaa.nettop.vo;

import edu.nuaa.nettop.common.model.Link;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 18:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerfScreenRequest {

    private String wlid;

    private List<String> serverIps = new ArrayList<>();

    private Map<String, String> linkInfoMap;

    private Map<String, Integer> linkBandwidthMap;

    private String routerName;

    private String routerDeployServer;
}
