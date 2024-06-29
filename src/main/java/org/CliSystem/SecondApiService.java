package org.CliSystem;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


//TODO Нужно будет убрать этот класс, когда появиться слеш,
// а то ретрофит невозможно тестить без самих отправок запросов
public class SecondApiService {

    public void save(String url, String path){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ApiDto apiDto = ApiDto.builder()
                .name(path)
                .build();
        HttpEntity<Object> requestEntity = new HttpEntity<>(apiDto, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        System.out.println(responseEntity.getBody());
    }
}
