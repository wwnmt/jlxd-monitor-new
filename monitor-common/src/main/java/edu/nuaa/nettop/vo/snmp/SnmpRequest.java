package edu.nuaa.nettop.vo.snmp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 15:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpRequest implements Serializable {

    private String communityName;   //public

    private String deviceIp;    //设备Ip

    private Integer port;       //snmp端口号161

    private int version;    //默认1
}
