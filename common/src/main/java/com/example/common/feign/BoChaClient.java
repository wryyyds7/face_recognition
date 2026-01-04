package com.example.common.feign;
import com.example.common.aop.Log;
import com.example.common.constant.APIConstant;
import com.example.common.domain.entity.BoChaResult.SearchApiResponse;
import com.example.common.domain.entity.Request.BoChaSearchRequest;
import com.example.common.domain.enums.BusinessType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 这个是第三方API，使用Feign进行调用
 */
@FeignClient(value = "BoChaSearchEngine", url = "https://api.bocha.cn/")
public interface BoChaClient {

    @Log(title = "博查API调用", businessType = BusinessType.OTHER)
    @PostMapping(value = "/v1/web-search", consumes = "application/json")
    public SearchApiResponse list(
            @RequestHeader(value = "Authorization", defaultValue = APIConstant.BOCHA_API_CODE) String authorization,
            @RequestBody BoChaSearchRequest requestBody
    );

    default SearchApiResponse search(String query) {
        BoChaSearchRequest request = new BoChaSearchRequest();
        request.setQuery(query);
        request.setSummary(true);  // 对应原来的summery参数
        request.setExclude("eastmoney.com|zol.com" +
                "|xueqiu.com|cehome.com" +
                "|sogou.com|stockstar.com" +
                "|fdxww.com|hexun.com" +
                "|chinaspv.com" +
                "|douban.com" +
                "|mbaike.lmjx.net" +
                "|news.afrindex.com" +
                "|brand.lmjx.net" +
                "|www.chinaspv.com.cn" +
                "|www.cnpp.cn" +
                "|m.media.beer" +
                "|m.pedaily.cn" +
                "|adquan.com" +
                "|xieniao.com" +
                "|www.trjcn.com" +
                "|www.haibuo.com" +
                "|wiki.mbalib.com" +
                "|www.rqoo.cn" +
                "|hao.77shw.com"
                );

        return list(
                APIConstant.BOCHA_API_CODE,

                request
        );
    }
}
