package com.group.repairbackend.service;

import com.group.repairbackend.model.RepairOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
}
