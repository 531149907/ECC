package com.ecc.dao;

import com.ecc.domain.transaction.impl.TicketTransaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TicketTransactionMapper {

    @Insert("insert into t_ticket(id,fileId,signFor,signer,permission,timestamp,code) values(#{id},#{fileId},#{signFor},#{signer},#{permission},#{timestamp},#{code})")
    void addTicketTransaction(TicketTransaction transaction);

    @Insert("insert into t_ticket_revoke(ticketId,timestamp,ip,port) values(#{arg0},#{arg1},#{arg2},#{arg3})")
    void addTicketRevoke(String ticketId, String timestamp, String ip, Integer port);

    @Select("select count(*) from t_ticket_revoke where ticketId = #{arg0}")
    int getTicketRevokeInfo(String ticketId);
}
