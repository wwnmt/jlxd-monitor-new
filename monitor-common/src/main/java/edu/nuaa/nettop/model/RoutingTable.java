package edu.nuaa.nettop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-10-02
 * Time: 15:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutingTable {

    private String nodeName;

    private List<RouteContent> contents = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteContent{
        private String dest;
        private String gateway;
        private String mask;
        private String flags;
        private String metric;
        private String ref;
        private String iFace;
    }
}
