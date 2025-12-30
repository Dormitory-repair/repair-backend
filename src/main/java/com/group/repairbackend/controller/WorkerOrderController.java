package com.group.repairbackend.controller;

import com.group.repairbackend.model.Result;
import com.group.repairbackend.service.RepairOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/worker/orders")
public class WorkerOrderController {

    @Resource
    private RepairOrderService repairOrderService;

    @GetMapping("/stats")
    public Result getWorkerStats(HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }
        Map<String, Object> stats = repairOrderService.getWorkerStats(workerId);
        return Result.success(stats);
    }

    // 获取未接订单
    @GetMapping("/pending")
    public Result getPendingOrders(@RequestParam(required = false) String workType,
                                   HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }

        try {
            List<Map<String, Object>> orders = repairOrderService.getPendingOrders(workType);
            return Result.success(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取未接订单失败");
        }
    }

    // 获取已接订单
    @GetMapping("/accepted")
    public Result getAcceptedOrders(HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }

        try {
            List<Map<String, Object>> orders = repairOrderService.getAcceptedOrders(workerId);
            return Result.success(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取已接订单失败");
        }
    }

//     //获取历史订单
//    @GetMapping("/history")
//    public Result getOrderHistory(HttpSession session) {
//        Integer workerId = (Integer) session.getAttribute("workerId");
//        if (workerId == null) {
//            return Result.error("请先登录");
//        }
//
//        try {
//            List<Map<String, Object>> orders = repairOrderService.getOrderHistory(workerId,status);
//            return Result.success(orders);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("获取历史订单失败");
//        }
//    }
    // 获取历史订单（添加状态筛选参数）
    @GetMapping("/history")
    public Result getOrderHistory(@RequestParam(required = false, defaultValue = "all") String status,
                                  HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }

        try {
            List<Map<String, Object>> orders = repairOrderService.getOrderHistory(workerId, status);
            return Result.success(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取历史订单失败");
        }
    }

    // 接单
    @PostMapping("/{orderId}/accept")
    public Result acceptOrder(@PathVariable String orderId, HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }

        try {
            boolean success = repairOrderService.acceptOrder(orderId, workerId);
            if (success) {
                return Result.success("接单成功");
            } else {
                return Result.error("接单失败，订单可能已被他人接单");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("接单失败");
        }
    }

    // 完成订单
    @PostMapping("/{orderId}/complete")
    public Result completeOrder(@PathVariable String orderId, HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }

        try {
            boolean success = repairOrderService.completeOrder(orderId, workerId);
            if (success) {
                return Result.success("订单完成");
            } else {
                return Result.error("完成订单失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("完成订单失败");
        }
    }

    // 获取订单详情
    @GetMapping("/{orderId}")
    public Result getOrderDetail(@PathVariable String orderId) {
        try {
            Map<String, Object> order = repairOrderService.getOrderDetail(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单详情失败");
        }
    }
}
