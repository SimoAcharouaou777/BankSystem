package com.youcode.bankify.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class IndexInitializer {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void createTransactionIndex() {
        try {

            boolean exists = elasticsearchClient.indices().exists(
                    ExistsRequest.of(e -> e.index("transactions"))
            ).value();

            if (!exists) {

                CreateIndexRequest request = new CreateIndexRequest.Builder()
                        .index("transactions")
                        .mappings(m -> m
                                .properties("amount", p -> p.double_(d -> d))
                                .properties("type", p -> p.keyword(k -> k))
                                .properties("status", p -> p.keyword(k -> k))
                                .properties("date", p -> p.date(d -> d.format("yyyy-MM-dd'T'HH:mm:ss")))
                                .properties("bankAccount", p -> p.object(o -> o))
                                .properties("user", p -> p.object(o -> o))
                        )
                        .build();

                CreateIndexResponse createIndexResponse = elasticsearchClient.indices().create(request);
                System.out.println("Index created: " + createIndexResponse.acknowledged());
            } else {
                System.out.println("Index 'transactions' already exists.");
            }
        } catch (Exception e) {
            System.err.println("Failed to create index: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
