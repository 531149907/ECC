package com.ecc.dao;

import com.ecc.domain.peer.Verification;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface VerificationMapper {
    @Insert("insert into t_verify(email, code, password) values(#{email}, #{code}, #{password})")
    void addVerification(Verification verification);

    @Select("select * from t_verify where email = #{arg0}")
    Verification getVerification(String email);

    @Delete("delete from t_verify where email = #{arg0}")
    void deleteVerification(String email);

    @Update("update t_verify set code = #{code}, password = #{password} where email = #{email}")
    void updateVerification(Verification verification);
}
