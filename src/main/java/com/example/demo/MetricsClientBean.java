package com.example.demo;

//import com.example.webservice.Controller.UserController;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsClientBean {
    private final static Logger logger = LoggerFactory.getLogger(MetricsClientBean.class);

    @Value("${publish.metrics}")
    private boolean publishMetrics;

    @Value("${metrics.server.hostname}")
    private String metricsServerHost;

    @Value("${metrics.server.port}")
    private int metricsServerPort;

    @Bean
    public StatsDClient metricsClient() {
        logger.info("publishMetrics = " + publishMetrics);

        if (publishMetrics) {
            return new NonBlockingStatsDClient("cyse6225", metricsServerHost, metricsServerPort);
        }

        return new NoOpStatsDClient();
    }

}
