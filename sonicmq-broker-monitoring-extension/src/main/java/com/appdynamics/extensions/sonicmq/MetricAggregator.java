package com.appdynamics.extensions.sonicmq;


import com.appdynamics.extensions.sonicmq.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class MetricAggregator {

    private Configuration config;
    private BrokerCollector brokerCollector;
    private ContainerCollector containerCollector;

    public MetricAggregator(Configuration config){
        this(new BrokerCollector(),new ContainerCollector());
        this.config = config;
    }

    public MetricAggregator(BrokerCollector brokerCollector,ContainerCollector containerCollector){
        this.brokerCollector = brokerCollector;
        this.containerCollector = containerCollector;
    }

    public Map<String, String> getMetrics() {
        Map<String,String> metrics = new HashMap<String, String>();
        metrics.putAll(brokerCollector.getMetrics(config));
        metrics.putAll(containerCollector.getMetrics(config));
        return metrics;
    }


}
