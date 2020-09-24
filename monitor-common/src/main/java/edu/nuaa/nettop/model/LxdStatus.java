package edu.nuaa.nettop.model;

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
 * Time: 15:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LxdStatus implements Serializable {

    private final List<PortStatus> interfaceList = new ArrayList<>();
    private String name;
    private int status;

    private long diskUsage;

    private long memUsage;

    private long memUsagePeak;

    private long cpuTime;

    public void addInterface(PortStatus port) {
        interfaceList.add(port);
    }
}
