package com.example.common.domain.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 投诉实体类
 *
 * @author example
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {
    private Long complaintId;
    private Long userId;
    private String complainedType;
    private Long complainedId;
    private String complaintTitle;
    private String complaintContent;
    private String complaintStatus;
    private String handleResult;
    private Long handlerId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
