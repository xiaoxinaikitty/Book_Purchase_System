package com.bookmanager.controller.admin;

import com.bookmanager.common.Result;
import com.bookmanager.dto.RecommendConfigDTO;
import com.bookmanager.entity.RecommendConfig;
import com.bookmanager.exception.BusinessException;
import com.bookmanager.service.RecommendConfigService;
import com.bookmanager.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 推荐配置控制器（管理员端）
 */
@Api(tags = "推荐配置-管理员端")
@RestController
@RequestMapping("/api/admin/recommend")
public class AdminRecommendConfigController {

    @Autowired
    private RecommendConfigService recommendConfigService;

    @ApiOperation("获取推荐配置")
    @GetMapping("/config")
    public Result<RecommendConfig> getConfig() {
        checkAdmin();
        return Result.success(recommendConfigService.getConfig());
    }

    @ApiOperation("更新推荐配置")
    @PutMapping("/config")
    public Result<RecommendConfig> updateConfig(@RequestBody RecommendConfigDTO dto) {
        checkAdmin();
        validate(dto);
        RecommendConfig config = new RecommendConfig();
        config.setKValue(dto.getKValue());
        config.setSimilarityType(dto.getSimilarityType());
        config.setMinSimilarity(dto.getMinSimilarity());
        config.setWeightReview(dto.getWeightReview());
        config.setWeightFavorite(dto.getWeightFavorite());
        config.setWeightBrowse(dto.getWeightBrowse());
        config.setWeightPurchase(dto.getWeightPurchase());
        return Result.success("更新成功", recommendConfigService.updateConfig(config));
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "无权限访问");
        }
    }

    private void validate(RecommendConfigDTO dto) {
        if (dto == null) {
            throw new BusinessException("参数不能为空");
        }
        if (dto.getKValue() == null || dto.getKValue() < 1 || dto.getKValue() > 50) {
            throw new BusinessException("K值范围为1-50");
        }
        if (dto.getSimilarityType() == null ||
                (!"cosine".equalsIgnoreCase(dto.getSimilarityType())
                        && !"euclidean".equalsIgnoreCase(dto.getSimilarityType()))) {
            throw new BusinessException("相似度类型仅支持 cosine 或 euclidean");
        }
        if (dto.getMinSimilarity() == null || dto.getMinSimilarity() < 0 || dto.getMinSimilarity() > 1) {
            throw new BusinessException("最小相似度范围为0-1");
        }
        if (!isWeightValid(dto.getWeightReview())
                || !isWeightValid(dto.getWeightFavorite())
                || !isWeightValid(dto.getWeightBrowse())
                || !isWeightValid(dto.getWeightPurchase())) {
            throw new BusinessException("权重范围为0-5");
        }
    }

    private boolean isWeightValid(Double value) {
        return value != null && value >= 0 && value <= 5;
    }
}
