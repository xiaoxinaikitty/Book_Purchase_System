package com.bookmanager.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 推荐配置DTO
 */
@Data
public class RecommendConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer kValue;

    private String similarityType;

    private Double minSimilarity;

    private Double weightReview;

    private Double weightFavorite;

    private Double weightBrowse;

    private Double weightPurchase;
}
