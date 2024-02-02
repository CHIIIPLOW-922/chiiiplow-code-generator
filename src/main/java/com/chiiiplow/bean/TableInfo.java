package com.chiiiplow.bean;



import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 表格信息
 * @author CHIIIPLOW
 * @date 2024/01/25
 */
public class TableInfo {

    /**
     *表名
     */
    private String tableName;

    /**
     * Bean名称
     */
    private String beanName;

    /**
     * Bean参数名称
     */
    private String beanParamName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 字段列表
     */
    private List<FieldInfo> fieldList;

    /**
     * 扩展字段列表
     */
    private List<FieldInfo> extendFieldList;


    /**
     * 唯一索引集合
     */
    private Map<String,List<FieldInfo>> keyIndexMap = new LinkedHashMap<>();

    /**
     * 是否有日期
     */
    private Boolean haveDate = false;

    /**
     * 是否有日期时间
     */
    private Boolean haveDateTime = false;


    /**
     * 是否有大十进制
     */
    private Boolean haveBigDecimal = false;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanParamName() {
        return beanParamName;
    }

    public void setBeanParamName(String beanParamName) {
        this.beanParamName = beanParamName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public List<FieldInfo> getExtendFieldList() {
        return extendFieldList;
    }

    public void setExtendFieldList(List<FieldInfo> extendFieldList) {
        this.extendFieldList = extendFieldList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return keyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        this.keyIndexMap = keyIndexMap;
    }

    public Boolean getHaveDate() {
        return haveDate;
    }

    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
    }

    public Boolean getHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(Boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public Boolean getHaveBigDecimal() {
        return haveBigDecimal;
    }

    public void setHaveBigDecimal(Boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }
}
