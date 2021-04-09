package com.example.demo.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;

@Component
public class HelloTest {

	@Resource
	private BaseClickHourceService clickHourceService;
	
	
	@PostConstruct
	public void test() throws Exception {
		String sql = "select ad_name,currency,`domain`,SUM(purchase_count) as purchase," +
				" SUM(purchase_sum_value) as purchase_value ,SUM(registration_count) as registration " +
				"from facebook.sre_reporting_hourly_stats   " +
				" where  toDateTime(date_str)  >=  ? and   toDateTime(date_str)  < ? and domain = ? " +
				"group by ad_name,currency,domain";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		Date date = format.parse("2021-04-04");

		SimpleDateFormat format8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format8.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		String startDate = format8.format(date);
		String endDate = format8.format(DateUtils.addDays(date, 1));
		final JSONArray shein = clickHourceService.queryByPreparedStatement(sql, startDate, endDate, "shein");

		for (int i = 0; i < shein.size(); i++) {
			System.out.println(shein.get(i));
		}

	}
}
