package com.youcode.bankify.repository.elasticsearch;

import com.youcode.bankify.entity.Transaction;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionSearchRepository extends ElasticsearchRepository<Transaction,Long> , TransactionSearchRepositoryCustom {

}
