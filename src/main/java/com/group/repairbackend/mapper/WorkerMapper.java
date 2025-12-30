package com.group.repairbackend.mapper;

import com.group.repairbackend.model.Worker;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkerMapper {
    @Insert("INSERT INTO worker " +
            "(name, worker_code, password, phone, work_type, hire_date, monthly_orders, created_time) " +
            "VALUES " +
            "(#{name}, #{workerCode},#{password}, #{phone}, #{workType}, #{hireDate}, #{monthlyOrders}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Worker worker);

//    @Select("SELECT COUNT(*) FROM worker WHERE name = #{name}")
//    int existsByName(@Param("name") String name);

    // 检查工号是否已存在（新增）
    @Select("SELECT COUNT(*) FROM worker WHERE worker_code = #{workerCode}")
    int existsByWorkerCode(@Param("workerCode") String workerCode);

    @Update("UPDATE worker SET " +
            "name = #{name}, " +
            "worker_code = #{workerCode}, " +
            "phone = #{phone}, " +
            "work_type = #{workType}, " +
            "hire_date = #{hireDate}, " +
            "password = #{password} " +
            "WHERE id = #{id}")
    int updateWorker(Worker worker);

    // 查询所有工人
    @Select("SELECT * FROM worker ORDER BY id ASC")
    List<Map<String, Object>> getAllWorkers();

    // 根据姓名模糊查询
    @Select("SELECT * FROM worker WHERE name LIKE CONCAT('%', #{name}, '%') ORDER BY id ASC")
    List<Map<String, Object>> searchWorkers(@Param("name") String name);

    @Delete("DELETE FROM worker WHERE id=#{id}")
    int deleteWorker(@Param("id") Integer id);

    @Select("SELECT * FROM worker WHERE worker_code = #{workerCode} AND password = #{password}")
    Worker selectByCodeAndPassword(@Param("workerCode") String workerCode, @Param("password") String password);

    @Select("SELECT * FROM worker WHERE name = #{name}")
    Worker selectByName(@Param("name") String name);

    // 按工号查询（新增）
    @Select("SELECT * FROM worker WHERE worker_code = #{workerCode}")
    Worker selectByWorkerCode(@Param("workerCode") String workerCode);


    @Select("SELECT * FROM worker WHERE id = #{id}")
    Worker selectById(@Param("id") Integer id);

    @Update("UPDATE worker SET phone = #{phone}, work_type = #{workType} WHERE id = #{id}")
    int updateProfile(@Param("id") Integer id,
                      @Param("phone") String phone,
                      @Param("workType") String workType);

    @Update("UPDATE worker SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

}
