package com.appdynamics.extensions.sonicmq.config;


public class BrokerConfig {

    private String displayName;
    private String objectName;
    private ContainerConfig container;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public ContainerConfig getContainer() {
        return container;
    }

    public void setContainer(ContainerConfig container) {
        this.container = container;
    }
}
