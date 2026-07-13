package com.example.stalzone_chatbot.ingestion;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.web.client.RestClient;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

import com.example.stalzone_chatbot.ingestion.GithubDataFetcher.GameDocument;
import com.fasterxml.jackson.databind.ObjectMapper;

@WireMockTest
class GithubDataFetcherTest {
    private GithubDataFetcher fetcher;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmInfo) {
        fetcher = new GithubDataFetcher(
            RestClient.builder().baseUrl(wmInfo.getHttpBaseUrl()).build(),  // apiClient
            RestClient.builder().baseUrl(wmInfo.getHttpBaseUrl()).build(),  // rawClient
            new ObjectMapper(),                        // objectMapper
            "test-repo",                               // githubRepo
            "global/items/"                            // itemPathPrefix
        );
    }   

    @Test
    void happyPath_returnsParsedDocuments() throws Exception {

        // Load fixture data
        String fixtureBody = new String(
            getClass().getResourceAsStream("/0r2g1.json").readAllBytes()
        );

        // Load fixture data for the tree response
        String treeResponseBody = new String(
            getClass().getResourceAsStream("/fixtures/tree-response.json").readAllBytes()
        );

        // Stub the GitHub API response for the tree endpoint
        stubFor(get(urlEqualTo("/git/trees/main?recursive=1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(treeResponseBody)));
        
        // stub per-file response
        stubFor(get(urlEqualTo("/global/items/weapon/assault_rifle/0r2g1.json"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(fixtureBody)));

        List<GameDocument> result = fetcher.fetchItemData();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("0r2g1");
        assertThat(result.get(0).category()).isEqualTo("weapon/assault_rifle");
        assertThat(result.get(0).nameEn()).isEqualTo("9A-91");
    }

}
