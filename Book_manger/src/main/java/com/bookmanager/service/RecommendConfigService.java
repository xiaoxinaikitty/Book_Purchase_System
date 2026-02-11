package com.bookmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookmanager.entity.RecommendConfig;

/**
 * 推荐配置服务接口
 */
public interface RecommendConfigService extends IService<RecommendConfig> {

    RecommendConfig getConfig();

    RecommendConfig updateConfig(RecommendConfig config);
}
