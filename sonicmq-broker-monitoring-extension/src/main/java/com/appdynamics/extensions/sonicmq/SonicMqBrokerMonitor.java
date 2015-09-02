package com.appdynamics.extensions.sonicmq;


import com.appdynamics.extensions.PathResolver;
import com.appdynamics.extensions.sonicmq.config.Configuration;
import com.appdynamics.extensions.yml.YmlReader;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * This extension will extract metrics from Sonic MQ.
 */

public class SonicMqBrokerMonitor extends AManagedMonitor{

    public static final Logger logger = Logger.getLogger(SonicMqBrokerMonitor.class);
    public static final String METRIC_SEPARATOR = "|";
    public static final String CONFIG_ARG = "config-file";


    public SonicMqBrokerMonitor() {
        String msg = "Using Monitor Version [" + getImplementationVersion() + "]";
        logger.info(msg);
        System.out.println(msg);
    }

    public TaskOutput execute(Map<String, String> taskArgs, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        if (taskArgs != null) {
            logger.info( "Starting the SonicMQ Monitoring task.");
            logger.debug("Task Arguments Passed ::" + taskArgs);
            String configFilename = taskArgs.get(CONFIG_ARG);
            try {
                File configFile = PathResolver.getFile(configFilename, AManagedMonitor.class);
                if(configFile == null){
                    throw new FileNotFoundException();
                }
                //read the config.
                Configuration config = YmlReader.readFromFile(configFile,Configuration.class);
                final MetricAggregator metricAggregator = new MetricAggregator(config);
                Map<String,String> metrics = metricAggregator.getMetrics();
                printMetrics(config,metrics);
                
                logger.info("SonicMQ monitoring task completed successfully.");
                return new TaskOutput("SonicMQ monitoring task completed successfully.");
            } catch (FileNotFoundException e) {
                logger.error("Config file not found :: " + configFilename, e);
            } catch(YmlReader.InvalidYmlPathException iype){
                logger.error("Cannot read YAML file ::" + configFilename,iype);
            } catch (Exception e) {
                logger.error("Metrics collection failed", e);
            }
        }
        throw new TaskExecutionException("SonicMQ monitoring task completed with failures.");
    }

    private void printMetrics(Configuration config,Map<String, String> metrics) {
        for(Map.Entry<String,String> entry : metrics.entrySet()){
            printAverageAverageIndividual(config.getMetricPrefix() + entry.getKey(), entry.getValue());
        }
    }


    private void printAverageAverageIndividual(String metricPath, String metricValue) {
        printMetric(metricPath, metricValue,
                MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE,
                MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL
        );
    }


    /**
     * A helper method to report the metrics.
     * @param metricPath
     * @param metricValue
     * @param aggType
     * @param timeRollupType
     * @param clusterRollupType
     */
    private void printMetric(String metricPath,String metricValue,String aggType,String timeRollupType,String clusterRollupType) {
        MetricWriter metricWriter = getMetricWriter(metricPath,
                aggType,
                timeRollupType,
                clusterRollupType
        );
        //System.out.println(getLogPrefix()+"Sending [" + aggType + METRIC_SEPARATOR + timeRollupType + METRIC_SEPARATOR + clusterRollupType
        //            + "] metric = " + metricPath + " = " + metricValue);
        if (logger.isDebugEnabled()) {
            logger.debug("Sending [" + aggType + METRIC_SEPARATOR + timeRollupType + METRIC_SEPARATOR + clusterRollupType
                    + "] metric = " + metricPath + " = " + metricValue);
        }
        metricWriter.printMetric(metricValue);
    }


    public static String getImplementationVersion() {
        return SonicMqBrokerMonitor.class.getPackage().getImplementationTitle();
    }
}
