package com.l1nker4.lrpc.server;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.time.LocalDateTime;
import java.util.List;

public class Main5 {

    public static void main(String[] args) throws Exception { 

        // Zookeeper集群地址，多个节点地址可以用逗号分隔 

        String zkAddress = "127.0.0.1:2181"; 

        // 重试策略，如果连接不上ZooKeeper集群，会重试三次，重试间隔会递增 

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);

        // 创建Curator Client并启动，启动成功之后，就可以与Zookeeper进行交互了 

        CuratorFramework client = CuratorFrameworkFactory

           .newClient(zkAddress, retryPolicy); 

        client.start();

        // 创建PathChildrenCache实例，监听的是"user"这个节点 

        PathChildrenCache childrenCache = new PathChildrenCache(client, "/user", true);

        // StartMode指定的初始化的模式 

        // NORMAL:普通异步初始化 

        // BUILD_INITIAL_CACHE:同步初始化 

        // POST_INITIALIZED_EVENT:异步初始化，初始化之后会触发事件 

        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE); 

        // childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT); 

        // childrenCache.start(PathChildrenCache.StartMode.NORMAL); 

        List<ChildData> children = childrenCache.getCurrentData();

        System.out.println("获取子节点列表："); 

        // 如果是BUILD_INITIAL_CACHE可以获取这个数据，如果不是就不行 

        children.forEach(childData -> { 

            System.out.println(new String(childData.getData())); 

        });
        childrenCache.getListenable().addListener(((client1, event) -> { 

            System.out.println(LocalDateTime.now() + "  " + event.getType());

            if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {

                System.out.println("PathChildrenCache:子节点初始化成功..."); 

            } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) { 

                String path = event.getData().getPath(); 

                System.out.println("PathChildrenCache添加子节点:" + event.getData().getPath()); 

                System.out.println("PathChildrenCache子节点数据:" + new String(event.getData().getData())); 

            } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) { 

                System.out.println("PathChildrenCache删除子节点:" + event.getData().getPath()); 

            } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) { 

                System.out.println("PathChildrenCache修改子节点路径:" + event.getData().getPath()); 

                System.out.println("PathChildrenCache修改子节点数据:" + new String(event.getData().getData())); 

            } 

        }));
        System.in.read();
    } 

}