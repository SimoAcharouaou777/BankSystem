package com.youcode.bankify.repository.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.youcode.bankify.entity.Transaction;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionSearchRepositoryCustomImpl implements TransactionSearchRepositoryCustom{

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Override
    public List<Transaction> searchByCriteria(BigDecimal amount, String type, String status, LocalDateTime startDate, LocalDateTime endDate){
        List<Transaction> transactions = new ArrayList<>();

        try{
            List<Query> queries = new ArrayList<>();
            if(amount != null){
                queries.add(Query.of(q -> q
                        .term(t -> t
                                .field("amount")
                                .value(FieldValue.of(amount.doubleValue())))));
            }
            if(type != null){
                queries.add(Query.of(q -> q
                        .term(t -> t
                                .field("type")
                                .value(FieldValue.of(type)))));
            }
            if(status != null){
                queries.add(Query.of(q -> q
                        .term(t -> t
                                .field("status")
                                .value(FieldValue.of(status)))));
            }
            if(startDate != null && endDate != null){
                queries.add(Query.of(q -> q
                        .range(r -> r
                                .field("date")
                                .gte(JsonData.of(startDate.toString()))
                                .lte(JsonData.of(endDate.toString())))));
            }

            SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                    .index("transactions")
                    .query(q -> q
                            .bool(b -> b
                                    .must(queries)));

            SearchResponse<Transaction> searchResponse = elasticsearchClient.search(searchBuilder.build(), Transaction.class);
            for(Hit<Transaction> hit : searchResponse.hits().hits()){
                transactions.add(hit.source());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return transactions;
    }
}
