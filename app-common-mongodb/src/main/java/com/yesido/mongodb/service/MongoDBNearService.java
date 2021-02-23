package com.yesido.mongodb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * 计算附近的地理位置
 * 
 * @author yesido
 * @date 2019年12月30日 下午2:24:02
 */
@Service
public class MongoDBNearService {
    /** 地球半径=6378137米 **/
    private final static int EARTH_RADIUS = 6378137;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 计算附近的地理位置
     * 
     * @param <T>
     * @param collection mongodb collection 名称
     * @param point 中心坐标距离
     * @param minDistance 最小距离，单位米
     * @param maxDistance 最大距离，单位米
     * @param limit 返回记录数量限制
     * @param clz 实体类
     * @return
     */
    public <T> List<T> geoNear(String collection, float[] point, Double minDistance, double maxDistance,
            Integer limit, Class<T> clz) {
        return geoNear(collection, point, minDistance, maxDistance, limit, null, clz);
    }

    /**
     * 计算附近的地理位置
     * 
     * @param <T>
     * @param collection mongodb collection 名称
     * @param point 中心坐标距离
     * @param minDistance 最小距离，单位米
     * @param maxDistance 最大距离，单位米
     * @param limit 返回记录数量限制
     * @param lastId 上次最后的记录id
     * @param clz 实体类
     * @return
     */
    public <T> List<T> geoNear(String collection, float[] point, double minDistance, double maxDistance,
            Integer limit, String lastId, Class<T> clz) {
        List<AggregationOperation> operations = new ArrayList<AggregationOperation>();

        NearQuery query = NearQuery.near(point[0], point[1]);
        query.spherical(true);
        query.distanceMultiplier(EARTH_RADIUS);
        query.minDistance(minDistance);
        query.maxDistance(maxDistance);
        operations.add(Aggregation.geoNear(query, "distance"));
        if (limit != null && limit > 0) {
            operations.add(Aggregation.limit(limit));
        }
        Query whereQuery = new Query();
        if (lastId != null && lastId.length() > 0) {
            whereQuery.addCriteria(Criteria.where("_id").ne(lastId));
        }
        query.query(whereQuery);
        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<T> results = mongoTemplate.aggregate(aggregation, collection, clz);
        return results.getMappedResults();
    }
}
