package com.central.common.lb.loadbalancer;

import com.central.common.constant.CommonConstant;
import com.central.common.lb.chooser.IRuleChooser;
import com.central.common.lb.utils.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义版本路由选择
 *
 * @author jarvis create by 2022/3/9
 */
@Slf4j
public class VersionLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final static String KEY_DEFAULT = "default";

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSuppliers;

    private String serviceId;

    private IRuleChooser ruleChooser;

    public VersionLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSuppliers, String serviceId, IRuleChooser ruleChooser) {
        this.serviceInstanceListSuppliers = serviceInstanceListSuppliers;
        this.serviceId = serviceId;
        this.ruleChooser = ruleChooser;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        // 从request中获取版本，兼容webflux方式
        RequestData requestData = ((RequestDataContext) (request.getContext())).getClientRequest();
        String version = getVersionFromRequestData(requestData);
        log.debug("选择的版本号为：{}", version);
        return serviceInstanceListSuppliers.getIfAvailable().get(request).next().map(instanceList->{
            return getInstanceResponse(instanceList, version);
        });
    }

    private String getVersionFromRequestData(RequestData requestData){
        Map<String, String> queryMap = QueryUtils.getQueryMap(requestData.getUrl());
        if(MapUtils.isNotEmpty(queryMap)&& queryMap.containsKey(CommonConstant.Z_L_T_VERSION)&& StringUtils.isNotBlank(queryMap.get(CommonConstant.Z_L_T_VERSION))){
            return queryMap.get(CommonConstant.Z_L_T_VERSION);
        }else if(requestData.getHeaders().containsKey(CommonConstant.Z_L_T_VERSION)){
            return requestData.getHeaders().get(CommonConstant.Z_L_T_VERSION).get(0);
        }
        return null;
    }
    /**
     * 1. 先获取到拦截的版本，如果不为空的话就将service列表过滤，寻找metadata中哪个服务是配置的版本，
     * 如果版本为空则不需要进行过滤直接提交给service选择器进行选择
     * 2. 如果没有找到版本对应的实例，则找所有版本为空或者版本号为default的实例
     * 3.将instance列表提交到选择器根据对应的策略返回一个instance
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance>instances, String version){
        List<ServiceInstance> filteredServiceIstanceList = instances;
        if(StringUtils.isNotBlank(version)){
            if(CollectionUtils.isNotEmpty(instances)){
                filteredServiceIstanceList = instances.stream()
                        .filter(item->item.getMetadata().containsKey(CommonConstant.METADATA_VERSION)&&
                                version.equals(item.getMetadata().get(CommonConstant.METADATA_VERSION)))
                        .collect(Collectors.toList());
            }
        }
        // 如果没有找到对应的版本实例时，选择版本号为空的或这版本为default的实例
        if(CollectionUtils.isEmpty(filteredServiceIstanceList)){
            filteredServiceIstanceList = instances.stream()
                    .filter(item->!item.getMetadata().containsKey(CommonConstant.METADATA_VERSION)||
                            StringUtils.isBlank(item.getMetadata().get(CommonConstant.METADATA_VERSION))
                            || "default".equals(item.getMetadata().get(CommonConstant.METADATA_VERSION)))
                    .collect(Collectors.toList());
        }
        // 经过两轮过滤后如果能找到的话就选择，不然返回空
        if(CollectionUtils.isNotEmpty(filteredServiceIstanceList)){
            ServiceInstance serviceInstance = this.ruleChooser.choose(filteredServiceIstanceList);
            if(!Objects.isNull(serviceInstance)){
                log.debug("使用serviceId为：{}服务， 选择version为：{}， 地址：{}:{}，", serviceId, version
                        , serviceInstance.getHost(), serviceInstance.getPort());
                return new DefaultResponse(serviceInstance);
            }
        }
        // 返回空的返回体
        return new EmptyResponse();
    }
}
