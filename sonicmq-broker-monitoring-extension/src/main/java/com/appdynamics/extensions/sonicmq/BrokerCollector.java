package com.appdynamics.extensions.sonicmq;


import com.appdynamics.extensions.sonicmq.config.BrokerConfig;
import com.appdynamics.extensions.sonicmq.config.Configuration;
import com.appdynamics.extensions.util.MetricUtils;
import com.google.common.base.Strings;
import com.sonicsw.mf.common.metrics.IMetric;
import com.sonicsw.mf.common.metrics.IMetricIdentity;
import com.sonicsw.mf.common.runtime.IComponentState;
import com.sonicsw.mf.jmx.client.JMSConnectorClient;
import com.sonicsw.mq.common.runtime.IDurableSubscriptionData;
import com.sonicsw.mq.common.runtime.IQueueData;
import com.sonicsw.mq.mgmtapi.runtime.IBrokerProxy;
import com.sonicsw.mq.mgmtapi.runtime.MQProxyFactory;
import org.apache.log4j.Logger;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class BrokerCollector extends Collector{

    public static final String IsPrimary = "1";
    public static final String IsBackup = "0";
    private static final Logger logger = Logger.getLogger(BrokerCollector.class);

    private static final IMetricIdentity[] metricIds = new IMetricIdentity[] {

            IBrokerProxy.BROKER_BYTES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.BROKER_BYTES_RECEIVEDPERSECOND_METRIC_ID,
            IBrokerProxy.BROKER_BYTES_TOPICDBSIZE_METRIC_ID,

            IBrokerProxy.BROKER_CONNECTIONS_COUNT_METRIC_ID,
            IBrokerProxy.BROKER_CONNECTIONS_REJECTEDPERMINUTE_METRIC_ID,

            IBrokerProxy.BROKER_MESSAGES_DELIVERED_METRIC_ID,
            IBrokerProxy.BROKER_MESSAGES_RECEIVED_METRIC_ID,
            IBrokerProxy.BROKER_MESSAGES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.BROKER_MESSAGES_RECEIVEDPERSECOND_METRIC_ID,

            IBrokerProxy.CONNECTION_BYTES_DELIVERED_METRIC_ID,
            IBrokerProxy.CONNECTION_BYTES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.CONNECTION_BYTES_RECEIVED_METRIC_ID,
            IBrokerProxy.CONNECTION_BYTES_RECEIVEDPERSECOND_METRIC_ID,

            IBrokerProxy.CONNECTION_MESSAGES_DELIVERED_METRIC_ID,
            IBrokerProxy.CONNECTION_MESSAGES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.CONNECTION_MESSAGES_RECEIVED_METRIC_ID,
            IBrokerProxy.CONNECTION_MESSAGES_RECEIVEDPERSECOND_METRIC_ID,

            IBrokerProxy.QUEUE_MESSAGES_COUNT_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_DELIVEREDPERSECOND_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_MAXAGE_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_MAXDEPTH_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_RECEIVEDPERSECOND_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_SIZE_METRIC_ID,
            IBrokerProxy.QUEUE_MESSAGES_TIMEINQUEUE_METRIC_ID
    };




    public Map<String, String> getMetrics(Configuration config) {
        logger.debug("Getting metrics from Broker. ");
        Map<String,String> metrics = new HashMap<String, String>();
        try{
            //connect JMX
            connect(config.getLocation(),config.getUsername(),config.getPassword(),config.getTimeout());
            List<BrokerConfig> brokers = config.getBrokers();
            if(brokers != null){
                for(BrokerConfig aBrokerConfig : brokers){
                    try {
                        IBrokerProxy proxy = getProxy(client, new ObjectName(aBrokerConfig.getObjectName()));

                        metrics.put(aBrokerConfig.getDisplayName() + METRIC_SEPARATOR + "IsPrimary", getReplicationType(proxy));
                        metrics.put(aBrokerConfig.getDisplayName() + METRIC_SEPARATOR + "IsActive",proxy.getState().toString());
                        metrics.put(aBrokerConfig.getDisplayName() + METRIC_SEPARATOR + "ReplicationState", proxy.getReplicationState().toString());

                        if(isBrokerActive(proxy)) {
                            proxy.enableMetrics(metricIds);
                            //set instance metrics
                            setBrokerMetrics(proxy, aBrokerConfig, metrics);
                            //set queue metrics
                            setQueueMetrics(proxy, aBrokerConfig, metrics,config.getQueueExcludePatterns());
                            //set topic metrics
                            setTopicMetrics(proxy,aBrokerConfig,metrics,config.getUserExcludePatterns(),config.getTopicExcludePatterns());
                        }

                    }
                    catch (MalformedObjectNameException e) {
                        logger.error("Failed to create proxy for id '"+ aBrokerConfig.getObjectName() +"': ", e);
                    }
                    catch(Exception e){
                        logger.error("Failed to fetch metrics for " + aBrokerConfig.getObjectName() + " : ", e);
                    }
                }
            }
        }
        finally{
            disconnect(config.getLocation());
        }
        logger.debug("Collected Broker metrics");
        return metrics;
    }

    private void setTopicMetrics(IBrokerProxy proxy, BrokerConfig aBrokerConfig, Map<String, String> metrics,List<String> userExcludePatterns,List<String> topicExcludePatterns) {
        //get topics for all users
        List<String> users = proxy.getUsersWithDurableSubscriptions(null);
        if(users != null){
            for(String user : users){
                if(!isExcluded(user, userExcludePatterns)){
                    List<IDurableSubscriptionData> topics = proxy.getDurableSubscriptions(user);
                    if(topics != null){
                        for(IDurableSubscriptionData topic : topics){
                            if(!isExcluded(topic.getTopicName(),topicExcludePatterns)){
                                String topicMetricPrefix = aBrokerConfig.getDisplayName() + METRIC_SEPARATOR + "users" + METRIC_SEPARATOR +
                                        user + METRIC_SEPARATOR + "topics" + METRIC_SEPARATOR + topic.getTopicName() + METRIC_SEPARATOR;
                                metrics.put(topicMetricPrefix + "MessageCount", MetricUtils.toWholeNumberString(topic.getMessageCount()));
                                metrics.put(topicMetricPrefix + "TotalMessageSize", MetricUtils.toWholeNumberString(topic.getMessageSize()));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isExcluded(String element, List<String> excludePatterns) {
        if(excludePatterns != null) {
            for (String pattern : excludePatterns) {
                if (element.matches(pattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBrokerActive(IBrokerProxy proxy) {
        return proxy.getState() == IComponentState.STATE_ONLINE;
    }


    private void setQueueMetrics(IBrokerProxy proxy, BrokerConfig aBrokerConfig, Map<String, String> metrics,List<String> excludePatterns) {
        ArrayList<IQueueData> queues = proxy.getQueues(null);
        if(queues != null && queues.size() > 0){
            for(IQueueData queue : queues){
                if(!isExcluded(queue.getQueueName(), excludePatterns)) {
                    String queueMetricPrefix = aBrokerConfig.getDisplayName() + METRIC_SEPARATOR + "queues" + METRIC_SEPARATOR + queue.getQueueName() + METRIC_SEPARATOR;
                    metrics.put(queueMetricPrefix + "MessageCount", MetricUtils.toWholeNumberString(queue.getMessageCount()));
                    metrics.put(queueMetricPrefix + "TotalMessageSize", MetricUtils.toWholeNumberString(queue.getTotalMessageSize()));
                }
            }
        }
        logger.debug("Collected queue metrics.");
    }




    private String getReplicationType(IBrokerProxy proxy) {
        return "PRIMARY".equals(proxy.getReplicationType()) ? IsPrimary : IsBackup;
    }

    private void setBrokerMetrics(IBrokerProxy proxy,BrokerConfig aBrokerConfig ,Map<String, String> metrics) {
        IMetric[] data = proxy.getMetricsData(metricIds, false).getMetrics();
        for (IMetric m : data) {
            String metricName = getMetricName(m.getMetricIdentity());
            if (!Strings.isNullOrEmpty(metricName)) {
                metricName = aBrokerConfig.getDisplayName() + Collector.METRIC_SEPARATOR + metricName;
                metrics.put(metricName, MetricUtils.toWholeNumberString(m.getValue()));
            }
        }
        logger.debug("Collected basic broker metrics.");
    }

    protected final IBrokerProxy getProxy(JMSConnectorClient client, ObjectName jmxName) {
        return MQProxyFactory.createBrokerProxy(client, jmxName);
    }

}
