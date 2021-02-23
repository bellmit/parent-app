package com.yesido.mongodb.base;

import java.util.ArrayList;
import java.util.List;

/**
 * mongodb分页控件
 * 
 * @author yesido
 * @date 2017年6月15日 上午9:45:37
 * @param <T>
 */
public class MongoPagination<T> {
    private final int DEFAULT_SIZE = 20;
    /** --每页大小-- **/
    private int pageSize;
    /** --记录总数-- **/
    private long total;
    /** --当前页-- **/
    private int currentPage;
    /** --数据-- **/
    private List<T> data;
    /** --查询条件-- **/
    private QueryParam queryParam;

    private Class<T> dataClass;

    public MongoPagination(Class<T> dataClass) {
        this.dataClass = dataClass;
        this.pageSize = DEFAULT_SIZE;
        this.currentPage = 1;
    }

    public MongoPagination(Class<T> dataClass, int currentPage) {
        this.dataClass = dataClass;
        this.pageSize = DEFAULT_SIZE;
        this.currentPage = currentPage;
    }

    public MongoPagination(Class<T> dataClass, int currentPage, int pageSize) {
        this.dataClass = dataClass;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<T> getData() {
        if (data == null) {
            data = new ArrayList<>();
        }
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }

    /**
     * 获取起始位置
     * 
     * @return
     */
    public int getStart() {
        return (currentPage - 1) * pageSize;
    }

    /**
     * 获取总页数
     * 
     * @return
     */
    public long getPages() {
        if (total <= 0) {
            return 1;
        }
        if (pageSize == 1) {
            return total;
        }
        if (total % pageSize == 0) {
            return total / pageSize;
        } else {
            return total / pageSize + 1;
        }
    }

}
