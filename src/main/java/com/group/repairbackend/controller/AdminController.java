package com.group.repairbackend.controller;

import com.group.repairbackend.model.Result;
import com.group.repairbackend.service.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {
    @Resource
    private AdminService adminService;

    @PostMapping("/addadmin")
    public Result register(@RequestBody Map<String, String> data) {

        String account = data.get("account");
        String password = data.get("password");
        String checkPassword = data.get("checkPassword");

        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            return Result.error("账号或密码不能为空");
        }

        return adminService.addadmin(account, password, checkPassword);
    }

    @PostMapping("/loginadmin")
    public Result login(@RequestBody Map<String, String> data) {

        String account = data.get("account");
        String password = data.get("password");

        if (StringUtils.isAnyBlank(account, password)) {
            return Result.error("账号或密码不能为空");
        }

        return adminService.login(account, password);
    }

    @GetMapping("/listadmin")
    public Result getAdminList() {
        List<Map<String, Object>> list = adminService.getAllAdmins();
        return Result.success(list);
    }

    @GetMapping("/searchadmin")
    public Result searchAdminList(@RequestParam(required = false) String account) {
        List<Map<String, Object>> list = adminService.searchAdmins(account);
        return Result.success(list);
    }

    @PostMapping("/deleteadmin")
    public Result deleteAdmin(@RequestBody Map<String, Object> data) {
        Long id = Long.valueOf(data.get("id").toString());
        boolean ok = adminService.deleteAdmin(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PostMapping("/updateAdminPassword")
    public Result updatePassword(@RequestBody Map<String, Object> data) {
        Integer id = Integer.valueOf(data.get("id").toString());
        String oldPassword = (String) data.get("oldPassword");
        String newPassword = (String) data.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return Result.error("密码不能为空");
        }

        boolean ok = adminService.updatePassword(id, oldPassword, newPassword);
        return ok ? Result.success("修改成功") : Result.error("旧密码错误");
    }

}
