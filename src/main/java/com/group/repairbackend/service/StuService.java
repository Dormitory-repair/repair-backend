package com.group.repairbackend.service;

import com.group.repairbackend.model.Result;

import java.util.List;
import java.util.Map;

public interface StuService {
    Result register(String account, String password, String checkPassword);
    Result login(String account, String password);
    List<Map<String, Object>> getAllStudents();
    List<Map<String, Object>> searchStudents(String account);
    boolean updatePassword(Long id, String password);
    boolean deleteStudent(Long id);
}
