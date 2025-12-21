package com.group.repairbackend.mapper;

import com.group.repairbackend.model.RepairOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RepairOrderMapper {
    @Insert(
            "INSERT INTO repair_order (" +
                    "order_id, living_area, building, room_number, repair_category, " +
                    "specific_item, problem_description, reporter_account, reporter_name, " +
                    "reporter_phone, repair_time, repair_images" +
                    ") VALUES (" +
                    "#{orderId}, #{livingArea}, #{building}, #{roomNumber}, #{repairCategory}, " +
                    "#{specificItem}, #{problemDescription}, #{reporterAccount}, #{reporterName}, " +
                    "#{reporterPhone}, #{repairTime}, #{repairImages}" +
                    ")"
    )
    void insertOrder(
            @Param("orderId") String orderId,
            @Param("livingArea") String livingArea,
            @Param("building") String building,
            @Param("roomNumber") String roomNumber,
            @Param("repairCategory") String repairCategory,
            @Param("specificItem") String specificItem,
            @Param("problemDescription") String problemDescription,
            @Param("reporterAccount") String reporterAccount,
            @Param("reporterName") String reporterName,
            @Param("reporterPhone") String reporterPhone,
            @Param("repairTime") LocalDateTime repairTime,
            @Param("repairImages") String repairImages
    );

    @Select("SELECT " +
            "reporter_account, reporter_name, reporter_phone, " +
            "living_area, building, room_number, repair_category, specific_item, problem_description, " +
            "repair_images, is_completed, repair_time, completed_time " +
            "FROM repair_order " +
            "WHERE reporter_account = #{account} " +
            "ORDER BY repair_time DESC")
    List<RepairOrder> getOrdersByAccount(@Param("account") String account);


}
