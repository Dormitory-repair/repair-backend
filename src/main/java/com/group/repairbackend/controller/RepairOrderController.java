package com.group.repairbackend.controller;

import com.group.repairbackend.model.RepairOrder;
import com.group.repairbackend.model.Result;
import com.group.repairbackend.service.RepairOrderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class RepairOrderController {
    @Resource
    private RepairOrderService repairOrderService;

    @PostMapping("/addorder")
    public Result addOrder(
            @RequestParam String reporterAccount,
            @RequestParam String reporterName,
            @RequestParam String reporterPhone,
            @RequestParam String livingArea,
            @RequestParam String building,
            @RequestParam String roomNumber,
            @RequestParam String repairCategory,
            @RequestParam(required = false) String specificItem,
            @RequestParam(required = false) String problemDescription,
            @RequestParam(required = false) MultipartFile[] images
    ) {
        repairOrderService.addOrder(
                reporterAccount,
                reporterName,
                reporterPhone,
                livingArea,
                building,
                roomNumber,
                repairCategory,
                specificItem,
                problemDescription,
                images
        );
        return Result.success();
    }

    @GetMapping("/myorders")
    public Result getMyOrders(@RequestParam String account) {
        if (account == null || account.trim().isEmpty()) {
            return Result.error("账号不能为空");
        }

        try {
            List<RepairOrder> orders = repairOrderService.getOrdersByAccount(account);
            return Result.success(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public Result getOrderDetail(@PathVariable String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return Result.error("订单ID不能为空");
        }

        try {
            Map<String, Object> orderDetail = repairOrderService.getOrderDetail(orderId);
            if (orderDetail == null) {
                return Result.error("订单不存在");
            }
            return Result.success(orderDetail);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单详情失败: " + e.getMessage());
        }
    }
}
