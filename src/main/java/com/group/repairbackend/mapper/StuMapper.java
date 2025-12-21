package com.group.repairbackend.mapper;

import com.group.repairbackend.model.Stu;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StuMapper {

    @Insert("INSERT INTO stu(account, password, created_time) " +
            "VALUES(#{account}, #{password}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Stu stu);

    @Select("SELECT COUNT(*) FROM stu WHERE account = #{account}")
    int existsByAccount(@Param("account") String account);

    @Select("SELECT id, account, password, created_time " +
            "FROM stu WHERE account = #{account}")
    Stu selectByAccount(@Param("account") String account);

    // 查询所有学生，返回所有字段（Map形式）
    @Select("SELECT * FROM stu ORDER BY id ASC")
    List<Map<String, Object>> getAllStudents();

    // 根据账号模糊查询
    @Select("SELECT * FROM stu WHERE account LIKE CONCAT('%', #{account}, '%') ORDER BY id ASC")
    List<Map<String, Object>> searchStudents(@Param("account") String account);

    @Delete("DELETE FROM stu WHERE id=#{id}")
    int deleteStudent(@Param("id") Long id);

    @Update("UPDATE stu SET password=#{password} WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
