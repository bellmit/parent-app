package com.yesido.mongodb.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UpdateParam<T> {

    private Update update;

    /** --查询条件-- **/
    private QueryParam queryParam;

    /** --更新字段-参数-- **/
    private Map<String, Object> filedParams;

    private Class<T> dataClass;

    public UpdateParam(Class<T> dataClass) {
        super();
        this.dataClass = dataClass;
    }

    public QueryParam getQueryParam() {
        if (this.queryParam == null) {
            this.queryParam = QueryParam.newInstance();
        }
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }

    public Map<String, Object> getFiledParams() {
        if (this.filedParams == null) {
            this.filedParams = new HashMap<>();
        }
        return filedParams;
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

    public UpdateParam<T> addFiledParam(String key, Object value) {
        getFiledParams().put(key, value);
        return this;
    }

    public UpdateParam<T> addFiledParam(Map<String, Object> filedParams) {
        getFiledParams().putAll(filedParams);
        return this;
    }

    public Query finalQuery() {
        return getQueryParam().finalQuery();
    }

    public Update finalUpdate() {
        this.update = new Update();
        if (this.filedParams != null && this.filedParams.size() > 0) {
            for (Entry<String, Object> entry : this.filedParams.entrySet()) {
                this.update.set(entry.getKey(), entry.getValue());
            }
        }
        return this.update;
    }
}
