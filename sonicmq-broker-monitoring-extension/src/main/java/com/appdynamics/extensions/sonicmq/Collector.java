package com.appdynamics.extensions.sonicmq;


import com.sonicsw.mf.common.metrics.IMetricIdentity;
import com.sonicsw.mf.jmx.client.JMSConnectorAddress;
import com.sonicsw.mf.jmx.client.JMSConnectorClient;
import org.apache.log4j.Logger;

import java.util.Hashtable;

public abstract class Collector {

    public static final String CONNECTION_URLS = "ConnectionURLs";
    public static final String DEFAULT_USER = "DefaultUser";
    public static final String DEFAULT_PASSWORD = "DefaultPassword";
    public static final String METRIC_SEPARATOR = "|";
    protected JMSConnectorClient client;
    public static final Logger logger = Logger.getLogger(SonicMqBrokerMonitor.class);

    public void connect(String location,String username,String password,int timeout)
    {
        if(client != null) {
            return;
        }

        logger.debug("Connecting to '"+ location+"'...");
        client = new JMSConnectorClient();
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(CONNECTION_URLS, location);
        env.put(DEFAULT_USER, username);
        env.put(DEFAULT_PASSWORD, password);
        client.connect(new JMSConnectorAddress(env), timeout);
        logger.debug("Connected to '"+ location+"'");
    }

    /**
     * Disconnects the {@link JMSConnectorClient}.
     */
    public void disconnect(String location)
    {
        if(client == null) {
            return;
        }

        logger.debug("Disconnecting from '"+ location+"'...");
        client.disconnect();
        client = null;
        logger.debug("Disconnected from '"+ location+"'");
    }


    public String getMetricName(IMetricIdentity metricIdentity) {
        if(metricIdentity != null && metricIdentity.getNameComponents()!= null && metricIdentity.getNameComponents().length == 3){
            return metricIdentity.getNameComponents()[0] + METRIC_SEPARATOR + metricIdentity.getNameComponents()[1] + METRIC_SEPARATOR + metricIdentity.getNameComponents()[2];
        }
        logger.warn("Metric not found - " + metricIdentity.getName() + " ; Absolute Name = " + metricIdentity.getAbsoluteName());
        return "";
    }

}
