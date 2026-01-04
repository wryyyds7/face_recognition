package com.homework.common.domain.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 投诉附件实体类
 *
 * @author homework
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintAttachment {
    private Long attachmentId;
    private Long complaintId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createTime;
}
