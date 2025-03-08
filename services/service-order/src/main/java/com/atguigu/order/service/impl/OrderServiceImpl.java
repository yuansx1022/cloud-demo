package com.atguigu.order.service.impl;

import com.atguigu.order.bean.Order;
import com.atguigu.order.service.OrderService;
import com.atguigu.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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

    @Override
    public Order createOrder(Long productId, Long userId) {
        Product product = getProductFormRemote(productId);

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

    public Product getProductFormRemote(Long productId){
        //1.获取服到商品服务所在的所有机器ip+port
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance instance = instances.get(0);
        //http://localhost:9002/product/7
        //远程url
        String url = "http://"+instance.getHost()+":"+instance.getPort()+"/product/"+productId;
        log.info("请求路径：{}",url);
        //给远程发送请求
        Product product = restTemplate.getForObject(url, Product.class);
        return product;

    }
}
