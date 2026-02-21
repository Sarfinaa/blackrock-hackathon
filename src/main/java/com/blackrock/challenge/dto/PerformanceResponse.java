package com.blackrock.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PerformanceResponse {

    @JsonProperty("response_time")
    private String responseTime;

    @JsonProperty("memory_usage")
    private String memoryUsage;

    @JsonProperty("current_thread_count")
    private int currentThreadCount;

    public PerformanceResponse() {
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(String memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public int getCurrentThreadCount() {
        return currentThreadCount;
    }

    public void setCurrentThreadCount(int currentThreadCount) {
        this.currentThreadCount = currentThreadCount;
    }
}
