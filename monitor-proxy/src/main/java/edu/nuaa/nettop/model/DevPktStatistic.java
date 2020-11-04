package edu.nuaa.nettop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-10-29
 * Time: 11:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevPktStatistic {

    private Long time;

    private Long ipIn;  //IP分组接收数量

    private Long ipOut; //IP分组发送数量

    private Long tcpIn; //TCP报文段接收数量

    private Long tcpOut;    //TCP报文段发送数量

    private Long udpIn;     //udp接收数量

    private Long udpOut;    //udp发送数量

    private Long icmpIn;

    private Long icmpOut;
}
