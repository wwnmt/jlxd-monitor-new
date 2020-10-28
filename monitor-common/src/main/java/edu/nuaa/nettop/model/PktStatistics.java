package edu.nuaa.nettop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-10-28
 * Time: 11:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PktStatistics implements Serializable {

    private static final long serialVersionUID = -2228435431073426526L;

    private Long ipIn;  //IP分组接收数量

    private Long ipOut; //IP分组发送数量

    private Long tcpIn; //TCP报文段接收数量

    private Long tcpOut;    //TCP报文段发送数量

    private Long udpIn;     //udp接收数量

    private Long udpOut;    //udp发送数量

    private Long ipInErrs;    //ip接收错误报文数量（综合ip地址错误、MAC地址错误和路由错误）

    private Long tcpInErrs;   //tcp接收错误报文数量

    private Long udpInErrs;   //udp接收错误报文数量
}
