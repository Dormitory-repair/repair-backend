package com.group.repairbackend.service.impl;

import com.group.repairbackend.mapper.WorkerMapper;
import com.group.repairbackend.model.Result;
import com.group.repairbackend.model.Worker;
import com.group.repairbackend.service.WorkerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
                            String password,
                            String checkPassword,
                            String phone,
                            String workType,
                            String hireDate) {

        // 非空校验
        if (StringUtils.isAnyBlank(name, password, checkPassword, phone, workType, hireDate)) {
            return Result.error("参数不能为空");
        }

        // 密码一致性校验
        if (!password.equals(checkPassword)) {
            return Result.error("两次输入的密码不一致");
        }

        // 员工ID格式校验（字母数字）
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            return Result.error("员工ID只能包含字母和数字");
        }

        // 员工是否已存在
        int exist = workerMapper.existsByName(name);
        if (exist > 0) {
            return Result.error("员工ID已存在");
        }

        // 密码加密（与学生一致）
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );

        // 组装实体
        Worker worker = new Worker();
        worker.setName(name);
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
                               String phone,
                               String workType,
                               String hireDate,
                               String password) {

        // 非空校验
        if (StringUtils.isAnyBlank(name, phone, workType, hireDate, password)) {
            return Result.error("参数不能为空");
        }

        // 员工是否存在
        int exist = workerMapper.existsByName(name);
        if (exist == 0) {
            return Result.error("员工不存在");
        }

        // 密码加密（与学生、添加工人完全一致）
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );

        // 组装实体
        Worker worker = new Worker();
        worker.setName(name);
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

    public List<Map<String, Object>> getAllWorkers() {
        return workerMapper.getAllWorkers();
    }

    public List<Map<String, Object>> searchWorkers(String name) {
        if(name == null || name.isEmpty()) {
            return workerMapper.getAllWorkers();
        }
        return workerMapper.searchWorkers(name);
    }

    public boolean deleteWorker(Long id) {
        return workerMapper.deleteWorker(id) > 0;
    }

}
