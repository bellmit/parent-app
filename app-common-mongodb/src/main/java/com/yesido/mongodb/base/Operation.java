package com.yesido.mongodb.base;

/**
 * 操作
 * 
 * @author yesido
 * @date 2020年1月7日 上午10:16:14
 */
public enum Operation {

    /** 相等 **/
    IS(1),
    /** 不等 **/
    NE(2),
    /** 小于 **/
    LT(3),
    /** 小于等于 **/
    LTE(4),
    /** 大于 **/
    GT(5),
    /** 大于等于 **/
    GTE(6),
    /** 未知 **/
    UNKNOW(-1);

    private int operation;


    private Operation(int operation) {
        this.operation = operation;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

}
