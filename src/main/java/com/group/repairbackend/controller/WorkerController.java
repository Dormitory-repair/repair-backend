package com.group.repairbackend.controller;

import com.group.repairbackend.model.Result;
import com.group.repairbackend.service.WorkerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class WorkerController {
    @Resource
    private WorkerService workerService;

    @PostMapping("/addworker")
    public Result addWorker(@RequestBody Map<String, String> data) {

        String name = data.get("name"); // 员工ID
        String password = data.get("password");
        String checkPassword = data.get("checkPassword");
        String phone = data.get("phone");
        String workType = data.get("workType");
        String hireDate = data.get("hireDate"); // yyyy-MM-dd

        if (StringUtils.isAnyBlank(name, password, checkPassword, phone, workType, hireDate)) {
            return Result.error("参数不能为空");
        }

        return workerService.addWorker(
                name, password, checkPassword, phone, workType, hireDate
        );
    }

    @PostMapping("/update")
    public Result updateWorker(@RequestBody Map<String, String> data) {

        String name = data.get("name"); // 员工ID
        String phone = data.get("phone");
        String workType = data.get("workType");
        String hireDate = data.get("hireDate");
        String password = data.get("password");

        if (StringUtils.isAnyBlank(name, phone, workType, hireDate, password)) {
            return Result.error("参数不能为空");
        }

        return workerService.updateWorker(
                name, phone, workType, hireDate, password
        );
    }

    @GetMapping("/listworker")
    public Result getWorkerList() {
        List<Map<String, Object>> list = workerService.getAllWorkers();
        return Result.success(list);
    }

    @GetMapping("/searchworker")
    public Result searchWorkerList(@RequestParam(required = false) String name) {
        List<Map<String, Object>> list = workerService.searchWorkers(name);
        return Result.success(list);
    }

    @PostMapping("/deleteworker")
    public Result deleteWorker(@RequestBody Map<String, Object> data) {
        Long id = Long.valueOf(data.get("id").toString());
        boolean ok = workerService.deleteWorker(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }
}
