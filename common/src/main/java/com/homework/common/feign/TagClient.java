package com.homework.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import com.homework.common.domain.entity.AjaxResult;

@FeignClient(value = "tag")
public interface TagClient {

    /**
     * 根据实体类型和ID获取标签
     */
    @GetMapping("/entity/tag/byEntity/{entityType}/{entityId}")
    public AjaxResult getTagsByEntity(@PathVariable("entityType") String entityType, @PathVariable("entityId") Long entityId);

    /**
     * 批量添加实体标签
     */
    @PostMapping("/entity/tag/batch")
    public AjaxResult batchAddEntityTags(@RequestBody List<EntityTagDTO> entityTags);

    /**
     * 根据实体类型和ID删除所有标签
     */
    @DeleteMapping("/entity/tag/byEntity/{entityType}/{entityId}")
    public AjaxResult removeTagsByEntity(@PathVariable("entityType") String entityType, @PathVariable("entityId") Long entityId);

    /**
     * 实体标签DTO
     */
    public static class EntityTagDTO {
        private String entityType;
        private Long entityId;
        private Long tagId;

        // 构造方法、getter和setter
        public EntityTagDTO() {
        }

        public EntityTagDTO(String entityType, Long entityId, Long tagId) {
            this.entityType = entityType;
            this.entityId = entityId;
            this.tagId = tagId;
        }

        public String getEntityType() {
            return entityType;
        }

        public void setEntityType(String entityType) {
            this.entityType = entityType;
        }

        public Long getEntityId() {
            return entityId;
        }

        public void setEntityId(Long entityId) {
            this.entityId = entityId;
        }

        public Long getTagId() {
            return tagId;
        }

        public void setTagId(Long tagId) {
            this.tagId = tagId;
        }
    }
}
