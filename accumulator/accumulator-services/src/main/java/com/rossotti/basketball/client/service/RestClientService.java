package com.rossotti.basketball.client.service;

import com.rossotti.basketball.util.service.PropertyService;
import com.rossotti.basketball.util.service.exception.PropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestClientService {

	private final PropertyService propertyService;

	@Autowired
	public RestClientService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	private HttpEntity<String> getEntity() throws PropertyException {
		String accessToken = propertyService.getProperty_String("xmlstats.accessToken");
		String userAgent = propertyService.getProperty_String("xmlstats.userAgent");

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		headers.set(HttpHeaders.USER_AGENT, userAgent);
		return new HttpEntity<>(headers);
	}

	private RestTemplate getRestTemplate() {
		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
		restTemplateBuilder.additionalMessageConverters(new MappingJackson2HttpMessageConverter());
		return restTemplateBuilder.build();
	}

	public ResponseEntity<byte[]> getJson(String eventUrl) {
		return getRestTemplate().exchange(eventUrl, HttpMethod.GET, getEntity(), byte[].class);
	}
}