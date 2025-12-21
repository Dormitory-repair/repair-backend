package com.group.repairbackend.controller;

import com.group.repairbackend.model.Result;
import com.group.repairbackend.service.StuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class StuController {
    @Resource
    private StuService stuService;

    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> data) {

        String account = data.get("account");
        String password = data.get("password");
        String checkPassword = data.get("checkPassword");

        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            return Result.error("账号或密码不能为空");
        }

        return stuService.register(account, password, checkPassword);
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> data) {

        String account = data.get("account");
        String password = data.get("password");

        if (StringUtils.isAnyBlank(account, password)) {
            return Result.error("账号或密码不能为空");
        }

        return stuService.login(account, password);
    }

    // 获取学生列表
    @GetMapping("/list")
    public Result getStudentList() {
        List<Map<String, Object>> list = stuService.getAllStudents();
        return Result.success(list);
    }

    @GetMapping("/search")
    public Result searchStudentList(@RequestParam(required = false) String account) {
        List<Map<String, Object>> list = stuService.searchStudents(account);
        return Result.success(list);
    }



    // 修改密码
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Map<String, Object> data) {
        Long id = Long.valueOf(data.get("id").toString());
        String password = (String) data.get("password");
        if(password == null) return Result.error("密码不能为空");

        boolean ok = stuService.updatePassword(id, password);
        return ok ? Result.success("修改成功") : Result.error("修改失败");
    }

    // 删除学生
    @PostMapping("/delete")
    public Result deleteStudent(@RequestBody Map<String, Object> data) {
        Long id = Long.valueOf(data.get("id").toString());
        boolean ok = stuService.deleteStudent(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }


}
