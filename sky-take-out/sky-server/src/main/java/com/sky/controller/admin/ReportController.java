package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业统计指定时间区间
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
                                                           @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额数据统计{}，{}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin,end));
    }

    /**
     * 用户数据统计指定时间区间
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
                                                @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户数据统计{}，{}", begin, end);
        return Result.success(reportService.getUserStatistics(begin,end));
    }

    /**
     * 订单统计指定时间区间
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单数据统计{}，{}", begin, end);
        return Result.success(reportService.getOrdersStatistics(begin,end));
    }

    /**
     * 销量前十
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("销量前十")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat (pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单数据统计{}，{}", begin, end);
        return Result.success(reportService.getSalesTop10(begin,end));
    }

    /**
     * 导出数据
     * @param response
     * @param begin
     * @param end
     */
    @GetMapping("/export")
    @ApiOperation("导出数据")
    public void export(HttpServletResponse response, String begin, String end) throws IOException {
        reportService.exportBusinessData(response);
    }
}