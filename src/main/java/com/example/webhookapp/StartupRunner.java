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
	          "name": "Sakshi Pimpale",
	          "regNo": "250850120149",
	          "email": "sakshipimple517@gmail.com"
	        }
	        """;

	    HttpEntity<String> requestEntity =
	        new HttpEntity<>(requestBody, headers);

	    String response =
	        restTemplate.postForObject(url, requestEntity, String.class);

	  
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode jsonNode = mapper.readTree(response);

	    String webhookUrl = jsonNode.get("webhook").asText();
	    String accessToken = jsonNode.get("accessToken").asText();

	    System.out.println("Webhook URL: " + webhookUrl);
	    System.out.println("Access Token: " + accessToken);
	    
	
	    String finalSqlQuery = """
	   SELECT
    d.DEPARTMENT_NAME,
    t.total_salary AS SALARY,
    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME,
    TIMESTAMPDIFF(YEAR, e.DOB, CURRENT_DATE) AS AGE
FROM (
    SELECT
        e.EMP_ID,
        e.DEPARTMENT,
        SUM(p.AMOUNT) AS total_salary,
        ROW_NUMBER() OVER (
            PARTITION BY e.DEPARTMENT
            ORDER BY SUM(p.AMOUNT) DESC
        ) AS rn
    FROM EMPLOYEE e
    JOIN PAYMENTS p
        ON e.EMP_ID = p.EMP_ID
    WHERE DAY(p.PAYMENT_TIME) <> 1
    GROUP BY e.EMP_ID, e.DEPARTMENT
) t
JOIN EMPLOYEE e
    ON t.EMP_ID = e.EMP_ID
JOIN DEPARTMENT d
    ON t.DEPARTMENT = d.DEPARTMENT_ID
WHERE t.rn = 1;

	    """;
      
	
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

