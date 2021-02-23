package com.yesido.mongodb.repository;

import java.util.List;
import java.util.Set;

import com.yesido.mongodb.base.MongoPagination;
import com.yesido.mongodb.base.QueryParam;
import com.yesido.mongodb.base.UpdateParam;

public interface IRepository {

    public <T> List<T> list(Class<T> clz);

    public <T> void saveOrUpdate(T t);

    public <T> void update(UpdateParam<T> updateParam);

    public <T> T get(String id, Class<T> clz);

    public <T> void delete(String id, Class<T> clz);

    public <T> void delete(T t);

    public <T> void createCollection(Class<T> clz);

    public <T> void dropCollection(Class<T> clz);

    public Set<String> getCollection();

    public <T> void findPage(MongoPagination<T> pagination);

    public <T> T findOne(Class<T> clz, QueryParam queryParams);

    public <T> List<T> findList(Class<T> clz, QueryParam queryParams);

}
