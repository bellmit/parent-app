package com.yesido.mongodb.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesido.mongodb.base.MongoPagination;
import com.yesido.mongodb.base.QueryParam;
import com.yesido.mongodb.base.UpdateParam;
import com.yesido.mongodb.repository.BaseRepository;

@Service
public class MongoDBService {

    @Autowired
    private BaseRepository repository;

    public <T> List<T> list(Class<T> clz) {
        return repository.list(clz);
    }

    public <T> void save(T t) {
        repository.saveOrUpdate(t);
    }

    public <T> void update(UpdateParam<T> updateParam) {
        repository.update(updateParam);
    }

    public <T> T get(String id, Class<T> clz) {
        return repository.get(id, clz);
    }

    public <T> void delete(String id, Class<T> clz) {
        repository.delete(id, clz);;
    }

    public <T> void delete(T t) {
        repository.delete(t);
    }

    public <T> void createCollection(Class<T> clz) {
        repository.createCollection(clz);
    }

    public <T> void dropCollection(Class<T> clz) {
        repository.dropCollection(clz);
    }

    public Set<String> getCollection() {
        Set<String> colls = repository.getCollection();
        return colls;
    }

    public <T> void findPage(MongoPagination<T> pagination) {
        repository.findPage(pagination);
    }

    public <T> T findOne(Class<T> clz, QueryParam queryParams) {
        return repository.findOne(clz, queryParams);
    }

    public <T> List<T> findList(Class<T> clz, QueryParam queryParams) {
        return repository.findList(clz, queryParams);
    }

}
