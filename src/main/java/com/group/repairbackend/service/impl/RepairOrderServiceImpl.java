package com.group.repairbackend.service.impl;

import com.group.repairbackend.mapper.RepairOrderMapper;
import com.group.repairbackend.model.RepairOrder;
import com.group.repairbackend.service.RepairOrderService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {
    @Resource
    private RepairOrderMapper repairOrderMapper;

    private static final String UPLOAD_DIR = "D:/IDEAproject/repair_uploads/order/";
    @Override
    public void addOrder(
            String reporterAccount,
            String reporterName,
            String reporterPhone,
            String livingArea,
            String building,
            String roomNumber,
            String repairCategory,
            String specificItem,
            String problemDescription,
            MultipartFile[] images
    ) {

        // 1. 生成订单号
        String orderId = UUID.randomUUID().toString().replace("-", "");

        // 2. 处理图片
        String imagePaths = null;
        if (images != null && images.length > 0) {
            StringBuilder sb = new StringBuilder();
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            for (MultipartFile file : images) {
                if (file.isEmpty()) continue;

                String originalName = file.getOriginalFilename();
                if (originalName == null) continue;

                // 安全处理文件名work_type
                String safeName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
                String fileName = UUID.randomUUID() + "_" + safeName;

                File dest = new File(dir, fileName);
                try {
                    file.transferTo(dest);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("图片上传失败: " + e.getMessage());
                }

                sb.append("/upload/order/").append(fileName).append(",");
            }
            if (sb.length() > 0) {
                imagePaths = sb.substring(0, sb.length() - 1);
            }
        }


        // 3. 插入数据库
        repairOrderMapper.insertOrder(
                orderId,
                livingArea,
                building,
                roomNumber,
                repairCategory,
                specificItem,
                problemDescription,
                reporterAccount,
                reporterName,
                reporterPhone,
                LocalDateTime.now(),
                imagePaths
        );
    }

    public List<RepairOrder> getOrdersByAccount(String account) {
        return repairOrderMapper.getOrdersByAccount(account);
    }
}
