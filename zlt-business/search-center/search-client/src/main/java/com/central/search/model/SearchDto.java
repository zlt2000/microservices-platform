package com.central.search.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zlt
 * @date 2019/4/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SearchDto implements Serializable {
    private static final long serialVersionUID = -2084416068307485742L;
    /**
     * 搜索关键字
     */
    private String queryStr;
    /**
     * 当前页数
     */
    private Integer page;
    /**
     * 每页显示数
     */
    private Integer limit;
    /**
     * 排序字段
     */
    private String sortCol;
    /**
     * 排序顺序
     */
    private String sortOrder = "DESC";
    /**
     * 是否显示高亮
     */
    private Boolean isHighlighter;
    /**
     * es的路由
     */
    private String routing;
}
