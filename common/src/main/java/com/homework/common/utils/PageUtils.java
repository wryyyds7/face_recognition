package com.homework.common.utils;

import com.homework.common.exception.UtilException;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;

import static com.homework.common.config.ServerConfig.getRequest;

/**
 * 表格数据处理
 *
 * @author ruoyi
 */
public class PageUtils
{
    /** 当前记录起始索引 */
    private Integer pageNum;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        this.isAsc = isAsc;
    }

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    private String isAsc = "asc";

    /** 分页参数合理化 */
    private Boolean reasonable = true;

    public Boolean getReasonable() {
        return reasonable;
    }

    public void setReasonable(Boolean reasonable) {
        this.reasonable = reasonable;
    }

    /**
     * 封装分页对象
     */
    public static PageUtils getPageUtils()
    {
        PageUtils pageUtils = new PageUtils();
        if(getRequest().getParameter("pageNum") == null || getRequest().getParameter("pageNum") == "0"){
            pageUtils.setPageNum(1);
        }
        else{
            pageUtils.setPageNum(Integer.valueOf(getRequest().getParameter("pageNum")));
        }
        if(getRequest().getParameter("pageSize") == null || getRequest().getParameter("pageSize") == "0"){
            pageUtils.setPageSize(10);
        }
        else {
            pageUtils.setPageSize(Integer.valueOf(getRequest().getParameter("pageSize")));

        }
        if(getRequest().getParameter("reasonable") == null){
            pageUtils.setReasonable(true);
        }
        else{
            pageUtils.setReasonable(Boolean.valueOf(getRequest().getParameter("reasonable")));
        }
        pageUtils.setOrderByColumn(getRequest().getParameter("orderByColumn"));
        pageUtils.setIsAsc(getRequest().getParameter("isAsc"));

        return pageUtils;
    }



    public String getOrderBy()
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            return "";
        }
        return orderByColumn + " " + isAsc;
    }
    public static String escapeOrderBySql(String value)
    {
        // TODO: 可以加上表达式做一遍过滤
        if (StringUtils.length(value) > 500)
        {
            throw new UtilException("参数已超过最大限制，不能进行查询");
        }
        return value;
    }

}
