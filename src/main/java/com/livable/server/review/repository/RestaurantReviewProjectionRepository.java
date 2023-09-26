package com.livable.server.review.repository;

import com.livable.server.core.util.ImageSeparator;
import com.livable.server.review.dto.RestaurantReviewProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class RestaurantReviewProjectionRepository {

    private static final String FIND_RESTAURANT_REVIEW_QUERY;

    static {
        FIND_RESTAURANT_REVIEW_QUERY = "SELECT " +
                "m.name AS memberName, " +
                "res.id AS restaurantId, " +
                "res.name AS restaurantName, " +
                "r.id AS reviewId, " +
                "r.created_at AS reviewCreatedAt, " +
                "r.description AS reviewDescription, " +
                "rr.taste AS reviewTaste, " +
                "rr.amount AS reviewAmount, " +
                "rr.service AS reviewService, " +
                "rr.speed AS reviewSpeed, " +
                "GROUP_CONCAT(ri.url SEPARATOR :separator) AS images " +
                "FROM review r " +
                "INNER JOIN restaurant_review rr ON r.id = rr.id " +
                "INNER JOIN member m ON r.member_id = m.id " +
                "INNER JOIN restaurant res ON rr.restaurant_id = res.id " +
                "LEFT JOIN review_image ri ON ri.review_id = r.id " +
                "WHERE rr.restaurant_id IN " +
                "(SELECT restaurant_id " +
                "FROM building_restaurant_map " +
                "WHERE building_id = :buildingId) " +
                "GROUP BY r.id, rr.id, m.id, res.id " +
                "ORDER BY r.created_at DESC " +
                "LIMIT :limit " +
                "OFFSET :offset";
    }

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantReviewProjection> findRestaurantReviewProjectionByBuildingId(Long buildingId, Pageable pageable) {

        Query query = entityManager.createNativeQuery(FIND_RESTAURANT_REVIEW_QUERY, "RestaurantReviewListMapping")
                .setParameter("separator", ImageSeparator.IMAGE_SEPARATOR)
                .setParameter("buildingId", buildingId)
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset());

        return (List<RestaurantReviewProjection>) query.getResultList();
    }
}
