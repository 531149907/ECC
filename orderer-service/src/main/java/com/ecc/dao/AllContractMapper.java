package com.ecc.dao;

import com.ecc.domain.contract.Contract;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface AllContractMapper {
    @Insert("insert into t_all_contract(id,contract,transactionType,transactionId,transactionHash,timestamp,senderSign,receiverSign) values(#{id},#{contract},#{transactionType},#{transactionId},#{transactionHash},#{timestamp},#{senderSign},#{receiverSign})")
    void addContract(Contract contract);

    @Select("select * from t_all_contract where id = #{arg0}")
    void getContract(String id);
}
