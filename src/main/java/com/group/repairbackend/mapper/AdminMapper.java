package com.group.repairbackend.mapper;

import com.group.repairbackend.model.Admin;
import com.group.repairbackend.model.Stu;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {

    @Insert("INSERT INTO admin(account, password, created_time) " +
            "VALUES(#{account}, #{password}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    @Select("SELECT COUNT(*) FROM admin WHERE account = #{account}")
    int existsByAccount(@Param("account") String account);

    @Select("SELECT id, account, password, created_time " +
            "FROM admin WHERE account = #{account}")
    Stu selectByAccount(@Param("account") String account);

    @Select("SELECT * FROM admin ORDER BY id ASC")
    List<Map<String, Object>> getAllAdmins();

    @Select("SELECT * FROM admin WHERE account LIKE CONCAT('%', #{account}, '%') ORDER BY id ASC")
    List<Map<String, Object>> searchAdmins(@Param("account") String account);

    @Delete("DELETE FROM admin WHERE id=#{id}")
    int deleteAdmin(@Param("id") Long id);

    @Select("SELECT password FROM admin WHERE id = #{id}")
    String getPasswordById(@Param("id") Integer id);

    @Update("UPDATE admin SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
}
