package com.group.repairbackend.service;

import com.group.repairbackend.model.Result;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Result addadmin(String account, String password, String checkPassword);
    Result login(String account, String password);
    List<Map<String, Object>> getAllAdmins();
    List<Map<String, Object>> searchAdmins(String account);
    boolean deleteAdmin(Long id);
    boolean updatePassword(Integer id, String oldPassword, String newPassword);
}
