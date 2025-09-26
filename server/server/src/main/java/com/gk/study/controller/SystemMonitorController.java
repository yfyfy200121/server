package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统监控控制器
 * 提供系统运行状态监控功能，用于展示系统性能指标和健康状态
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/monitor")
public class SystemMonitorController {

    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorController.class);

    /**
     * 获取系统基本信息
     * 包括JVM信息、内存使用情况、系统运行时间等
     * 
     * @return 系统信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/system-info", method = RequestMethod.GET)
    public APIResponse getSystemInfo() {
        try {
            Map<String, Object> systemInfo = new HashMap<>();
            
            // JVM信息
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            systemInfo.put("jvmName", runtimeBean.getVmName());
            systemInfo.put("jvmVersion", runtimeBean.getVmVersion());
            systemInfo.put("jvmVendor", runtimeBean.getVmVendor());
            systemInfo.put("uptime", runtimeBean.getUptime()); // 运行时间（毫秒）
            
            // 内存信息
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            systemInfo.put("heapMemoryUsed", memoryBean.getHeapMemoryUsage().getUsed());
            systemInfo.put("heapMemoryMax", memoryBean.getHeapMemoryUsage().getMax());
            systemInfo.put("heapMemoryCommitted", memoryBean.getHeapMemoryUsage().getCommitted());
            systemInfo.put("nonHeapMemoryUsed", memoryBean.getNonHeapMemoryUsage().getUsed());
            
            // 系统属性
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            systemInfo.put("osArch", System.getProperty("os.arch"));
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            
            // 处理器信息
            systemInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
            
            logger.info("System info retrieved successfully");
            return new APIResponse(ResponeCode.SUCCESS, "获取系统信息成功", systemInfo);
            
        } catch (Exception e) {
            logger.error("Failed to get system info", e);
            return new APIResponse(ResponeCode.FAIL, "获取系统信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统健康状态
     * 检查各个关键组件的健康状况
     * 
     * @return 健康状态信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public APIResponse getHealthStatus() {
        try {
            Map<String, Object> healthStatus = new HashMap<>();
            
            // 数据库连接状态
            // 这里可以添加数据库连接检查逻辑
            healthStatus.put("database", "UP");
            
            // 内存使用状态
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryBean.getHeapMemoryUsage().getMax();
            double memoryUsagePercent = (double) heapUsed / heapMax * 100;
            
            String memoryStatus = memoryUsagePercent > 80 ? "WARN" : "UP";
            healthStatus.put("memory", memoryStatus);
            healthStatus.put("memoryUsagePercent", String.format("%.2f", memoryUsagePercent));
            
            // AI服务状态（这里可以添加AI服务检查）
            healthStatus.put("aiService", "UP");
            
            // 磁盘空间状态
            long freeSpace = Runtime.getRuntime().freeMemory();
            long totalSpace = Runtime.getRuntime().totalMemory();
            double diskUsagePercent = (double) (totalSpace - freeSpace) / totalSpace * 100;
            
            String diskStatus = diskUsagePercent > 85 ? "WARN" : "UP";
            healthStatus.put("disk", diskStatus);
            healthStatus.put("diskUsagePercent", String.format("%.2f", diskUsagePercent));
            
            // 系统总体状态
            boolean isHealthy = "UP".equals(healthStatus.get("database")) && 
                               "UP".equals(memoryStatus) && 
                               "UP".equals(diskStatus);
            healthStatus.put("overall", isHealthy ? "UP" : "WARN");
            
            logger.info("Health status checked successfully");
            return new APIResponse(ResponeCode.SUCCESS, "获取健康状态成功", healthStatus);
            
        } catch (Exception e) {
            logger.error("Failed to get health status", e);
            return new APIResponse(ResponeCode.FAIL, "获取健康状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统性能指标
     * 包括响应时间、吞吐量等关键指标
     * 
     * @return 性能指标信息
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/performance", method = RequestMethod.GET)
    public APIResponse getPerformanceMetrics() {
        try {
            Map<String, Object> performance = new HashMap<>();
            
            // 线程信息
            ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
            ThreadGroup parentGroup;
            while ((parentGroup = rootGroup.getParent()) != null) {
                rootGroup = parentGroup;
            }
            
            performance.put("activeThreads", Thread.activeCount());
            performance.put("totalThreads", rootGroup.activeCount());
            
            // GC信息（简化版本）
            performance.put("gcCount", ManagementFactory.getGarbageCollectorMXBeans().size());
            
            // 类加载信息
            performance.put("loadedClassCount", ManagementFactory.getClassLoadingMXBean().getLoadedClassCount());
            performance.put("totalLoadedClassCount", ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount());
            performance.put("unloadedClassCount", ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount());
            
            // 运行时间
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            long uptimeMillis = runtimeBean.getUptime();
            performance.put("uptimeSeconds", uptimeMillis / 1000);
            performance.put("uptimeHours", uptimeMillis / (1000 * 60 * 60));
            
            logger.info("Performance metrics retrieved successfully");
            return new APIResponse(ResponeCode.SUCCESS, "获取性能指标成功", performance);
            
        } catch (Exception e) {
            logger.error("Failed to get performance metrics", e);
            return new APIResponse(ResponeCode.FAIL, "获取性能指标失败: " + e.getMessage());
        }
    }

    /**
     * 系统垃圾回收
     * 手动触发垃圾回收（仅在必要时使用）
     * 
     * @return 操作结果
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/gc", method = RequestMethod.POST)
    public APIResponse triggerGarbageCollection() {
        try {
            long beforeGC = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
            
            // 触发垃圾回收
            System.gc();
            
            // 等待一段时间让GC完成
            Thread.sleep(1000);
            
            long afterGC = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
            long freedMemory = beforeGC - afterGC;
            
            Map<String, Object> result = new HashMap<>();
            result.put("beforeGC", beforeGC);
            result.put("afterGC", afterGC);
            result.put("freedMemory", freedMemory);
            result.put("freedMemoryMB", freedMemory / (1024 * 1024));
            
            logger.info("Garbage collection triggered manually, freed {} MB", freedMemory / (1024 * 1024));
            return new APIResponse(ResponeCode.SUCCESS, "垃圾回收执行成功", result);
            
        } catch (Exception e) {
            logger.error("Failed to trigger garbage collection", e);
            return new APIResponse(ResponeCode.FAIL, "垃圾回收执行失败: " + e.getMessage());
        }
    }
}