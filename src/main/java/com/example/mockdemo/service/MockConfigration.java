package com.example.mockdemo.service;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.tomakehurst.wiremock.client.WireMock.recordSpec;

@Configuration
public class MockConfigration {

    private WireMockServer wireMockServer;

    @Bean
    @ConditionalOnProperty(name = "mock.mode", havingValue = "RECORD")
    public WireMockServer wireMockRecordServer(@Value("${external.service.url}") String targetUrl,
                                               @Value("${mock.port}") int mockPort) {
        this.wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(mockPort)
                .usingFilesUnderDirectory("src/main/resources/__wiremock__"));
        this.wireMockServer.start();
        this.wireMockServer.startRecording(recordSpec()
                .forTarget(targetUrl)
                .makeStubsPersistent(true)
                .ignoreRepeatRequests());
        return this.wireMockServer;
    }

    @Bean
    @ConditionalOnProperty(name = "mock.mode", havingValue = "MOCK")
    public WireMockServer wireMockServer(@Value("${mock.port}") int mockPort) {
        this.wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(mockPort)
                .usingFilesUnderClasspath("__wiremock__"));
        this.wireMockServer.start();
        return this.wireMockServer;
    }

    @PreDestroy
    public void shutdownWireMock() {
        if (this.wireMockServer != null && this.wireMockServer.isRunning()) {
            this.wireMockServer.stopRecording();
            this.wireMockServer.stop();
        }
    }
}

