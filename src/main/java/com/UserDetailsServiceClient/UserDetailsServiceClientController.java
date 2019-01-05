package com.UserDetailsServiceClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

//import com.userdetails.model.UserDetails;

@RestController
public class UserDetailsServiceClientController {

	@Autowired
	private DiscoveryClient discoveryClient;
	


	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "getDataFallBack")
	public List listAllUsers(){
		List<ServiceInstance> instances=discoveryClient.getInstances("eurekauserservice");
		ServiceInstance serviceInstance=instances.get(0);
		 String baseUrl=serviceInstance.getUri().toString();	
		System.out.println("baseurl is "+baseUrl);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List> response=null;
		System.out.println("in UserDetailsServiceClientController");
		
		try{
			baseUrl=baseUrl+"/UserDetails/listAll";
			response=restTemplate.exchange(baseUrl,HttpMethod.GET, getHeaders(),List.class);
			}catch (Exception ex)
			{
				System.out.println(ex);
			}
			System.out.println(response.getBody());
		return response.getBody();
	}
	
	
	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}
	public List getDataFallBack() {
		List dummydata=new ArrayList();
		dummydata.add("testuserid");
		dummydata.add("testusername");
		return dummydata;
		
	}
}
