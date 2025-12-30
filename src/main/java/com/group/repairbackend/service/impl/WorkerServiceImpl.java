package com.group.repairbackend.service.impl;

import com.group.repairbackend.mapper.WorkerMapper;
import com.group.repairbackend.model.Result;
import com.group.repairbackend.model.Worker;
import com.group.repairbackend.service.WorkerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkerServiceImpl implements WorkerService {
    private static final String SALT = "repair_system";

    @Resource
    private WorkerMapper workerMapper;

    @Override
    public Result addWorker(String name,
                            String workerCode,
                            String password,
                            String checkPassword,
                            String phone,
                            String workType,
                            String hireDate) {

        // 非空校验
        if (StringUtils.isAnyBlank(name, workerCode,password, checkPassword, phone, workType, hireDate)) {
            return Result.error("参数不能为空");
        }

        // 密码一致性校验
        if (!password.equals(checkPassword)) {
            return Result.error("两次输入的密码不一致");
        }

        // 员工ID格式校验（字母数字）
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(workerCode);
        if (!matcher.matches()) {
            return Result.error("员工ID只能包含字母和数字");
        }

        // 检查工号是否已存在（新增）
        int exist = workerMapper.existsByWorkerCode(workerCode);
        if (exist > 0) {
            return Result.error("工号已存在");
        }

        // 密码加密（与学生一致）
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );

        // 组装实体
        Worker worker = new Worker();
        worker.setName(name);
        worker.setWorkerCode(workerCode);  // 设置工号
        worker.setPassword(encryptedPassword);
        worker.setPhone(phone);
        worker.setWorkType(workType);
        worker.setHireDate(LocalDate.parse(hireDate));
        worker.setMonthlyOrders(0); // 初始为 0
        worker.setCreatedTime(LocalDateTime.now());

        // 插入数据库
        int result = workerMapper.insert(worker);

        if (result > 0) {
            return Result.success("维修工添加成功");
        } else {
            return Result.error("维修工添加失败");
        }
    }

    @Override
    public Result updateWorker(String name,
                               String workerCode,
                               String phone,
                               String workType,
                               String hireDate,
                               String password) {

        // 非空校验
        if (StringUtils.isAnyBlank(name, phone, workType, hireDate, password)) {
            return Result.error("参数不能为空");
        }

        // 按工号查找员工是否存在
        Worker existingWorker = workerMapper.selectByWorkerCode(workerCode);
        if (existingWorker == null) {
            return Result.error("员工不存在");
        }

        // 密码加密（与学生、添加工人完全一致）
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );

        // 组装实体
        Worker worker = new Worker();
        worker.setId(existingWorker.getId());  // 设置ID
        worker.setName(name);
        worker.setWorkerCode(workerCode);      // 添加工号
        worker.setPhone(phone);
        worker.setWorkType(workType);
        worker.setHireDate(LocalDate.parse(hireDate));
        worker.setPassword(encryptedPassword);

        int result = workerMapper.updateWorker(worker);

        if (result > 0) {
            return Result.success("维修工信息修改成功");
        } else {
            return Result.error("维修工信息修改失败");
        }
    }


    @Override
    public Result login(String workerCode, String password) {
        if (StringUtils.isAnyBlank(workerCode, password)) {
            return Result.error("用户名和密码不能为空");
        }

        // 密码加密（与添加/更新时一致）
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );

        Worker worker = workerMapper.selectByCodeAndPassword(workerCode, encryptedPassword);
        if (worker == null) {
            return Result.error("工号或密码错误");
        }

        // 移除密码等敏感信息
        worker.setPassword(null);
        return Result.success("登录成功", worker);
    }

    // 添加测试方法
    @PostConstruct
    public void init() {
        System.out.println("====================================");
        System.out.println("密码加密测试:");
        String testPassword = "123456";
        String encrypted = DigestUtils.md5DigestAsHex(
                (SALT + testPassword).getBytes(StandardCharsets.UTF_8)
        );
        System.out.println("SALT: " + SALT);
        System.out.println("明文密码: " + testPassword);
        System.out.println("加密结果: " + encrypted);
        System.out.println("====================================");
    }


    @Override
    public Worker getWorkerById(Integer id) {
        return workerMapper.selectById(id);
    }

    public Result getWorkerProfile(Integer id) {
        try {
            Worker worker = workerMapper.selectById(id);
            if (worker == null) {
                return Result.error("工人不存在");
            }

            // 移除密码等敏感信息
            worker.setPassword(null);
            return Result.success(worker);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取个人信息失败");
        }
    }

    public Result updateProfile(Integer id, String phone, String workType) {
        try {
            if (StringUtils.isAnyBlank(phone, workType)) {
                return Result.error("参数不能为空");
            }

            // 这里需要先在WorkerMapper中添加updateProfile方法
            int result = workerMapper.updateProfile(id, phone, workType);
            if (result > 0) {
                return Result.success("个人信息更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败");
        }
    }

    public Result changePassword(Integer id, String oldPassword, String newPassword, String confirmPassword) {
        try {
            if (StringUtils.isAnyBlank(oldPassword, newPassword, confirmPassword)) {
                return Result.error("参数不能为空");
            }

            if (!newPassword.equals(confirmPassword)) {
                return Result.error("两次输入的新密码不一致");
            }

            // 验证旧密码
            Worker worker = workerMapper.selectById(id);
            if (worker == null) {
                return Result.error("用户不存在");
            }

            String encryptedOldPassword = DigestUtils.md5DigestAsHex(
                    (SALT + oldPassword).getBytes(StandardCharsets.UTF_8)
            );

            if (!worker.getPassword().equals(encryptedOldPassword)) {
                return Result.error("旧密码错误");
            }

            // 更新密码
            String encryptedNewPassword = DigestUtils.md5DigestAsHex(
                    (SALT + newPassword).getBytes(StandardCharsets.UTF_8)
            );

            int result = workerMapper.updatePassword(id, encryptedNewPassword);
            if (result > 0) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改密码失败");
        }
    }

    public List<Map<String, Object>> getAllWorkers() {
        return workerMapper.getAllWorkers();
    }

    public List<Map<String, Object>> searchWorkers(String name) {
        if(name == null || name.isEmpty()) {
            return workerMapper.getAllWorkers();
        }
        return workerMapper.searchWorkers(name);
    }

    public boolean deleteWorker(Integer id) {
        return workerMapper.deleteWorker(id) > 0;
    }

}
