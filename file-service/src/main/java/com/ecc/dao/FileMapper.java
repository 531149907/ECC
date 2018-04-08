package com.ecc.dao;

import com.ecc.domain.transaction.Transaction;
import com.ecc.domain.transaction.impl.FileTransaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface FileMapper {

    @Select("select * from t_file where id = #{arg0}")
    FileTransaction getTransactionById(String id);

    @Insert("insert into t_file(id,originalFileName,hashedFileName,fileHash,shardId,shardFileName,shardOriginalName,shardHash,fileLevel,timestamp,owner,holder) values(#{id},#{originalFileName},#{hashedFileName},#{fileHash},#{shardId},#{shardFileName],#{shardOriginalName},#{shardHash},#{fileLevel},#{timestamp},#{owner},#{holder})")
    void addFileTransaction(Transaction transaction);

    @Select("select shardHash from t_file where shardFileName = #{arg0}")
    String getShardHashByShardFileName(String shardFileName);

    @Select("select * from t_file where hashedFileName = #{arg0}")
    List<FileTransaction> getFileTransactions(String hashedFileName);
}
