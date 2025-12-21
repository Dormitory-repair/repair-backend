package com.group.repairbackend.mapper;

import com.group.repairbackend.model.Worker;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkerMapper {
    @Insert("INSERT INTO worker " +
            "(name, password, phone, work_type, hire_date, monthly_orders, created_time) " +
            "VALUES " +
            "(#{name}, #{password}, #{phone}, #{workType}, #{hireDate}, #{monthlyOrders}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Worker worker);

    @Select("SELECT COUNT(*) FROM worker WHERE name = #{name}")
    int existsByName(@Param("name") String name);

    @Update("UPDATE worker SET " +
            "phone = #{phone}, " +
            "work_type = #{workType}, " +
            "hire_date = #{hireDate}, " +
            "password = #{password} " +
            "WHERE name = #{name}")
    int updateWorker(Worker worker);

    // 查询所有工人
    @Select("SELECT * FROM worker ORDER BY id ASC")
    List<Map<String, Object>> getAllWorkers();

    // 根据账号模糊查询
    @Select("SELECT * FROM worker WHERE name LIKE CONCAT('%', #{name}, '%') ORDER BY id ASC")
    List<Map<String, Object>> searchWorkers(@Param("name") String name);

    @Delete("DELETE FROM worker WHERE id=#{id}")
    int deleteWorker(@Param("id") Long id);

}
