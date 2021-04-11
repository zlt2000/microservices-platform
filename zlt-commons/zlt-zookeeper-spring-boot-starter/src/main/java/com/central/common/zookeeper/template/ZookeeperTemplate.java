package com.central.common.zookeeper.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.central.common.constant.CommonConstant;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * zookeeper模板类
 *
 * @author zlt
 * @version 1.0
 * @date 2021/4/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Component
public class ZookeeperTemplate {
    private final CuratorFramework client;

    public ZookeeperTemplate(CuratorFramework client) {
        this.client = client;
    }

    /**
     * 创建空节点，默认持久节点
     *
     * @param path 节点路径
     * @param node 节点名称
     * @return 完整路径
     */
    @SneakyThrows
    public String createNode(String path, String node) {
        return createNode(path, node, CreateMode.PERSISTENT);
    }

    /**
     * 创建带类型的空节点
     * @param path 节点路径
     * @param node 节点名称
     * @param createMode 类型
     * @return 完整路径
     */
    @SneakyThrows
    public String createNode(String path, String node, CreateMode createMode) {
        path = buildPath(path, node);
        client.create()
                .orSetData()
                .creatingParentsIfNeeded()
                .withMode(createMode)
                .forPath(path);
        return path;
    }


    /**
     * 创建节点，默认持久节点
     * @param path 节点路径
     * @param node 节点名称
     * @param value 节点值
     * @return 完整路径
     */
    @SneakyThrows
    public String createNode(String path, String node, String value) {
        return createNode(path, node, value, CreateMode.PERSISTENT);
    }

    /**
     * 创建节点，默认持久节点
     * @param path 节点路径
     * @param node 节点名称
     * @param value 节点值
     * @param createMode 节点类型
     * @return 完整路径
     */
    @SneakyThrows
    public String createNode(String path, String node, String value, CreateMode createMode) {
        Assert.isTrue(StrUtil.isNotEmpty(value), "zookeeper节点值不能为空!");

        path = buildPath(path, node);
        client.create()
                .orSetData()
                .creatingParentsIfNeeded()
                .withMode(createMode)
                .forPath(path, value.getBytes());
        return path;
    }

    /**
     * 获取节点数据
     * @param path 路径
     * @param node 节点名称
     * @return 节点值
     */
    @SneakyThrows
    public String get(String path, String node) {
        path = buildPath(path, node);
        byte[] bytes = client.getData().forPath(path);
        if (bytes.length > 0) {
            return new String(bytes);
        }
        return null;
    }

    /**
     * 更新节点数据
     * @param path 节点路径
     * @param node 节点名称
     * @param value 更新值
     * @return 完整路径
     */
    @SneakyThrows
    public String update(String path, String node, String value) {
        Assert.isTrue(StrUtil.isNotEmpty(value), "zookeeper节点值不能为空!");

        path = buildPath(path, node);
        client.setData().forPath(path, value.getBytes());
        return path;
    }

    /**
     * 删除节点，并且递归删除子节点
     * @param path 路径
     * @param node 节点名称
     */
    @SneakyThrows
    public void delete(String path, String node) {
        path = buildPath(path, node);
        client.delete().quietly().deletingChildrenIfNeeded().forPath(path);
    }

    /**
     * 获取子节点
     * @param path 节点路径
     * @return 子节点集合
     */
    @SneakyThrows
    public List<String> getChildren(String path) {
        if(StrUtil.isEmpty(path)) {
            return null;
        }

        if (!path.startsWith(CommonConstant.PATH_SPLIT)) {
            path = CommonConstant.PATH_SPLIT + path;
        }
        return client.getChildren().forPath(path);
    }

    /**
     * 判断节点是否存在
     * @param path 路径
     * @param node 节点名称
     * @return 结果
     */
    public boolean exists(String path, String node) {
        List<String> list = getChildren(path);
        return CollUtil.isNotEmpty(list) && list.contains(node);
    }

    /**
     * 对一个节点进行监听，监听事件包括指定的路径节点的增、删、改的操作
     * @param path 节点路径
     * @param listener 回调方法
     */
    public void watchNode(String path, NodeCacheListener listener) {
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forNodeCache(listener)
                .build();
        CuratorCache curatorCache = CuratorCache.builder(client, path).build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
    }


    /**
     * 对指定的路径节点的一级子目录进行监听，不对该节点的操作进行监听，对其子目录的节点进行增、删、改的操作监听
     * @param path 节点路径
     * @param listener 回调方法
     */
    public void watchChildren(String path, PathChildrenCacheListener listener) {
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forPathChildrenCache(path, client, listener)
                .build();
        CuratorCache curatorCache = CuratorCache.builder(client, path).build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
    }

    /**
     * 将指定的路径节点作为根节点（祖先节点），对其所有的子节点操作进行监听，呈现树形目录的监听
     * @param path 节点路径
     * @param maxDepth 回调方法
     * @param listener 监听
     */
    public void watchTree(String path, int maxDepth, TreeCacheListener listener) {
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forTreeCache(client, listener)
                .build();
        CuratorCache curatorCache = CuratorCache.builder(client, path).build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
    }

    /**
     * 转换路径
     * @param path 路径
     * @param node 节点名
     */
    private String buildPath(String path, String node) {
        Assert.isTrue(StrUtil.isNotEmpty(path) && StrUtil.isNotEmpty(node)
                , "zookeeper路径或者节点名称不能为空！");

        if (!path.startsWith(CommonConstant.PATH_SPLIT)) {
            path = CommonConstant.PATH_SPLIT + path;
        }

        if (CommonConstant.PATH_SPLIT.equals(path)) {
            return path + node;
        } else {
            return path + CommonConstant.PATH_SPLIT + node;
        }
    }
}
