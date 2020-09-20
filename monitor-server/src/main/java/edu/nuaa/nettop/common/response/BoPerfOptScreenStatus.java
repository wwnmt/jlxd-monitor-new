/*
 * 性能优化数字大屏监控数据
 */
package edu.nuaa.nettop.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoPerfOptScreenStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2827518726281241031L;

    private String wlid;    //网络id

    private long tm;    //时序间隔

    private List<BoServerStatus> routers = new ArrayList<>();   //集群中各被显示路由器的资源监控数据

    private BoNetServStatus sevstatus;   //集群服务器的总体资源利用率

    private List<BoLinkStatus> links = new ArrayList<>();   //链路流量吞吐量 topN
    
    private List<BoLinkLrbStatus> rlinks = new ArrayList<>();   //链路容量与流量大小比值的前10位的柱状图
}
