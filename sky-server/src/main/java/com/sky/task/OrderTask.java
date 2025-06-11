package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedissonClient redissonClient;

    //处理支付超时订单

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {
        log.info("处理支付超时订单:{}", new Date());
        RLock lock = redissonClient.getLock("ORDER_TASK_LOCK");
        try {
            if (lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                LocalDateTime time = LocalDateTime.now().minusMinutes(15); // 修正时间计算
                List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT, time);
                
                if (ordersList != null && !ordersList.isEmpty()) {
                    ordersList.forEach(orders -> {
                        orders.setStatus(Orders.CANCELLED);
                        orders.setCancelReason("订单超时，自动取消");
                        orders.setCancelTime(LocalDateTime.now());
                        orderMapper.update(orders);
                    });
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("锁获取被中断", e);
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        
    }

    //处理派送中的订单状态
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("处理派送中的订单状态:{}", new Date());
        LocalDateTime time = LocalDateTime.now().minusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
