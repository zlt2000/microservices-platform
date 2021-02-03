package com.central.search.service.impl;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.CommonConstant;
import com.central.search.model.AggItemVo;
import com.central.search.service.IAggregationService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.range.ParsedDateRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聚合分析服务
 *
 * @author zlt
 * @date 2019/5/7
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Service
public class AggregationServiceImpl implements IAggregationService {
    private final RestHighLevelClient client;

    public AggregationServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * 访问统计聚合查询，需要es里面提供以下结构的数据
     * {
     *    ip, //访问ip
     * 	  browser, //浏览器
     * 	  operatingSystem, //操作系统
     * 	  timestamp //日志时间
     * }
     *
     * @param indexName 索引名
     * @param routing es的路由
     * @return 返回结果样例如下
     * {
     *   "currDate_uv": 219,
     *   "currDate_pv": 2730,
     *   "currWeek_pv": 10309,
     *   "currHour_uv": 20,
     *   "browser_datas": [
     *     {
     *       "name": "CHROME",
     *       "value": 7416
     *     },
     *     {
     *       "name": "SAFARI",
     *       "value": 232
     *     },
     *     ...
     *   ],
     *   "browser_legendData": [
     *     "CHROME",
     *     "SAFARI",
     *     ...
     *   ],
     *   "operatingSystem_datas": [
     *     {
     *       "name": "WINDOWS_10",
     *       "value": 6123
     *     },
     *     {
     *       "name": "MAC_OS_X",
     *       "value": 1455
     *     },
     *     ...
     *   ],
     *   "currMonth_pv": 10311,
     *   "statWeek_uv": [
     *     487,
     *     219,
     *     ...
     *   ],
     *   "operatingSystem_legendData": [
     *     "WINDOWS_10",
     *     "MAC_OS_X",
     *     ...
     *   ],
     *   "statWeek_items": [
     *     "2019-05-08",
     *     "2019-05-09",
     *     ...
     *   ],
     *   "statWeek_pv": [
     *     7567,
     *     2730
     *     ...
     *   ]
     * }
     */
    @Override
    public Map<String, Object> requestStatAgg(String indexName, String routing) throws IOException {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        LocalDate localDate = LocalDate.now();
        LocalDateTime curDateTime = LocalDateTime.now();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder).routing(routing);
        searchSourceBuilder.aggregation(
            //聚合查询当天的数据
            AggregationBuilders
                    .dateRange("currDate")
                    .field("timestamp")
                    .addRange(
                            zonedDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0), zonedDateTime.plusDays(1)
                    )
                    .subAggregation(
                            AggregationBuilders
                                    .cardinality("uv")
                                    .field("ip.keyword")
                    )
        ).aggregation(
            //聚合查询7天内的数据
            AggregationBuilders
                    .dateRange("curr24Hour")
                    .field("timestamp")
                    .addRange(zonedDateTime.minusDays(1), zonedDateTime)
                    .subAggregation(
                            //聚合并且按小时分组查询当天内的数据
                            AggregationBuilders
                                    .dateHistogram("statDate")
                                    .field("timestamp")
                                    .fixedInterval(new DateHistogramInterval("90m"))
                                    .format(CommonConstant.DATETIME_FORMAT)
                                    //时区相差8小时
                                    .timeZone(ZoneId.of(CommonConstant.TIME_ZONE_GMT8))
                                    .minDocCount(0L)
                                    .extendedBounds(new LongBounds(
                                            curDateTime.minusDays(1).format(DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT)),
                                            curDateTime.format(DateTimeFormatter.ofPattern(CommonConstant.DATETIME_FORMAT))
                                    ))
                                    .subAggregation(
                                            AggregationBuilders
                                                    .cardinality("uv")
                                                    .field("ip.keyword")
                                    )
                    )
        ).aggregation(
            //聚合查询7天内的数据
            AggregationBuilders
                    .dateRange("currWeek")
                    .field("timestamp")
                    .addRange(zonedDateTime.minusDays(7), zonedDateTime)
                    .subAggregation(
                            //聚合并且按日期分组查询7天内的数据
                            AggregationBuilders
                                    .dateHistogram("statWeek")
                                    .field("timestamp")
                                    .calendarInterval(DateHistogramInterval.DAY)
                                    .format(CommonConstant.DATE_FORMAT)
                                    //时区相差8小时
                                    .timeZone(ZoneId.of(CommonConstant.TIME_ZONE_GMT8))
                                    .minDocCount(0L)
                                    .extendedBounds(new LongBounds(
                                            localDate.minusDays(6).format(DateTimeFormatter.ofPattern(CommonConstant.DATE_FORMAT)),
                                            localDate.format(DateTimeFormatter.ofPattern(CommonConstant.DATE_FORMAT))
                                    ))
                                    .subAggregation(
                                            AggregationBuilders
                                                    .cardinality("uv")
                                                    .field("ip.keyword")
                                    )
                    )
        ).aggregation(
            //聚合查询30天内的数据
            AggregationBuilders
                    .dateRange("currMonth")
                    .field("timestamp")
                    .addRange(zonedDateTime.minusDays(30), zonedDateTime)
        ).aggregation(
            //聚合查询浏览器的数据
            AggregationBuilders
                    .terms("browser")
                    .field("browser.keyword")
        ).aggregation(
            //聚合查询操作系统的数据
            AggregationBuilders
                    .terms("operatingSystem")
                    .field("operatingSystem.keyword")
        ).aggregation(
            //聚合查询1小时内的数据
            AggregationBuilders
                    .dateRange("currHour")
                    .field("timestamp")
                    .addRange(
                            zonedDateTime.minusHours(1), zonedDateTime
                    )
                    .subAggregation(
                            AggregationBuilders
                                    .cardinality("uv")
                                    .field("ip.keyword")
                    )
        ).size(0);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = response.getAggregations();
        Map<String, Object> result = new HashMap<>(15);
        if (aggregations != null) {
            setCurrDate(result, aggregations);
            setCurr24Hour(result, aggregations);
            setCurrWeek(result, aggregations);
            setCurrMonth(result, aggregations);
            setTermsData(result, aggregations, "browser");
            setTermsData(result, aggregations, "operatingSystem");
            setCurrHour(result, aggregations);
        }
        return result;
    }
    /**
     * 赋值当天统计
     */
    private void setCurrDate(Map<String, Object> result, Aggregations aggregations) {
        ParsedDateRange currDate = aggregations.get("currDate");
        Range.Bucket bucket = currDate.getBuckets().get(0);
        ParsedCardinality cardinality = bucket.getAggregations().get("uv");
        result.put("currDate_pv", bucket.getDocCount());
        result.put("currDate_uv", cardinality.getValue());
    }
    /**
     * 赋值周统计
     */
    private void setCurr24Hour(Map<String, Object> result, Aggregations aggregations) {
        ParsedDateRange curr24Hour = aggregations.get("curr24Hour");
        Range.Bucket bucket = curr24Hour.getBuckets().get(0);
        //赋值天趋势统计
        setStatDate(result, bucket.getAggregations());
    }
    /**
     * 赋值周统计
     */
    private void setCurrWeek(Map<String, Object> result, Aggregations aggregations) {
        ParsedDateRange currWeek = aggregations.get("currWeek");
        Range.Bucket bucket = currWeek.getBuckets().get(0);
        result.put("currWeek_pv", bucket.getDocCount());
        //赋值周趋势统计
        setStatWeek(result, bucket.getAggregations());
    }
    /**
     * 赋值月统计
     */
    private void setCurrMonth(Map<String, Object> result, Aggregations aggregations) {
        ParsedDateRange currMonth = aggregations.get("currMonth");
        Range.Bucket bucket = currMonth.getBuckets().get(0);
        result.put("currMonth_pv", bucket.getDocCount());
    }
    /**
     * 赋值单字段统计
     */
    private void setTermsData(Map<String, Object> result, Aggregations aggregations, String key) {
        Terms terms = aggregations.get(key);
        List<String> legendData = new ArrayList<>();
        List<AggItemVo> datas = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            legendData.add((String)bucket.getKey());
            AggItemVo item = new AggItemVo();
            item.setName((String)bucket.getKey());
            item.setValue(bucket.getDocCount());
            datas.add(item);
        }
        result.put(key+"_legendData", legendData);
        result.put(key+"_datas", datas);
    }
    /**
     * 赋值周趋势统计
     */
    private void setStatWeek(Map<String, Object> result, Aggregations aggregations) {
        ParsedDateHistogram agg = aggregations.get("statWeek");
        List<String> items = new ArrayList<>();
        List<Long> uv = new ArrayList<>();
        List<Long> pv = new ArrayList<>();
        ParsedCardinality cardinality;
        for (Histogram.Bucket bucket : agg.getBuckets()) {
            items.add(bucket.getKeyAsString());
            pv.add(bucket.getDocCount());

            cardinality = bucket.getAggregations().get("uv");
            uv.add(cardinality.getValue());
        }
        result.put("statWeek_items", items);
        result.put("statWeek_uv", uv);
        result.put("statWeek_pv", pv);
    }
    /**
     * 赋值小时内统计-当前在线数
     */
    private void setCurrHour(Map<String, Object> result, Aggregations aggregations) {
        ParsedDateRange currDate = aggregations.get("currHour");
        Range.Bucket bucket = currDate.getBuckets().get(0);
        ParsedCardinality cardinality = bucket.getAggregations().get("uv");
        result.put("currHour_uv", cardinality.getValue());
    }
    /**
     * 赋值天趋势统计
     */
    private void setStatDate(Map<String, Object> result, Aggregations aggregations) {
        ParsedDateHistogram agg = aggregations.get("statDate");
        List<String> items = new ArrayList<>();
        List<Long> uv = new ArrayList<>();
        List<Long> pv = new ArrayList<>();
        ParsedCardinality cardinality;
        for (Histogram.Bucket bucket : agg.getBuckets()) {
            items.add(getTimeByDatetimeStr(bucket.getKeyAsString()));
            pv.add(bucket.getDocCount());

            cardinality = bucket.getAggregations().get("uv");
            uv.add(cardinality.getValue());
        }
        result.put("statDate_items", items);
        result.put("statDate_uv", uv);
        result.put("statDate_pv", pv);
    }

    /**
     * 2020-03-10 01:30:00 获取时间值：03-10 01:30
     * @return
     */
    private String getTimeByDatetimeStr(String datetimeStr) {
        if (StrUtil.isNotEmpty(datetimeStr)) {
            return datetimeStr.substring(5, 16);
        }
        return "";
    }
}
