package com.group.repairbackend.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Worker {

    private Integer id;

    private String name;        // 姓名(员工ID）

    private String password;    // 密码

    private String phone;       // 电话

    private String workType;    // 工种

    private LocalDate hireDate; // 入职时间

    private Integer monthlyOrders; // 本月接单数

    private LocalDateTime createdTime; // 创建时间
}
