package com.group.repairbackend.model;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Admin {
    private Integer id;
    private String account; //账号
    private String password;
    private LocalDateTime createdTime;
}
