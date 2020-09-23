package edu.nuaa.nettop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-23
 * Time: 9:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServPort {

    List<Port> ports = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Port{
        private String mc;   //服务器名称

        private String dkmc;  //端口名

        private String ip;   //端口IP

        private String recvBytes;

        private String sendBytes;

        private int zt;   //状态
    }
}
