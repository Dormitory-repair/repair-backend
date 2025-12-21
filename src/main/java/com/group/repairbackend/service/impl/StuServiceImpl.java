package com.group.repairbackend.service.impl;

import com.group.repairbackend.model.Stu;
import com.group.repairbackend.mapper.StuMapper;
import com.group.repairbackend.model.Result;
import com.group.repairbackend.service.StuService;
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
public class StuServiceImpl implements StuService {
    @Resource
    private StuMapper stuMapper;

    private static final String SALT = "repair_system";

    @Override
    public Result register(String account, String password, String checkPassword) {

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
        int exist = stuMapper.existsByAccount(account);
        if (exist > 0) {
            return Result.error("账号已存在");
        }

        // 密码加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );

        // 组装实体
        Stu stu = new Stu();
        stu.setAccount(account);
        stu.setPassword(encryptedPassword);
        stu.setCreatedTime(LocalDateTime.now());

        // 插入数据库
        int result = stuMapper.insert(stu);

        if (result > 0) {
            return Result.success("学生注册成功");
        } else {
            return Result.error("学生注册失败");
        }
    }

    @Override
    public Result login(String account, String password) {

        // 非空校验
        if (StringUtils.isAnyBlank(account, password)) {
            return Result.error("账号或密码不能为空");
        }

        // 查询学生
        Stu stu = stuMapper.selectByAccount(account);
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

    // 查询学生列表
    public List<Map<String, Object>> getAllStudents() {
        return stuMapper.getAllStudents();
    }

    public List<Map<String, Object>> searchStudents(String account) {
        if(account == null || account.isEmpty()) {
            return stuMapper.getAllStudents();
        }
        return stuMapper.searchStudents(account);
    }

    // 修改密码
    public boolean updatePassword(Long id, String password) {
        // 对密码进行加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );
        return stuMapper.updatePassword(id, encryptedPassword) > 0;
    }

    // 删除学生
    public boolean deleteStudent(Long id) {
        return stuMapper.deleteStudent(id) > 0;
    }




}
