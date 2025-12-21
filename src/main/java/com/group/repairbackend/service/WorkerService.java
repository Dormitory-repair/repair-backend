package com.group.repairbackend.service;

import com.group.repairbackend.model.Result;

import java.util.List;
import java.util.Map;

public interface WorkerService {
    Result addWorker(String name,
                     String password,
                     String checkPassword,
                     String phone,
                     String workType,
                     String hireDate);

    Result updateWorker(String name,
                        String phone,
                        String workType,
                        String hireDate,
                        String password);

    List<Map<String, Object>> getAllWorkers();
    List<Map<String, Object>> searchWorkers(String name);
    boolean deleteWorker(Long id);
}
