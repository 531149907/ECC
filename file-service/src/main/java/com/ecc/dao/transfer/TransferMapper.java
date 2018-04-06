package com.ecc.dao.transfer;

import com.ecc.domain.transaction.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TransferMapper {

    @Insert("insert into t_file(id,originalFileName,hashedFileName,fileHash,shardId,shardHash,fileLevel,timestamp,owner,holder) values(#{id},#{originalFileName},#{hashedFileName},#{fileHash},#{shardId},#{shardHash},#{fileLevel},#{timestamp},#{owner},#{holder})")
    void addFileTransaction(Transaction transaction);
}
