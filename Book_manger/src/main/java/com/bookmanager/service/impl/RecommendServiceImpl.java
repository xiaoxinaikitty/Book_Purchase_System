package com.bookmanager.service.impl;

import com.bookmanager.entity.Book;
import com.bookmanager.mapper.ReviewMapper;
import com.bookmanager.service.BookService;
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
    private ReviewMapper reviewMapper;

    @Override
    public List<Book> personalRecommend(Long userId, int k, int limit) {
        if (userId == null) {
            return Collections.emptyList();
        }
        int kValue = k <= 0 ? 5 : k;
        int limitValue = limit <= 0 ? 10 : limit;

        Map<Long, Map<Long, Integer>> userRatings = buildUserRatingMatrix();
        Map<Long, Integer> targetRatings = userRatings.get(userId);
        if (targetRatings == null || targetRatings.isEmpty()) {
            return hotRecommend(limitValue);
        }

        List<UserSimilarity> similarities = new ArrayList<>();
        for (Map.Entry<Long, Map<Long, Integer>> entry : userRatings.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) {
                continue;
            }
            double sim = cosineSimilarity(targetRatings, entry.getValue());
            if (sim > 0) {
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
            Map<Long, Integer> neighborRatings = userRatings.get(neighbor.userId);
            if (neighborRatings == null) {
                continue;
            }
            for (Map.Entry<Long, Integer> ratingEntry : neighborRatings.entrySet()) {
                Long bookId = ratingEntry.getKey();
                if (targetRatings.containsKey(bookId)) {
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
        List<Book> personal = personalRecommend(userId, 5, limitValue);
        if (personal.size() < limitValue) {
            fillWithHot(personal, limitValue);
        }
        return personal;
    }

    @Override
    public List<Book> newBookRecommend(int limit) {
        return bookService.getNewBooks(limit);
    }

    private Map<Long, Map<Long, Integer>> buildUserRatingMatrix() {
        Map<Long, Map<Long, Integer>> userRatings = new HashMap<>();
        List<Map<String, Object>> rows = reviewMapper.selectUserRatingMatrix();
        for (Map<String, Object> row : rows) {
            Long userId = row.get("user_id") instanceof Number ? ((Number) row.get("user_id")).longValue() : null;
            Long bookId = row.get("book_id") instanceof Number ? ((Number) row.get("book_id")).longValue() : null;
            Integer rating = row.get("rating") instanceof Number ? ((Number) row.get("rating")).intValue() : null;
            if (userId == null || bookId == null || rating == null) {
                continue;
            }
            userRatings.computeIfAbsent(userId, key -> new HashMap<>()).put(bookId, rating);
        }
        return userRatings;
    }

    private double cosineSimilarity(Map<Long, Integer> a, Map<Long, Integer> b) {
        double dot = 0;
        double normA = 0;
        double normB = 0;
        for (Integer rating : a.values()) {
            normA += rating * rating;
        }
        for (Integer rating : b.values()) {
            normB += rating * rating;
        }
        for (Map.Entry<Long, Integer> entry : a.entrySet()) {
            Integer otherRating = b.get(entry.getKey());
            if (otherRating != null) {
                dot += entry.getValue() * otherRating;
            }
        }
        if (dot == 0 || normA == 0 || normB == 0) {
            return 0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
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

    private static class UserSimilarity {
        private final Long userId;
        private final double similarity;

        private UserSimilarity(Long userId, double similarity) {
            this.userId = userId;
            this.similarity = similarity;
        }
    }
}

