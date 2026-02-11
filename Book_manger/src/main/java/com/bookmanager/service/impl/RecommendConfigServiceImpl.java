package com.bookmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmanager.entity.RecommendConfig;
import com.bookmanager.mapper.RecommendConfigMapper;
import com.bookmanager.service.RecommendConfigService;
import org.springframework.stereotype.Service;

/**
 * 推荐配置服务实现类
 */
@Service
public class RecommendConfigServiceImpl extends ServiceImpl<RecommendConfigMapper, RecommendConfig>
        implements RecommendConfigService {

    @Override
    public RecommendConfig getConfig() {
        RecommendConfig config = this.lambdaQuery()
                .orderByAsc(RecommendConfig::getId)
                .last("LIMIT 1")
                .one();
        if (config == null) {
            config = defaultConfig();
            this.save(config);
        }
        return config;
    }

    @Override
    public RecommendConfig updateConfig(RecommendConfig config) {
        RecommendConfig current = getConfig();
        current.setKValue(config.getKValue());
        current.setSimilarityType(config.getSimilarityType());
        current.setMinSimilarity(config.getMinSimilarity());
        current.setWeightReview(config.getWeightReview());
        current.setWeightFavorite(config.getWeightFavorite());
        current.setWeightBrowse(config.getWeightBrowse());
        current.setWeightPurchase(config.getWeightPurchase());
        this.updateById(current);
        return current;
    }

    private RecommendConfig defaultConfig() {
        RecommendConfig config = new RecommendConfig();
        config.setKValue(5);
        config.setSimilarityType("cosine");
        config.setMinSimilarity(0.1);
        config.setWeightReview(1.0);
        config.setWeightFavorite(0.7);
        config.setWeightBrowse(0.3);
        config.setWeightPurchase(1.2);
        return config;
    }
}
