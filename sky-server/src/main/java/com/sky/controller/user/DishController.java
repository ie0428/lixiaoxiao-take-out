package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Tag(name= "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        //构造redis当中的key，规则dish_分类id
        String key = "dish_" + categoryId;
        //获取分布式锁对象
        RLock lock = redissonClient.getLock("DISH_CACHE_LOCK:" + categoryId);

        try {
            lock.lock();
            List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
            if (list != null && !list.isEmpty()) {
                return Result.success(list);
            }

            // 查询数据库
            Dish dish = new Dish();
            dish.setCategoryId(categoryId);
            dish.setStatus(StatusConstant.ENABLE);
            list = dishService.listWithFlavor(dish);

            // 设置缓存时添加过期时间（合并两处set操作）
            redisTemplate.opsForValue().set(key, list, 30, TimeUnit.MINUTES);

            return Result.success(list);
        } finally {
            lock.unlock();
        }
    }
}
