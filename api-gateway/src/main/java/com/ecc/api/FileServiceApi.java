package com.ecc.api;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Component
@FeignClient("file-service")
public interface FileServiceApi {
    @RequestMapping(value = "transaction/ticket", method = PUT)
    void addTicketTransaction(@RequestBody TicketTransaction transaction);

    @RequestMapping(value = "transaction/ticket/revoke", method = PUT)
    void addTicketRevoke(@RequestParam("ticketId") String ticketId,
                         @RequestParam("timestamp") String timestamp,
                         @RequestParam("ip") String ip,
                         @RequestParam("port") Integer port);

    @RequestMapping(value = "transaction/ticket/revoke", method = GET)
    boolean isTicketRevoked(@RequestParam("ticketId") String ticketId);

    @RequestMapping(value = "transaction/file", method = PUT)
    void addFileTransaction(@RequestBody FileTransaction transaction);

    @RequestMapping(value = "transaction/file/hash", method = GET)
    String getShardHashByHashedShardName(@RequestParam("hashedShareName") String hashedShardName);

    @RequestMapping(value = "transaction/file/id",method = GET)
    FileTransaction getFileTransaction(@RequestParam("id") String id);

    @RequestMapping(value = "transaction/files/fileId", method = GET)
    List<FileTransaction> getFileTransactionsByFileId(@RequestParam("fileId") String fileId);

    @RequestMapping(value = "transaction/files/owner", method = GET)
    List<String> getOwnerFileIds(@RequestParam("owner") String owner);
}
