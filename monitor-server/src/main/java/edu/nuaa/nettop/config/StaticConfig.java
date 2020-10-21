package edu.nuaa.nettop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class StaticConfig {

    public static String WEB_IP;

    public static int PORT;

    public static String REDIS_HOST;

    public static int REDIS_PORT;

    public static String REDIS_PASSWORD;

    @Value("${web.ip}")
    private String webIp;

    @Value("${web.port}")
    private int port;

    @Value("${web.redis.host}")
    private String redisHost;

    @Value("${web.redis.port}")
    private int redisPort;

    @Value("${web.redis.password}")
    private String redisPwd;

    @PostConstruct
    public void getApiToken() {
        StaticConfig.WEB_IP = this.webIp;
        StaticConfig.PORT = this.port;
        StaticConfig.REDIS_HOST = this.redisHost;
        StaticConfig.REDIS_PORT = this.redisPort;
        StaticConfig.REDIS_PASSWORD = this.redisPwd;
    }
}
