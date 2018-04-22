package com.ecc.dao;

import com.ecc.domain.transaction.impl.FileTransaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface FileTransactionMapper {
    @Insert("insert into t_file(transactionId,fileId,originalFileName,hashedFileName,fileHash,shardId,originalShardName,hashedShardName,shardHash,fileLevel,timestamp,owner,holder) values(#{transactionId},#{fileId},#{originalFileName},#{hashedFileName},#{fileHash},#{shardId},#{originalShardName},#{hashedShardName},#{shardHash},#{fileLevel},#{timestamp},#{owner},#{holder})")
    void addFileTransaction(FileTransaction transaction);

    @Select("select * from t_file where transactionId = #{arg0}")
    FileTransaction getFileTransactionById(String transactionId);

    @Select("select * from t_file where fileId = #{arg0}")
    List<FileTransaction> getFileTransactionsByFileId(String fileId);

    @Select("select distinct fileId from t_file where owner = #{arg0}")
    List<String> getOwnersFileIds(String owner);

    @Select("select shardHash from t_file where hashedShardName = #{arg0}")
    String getShardHashByHashedShardName(String hashedShardName);
}
