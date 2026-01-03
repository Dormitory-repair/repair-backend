package com.group.repairbackend.controller;

import com.group.repairbackend.model.Result;
import com.group.repairbackend.model.Worker;
import com.group.repairbackend.service.WorkerService;
import com.group.repairbackend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WorkerController {
    @Resource
    private WorkerService workerService;

    @PostMapping("/addworker")
    public Result addWorker(@RequestBody Map<String, String> data) {

        String name = data.get("name");
        String workerCode = data.get("workerCode");
        String password = data.get("password");
        String checkPassword = data.get("checkPassword");
        String phone = data.get("phone");
        String workType = data.get("workType");
        String hireDate = data.get("hireDate"); // yyyy-MM-dd

        if (StringUtils.isAnyBlank(name, workerCode, password, checkPassword, phone, workType, hireDate)) {
            return Result.error("参数不能为空");
        }

        return workerService.addWorker(
                name, workerCode,password, checkPassword, phone, workType, hireDate
        );
    }

    @PostMapping("/update")
    public Result updateWorker(@RequestBody Map<String, String> data) {

        String name = data.get("name");
        String workerCode = data.get("workerCode");  // 新增
        String phone = data.get("phone");
        String workType = data.get("workType");
        String hireDate = data.get("hireDate");
        String password = data.get("password");

        if (StringUtils.isAnyBlank(name, phone, workType, hireDate, password)) {
            return Result.error("参数不能为空");
        }

        return workerService.updateWorker(
                name,workerCode, phone, workType, hireDate, password
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
        Integer id = Integer.valueOf(data.get("id").toString());
        boolean ok = workerService.deleteWorker(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/worker/login")
    public Result login(@RequestBody Map<String, String> data) {
        String workerCode = data.get("workerCode");
        String password = data.get("password");

        if (workerCode == null || workerCode.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return Result.error("工号和密码不能为空");
        }

        Result result = workerService.login(workerCode, password);
        // 如果登录成功，生成JWT Token
        if (result.getCode() == 1 && result.getData() != null) {
            Worker worker = (Worker) result.getData();

            // 创建JWT Claims
            Claims claims = Jwts.claims();
            claims.put("workerId", worker.getId());
            claims.put("workerCode", worker.getWorkerCode());
            claims.put("name", worker.getName());
            claims.put("phone", worker.getPhone());
            claims.put("workType", worker.getWorkType());
            // 可以添加角色信息，如果需要的话
            // claims.put("role", worker.getRole());

            // 生成JWT Token
            String token = JwtUtil.generateToken(claims);

            // 创建返回数据
            Map<String, Object> returnData = new HashMap<>();
            returnData.put("token", token);
            returnData.put("workerInfo", worker);

            return Result.success("登录成功", returnData);
        }

        // 如果登录失败，直接返回Service的结果
        return result;

    }

    @GetMapping("/profile")
    public Result getProfile(@RequestParam Integer workerId) {
        if (workerId == null) {
            return Result.error("workerId不能为空");
        }
        return workerService.getWorkerProfile(workerId);
    }

    @PostMapping("/profile/update")
    public Result updateProfile(@RequestBody Map<String, Object> data) {
        Integer workerId = (Integer) data.get("workerId");
        String phone = (String) data.get("phone");
        String workType = (String) data.get("workType");

        if (workerId == null) {
            return Result.error("workerId不能为空");
        }

        return workerService.updateProfile(workerId, phone, workType);
    }

    @PostMapping("/change-password")
    public Result changePassword(@RequestBody Map<String, String> data) {
        Integer workerId = Integer.parseInt(data.get("workerId"));
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        String confirmPassword = data.get("confirmPassword");

        if (workerId == null) {
            return Result.error("workerId不能为空");
        }

        return workerService.changePassword(workerId, oldPassword, newPassword, confirmPassword);
    }

    @PostMapping("/logout")
    public Result logout() {

        return Result.success("退出成功");
    }

}
