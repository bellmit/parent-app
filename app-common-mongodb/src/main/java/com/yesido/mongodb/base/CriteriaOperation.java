package com.yesido.mongodb.base;

/**
 * 封装操作参数
 * 
 * @author yesido
 * @date 2020年1月7日 上午10:16:23
 */
public class CriteriaOperation {

    /** 字段名称 **/
    private String filed;
    /** 取值 **/
    private Object value;
    /** 操作 **/
    private Operation operation;

    public CriteriaOperation() {}

    public CriteriaOperation(String filed, Object value, Operation operation) {
        this.filed = filed;
        this.value = value;
        this.operation = operation;
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

}
