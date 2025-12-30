package com.group.repairbackend.service;

import com.group.repairbackend.model.Result;
import com.group.repairbackend.model.Worker;

import java.util.List;
import java.util.Map;

public interface WorkerService {
    Result addWorker(String name,
                     String workerCode,
                     String password,
                     String checkPassword,
                     String phone,
                     String workType,
                     String hireDate);

    Result updateWorker(String name,
                        String workerCode,
                        String phone,
                        String workType,
                        String hireDate,
                        String password);

    List<Map<String, Object>> getAllWorkers();
    List<Map<String, Object>> searchWorkers(String name);
    boolean deleteWorker(Integer id);
    Result login(String workerCode, String password);
    Worker getWorkerById(Integer id);

    Result getWorkerProfile(Integer id);
    Result updateProfile(Integer id, String phone, String workType);
    Result changePassword(Integer id, String oldPassword, String newPassword, String confirmPassword);}
