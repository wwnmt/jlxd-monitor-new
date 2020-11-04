package edu.nuaa.nettop.common.response.perf;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-10-28
 * Time: 9:55
 */
public class BoDevNetCount implements Serializable {


    private static final long serialVersionUID = 7889116897836650863L;

    private Long ipIn;  //IP分组接收数量

    private Long ipOut; //IP分组发送数量

    private Long tcpIn; //TCP报文段接收数量

    private Long tcpOut;    //TCP报文段发送数量

    private Long udpIn;     //udp接收数量

    private Long udpOut;    //udp发送数量

    private Long icmpIn;

    private Long icmpOut;

    private Long ipInErrs;    //ip接收错误报文数量（综合ip地址错误、MAC地址错误和路由错误）

    private Long tcpInErrs;   //tcp接收错误报文数量

    private Long udpInErrs;   //udp接收错误报文数量

    public Long getIcmpIn() {
        return icmpIn;
    }

    public void setIcmpIn(Long icmpIn) {
        this.icmpIn = icmpIn;
    }

    public Long getIcmpOut() {
        return icmpOut;
    }

    public void setIcmpOut(Long icmpOut) {
        this.icmpOut = icmpOut;
    }

    public Long getIpIn() {
        return ipIn;
    }

    public void setIpIn(Long ipIn) {
        this.ipIn = ipIn;
    }

    public Long getIpOut() {
        return ipOut;
    }

    public void setIpOut(Long ipOut) {
        this.ipOut = ipOut;
    }

    public Long getTcpIn() {
        return tcpIn;
    }

    public void setTcpIn(Long tcpIn) {
        this.tcpIn = tcpIn;
    }

    public Long getTcpOut() {
        return tcpOut;
    }

    public void setTcpOut(Long tcpOut) {
        this.tcpOut = tcpOut;
    }

    public Long getUdpIn() {
        return udpIn;
    }

    public void setUdpIn(Long udpIn) {
        this.udpIn = udpIn;
    }

    public Long getUdpOut() {
        return udpOut;
    }

    public void setUdpOut(Long udpOut) {
        this.udpOut = udpOut;
    }

    public Long getIpInErrs() {
        return ipInErrs;
    }

    public void setIpInErrs(Long ipInErrs) {
        this.ipInErrs = ipInErrs;
    }

    public Long getTcpInErrs() {
        return tcpInErrs;
    }

    public void setTcpInErrs(Long tcpInErrs) {
        this.tcpInErrs = tcpInErrs;
    }

    public Long getUdpInErrs() {
        return udpInErrs;
    }

    public void setUdpInErrs(Long udpInErrs) {
        this.udpInErrs = udpInErrs;
    }
}
