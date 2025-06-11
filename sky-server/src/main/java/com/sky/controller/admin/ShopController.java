package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Tag(name = "店铺相关接口")
@Slf4j
public class ShopController {
    public static final String KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    /**
     * 设置店铺的营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @Operation(summary = "设置店铺的营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态:{}", status == 1 ? "开启" : "关闭");
        RLock lock = redissonClient.getLock("SHOP_STATUS_LOCK");
        try {
            lock.lock();
            // 修改获取时的类型转换
            String currentStr =  stringRedisTemplate.opsForValue().get(KEY);
            Integer current = currentStr != null ? Integer.valueOf(currentStr) : null;
            
            if (current != null && current.equals(status)) {
                return Result.success("状态未更新，无需重复操作");
            }
            stringRedisTemplate.opsForValue().set(KEY, String.valueOf(status));
            return Result.success("状态更新成功为："+ (status == 1 ? "营业中" : "已打烊"));// 返回成功信息
        } catch (Exception e) {
            log.error("设置店铺状态失败", e);
            return Result.error("系统异常，请稍后再试"); // 捕获异常并返回错误信息
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    @Operation(summary = "获取店铺的营业状态")
    public Result<Integer> getStatus(){
         // 修改获取时的类型转换
         String statusStr = stringRedisTemplate.opsForValue().get(KEY);
         Integer status = statusStr != null ? Integer.valueOf(statusStr) : null;
        log.info("获取店铺状态:{}",status == 1 ? "开启" : "关闭");
        return Result.success(status);
    }

}
