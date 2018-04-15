package com.ecc.service;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestTemplate {
    private org.springframework.web.client.RestTemplate restTemplate;

    public RestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(30 * 1000);
        factory.setConnectTimeout(30 * 1000);

        this.restTemplate = new org.springframework.web.client.RestTemplate(factory);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        messageConverters.removeIf(converter -> converter instanceof StringHttpMessageConverter);
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("utf-8")));
    }

    public <T> T get(String url, HashMap<String, Object> params, Class<T> clazz) {
        if (params == null) {
            params = new HashMap<>();
        }
        url = url(url) + "?";
        StringBuilder builder = new StringBuilder();
        builder.append(url);

        for (String key : params.keySet()) {
            builder.append(key).append("=").append(params.get(key)).append("&");
        }

        return restTemplate.exchange(builder.toString(), HttpMethod.GET, null, clazz, new HashMap<>()).getBody();
    }

    public <T> T post(String url, HashMap<String, Object> params, Class<T> clazz, boolean hasBody){
        if (params == null) {
            params = new HashMap<>();
        }
        url = url(url);

        return restTemplate.postForEntity(url,params.get("contract"),clazz,new HashMap<>()).getBody();
    }

    public <T> T post(String url, HashMap<String, Object> params, Class<T> clazz) {
        if (params == null) {
            params = new HashMap<>();
        }
        url = url(url);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        for (String key : params.keySet()) {
            map.add(key, params.get(key));
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, httpHeaders);

        return restTemplate.exchange(url, HttpMethod.POST, entity, clazz, new HashMap<>()).getBody();
    }

    public String download(String url, HashMap<String, Object> params, String downloadPath) {
        if (params == null) {
            params = new HashMap<>();
        }
        url = url(url);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        for (String key : params.keySet()) {
            map.add(key, params.get(key));
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.ALL);
        httpHeaders.setAccept(mediaTypes);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, httpHeaders);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class, new HashMap<>());

        Path downloadDir = Paths.get(downloadPath);
        Path filePath = Paths.get(downloadDir.toString() + "/" + params.get("fileName"));
        try {
            if (!Files.exists(downloadDir)) {
                Files.createDirectories(downloadDir);
            }
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            Files.write(filePath, responseEntity.getBody());
            return "download successfully!";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "download failed!";
    }

    private String url(String url) {
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        return url;
    }
}
