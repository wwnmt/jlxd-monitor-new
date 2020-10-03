package edu.nuaa.nettop.common.constant;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 17:27
 */
@Getter
public enum TaskType {

    NET_TASK(0, "net"),
    NODE_TASK(1, "node"),
    DDOS_SCREEN(2, "ddos_screen"),
    VR_SCREEN(3, "vr_screen"),
    PRO_SCREEN(4, "pro_screen"),
    ROUTER_ATTACK_SCREEN(5, "ospf_attack_screen"),
    ROUTING_TABLE(6, "rt");

    private final int code;
    private final String desc;

    TaskType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
