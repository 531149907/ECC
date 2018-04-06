package com.ecc.dao.transaction;

import com.ecc.domain.transaction.impl.FileTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TransactionMapper {
    @Select("select * from t_file where id = #{arg0}")
    FileTransaction getTransactionById(String id);
}
