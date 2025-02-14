package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入对应的口味数据
     * @param flavors
     * @return
     */
    void insertBatch(List<DishFlavor> flavors);
}
