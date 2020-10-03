package edu.nuaa.nettop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-10-03
 * Time: 13:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RtRequest {

    private String wlid;
    private String nodeName;
    private String serverIp;
}
