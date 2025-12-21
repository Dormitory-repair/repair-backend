package com.group.repairbackend.service.impl;

import com.group.repairbackend.mapper.AdminMapper;
import com.group.repairbackend.model.Admin;
import com.group.repairbackend.model.Result;
import com.group.repairbackend.model.Stu;
import com.group.repairbackend.service.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AdminServiceImpl implements AdminService {
    @Resource
    private AdminMapper adminMapper;

    private static final String SALT = "repair_system";

    @Override
    public Result addadmin(String account, String password, String checkPassword) {

        // 非空校验
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            return Result.error("账号或密码不能为空");
        }

        // 密码一致性
        if (!password.equals(checkPassword)) {
            return Result.error("两次输入的密码不一致");
        }

        // 账号格式校验（字母数字）
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(account);
        if (!matcher.matches()) {
            return Result.error("账号只能包含字母和数字");
        }

        //  账号是否已存在
        int exist = adminMapper.existsByAccount(account);
        if (exist > 0) {
            return Result.error("账号已存在");
        }

        // 密码加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );

        // 组装实体
        Admin admin = new Admin();
        admin.setAccount(account);
        admin.setPassword(encryptedPassword);
        admin.setCreatedTime(LocalDateTime.now());

        // 插入数据库
        int result = adminMapper.insert(admin);

        if (result > 0) {
            return Result.success("管理员添加成功");
        } else {
            return Result.error("管理员添加失败");
        }
    }

    @Override
    public Result login(String account, String password) {

        // 非空校验
        if (StringUtils.isAnyBlank(account, password)) {
            return Result.error("账号或密码不能为空");
        }

        // 查询学生
        Stu stu = adminMapper.selectByAccount(account);
        if (stu == null) {
            return Result.error("账号不存在");
        }

        //加密前端传来的密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );

        // 校验密码
        if (!encryptedPassword.equals(stu.getPassword())) {
            return Result.error("密码错误");
        }

        // 登录成功（可返回学生信息，密码置空）
        stu.setPassword(null);
        return Result.success("登录成功", stu);
    }

    public List<Map<String, Object>> getAllAdmins() {
        return adminMapper.getAllAdmins();
    }

    public List<Map<String, Object>> searchAdmins(String account) {
        if(account == null || account.isEmpty()) {
            return adminMapper.getAllAdmins();
        }
        return adminMapper.searchAdmins(account);
    }

    public boolean deleteAdmin(Long id) {
        return adminMapper.deleteAdmin(id) > 0;
    }

    @Override
    public boolean updatePassword(Integer id, String oldPassword, String newPassword) {

        // 1. 查询数据库中原密码
        String dbPassword = adminMapper.getPasswordById(id);
        if (dbPassword == null) return false;

        // 2. 对旧密码加密并校验
        String encryptedOldPassword = DigestUtils.md5DigestAsHex(
                (SALT + oldPassword).getBytes(StandardCharsets.UTF_8)
        );

        if (!dbPassword.equals(encryptedOldPassword)) {
            return false;
        }

        // 3. 新密码加密
        String encryptedNewPassword = DigestUtils.md5DigestAsHex(
                (SALT + newPassword).getBytes(StandardCharsets.UTF_8)
        );

        // 4. 更新密码
        return adminMapper.updatePassword(id, encryptedNewPassword) > 0;
    }
}
