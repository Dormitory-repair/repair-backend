package com.group.repairbackend.controller;

import com.group.repairbackend.model.Admin;
import com.group.repairbackend.model.Result;
import com.group.repairbackend.service.AdminService;
import com.group.repairbackend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
    public Result loginAdmin(@RequestBody Map<String, String> data) {

        String account = data.get("account");
        String password = data.get("password");

        if (StringUtils.isAnyBlank(account, password)) {
            return Result.error("账号或密码不能为空");
        }

        Result loginResult = adminService.login(account, password);

        if (loginResult.getCode() != 1) {
            return loginResult;
        }

        Admin admin = (Admin) loginResult.getData();

        Claims claims = Jwts.claims();
        claims.put("id", admin.getId());
        claims.put("account", admin.getAccount());

        String token = JwtUtil.generateToken(claims);

        Result result = Result.success("管理员登录成功");
        result.setData(token);
        return result;
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
