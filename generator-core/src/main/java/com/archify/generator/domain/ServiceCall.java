package com.archify.generator.domain;

public class ServiceCall {
    private String targetService;
    private String path;

    public ServiceCall() {
    }

    public ServiceCall(String targetService, String path) {
        this.targetService = targetService;
        this.path = path;
    }

    public String getTargetService() {
        return targetService;
    }

    public void setTargetService(String targetService) {
        this.targetService = targetService;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
