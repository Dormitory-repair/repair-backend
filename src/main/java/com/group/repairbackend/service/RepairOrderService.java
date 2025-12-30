package com.group.repairbackend.service;

import com.group.repairbackend.model.RepairOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RepairOrderService {
    void addOrder(
            String reporterAccount,
            String reporterName,
            String reporterPhone,
            String livingArea,
            String building,
            String roomNumber,
            String repairCategory,
            String specificItem,
            String problemDescription,
            MultipartFile[] images
    );

    List<RepairOrder> getOrdersByAccount(String account);

    // 工人端订单服务
    List<Map<String, Object>> getPendingOrders(String workType);
    List<Map<String, Object>> getAcceptedOrders(Integer workerId);
    List<Map<String, Object>> getOrderHistory(Integer workerId,String status);
    boolean acceptOrder(String orderId, Integer workerId);
    boolean completeOrder(String orderId, Integer workerId);
    Map<String, Object> getOrderDetail(String orderId);

    // 获取工人统计数据
    Map<String, Object> getWorkerStats(Integer workerId);
}
