package com.group.repairbackend.controller;

import com.group.repairbackend.model.RepairOrder;
import com.group.repairbackend.model.Result;
import com.group.repairbackend.service.RepairOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

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
    public Result getOrdersByAccount(@RequestParam String account) {
        if (account == null || account.isEmpty()) {
            return Result.error("账号不能为空");
        }
        List<RepairOrder> orders = repairOrderService.getOrdersByAccount(account);
        return Result.success(orders);
    }
}
