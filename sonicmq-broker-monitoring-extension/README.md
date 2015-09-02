sonicmq-monitoring-extension
============================
An AppDynamics extension to be used with a stand alone Java machine agent to provide metrics for SonicMQ server.


## Use Case ##
Industry-leading companies rely on SonicMQ Continuously Available Architecture (CAA) for mission-critical communications within the enterprise and for connecting remote business partners and customers. 
Its guaranteed message delivery system ensures that messages are not lost due to any type of software hardware or network failure.


## Metrics Provided ##


### Broker Metrics ###

* BROKER_BYTES_DELIVEREDPERSECOND
* BROKER_BYTES_RECEIVEDPERSECOND
* BROKER_BYTES_TOPICDBSIZE
* BROKER_CONNECTIONS_COUNT
* BROKER_CONNECTIONS_REJECTEDPERMINUTE
* BROKER_MESSAGES_DELIVERED
* BROKER_MESSAGES_RECEIVED
* BROKER_MESSAGES_DELIVEREDPERSECOND
* BROKER_MESSAGES_RECEIVEDPERSECOND
* CONNECTION_BYTES_DELIVERED
* CONNECTION_BYTES_DELIVEREDPERSECOND
* CONNECTION_BYTES_RECEIVED
* CONNECTION_BYTES_RECEIVEDPERSECOND
* CONNECTION_MESSAGES_DELIVERED
* CONNECTION_MESSAGES_DELIVEREDPERSECOND
* CONNECTION_MESSAGES_RECEIVED
* CONNECTION_MESSAGES_RECEIVEDPERSECOND
* QUEUE_MESSAGES_COUNT
* QUEUE_MESSAGES_DELIVEREDPERSECOND
* QUEUE_MESSAGES_MAXAGE
* QUEUE_MESSAGES_MAXDEPTH
* QUEUE_MESSAGES_RECEIVEDPERSECOND
* QUEUE_MESSAGES_SIZE
* QUEUE_MESSAGES_TIMEINQUEUE

### Container Metrics ###

* SYSTEM_MEMORY_MAXUSAGE
* SYSTEM_MEMORY_CURRENTUSAGE
* SYSTEM_THREADS_CURRENTTOTAL
* SYSTEM_THREADS_CURRENTPOOLSIZE
* SYSTEM_THREADS_MAXPOOLSIZE
* SYSTEM_THREADS_POOLWAITS

Note : By default, a Machine agent or a AppServer agent can send a fixed number of metrics to the controller. To change this limit, please follow the instructions mentioned [here](http://docs.appdynamics.com/display/PRO14S/Metrics+Limits).
For eg.  
```    
    java -Dappdynamics.agent.maxMetrics=2500 -jar machineagent.jar
```

## Installation ##

1. Copy the following jars to `src/lib`
   * broker.jar
   * mfcontext.jar
   * mgmt_client.jar
   * sonic_Client.jar
   * sonic_Crypto.jar
   * sonic_mgmt_client.jar
2. Run "mvn clean install" and find the SonicmqMonitor.zip file in the "target" folder. You can also download the SonicmqMonitor.zip from [AppDynamics Exchange][].
3. Unzip as "SonicmqMonitor" and copy the "SonicmqMonitor" directory to `<MACHINE_AGENT_HOME>/monitors`


## Configuration ##

Note : Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a [yaml validator](http://yamllint.com/)

1. Configure sonicmq by editing the config.yml file in `<MACHINE_AGENT_HOME>/monitors/SonicmqMonitor/`.
   For eg.
   ```
       #location for eg. tcp://localhost:2506
       location: ""
       
       username: ""
       
       password: ""
       
       brokerDomain: ""
       
       containerDomain: ""
       
       #prefix used to show up metrics in AppDynamics
       metricPrefix: "Custom Metrics|SonicMq|"
       
       #JMX timeout in seconds
       timeout: 10

   ```


2. Paste the following SonicMQ jars in the `monitors/SonicmqMonitor/lib` directory
   * broker.jar
   * mfcontext.jar
   * mgmt_client.jar
   * sonic_Client.jar
   * sonic_Crypto.jar
   * sonic_mgmt_client.jar

3. Configure the path to the config.yml file by editing the <task-arguments> in the monitor.xml file in the `<MACHINE_AGENT_HOME>/monitors/SonicmqMonitor/` directory. Below is the sample

     ```
     <task-arguments>
         <!-- config file-->
         <argument name="config-file" is-required="true" default-value="monitors/SonicmqMonitor/config.yml" />
          ....
     </task-arguments>
    ```



## Contributing ##

Always feel free to fork and contribute any changes directly via [GitHub][].

## Community ##

Find out more in the [AppDynamics Exchange][].

## Support ##

For any questions or feature request, please contact [AppDynamics Center of Excellence][].

**Version:** 1.0.0
**Controller Compatibility:** 3.7+
**SonicMQ Versions Tested On:** 8.5.1

[Github]: https://github.com/Appdynamics/sonicmq-monitoring-extension
[AppDynamics Exchange]: http://community.appdynamics.com/t5/AppDynamics-eXchange/idb-p/extensions
[AppDynamics Center of Excellence]: mailto:help@appdynamics.com
