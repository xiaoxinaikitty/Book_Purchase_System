package com.bookmanager.service.impl;

import com.bookmanager.entity.Book;
import com.bookmanager.entity.RecommendConfig;
import com.bookmanager.mapper.RecommendMapper;
import com.bookmanager.service.BookService;
import com.bookmanager.service.RecommendConfigService;
import com.bookmanager.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类（KNN算法）
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private BookService bookService;

    @Autowired
    private RecommendMapper recommendMapper;

    @Autowired
    private RecommendConfigService recommendConfigService;

    @Override
    public List<Book> personalRecommend(Long userId, int k, int limit) {
        if (userId == null) {
            return Collections.emptyList();
        }
        RecommendConfig config = recommendConfigService.getConfig();
        int kValue = k > 0 ? k : (config.getKValue() == null ? 5 : config.getKValue());
        int limitValue = limit <= 0 ? 10 : limit;

        Map<Long, Map<Long, Double>> userScores = buildUserScoreMatrix(config);
        Map<Long, Double> targetScores = userScores.get(userId);
        if (targetScores == null || targetScores.isEmpty()) {
            return hotRecommend(limitValue);
        }

        List<UserSimilarity> similarities = new ArrayList<>();
        for (Map.Entry<Long, Map<Long, Double>> entry : userScores.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) {
                continue;
            }
            double sim = calcSimilarity(config.getSimilarityType(), targetScores, entry.getValue());
            if (sim >= getMinSimilarity(config)) {
                similarities.add(new UserSimilarity(otherUserId, sim));
            }
        }

        similarities.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        List<UserSimilarity> neighbors = similarities.stream()
                .limit(kValue)
                .collect(Collectors.toList());

        if (neighbors.isEmpty()) {
            return hotRecommend(limitValue);
        }

        Map<Long, Double> scoreMap = new HashMap<>();
        for (UserSimilarity neighbor : neighbors) {
            Map<Long, Double> neighborRatings = userScores.get(neighbor.userId);
            if (neighborRatings == null) {
                continue;
            }
            for (Map.Entry<Long, Double> ratingEntry : neighborRatings.entrySet()) {
                Long bookId = ratingEntry.getKey();
                if (targetScores.containsKey(bookId)) {
                    continue;
                }
                scoreMap.merge(bookId, neighbor.similarity * ratingEntry.getValue(), Double::sum);
            }
        }

        if (scoreMap.isEmpty()) {
            return hotRecommend(limitValue);
        }

        List<Long> rankedBookIds = scoreMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Book> recommended = fetchBooksInOrder(rankedBookIds, limitValue);
        if (recommended.size() < limitValue) {
            fillWithHot(recommended, limitValue);
        }
        return recommended;
    }

    @Override
    public List<Book> hotRecommend(int limit) {
        return bookService.getHotBooks(limit);
    }

    @Override
    public List<Book> similarBookRecommend(Long bookId, int limit) {
        if (bookId == null) {
            return Collections.emptyList();
        }
        int limitValue = limit <= 0 ? 6 : limit;
        Book current = bookService.getById(bookId);
        if (current == null || current.getCategoryId() == null) {
            return hotRecommend(limitValue);
        }

        List<Book> list = bookService.lambdaQuery()
                .eq(Book::getStatus, 1)
                .eq(Book::getCategoryId, current.getCategoryId())
                .ne(Book::getId, bookId)
                .orderByDesc(Book::getRating)
                .orderByDesc(Book::getSales)
                .last("LIMIT " + limitValue)
                .list();
        if (list == null || list.isEmpty()) {
            return hotRecommend(limitValue);
        }
        return list;
    }

    @Override
    public List<Book> guessYouLike(Long userId, int limit) {
        int limitValue = limit <= 0 ? 10 : limit;
        if (userId == null) {
            return hotRecommend(limitValue);
        }
        List<Book> personal = personalRecommend(userId, 0, limitValue);
        if (personal.size() < limitValue) {
            fillWithHot(personal, limitValue);
        }
        return personal;
    }

    @Override
    public List<Book> newBookRecommend(int limit) {
        return bookService.getNewBooks(limit);
    }

    private Map<Long, Map<Long, Double>> buildUserScoreMatrix(RecommendConfig config) {
        Map<Long, Map<Long, Double>> userScores = new HashMap<>();
        double weightReview = getWeight(config.getWeightReview(), 1.0);
        double weightFavorite = getWeight(config.getWeightFavorite(), 0.7);
        double weightBrowse = getWeight(config.getWeightBrowse(), 0.3);
        double weightPurchase = getWeight(config.getWeightPurchase(), 1.2);

        List<Map<String, Object>> reviewRows = recommendMapper.selectReviewScores();
        for (Map<String, Object> row : reviewRows) {
            Long userId = getLong(row.get("user_id"));
            Long bookId = getLong(row.get("book_id"));
            Double rating = getDouble(row.get("rating"));
            if (userId == null || bookId == null || rating == null) {
                continue;
            }
            addScore(userScores, userId, bookId, rating * weightReview);
        }

        List<Map<String, Object>> favoriteRows = recommendMapper.selectFavorites();
        for (Map<String, Object> row : favoriteRows) {
            Long userId = getLong(row.get("user_id"));
            Long bookId = getLong(row.get("book_id"));
            if (userId == null || bookId == null) {
                continue;
            }
            addScore(userScores, userId, bookId, 5.0 * weightFavorite);
        }

        List<Map<String, Object>> browseRows = recommendMapper.selectBrowseHistory();
        for (Map<String, Object> row : browseRows) {
            Long userId = getLong(row.get("user_id"));
            Long bookId = getLong(row.get("book_id"));
            Double count = getDouble(row.get("browse_count"));
            if (userId == null || bookId == null || count == null) {
                continue;
            }
            double normalized = Math.min(count, 5.0);
            addScore(userScores, userId, bookId, normalized * weightBrowse);
        }

        List<Map<String, Object>> purchaseRows = recommendMapper.selectPurchases();
        for (Map<String, Object> row : purchaseRows) {
            Long userId = getLong(row.get("user_id"));
            Long bookId = getLong(row.get("book_id"));
            if (userId == null || bookId == null) {
                continue;
            }
            addScore(userScores, userId, bookId, 5.0 * weightPurchase);
        }

        return userScores;
    }

    private void addScore(Map<Long, Map<Long, Double>> userScores, Long userId, Long bookId, Double score) {
        Map<Long, Double> map = userScores.computeIfAbsent(userId, key -> new HashMap<>());
        map.merge(bookId, score, Double::sum);
    }

    private double calcSimilarity(String similarityType, Map<Long, Double> a, Map<Long, Double> b) {
        if ("euclidean".equalsIgnoreCase(similarityType)) {
            return euclideanSimilarity(a, b);
        }
        return cosineSimilarity(a, b);
    }

    private double cosineSimilarity(Map<Long, Double> a, Map<Long, Double> b) {
        double dot = 0;
        double normA = 0;
        double normB = 0;
        for (Double rating : a.values()) {
            normA += rating * rating;
        }
        for (Double rating : b.values()) {
            normB += rating * rating;
        }
        for (Map.Entry<Long, Double> entry : a.entrySet()) {
            Double otherRating = b.get(entry.getKey());
            if (otherRating != null) {
                dot += entry.getValue() * otherRating;
            }
        }
        if (dot == 0 || normA == 0 || normB == 0) {
            return 0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private double euclideanSimilarity(Map<Long, Double> a, Map<Long, Double> b) {
        Set<Long> keys = new HashSet<>(a.keySet());
        keys.addAll(b.keySet());
        double sum = 0;
        for (Long key : keys) {
            double va = a.getOrDefault(key, 0.0);
            double vb = b.getOrDefault(key, 0.0);
            double diff = va - vb;
            sum += diff * diff;
        }
        double distance = Math.sqrt(sum);
        return 1.0 / (1.0 + distance);
    }

    private List<Book> fetchBooksInOrder(List<Long> bookIds, int limit) {
        if (bookIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> limitedIds = bookIds.stream().limit(limit).collect(Collectors.toList());
        List<Book> books = bookService.lambdaQuery()
                .eq(Book::getStatus, 1)
                .in(Book::getId, limitedIds)
                .list();
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, book -> book));
        List<Book> ordered = new ArrayList<>();
        for (Long id : limitedIds) {
            Book book = bookMap.get(id);
            if (book != null) {
                ordered.add(book);
            }
        }
        return ordered;
    }

    private void fillWithHot(List<Book> target, int limit) {
        if (target.size() >= limit) {
            return;
        }
        Set<Long> existing = target.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
        List<Book> hotList = hotRecommend(limit);
        for (Book book : hotList) {
            if (book == null || existing.contains(book.getId())) {
                continue;
            }
            target.add(book);
            existing.add(book.getId());
            if (target.size() >= limit) {
                break;
            }
        }
    }

    private Double getDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return value == null ? null : Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            return null;
        }
    }

    private Long getLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return value == null ? null : Long.parseLong(String.valueOf(value));
        } catch (Exception e) {
            return null;
        }
    }

    private double getWeight(Double value, double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return Math.max(0, value);
    }

    private double getMinSimilarity(RecommendConfig config) {
        if (config == null || config.getMinSimilarity() == null) {
            return 0.0;
        }
        return Math.max(0, Math.min(1, config.getMinSimilarity()));
    }

    private static class UserSimilarity {
        private final Long userId;
        private final double similarity;

        private UserSimilarity(Long userId, double similarity) {
            this.userId = userId;
            this.similarity = similarity;
        }
    }
}

