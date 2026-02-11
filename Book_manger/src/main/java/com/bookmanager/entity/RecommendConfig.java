package com.bookmanager.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 推荐算法配置实体
 */
@Data
@TableName("recommend_config")
public class RecommendConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * K值
     */
    private Integer kValue;

    /**
     * 相似度类型 cosine / euclidean
     */
    private String similarityType;

    /**
     * 最小相似度阈值
     */
    private Double minSimilarity;

    /**
     * 评分权重
     */
    private Double weightReview;

    /**
     * 收藏权重
     */
    private Double weightFavorite;

    /**
     * 浏览权重
     */
    private Double weightBrowse;

    /**
     * 购买权重
     */
    private Double weightPurchase;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
