package com.ecc.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface AllContractMapper {
    @Insert("insert into t_block(block) values(#{arg0})")
    void addBlock(byte[] object);

    @Select("select block from t_block where index = #{arg0}")
    byte[] getBlock(int index);
}
