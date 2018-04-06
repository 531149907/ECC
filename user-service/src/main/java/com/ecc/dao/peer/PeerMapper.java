package com.ecc.dao.peer;

import com.ecc.domain.peer.Peer;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface PeerMapper {
    @Insert("insert into t_peer(email,publicKey,regDate,ip,port,dir,level,channel) values(#{email},#{publicKey},#{regDate},#{ip},#{port},#{dir},#{level},#{channel})")
    void addPeer(Peer peer);

    @Select("select * from t_peer where email = #{arg0}")
    Peer getPeerByEmail(String email);

    @Select("select * from t_peer where ip = #{arg0}")
    Peer getPeerByIp(String ip);

    @Update("update t_peer set ip = #{ip}, port = #{port}, dir = #{dir}")
    void updatePeer(Peer peer);

    @Insert("insert into t_verify(email, code, password) values(#{arg0}, #{arg1}, #{arg2})")
    void addVerification(String email, String uuid, String password);

    @Select("select code from t_verify where email = #{arg0}")
    String getVerificationCode(String email);

    @Select("select password from t_verify where email = #{arg0}")
    String getVerificationPassword(String email);

    @Delete("delete from t_verify where email = #{arg0}")
    void deleteVerification(String email);
}
