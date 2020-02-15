package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author newHeart
 * @Create 2020/2/15 21:39
 */
@Service
public class EsCourseService {

    @Value("${xuecheng.elasticsearch.course.index}")
    private String es_index;

    @Value("${xuecheng.elasticsearch.course.type}")
    private String es_type;

    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String source_field;

    //    客户端
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //    查询
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam) {
//      设置索引
        SearchRequest searchRequest = new SearchRequest(es_index);
//       设置类型
        searchRequest.types(es_type);

//        搜索源生成器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

//        Bool形查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //source源字段过虑
        String[] split = source_field.split(",");
//        搜索源生成器绑定过滤字段
        searchSourceBuilder.fetchSource(split, new String[]{});

//        关键字查询
        if (StringUtils.isNoneEmpty(courseSearchParam.getKeyword())) {
//            匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "teachplan", "description");
//            设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");
//            提升权重Boost值
            multiMatchQueryBuilder.field("name", 10);
            boolQuery.must(multiMatchQueryBuilder);
        }
//            布尔查询
            searchSourceBuilder.query(boolQuery);
//        过滤
        if (StringUtils.isNoneEmpty(courseSearchParam.getMt())){
            boolQuery.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if (StringUtils.isNoneEmpty(courseSearchParam.getSt())){
            boolQuery.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if (StringUtils.isNoneEmpty(courseSearchParam.getGrade())){
            boolQuery.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
//            请求搜索
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = null;
            try {
                searchResponse = restHighLevelClient.search(searchRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            处理结果机
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
//            总数
        long totalHits = hits.getTotalHits();

        List<CoursePub> list = new ArrayList<>();
        try {
            for (SearchHit searchHit : searchHits) {
                CoursePub coursePub = new CoursePub();
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                coursePub.setName((String) sourceAsMap.get("name"));
                if (null != sourceAsMap.get("pic")) {
                    coursePub.setPic((String) sourceAsMap.get("pic"));
                }
                if (null != sourceAsMap.get("price")) {
                    coursePub.setPrice((Double) sourceAsMap.get("price"));
                }
                if (null != sourceAsMap.get("price_old")) {
                    coursePub.setPrice_old((Double) sourceAsMap.get("price_old"));
                }
                list.add(coursePub);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        QueryResult<CoursePub> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(totalHits);
        QueryResponseResult coursePubQueryResponseResult =new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return coursePubQueryResponseResult;
    }
}
