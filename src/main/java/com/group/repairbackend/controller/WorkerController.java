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
import javax.servlet.http.HttpSession;
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
    public Result login(@RequestBody Map<String, String> data,HttpSession session) {
        String workerCode = data.get("workerCode");
        String password = data.get("password");

        if (StringUtils.isEmpty(workerCode) || StringUtils.isEmpty(password)) {
            return Result.error("工号和密码不能为空");
        }

        Result result = workerService.login(workerCode, password);
        // 设置session
        if (result.getCode() == 1 && result.getData() != null) {
            Worker worker = (Worker) result.getData();
            Claims claims = Jwts.claims();
            claims.put("workerId", worker.getId());
            claims.put("workerCode", worker.getWorkerCode());
            claims.put("name", worker.getName());
            claims.put("phone", worker.getPhone());
            claims.put("workType", worker.getWorkType());
            // 生成 JWT Token
            String token = JwtUtil.generateToken(claims);
            // 创建返回数据
            Map<String, Object> returnData = new HashMap<>();
            returnData.put("token", token);
            returnData.put("workerInfo", worker);

            return Result.success("登录成功", returnData);
        }


        return result;
    }


    // 获取个人信息
    @GetMapping("/worker/profile")
    public Result getProfile(HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }

        return workerService.getWorkerProfile(workerId);
    }

    // 更新个人信息
    @PostMapping("/worker/profile/update")
    public Result updateProfile(@RequestBody Map<String, String> data, HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }

        String phone = data.get("phone");
        String workType = data.get("workType");

        return workerService.updateProfile(workerId, phone, workType);
    }

    // 修改密码
    @PostMapping("/worker/change-password")
    public Result changePassword(@RequestBody Map<String, String> data, HttpSession session) {
        Integer workerId = (Integer) session.getAttribute("workerId");
        if (workerId == null) {
            return Result.error("请先登录");
        }

        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        String confirmPassword = data.get("confirmPassword");

        return workerService.changePassword(workerId, oldPassword, newPassword, confirmPassword);
    }

    // 退出登录
    @PostMapping("/worker/logout")
    public Result logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功");
    }
}
