package com.yesido.mongodb.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class QueryParam {

    /** --查询条件-- **/
    private Query query;
    /** --查询条件-参数-- **/
    private List<CriteriaOperation> criterias;
    /** --排序-参数-- **/
    private LinkedHashMap<String, Direction> orderParams;

    public List<CriteriaOperation> getCriterias() {
        if (criterias == null) {
            criterias = new ArrayList<>();
        }
        return criterias;
    }

    public LinkedHashMap<String, Direction> getOrderParams() {
        if (orderParams == null) {
            orderParams = new LinkedHashMap<>();
        }
        return orderParams;
    }

    /**
     * 相等
     */
    public QueryParam addIsQueryParam(String filed, Object value) {
        return addQueryParam(filed, value, Operation.IS);
    }

    /**
     * 不等
     */
    public QueryParam addNeQueryParam(String filed, Object value) {
        return addQueryParam(filed, value, Operation.NE);
    }

    /**
     * 小于
     */
    public QueryParam addLtQueryParam(String filed, Object value) {
        return addQueryParam(filed, value, Operation.LT);
    }

    /**
     * 小于等于
     */
    public QueryParam addLteQueryParam(String filed, Object value) {
        return addQueryParam(filed, value, Operation.LTE);
    }

    /**
     * 大于
     */
    public QueryParam addGtQueryParam(String filed, Object value) {
        return addQueryParam(filed, value, Operation.GT);
    }

    /**
     * 大于等于
     */
    public QueryParam addGteQueryParam(String filed, Object value) {
        return addQueryParam(filed, value, Operation.GTE);
    }

    public QueryParam addQueryParam(String filed, Object value, Operation operation) {
        getCriterias().add(new CriteriaOperation(filed, value, operation));
        return this;
    }

    public QueryParam addOrderParam(String key, Direction value) {
        getOrderParams().put(key, value);
        return this;
    }

    public Query finalQuery() {
        query = new Query();
        setQueryParam();
        setOrderParams();
        return query;
    }

    private void setQueryParam() {
        if (criterias != null && criterias.size() > 0) {
            for (CriteriaOperation criteria : criterias) {
                query.addCriteria(creaeteCriteria(criteria));
            }
        }
    }

    private void setOrderParams() {
        if (orderParams != null && orderParams.size() > 0) {
            List<Order> orders = new ArrayList<>();
            for (Entry<String, Direction> entry : orderParams.entrySet()) {
                orders.add(new Order(entry.getValue(), entry.getKey()));
            }
            if (orders.size() > 0) {
                query.with(Sort.by(orders));
            }
            /*for (Entry<String, Direction> entry : orderParams.entrySet()) {
                query.with(new Sort(entry.getValue(), entry.getKey()));
            }*/
        }
    }

    private Criteria creaeteCriteria(CriteriaOperation criteria) {
        Criteria result = null;
        Operation operation = criteria.getOperation();
        String filed = criteria.getFiled();
        Object value = criteria.getValue();
        switch (operation) {
            case IS:
                result = Criteria.where(filed).is(value);
                break;
            case NE:
                result = Criteria.where(filed).ne(value);
                break;
            case LT:
                result = Criteria.where(filed).lt(value);
                break;
            case LTE:
                result = Criteria.where(filed).lte(value);
                break;
            case GT:
                result = Criteria.where(filed).gt(value);
                break;
            case GTE:
                result = Criteria.where(filed).gte(value);
                break;
            default:
                break;
        }
        return result;
    }

    public static QueryParam newInstance() {
        QueryParam queryParam = new QueryParam();
        return queryParam;
    }

}
