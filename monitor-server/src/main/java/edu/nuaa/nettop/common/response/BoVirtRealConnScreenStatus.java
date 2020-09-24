/*
 * 虚实结合数字大屏监控数据
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
public class BoVirtRealConnScreenStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2827518726281241031L;

    private String wlid;    //网络id

    private long tm;    //时序间隔

    private BoNetServStatus sevstatus;   //集群服务器的总体资源利用率

    private List<BoServerIntfStatus> topsdks = new ArrayList<>();   //服务器活跃网络接口的流量前5位

    private List<BoServerIntfStatus> sdks = new ArrayList<>();   //被监测服务器接口流量信息 3
}
