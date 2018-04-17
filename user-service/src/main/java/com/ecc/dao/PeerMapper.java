package com.ecc.dao;

import com.ecc.domain.peer.Peer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface PeerMapper {
    @Insert("insert into t_peer(email,publicKey,regDate,ip,port,dir,level,channel) values(#{email},#{publicKey},#{regDate},#{ip},#{port},#{dir},#{level},#{channel})")
    void addPeer(Peer peer);

    @Select("select * from t_peer where email = #{arg0}")
    Peer getPeerByEmail(String email);

    @Select("select * from t_peer where ip = #{arg0}")
    Peer getPeerByIp(String ip);

    @Select("select * from t_peer where status='up'")
    List<Peer> getUpPeers();

    @Select("select count(*) from t_peer where token = #{arg0}")
    int getTokenExists(String token);

    @Update("update t_peer set token = #{arg1} where email = #{arg0}")
    void updateToken(String email, String token);

    @Update("update t_peer set ip = #{ip}, port = #{port}, dir = #{dir}, token = #{token}, status = #{status} where email = #{email}}")
    void updatePeer(Peer peer);
}
