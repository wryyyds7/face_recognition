package com.homework.common.feign;

import com.homework.common.domain.dto.LearningPathRecommendationRequestDTO;
import com.homework.common.domain.dto.LearningPathRecommendationResponseDTO;
import com.homework.common.domain.dto.LearningResourceRecommendationRequestDTO;
import com.homework.common.domain.dto.SkillMasteryPredictionRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 学习路径推荐服务Feign客户端
 * 用于调用Python学习路径推荐服务的接口
 */
@FeignClient(value = "learning-path-recommendation", url = "http://localhost:5001")
public interface LearningPathRecommendationClient {

    /**
     * 推荐学习路径接口
     *
     * @param request 请求参数，包含userId、currentSkills、targetSkills、timeframe和learningStyle
     * @return 学习路径推荐结果
     */
    @PostMapping(value = "/api/recommend/learning-path", consumes = "application/json")
    LearningPathRecommendationResponseDTO recommendLearningPath(@RequestBody LearningPathRecommendationRequestDTO request);

    /**
     * 技能掌握度预测接口
     *
     * @param request 请求参数，包含userId和skillId
     * @return 技能掌握度预测结果
     */
    @PostMapping(value = "/api/predict/skill-mastery", consumes = "application/json")
    Object predictSkillMastery(@RequestBody SkillMasteryPredictionRequestDTO request);

    /**
     * 学习资源推荐接口
     *
     * @param request 请求参数，包含userId、skillId和limit
     * @return 学习资源推荐结果
     */
    @PostMapping(value = "/api/recommend/resources", consumes = "application/json")
    Object recommendResources(@RequestBody LearningResourceRecommendationRequestDTO request);
}
