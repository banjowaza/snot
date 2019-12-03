package com.intuit.secfraud.shared.snot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

import com.intuit.secfraud.shared.snot.SnotRag;

@Configuration
@ConfigurationProperties("snot")
@ComponentScan("com.intuit.secfraud.shared.snot")
@PropertySource("classpath:snot-defaults.properties") 
public class SnotConfiguration {

    @Bean
    @ConfigurationProperties("snot")
    public SnotProperties snotProperties() {
        return new SnotProperties();
    }

    @Bean
    @ConditionalOnMissingBean(name = "slackRestTemplate")
    public RestTemplate slackRestTemplate() {

        DefaultUriBuilderFactory handlerFactory = new DefaultUriBuilderFactory();
        handlerFactory.setEncodingMode(EncodingMode.VALUES_ONLY);

        return new RestTemplateBuilder().requestFactory(new ClientHttpRequestFactorySupplier())
                .errorHandler(new SnotRag())
                .uriTemplateHandler(handlerFactory)
                .build();
    }

}
