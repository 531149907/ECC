package com.ecc.service;

import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        messageConverters.add(new MappingJackson2HttpMessageConverter());
    }

    public <T> T get(String url, HashMap<String, String> params, @NonNull Class<T> responseType) {
        String fullUrl = appendUrl(url, params);
        return restTemplate.getForEntity(fullUrl, responseType, new HashMap<>()).getBody();
    }


    public <T> T post(String url, HashMap<String, String> params, @Nullable Object request, Class<T> responseType) {
        String fullUrl = appendUrl(url, params);
        return restTemplate.postForEntity(fullUrl, request, responseType).getBody();
    }

    public String download(String url, HashMap<String, String> params, String downloadPath) {
        String fullUrl = appendUrl(url, params);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(fullUrl, HttpMethod.POST, null, byte[].class, new HashMap<>());

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new CustomException(ExceptionCollection.FILE_DOWNLOAD_FAILED);
    }

    private String appendUrl(String url, HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        if (params == null) {
            params = new HashMap<>();
        }

        if (!url.contains("http")) {
            builder.append("http://");
        }
        builder.append(url).append("?");

        for (String key : params.keySet()) {
            builder.append(key).append("=").append(params.get(key)).append("&");
        }

        return builder.toString();
    }
}
