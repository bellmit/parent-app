package com.yesido.mongodb.repository;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.yesido.mongodb.base.MongoPagination;
import com.yesido.mongodb.base.QueryParam;
import com.yesido.mongodb.base.UpdateParam;

@org.springframework.stereotype.Repository
public class BaseRepository implements IRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public <T> List<T> list(Class<T> clz) {
        return mongoTemplate.findAll(clz);
    }

    @Override
    public <T> void saveOrUpdate(T t) {
        mongoTemplate.save(t);
    }

    @Override
    public <T> void update(UpdateParam<T> updateParam) {
        Query query = updateParam.finalQuery();
        Update update = updateParam.finalUpdate();
        mongoTemplate.updateMulti(query, update, updateParam.getDataClass());
    }

    @Override
    public <T> T get(String id, Class<T> clz) {
        return mongoTemplate.findById(id, clz);
    }

    @Override
    public <T> void delete(String id, Class<T> clz) {
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), clz);
    }

    @Override
    public <T> void delete(T t) {
        mongoTemplate.remove(t);
    }

    @Override
    public <T> void createCollection(Class<T> clz) {
        if (!mongoTemplate.collectionExists(clz)) {
            mongoTemplate.createCollection(clz);
        }
    }

    @Override
    public <T> void dropCollection(Class<T> clz) {
        if (mongoTemplate.collectionExists(clz)) {
            mongoTemplate.dropCollection(clz);
        }
    }

    @Override
    public Set<String> getCollection() {
        return mongoTemplate.getCollectionNames();

    }

    @Override
    public <T> void findPage(MongoPagination<T> pagination) {
        Query query = pagination.getQueryParam().finalQuery();
        query.skip(pagination.getStart());
        query.limit(pagination.getPageSize());
        long total = mongoTemplate.count(query, pagination.getDataClass());
        pagination.setTotal(total);
        if (total > 0) {
            List<T> data = mongoTemplate.find(query, pagination.getDataClass());
            pagination.setData(data);
        }
    }

    @Override
    public <T> T findOne(Class<T> clz, QueryParam queryParams) {
        return mongoTemplate.findOne(queryParams.finalQuery(), clz);
    }

    @Override
    public <T> List<T> findList(Class<T> clz, QueryParam queryParams) {
        return mongoTemplate.find(queryParams.finalQuery(), clz);
    }

}
