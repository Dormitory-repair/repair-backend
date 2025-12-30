package com.group.repairbackend.service.impl;

import com.group.repairbackend.mapper.RepairOrderMapper;
import com.group.repairbackend.model.RepairOrder;
import com.group.repairbackend.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    @Resource
    private RepairOrderMapper repairOrderMapper;

    @Value("${file.upload-path}")
    private String uploadDir;

    @Override
    public void addOrder(
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
    ) {

        // 1. 生成订单号
        String orderId = UUID.randomUUID().toString().replace("-", "");

        // 2. 处理图片
        String imagePaths = null;
        if (images != null && images.length > 0) {

            StringBuilder sb = new StringBuilder();

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            for (MultipartFile file : images) {
                if (file.isEmpty()) continue;

                String originalName = file.getOriginalFilename();
                if (originalName == null) continue;

                // 文件名安全处理
                String safeName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
                String fileName = UUID.randomUUID() + "_" + safeName;

                File dest = new File(dir, fileName);
                try {
                    file.transferTo(dest);
                } catch (Exception e) {
                    throw new RuntimeException("图片上传失败", e);
                }

                // 数据库存的是前端可访问路径
                sb.append("/upload/order/").append(fileName).append(",");
            }

            if (sb.length() > 0) {
                imagePaths = sb.substring(0, sb.length() - 1);
            }
        }

        // 3. 插入数据库
        repairOrderMapper.insertOrder(
                orderId,
                livingArea,
                building,
                roomNumber,
                repairCategory,
                specificItem,
                problemDescription,
                reporterAccount,
                reporterName,
                reporterPhone,
                LocalDateTime.now(),
                imagePaths
        );
    }

    @Override
    public List<RepairOrder> getOrdersByAccount(String account) {
        return repairOrderMapper.getOrdersByAccount(account);
    }

    @Override
    public List<Map<String, Object>> getPendingOrders(String workType) {
        return repairOrderMapper.selectPendingOrders(workType);
    }

    @Override
    public List<Map<String, Object>> getAcceptedOrders(Integer workerId) {
        return repairOrderMapper.selectAcceptedOrders(workerId);
    }

//    @Override
//    public List<Map<String, Object>> getOrderHistory(Integer workerId) {
//        return repairOrderMapper.selectOrderHistory(workerId,status);
//    }

    // RepairOrderServiceImpl.java 修改getOrderHistory方法
    @Override
    public List<Map<String, Object>> getOrderHistory(Integer workerId, String status) {
        // 先获取所有历史订单
        List<Map<String, Object>> allOrders = repairOrderMapper.selectOrderHistory(workerId);

        // 如果有状态筛选，在Service层过滤
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            return filterOrdersByStatus(allOrders, status);
        }

        return allOrders;
    }

    private List<Map<String, Object>> filterOrdersByStatus(List<Map<String, Object>> orders, String status) {
        return orders.stream()
                .filter(order -> {
                    Integer isCompleted = (Integer) order.get("is_completed");
                    Integer isAccepted = (Integer) order.get("is_accepted");

                    switch (status) {
                        case "completed":
                            return isCompleted != null && isCompleted == 1;
                        case "pending":
                            return isAccepted != null && isAccepted == 1 &&
                                    (isCompleted == null || isCompleted == 0);
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean acceptOrder(String orderId, Integer workerId) {
        try {
            // 检查订单是否已被接
            Map<String, Object> order = repairOrderMapper.selectOrderById(orderId);
            if (order == null) {
                return false;
            }

            Integer isAccepted = (Integer) order.get("is_accepted");
            if (isAccepted != null && isAccepted == 1) {
                return false;
            }

            // 接单
            int result = repairOrderMapper.acceptOrder(orderId, workerId);

            // 如果接单成功，更新工人本月接单数
            if (result > 0) {
                repairOrderMapper.incrementMonthlyOrders(workerId);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean completeOrder(String orderId, Integer workerId) {
        try {
            // 检查订单是否属于该工人且未完成
            Map<String, Object> order = repairOrderMapper.selectOrderById(orderId);
            if (order == null) {
                return false;
            }
            // 应该增加这个检查
            Integer isAccepted = (Integer) order.get("is_accepted");
            if (isAccepted == null || isAccepted != 1) {
                return false; // 订单未被接单，无法完成
            }

            Integer orderWorkerId = (Integer) order.get("worker_id");
            Integer isCompleted = (Integer) order.get("is_completed");

            if (orderWorkerId == null || !orderWorkerId.equals(workerId) ||
                    (isCompleted != null && isCompleted == 1)) {
                return false;
            }

            int result = repairOrderMapper.completeOrder(orderId, workerId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Object> getOrderDetail(String orderId) {
        return repairOrderMapper.selectOrderById(orderId);
    }

    @Override
    public Map<String, Object> getWorkerStats(Integer workerId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 获取今日接单数 - 直接在Service中写SQL逻辑
            Integer todayOrders = repairOrderMapper.selectTodayOrders(workerId);
            stats.put("todayOrders", todayOrders != null ? todayOrders : 0);

            // 获取本月完成数
            Integer monthCompleted = repairOrderMapper.selectMonthCompletedOrders(workerId);
            stats.put("monthCompleted", monthCompleted != null ? monthCompleted : 0);

            // 获取工人基本信息中的本月接单数
            Integer monthlyOrders = repairOrderMapper.selectWorkerMonthlyOrders(workerId);
            stats.put("monthlyOrders", monthlyOrders != null ? monthlyOrders : 0);

        } catch (Exception e) {
            e.printStackTrace();
            // 设置默认值
            stats.put("todayOrders", 0);
            stats.put("monthCompleted", 0);
            stats.put("monthlyOrders", 0);
        }

        return stats;
    }
}