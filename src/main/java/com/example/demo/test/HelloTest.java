package com.example.demo.test;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.demo.model.AggressResult;
import com.example.demo.service.ClickHourceService;

@Component
public class HelloTest {

	@Resource
	private ClickHourceService clickHourceService;
	
	
	@PostConstruct
	public void test() throws Exception {
//		JSONArray queryByStatement = clickHourceService.queryByStatement("select ad_name,currency,`domain`,SUM(purchase_count) as purchase,SUM(purchase_sum_value) as purchase_value ,SUM(registration_count) as registration from facebook.sre_reporting_hourly_stats   where date_str >=  '2021-04-04 00:00:00' and  date_str < '2021-04-05 00:00:00' and domain = 'shein' group by ad_name,currency,domain");
	   
 List<AggressResult> result = clickHourceService.queryyPrepareStatement(AggressResult.class,"select ad_name,currency,`domain`,SUM(purchase_count) as purchase,SUM(purchase_sum_value) as purchase_value ,SUM(registration_count) as registration from facebook.sre_reporting_hourly_stats   where  toDateTime(date_str)  >=  ? and   toDateTime(date_str)  < ? and domain = ? group by ad_name,currency,domain", "2021-04-04 00:00:00", "2021-04-05 00:00:00","shein");
 
 result.forEach(element ->{
	 System.out.println(JSON.toJSONString(element));
 });

	     
	     
	}
}
