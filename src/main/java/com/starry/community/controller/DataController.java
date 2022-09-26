package com.starry.community.controller;

import com.starry.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * @author Starry
 * @create 2022-09-26-2:25 PM
 * @Describe 数据统计
 */
@Controller
@RequestMapping("/data")
public class DataController {
    @Autowired
    private DataService dataService;

    private DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @RequestMapping (value = "/getPage", method = {RequestMethod.GET, RequestMethod.POST})
    public String getPage() {
        return "/site/admin/data";
    }

    @PostMapping("/uv")
    public String getUV(String begin, String end, Model model) {
        LocalDate start = LocalDate.parse(begin, dateTimeFormat);
        LocalDate end1 = LocalDate.parse(end, dateTimeFormat);
        Map<String, Long> uv = dataService.getUV(start, end1);
        model.addAttribute("uvMap",uv);
        model.addAttribute("uvStart", localDate2Date(start));
        model.addAttribute("uvEnd", localDate2Date(end1));
        return "/site/admin/data";
    }

    @PostMapping("/dau")
    public String getDAU(String begin, String end, Model model) {
        LocalDate start = LocalDate.parse(begin, dateTimeFormat);
        LocalDate end1 = LocalDate.parse(end, dateTimeFormat);
        Map<String, Long> uv = dataService.getDAU(start, end1);
        model.addAttribute("dauMap",uv);
        model.addAttribute("dauStart", localDate2Date(start));
        model.addAttribute("dauEnd", localDate2Date(end1));
        return "/site/admin/data";
    }

    public Date localDate2Date(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }
}
