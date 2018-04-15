package com.ecc.dao;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;
import java.util.List;

@Component
@Mapper
public interface FileMapper {

    @Insert("insert into t_file(transactionId,fileId,originalFileName,hashedFileName,fileHash,shardId,originalShardName,hashedShardName,shardHash,fileLevel,timestamp,owner,holder) values(#{transactionId},#{fileId},#{originalFileName},#{hashedFileName},#{fileHash},#{shardId},#{originalShardName},#{hashedShardName},#{shardHash},#{fileLevel},#{timestamp},#{owner},#{holder})")
    void addFileTransaction(FileTransaction transaction);

    @Insert("insert into t_ticket(id,fileId,signFor,signer,permission,timestamp) values(#{id},#{fileId},#{signFor},#{signer},#{permission},#{timestamp})")
    void addTicketTransaction(TicketTransaction transaction);

    @Insert("insert into t_ticket_revoke(ticketId,timestamp,ip,port) values(#{arg0},#{arg1},#{arg2},#{arg3})")
    void addTicketRevoke(String ticketId, String timestamp, String ip, Integer port);

    @Select("select * from t_file where transactionId = #{arg0}")
    FileTransaction getFileTransactionById(String id);

    @Select("select * from t_file where fileId = #{arg0}")
    List<FileTransaction> getFileTransactionsByFileId(String fileId);

    @Select("select distinct fileId from t_file where owner = #{arg0}")
    List<String> getOwnersFileIds(String owner);

    @Select("select shardHash from t_file where hashedShardName = #{arg0}")
    String getShardHashByHashedShardName(String hashedShardName);

    @Select("select * from t_ticket_revoke where ticketId = #{arg0}")
    Object getTicketRevokeInfo(String ticketId);
}
