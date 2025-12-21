package com.group.repairbackend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairOrder {

    private String orderId;              // 订单号（随机生成）

    private String livingArea;            // 生活区

    private String building;              // 宿舍楼

    private String roomNumber;            // 房间号

    private String repairCategory;        // 报修类目

    private String specificItem;          // 具体事项

    private String problemDescription;    // 问题描述

    private String reporterAccount;       // 报修账号（学生账号）

    private String reporterName;          // 报修人姓名

    private String reporterPhone;         // 报修人电话

    private LocalDateTime repairTime;      // 报修时间

    private String repairImages;           // 报修图片路径（逗号分隔）

    private Integer workerId;              // 接单维修工ID

    private Integer isAccepted;            // 是否接单：0-未接单，1-已接单

    private Integer isCompleted;           // 是否完成：0-未完成，1-已完成

    private LocalDateTime acceptedTime;    // 接单时间

    private LocalDateTime completedTime;   // 完成时间

    private LocalDateTime createdTime;     // 创建时间
}
