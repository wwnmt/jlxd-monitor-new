package edu.nuaa.nettop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-30
 * Time: 11:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OspfScreenRequest {

    private String wlid;

    private String pre;

    private String victim;

    private String vicServerIp;

    private String attacker;

    private String attServerIp;
}
