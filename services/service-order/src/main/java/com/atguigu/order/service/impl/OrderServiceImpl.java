package com.atguigu.order.service.impl;

import com.atguigu.order.bean.Order;
import com.atguigu.order.service.OrderService;
import com.atguigu.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired //一定导入spring-cloud-starter-loadBalancer负载均衡场景
    LoadBalancerClient loadBalancerClient;

    @Override
    public Order createOrder(Long productId, Long userId) {
        Product product = getProductFormRemoteWithAnnotation(productId);

        Order order = new Order();
        order.setId(1L);
        //总金额需要远程调用计算
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(product.getNum())));

        order.setUserId(userId);
        order.setNickName("张三");
        order.setAddress("尚硅谷");


        System.out.println("1111");
        //TODD 远程查询商品列表

        order.setProductList(Arrays.asList(product));
        return order;
    }


    //基于注解的负载均衡
    public Product getProductFormRemoteWithAnnotation(Long productId){
        //这里使用选择的实例的主机和端口信息来构建完整的请求URL
        String url = "http://service-product/product/"+productId;
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
        //发送HTTP请求到远程服务，并接收返回的产品信息
    }

    /**
     * 使用负载均衡器从远程服务获取产品信息
     * 该方法通过负载均衡器选择一个产品服务实例，并从该实例中获取产品信息
     *
     * @param productId 产品ID，用于指定要获取的产品
     * @return 返回获取到的产品信息对象
     */
    public Product getProductFormRemoteWithBalancer(Long productId){
        // 使用负载均衡器选择产品服务的一个实例
        ServiceInstance choose = loadBalancerClient.choose("service-product");

        // 构建请求产品的URL
        // 这里使用选择的实例的主机和端口信息来构建完整的请求URL
        String url = "http://"+choose.getHost()+":"+choose.getPort()+"/product/"+productId;
        // 记录请求路径的日志
        log.info("请求路径：{}",url);

        // 发送HTTP请求到远程服务，并接收返回的产品信息
        // 使用RestTemplate的getForObject方法发送GET请求，并将返回的响应体转换为Product对象
        Product product = restTemplate.getForObject(url, Product.class);

        // 返回获取到的产品信息
        return product;
    }


    /**
     * 从远程服务获取商品信息
     * 该方法通过发现服务客户端定位到商品服务，然后通过HTTP请求获取商品详细信息
     *
     * @param productId 商品ID，用于指定需要获取的商品
     * @return Product 返回获取到的商品对象，包含商品详细信息
     */
    public Product getProductFormRemote(Long productId){
      //1.获取服到商品服务所在的所有机器ip+port
      //通过服务发现获取所有提供商品服务的实例，以便选择一个实例进行通信
      List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
      //选择第一个实例进行通信，实际应用中应考虑负载均衡策略
      ServiceInstance instance = instances.get(0);
      //构建请求URL，包含主机地址和端口号，以及商品ID
      //http://localhost:9002/product/7
      //远程url
      String url = "http://"+instance.getHost()+":"+instance.getPort()+"/product/"+productId;
      //记录请求路径，用于调试和监控
      log.info("请求路径：{}",url);
      //给远程发送请求
      //使用RestTemplate发送HTTP GET请求到远程服务，并将响应体反序列化为Product对象
      Product product = restTemplate.getForObject(url, Product.class);
      //返回获取到的商品对象
      return product;

    }
}
