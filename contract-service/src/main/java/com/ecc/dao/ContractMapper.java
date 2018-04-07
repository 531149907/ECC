package com.ecc.dao;

import com.ecc.domain.contract.Contract;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ContractMapper {
    @Insert("insert into t_contract(id,contract,transactionType,transactionId,transactionHash,timestamp,senderSign,receiverSign) values(#{id},#{contract},#{transactionType},#{transactionId},#{transactionHash},#{timestamp},#{senderSign},#{receiverSign})")
    void addContract(Contract contract);

    @Select("select * from t_contract order by timestamp limit 10")
    List<Contract> getTop10Contracts();

    @Select("select count(*) from t_contract")
    int getContractsCount();

    @Delete("delete from t_contract whete id = #{arg0}")
    void deleteContractById(String id);
}
