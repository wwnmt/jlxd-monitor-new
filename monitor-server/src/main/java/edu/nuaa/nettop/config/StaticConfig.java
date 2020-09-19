package edu.nuaa.nettop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class StaticConfig {

    public static String WEB_IP;

    public static int PORT;

    @Value("${web.ip}")
    private String webIp;

    @Value("${web.port}")
    private int port;

    @PostConstruct
    public void getApiToken() {
        StaticConfig.WEB_IP = this.webIp;
        StaticConfig.PORT = this.port;
    }
}
