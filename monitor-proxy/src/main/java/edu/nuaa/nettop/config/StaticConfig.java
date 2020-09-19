package edu.nuaa.nettop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticConfig {

    public static int THRESHOLD;

    @Value("${monitor.threshold}")
    private int threshold;

    @PostConstruct
    public void getApiToken() {
        StaticConfig.THRESHOLD = this.threshold;
    }
}
