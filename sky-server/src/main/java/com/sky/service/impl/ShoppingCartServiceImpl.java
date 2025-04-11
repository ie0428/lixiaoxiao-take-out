package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;

import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
   @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        //只能查询自己购物车的数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //判断当前商品是否存在于购物车中
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList!=null&&shoppingCartList.size()>0){
            //如果已经存在，则更新数量
            ShoppingCart shoppingCart1 = shoppingCartList.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updateNumberById(shoppingCart1);
        }else {
            //如果不存在，则添加到购物车，默认数量为1

            //判断当前添加到购物车的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId!=null){
                //添加到购物车的是菜品
                Dish dish= dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else {
                //添加到购物车的是套餐
                Setmeal Setmeal= setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(Setmeal.getName());
                shoppingCart.setImage(Setmeal.getImage());
                shoppingCart.setAmount(Setmeal.getPrice());

            }

            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);

        }

    }

    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> list() {
        return shoppingCartMapper.list(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
    }

    /**
     * 减少购物车
     */
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        //只能查询自己购物车的数据
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //判断当前商品是否存在于购物车中
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList!=null&&shoppingCartList.size()>0){
            ShoppingCart shoppingCart1 = shoppingCartList.get(0);
            if (shoppingCart1.getNumber()>1){
                //如果已经存在，则更新数量
                shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
                shoppingCartMapper.updateNumberById(shoppingCart1);
            }else {
                //如果不存在，则删除商品
                shoppingCartMapper.deleteById(shoppingCart1.getId());
            }
        }
    }

    /**
     * 清空购物车
     */
    public void clean() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }
}
