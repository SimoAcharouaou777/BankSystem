package com.youcode.bankify.repository.elasticsearch;

import com.youcode.bankify.entity.Transaction;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;



public interface TransactionSearchRepository extends ElasticsearchRepository<Transaction,Long> , TransactionSearchRepositoryCustom {

}
