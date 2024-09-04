package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据id删除套餐和菜品的关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where dish_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 批量插入套餐表
     * @param setMealDishes
     */
    void insertBatch(List<SetmealDish> setMealDishes);
}
