package com.example.webhookapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



@Component
public class StartupRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {

	    RestTemplate restTemplate = new RestTemplate();

	    String url =
	        "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    String requestBody = """
	        {
	          "name": "John Doe",
	          "regNo": "REG12347",
	          "email": "john@example.com"
	        }
	        """;

	    HttpEntity<String> requestEntity =
	        new HttpEntity<>(requestBody, headers);

	    String response =
	        restTemplate.postForObject(url, requestEntity, String.class);

	    // 🔹 Parse JSON response
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode jsonNode = mapper.readTree(response);

	    String webhookUrl = jsonNode.get("webhook").asText();
	    String accessToken = jsonNode.get("accessToken").asText();

	    System.out.println("Webhook URL: " + webhookUrl);
	    System.out.println("Access Token: " + accessToken);
	    
	 // FINAL SQL QUERY (Question 1)
	    String finalSqlQuery = """
	    SELECT *
	    FROM your_table_name;
	    """;
      
	 // Send final SQL to webhook
	    HttpHeaders submitHeaders = new HttpHeaders();
	    submitHeaders.setContentType(MediaType.APPLICATION_JSON);
	    submitHeaders.set("Authorization", accessToken);

	    String submitBody = """
	    {
	      "finalQuery": "%s"
	    }
	    """.formatted(finalSqlQuery.replace("\n", " "));

	    HttpEntity<String> submitRequest =
	            new HttpEntity<>(submitBody, submitHeaders);

	    String submitResponse =
	            restTemplate.postForObject(webhookUrl, submitRequest, String.class);

	    System.out.println("Submission Response:");
	    System.out.println(submitResponse);

	}

}
