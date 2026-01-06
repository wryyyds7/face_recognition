package com.homework.common.feign;

import com.homework.common.constant.APIConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 百度地图API Feign客户端
 * 用于调用百度地图的IP定位接口
 */
@Component
@FeignClient(name = "baiduMap", url = "https://api.map.baidu.com")
public interface BaiduMapClient {

    /**
     * 通过IP地址获取地理位置信息
     *
     * @param ak    百度地图API密钥
     * @param ip    要查询的IP地址
     * @param coor  坐标类型，bd09ll表示百度经纬度坐标
     * @return 百度地图返回的地理位置信息
     */
    @GetMapping("/location/ip")
    Map<String, Object> getLocationByIp(@RequestParam("ak") String ak, 
                                        @RequestParam("ip") String ip, 
                                        @RequestParam("coor") String coor);

    /**
     * 默认方法，简化调用
     * 使用默认的AK和coor参数
     *
     * @param ip 要查询的IP地址
     * @return 百度地图返回的地理位置信息
     */
    default Map<String, Object> getLocationByIp(String ip) {
        return getLocationByIp(APIConstant.BAIDU_MAP_AK, ip, "bd09ll");
    }

    /**
     * 通过district_id获取天气信息
     *
     * @param districtId 地区ID（adcode）
     * @param dataType   数据类型，all表示所有数据
     * @param ak         百度地图API密钥
     * @return 百度地图返回的天气信息
     */
    @GetMapping("/weather/v1/")
    Map<String, Object> getWeatherByDistrictId(@RequestParam("district_id") String districtId,
                                               @RequestParam("data_type") String dataType,
                                               @RequestParam("ak") String ak);

    /**
     * 默认方法，简化调用
     * 使用默认的AK和data_type参数
     *
     * @param districtId 地区ID（adcode）
     * @return 百度地图返回的天气信息
     */
    default Map<String, Object> getWeatherByDistrictId(String districtId) {
        return getWeatherByDistrictId(districtId, "all", APIConstant.BAIDU_MAP_AK);
    }
}