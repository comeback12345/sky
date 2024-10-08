package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}...",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //删除redis中的缓存
        String key="dish_"+dishDTO.getCategoryId();
        cleanCatche(key);;
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品的批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}...",ids);
        dishService.deleteBatch(ids);

        //将redis所有的菜品数据清理掉
        cleanCatche("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}",id);
        DishVO dishVO=dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 更新菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新菜品")
    public Result<DishDTO> update(@RequestBody DishDTO dishDTO){
        log.info("更新菜品:{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //将redis所有的菜品数据清理掉
        cleanCatche("dish_*");
        return Result.success();
    }

    /**
     * 根据菜品id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("list")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list=dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 启售停售菜品
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启售停售菜品")
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable("status") Integer status,Long id){
        dishService.startOrStop(status,id);

        //将redis所有的菜品数据清理掉
        cleanCatche("dish_*");
        return Result.success();
    }

    private void cleanCatche(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
