package edu.nuaa.nettop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 17:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeRequest {

    private String wlid;

    private String nodeId;

    private String serverIp;

    private String name;

    private int timeout;

    public boolean validate() {
        return wlid != null && nodeId != null
                && serverIp != null && name != null
                && timeout != 0;
    }
}
