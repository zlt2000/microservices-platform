package com.central.oauth.component;

import com.central.common.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.core.endpoint.DefaultOAuth2AccessTokenResponseMapConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;

import java.util.Map;

/**
 * @author zlt
 * @version 1.0
 * @date 2023/12/6
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class CustomAccessTokenResponseHttpMessageConverter extends OAuth2AccessTokenResponseHttpMessageConverter {
    private Converter<OAuth2AccessTokenResponse, Map<String, Object>> accessTokenResponseParametersConverter = new DefaultOAuth2AccessTokenResponseMapConverter();
    private static final ParameterizedTypeReference<Result<Map<String, Object>>> STRING_OBJECT_MAP = new ParameterizedTypeReference<>() {};

    private final ObjectMapper objectMapper;
    public CustomAccessTokenResponseHttpMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void writeInternal(OAuth2AccessTokenResponse tokenResponse, HttpOutputMessage outputMessage)
            throws HttpMessageNotWritableException {
        try {
            Map<String, Object> tokenResponseParameters = this.accessTokenResponseParametersConverter.convert(tokenResponse);
            GenericHttpMessageConverter<Object> jsonMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
            jsonMessageConverter.write(Result.succeed(tokenResponseParameters), STRING_OBJECT_MAP.getType(), MediaType.APPLICATION_JSON, outputMessage);
        }
        catch (Exception ex) {
            throw new HttpMessageNotWritableException(
                    "An error occurred writing the OAuth 2.0 Access Token Response: " + ex.getMessage(), ex);
        }
    }
}
