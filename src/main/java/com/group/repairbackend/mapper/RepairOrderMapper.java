package com.group.repairbackend.mapper;

import com.group.repairbackend.model.RepairOrder;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    // 获取未接订单（按工种筛选）
    @Select({
            "<script>",
            "SELECT * FROM repair_order ",
            "WHERE is_accepted = 0 ",
            "<if test='workType != null and workType != \"\"'>",
            " AND repair_category = #{workType}",
            "</if>",
            " ORDER BY repair_time DESC",
            "</script>"
    })
    List<Map<String, Object>> selectPendingOrders(@Param("workType") String workType);

    // 获取工人已接订单（待完成）
    @Select("SELECT * FROM repair_order " +
            "WHERE worker_id = #{workerId} AND is_accepted = 1 AND is_completed = 0 " +
            "ORDER BY accepted_time DESC")
    List<Map<String, Object>> selectAcceptedOrders(@Param("workerId") Integer workerId);

     //获取工人历史订单
    @Select("SELECT * FROM repair_order " +
            "WHERE worker_id = #{workerId} " +
            "ORDER BY repair_time DESC")
    List<Map<String, Object>> selectOrderHistory(@Param("workerId") Integer workerId);
// 获取工人历史订单（支持状态筛选）
//    @Select({
//            "<script>",
//            "SELECT * FROM repair_order ",
//            "WHERE worker_id = #{workerId} ",
//            "<if test='status != null and status != \"\" and status != \"all\"'>",
//            "  <if test='status == \"completed\"'>",
//            "    AND is_completed = 1",
//            "  </if>",
//            "  <if test='status == \"pending\"'>",
//            "    AND is_accepted = 1 AND is_completed = 0",
//            "  </if>",
//            "  <!-- 可以根据需要添加其他状态 -->",
//            "</if>",
//            "ORDER BY repair_time DESC",
//            "</script>"
//    })
//    List<Map<String, Object>> selectOrderHistory(
//            @Param("workerId") Integer workerId,
//            @Param("status") String status
//    );
    // 接单
    @Update("UPDATE repair_order SET " +
            "worker_id = #{workerId}, " +
            "is_accepted = 1, " +
            "accepted_time = NOW() " +
            "WHERE order_id = #{orderId} AND is_accepted = 0")
    int acceptOrder(@Param("orderId") String orderId, @Param("workerId") Integer workerId);

    // 完成订单
    @Update("UPDATE repair_order SET " +
            "is_completed = 1, " +
            "completed_time = NOW() " +
            "WHERE order_id = #{orderId} AND worker_id = #{workerId} AND is_accepted = 1 AND is_completed = 0")
    int completeOrder(@Param("orderId") String orderId, @Param("workerId") Integer workerId);

    // 获取订单详情
    @Select("SELECT * FROM repair_order WHERE order_id = #{orderId}")
    Map<String, Object> selectOrderById(@Param("orderId") String orderId);

    // 获取今日接单数
    @Select("SELECT COUNT(*) as count FROM repair_order " +
            "WHERE worker_id = #{workerId} " +
            "AND DATE(accepted_time) = CURDATE()")
    Integer selectTodayOrders(@Param("workerId") Integer workerId);

    // 获取本月完成订单数
    @Select("SELECT COUNT(*) as count FROM repair_order " +
            "WHERE worker_id = #{workerId} " +
            "AND is_completed = 1 " +
            "AND MONTH(completed_time) = MONTH(CURDATE()) " +
            "AND YEAR(completed_time) = YEAR(CURDATE())")
    Integer selectMonthCompletedOrders(@Param("workerId") Integer workerId);

    // 获取工人本月接单数（从worker表）
    @Select("SELECT monthly_orders FROM worker WHERE id = #{workerId}")
    Integer selectWorkerMonthlyOrders(@Param("workerId") Integer workerId);

    // 更新工人本月接单数
    @Update("UPDATE worker SET monthly_orders = monthly_orders + 1 WHERE id = #{workerId}")
    int incrementMonthlyOrders(@Param("workerId") Integer workerId);


}
