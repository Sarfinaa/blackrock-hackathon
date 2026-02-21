package com.blackrock.challenge.service;

import com.blackrock.challenge.dto.PerformanceResponse;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class PerformanceService {

    public PerformanceResponse getMetrics(long startTime) {
        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;

        PerformanceResponse response = new PerformanceResponse();

        // 1. Response Time (HH:mm:ss.SSS)
        // Convert durationMs to HH:mm:ss.SSS format using UTC timezone so 0 ms is
        // 00:00:00.000
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        response.setResponseTime(sdf.format(new Date(durationMs)));

        // 2. Memory Usage (XXX.XX MB)
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double memoryInMB = usedMemory / (1024.0 * 1024.0);
        response.setMemoryUsage(String.format("%.2f MB", memoryInMB));

        // 3. Current Thread Count
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        response.setCurrentThreadCount(threadMXBean.getThreadCount());

        return response;
    }
}
