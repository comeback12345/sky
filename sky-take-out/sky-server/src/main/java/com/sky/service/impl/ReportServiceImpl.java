package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;
    /**
     * 营业数据统计指定时间区间
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //存放begin-end之间的日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnOverList=new ArrayList<>();
        for(LocalDate date : dateList){
            //查询date日期对应的营业额数据，状态为已完成的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0 : turnover;
            turnOverList.add(turnover);
        }
        String days = StringUtils.join(dateList, ",");
        return TurnoverReportVO.builder()
                .dateList(days)
                .turnoverList(StringUtils.join(turnOverList,","))
                .build();
    }

    /**
     * 用户数据统计指定时间区间
     * @param begin
     * @param end
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //存放begin-end之间的日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        //新增用户数量
        List<Integer> newUserList=new ArrayList<>();
        //总用户数量
        List<Integer> totalUserList=new ArrayList<>();
        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap<>();
            map.put("end",endTime);
            Integer totalUser=userMapper.countByMap(map);
            totalUserList.add(totalUser);
            map.put("begin",beginTime);
            Integer newUser=userMapper.countByMap(map);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    /**
     * 订单统计指定时间区间
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //存放begin-end之间的日期
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> orderCountList=new ArrayList<>();//总订单数
        List<Integer> validOrderCountList=new ArrayList<>();//有效订单数
        for(LocalDate date : dateList){
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(begintime, endtime, null);
            Integer validOrderCount = getOrderCount(begintime, endtime, Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        double orderCompletionRate=0.0;
        if(totalOrderCount!=0) orderCompletionRate=(double)validOrderCount/totalOrderCount;
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .orderCompletionRate(orderCompletionRate)
                .build();

    }

    /**
     * 根据条件统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end,Integer status) {
        Map map=new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.countByMap(map);
    }

    /**
     * 销量前十
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> saleTop10 = orderMapper.getSaleTop10(beginTime, endTime);
        List<String> name = saleTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(name, ",");
        List<Integer> number = saleTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(number, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //通过POI将数据写入到Excel文件中

        //基于模板文件创建一个新的Excel文件
        InputStream in =this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        XSSFWorkbook excel=new XSSFWorkbook(in);

        //获取表格文件的sheet页
        XSSFSheet sheet=excel.getSheet("Sheet1");
        sheet.getRow(1).getCell(1).setCellValue("时间："+begin+"至"+end);

        //第四行
        XSSFRow row = sheet.getRow(3);
        row.getCell(2).setCellValue(businessDataVO.getTurnover());
        row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
        row.getCell(6).setCellValue(businessDataVO.getNewUsers());

        //第五行
        row = sheet.getRow(4);
        row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
        row.getCell(4).setCellValue(businessDataVO.getUnitPrice());


        //下面详细数据
        for (int i = 0; i < 30; i++) {
            LocalDate date = begin.plusDays(i);
            //查询某一天的营业数据
            BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
            row = sheet.getRow(7 + i);
            row.getCell(1).setCellValue(date.toString());
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(3).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(5).setCellValue(businessData.getUnitPrice());
            row.getCell(6).setCellValue(businessData.getNewUsers());
        }

        ServletOutputStream out =response.getOutputStream();
        excel.write(out);

        //关闭资源
        out.close();
        excel.close();

    }
}
