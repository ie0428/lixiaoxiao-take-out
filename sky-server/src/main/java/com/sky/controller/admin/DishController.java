package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Tag(name = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient; // 新增Redisson客户端注入

        /**
         * 新增菜品（写操作）
         * @param dishDTO
         * @return
         */
    @PostMapping
    @Operation(summary = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品");
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("dishLock");
        try {
            rwLock.writeLock().lock();  // 获取写锁
            dishService.saveWithFlavor(dishDTO);
            cleanCache("dish_*");// 清理所有菜品的缓存数据
            return Result.success();
        } finally {
            rwLock.writeLock().unlock();  // 释放写锁
        }
        
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询");
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 菜品批量删除
     * @param ids
     * @return
     */

    @DeleteMapping
    @Operation(summary = "菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除");
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("dishLock");
        try {
            rwLock.writeLock().lock();
            dishService.deleteBatach(ids);
            cleanCache("dish_*");
            return Result.success();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品");
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */

    @PutMapping
    @Operation(summary = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品");
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("dishLock");
        try {
            rwLock.writeLock().lock();
            dishService.updateWithFlavor(dishDTO);
            cleanCache("dish_*");
            return Result.success();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "菜品起售停售")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("菜品起售停售");
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("dishLock");
        try {
            rwLock.writeLock().lock();
            dishService.startOrStop(status,id);
            cleanCache("dish_*");
            return Result.success();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("dishLock");
        try {
            rwLock.readLock().lock();  // 获取读锁
            List<Dish> list = dishService.list(categoryId);
            return Result.success(list);
        } finally {
            rwLock.readLock().unlock();  // 释放读锁
        }
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache (String pattern){
        Set keys= redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
