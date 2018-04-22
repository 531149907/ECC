package com.web;

import com.domain.Content;
import com.ecc.util.converter.DateUtil;
import com.google.gson.Gson;
import com.service.BlockList;
import com.service.FileList;
import com.service.NodeList;
import com.service.TransactionList;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api")
public class AllApi {
    @RequestMapping(value = "login",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(String email, String dir, String password) {
        return new Gson().toJson(Content.builder()
                .status(200)
                .message("success!")
                .data(dir).build());
    }

    @RequestMapping(value = "register",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String register(String email, String channel, String dir, String level) {
        return Content.builder()
                .status(200)
                .message("success!")
                .data(null).build().toString();
    }

    @RequestMapping(value = "logout",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String logout() {
        return Content.builder()
                .status(200)
                .message("success!")
                .data(null).build().toString();
    }

    //下载文件
    @RequestMapping(value = "download/files",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String downloadFileList() {
        List<FileList> list = new ArrayList<>();
        list.add(FileList.builder().fileId("b3d08417f68cd4fd41f2cf32c27473d2a5730214c2b9b21696d3243582c3f07e").fileName("企业实习初期检查表（企业导师版）(附件1）.docx").build());
        list.add(FileList.builder().fileId("b20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5UcmFuc2F").fileName("信软学院本科生企业实习管理办法（2016-2017)(附件3).docx").build());
        list.add(FileList.builder().fileId("2Y2OGNkNGZkNDFmMmNmMzJjMjc0NzNkMmE1").fileName("WeUI 1.0（2016.12.26）.sketch").build());

        return new Gson().toJson(Content.builder()
                .status(200)
                .message("success!")
                .data(list).build());
    }

    @RequestMapping(value = "download/file",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String downloadFileByFileId(@RequestParam(value = "fileId", required = false) String fileId,
                                        @RequestParam(value = "ticket", required = false) String ticket) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Content.builder()
                .status(200)
                .message("文件下载成功!")
                .data(null).build().toString();
    }

    //文件上传
    @RequestMapping(value = "upload/file",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String upload(String dir,
                          @RequestParam(value = "password", required = false) String password) {

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Content.builder()
                .status(200)
                .message("文件上传成功!")
                .data(null).build().toString();
    }

    //节点概况
    @RequestMapping(value = "node",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String node() {
        List<NodeList> nodeLists = new ArrayList<>();
        nodeLists.add(NodeList.builder().name("节点 0").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 1").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 2").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 3").host("192.156.42.1").status("down").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 3").host("192.156.42.1").status("down").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 3").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("down").build());
        nodeLists.add(NodeList.builder().name("节点 3").host("192.156.42.1").status("down").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 3").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("up").build());
        nodeLists.add(NodeList.builder().name("节点 4").host("192.156.42.1").status("down").build());

        return Content.builder()
                .status(200)
                .message("文件上传成功!")
                .data(nodeLists).build().toString();
    }

    //区块查询
    @RequestMapping(value = "block",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String blockList() {
        List<BlockList> blockLists = new ArrayList<>();
        blockLists.add(BlockList.builder().index(0).title("第 0 个区块 （785个合约）").build());
        blockLists.add(BlockList.builder().index(1).title("第 1 个区块 （785个合约）").build());
        blockLists.add(BlockList.builder().index(2).title("第 2 个区块 （785个合约）").build());
        blockLists.add(BlockList.builder().index(3).title("第 3 个区块 （785个合约）").build());
        blockLists.add(BlockList.builder().index(4).title("第 4 个区块 （785个合约）").build());
        blockLists.add(BlockList.builder().index(5).title("第 5 个区块 （785个合约）").build());

        return Content.builder()
                .status(200)
                .message("success")
                .data(blockLists).build().toString();
    }

    //弹窗
    @RequestMapping(value = "ticket",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String signTicket(String signFor, String password) {
        return Content.builder()
                .status(200)
                .message("success")
                .data("rO0ABXNyADFjb20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5pbXBsLlRpY2tldFRyYW5zYWN0aW9uh56OvJWswAACAAdMAARjb2RldAASTGphdmEvbGFuZy9TdHJpbmc7TAAGZmlsZUlkcQB+AAFMAAJpZHEAfgABTAAKcGVybWlzc2lvbnEAfgABTAAHc2lnbkZvcnEAfgABTAAGc2lnbmVycQB+AAFMAAl0aW1lc3RhbXBxAH4AAXhyACZjb20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5UcmFuc2FjdGlvbnT9oiguhKS1AgABTAAPdHJhbnNhY3Rpb25UeXBlcQB+AAF4cHQABnRpY2tldHB0AEBiM2QwODQxN2Y2OGNkNGZkNDFmMmNmMzJjMjc0NzNkMmE1NzMwMjE0YzJiOWIyMTY5NmQzMjQzNTgyYzNmMDdldAAkMjc1NDhlNWUtOTg4NC00Yjc3LWFiMDktMGM5OGQ3MzliOGFhdAAIZG93bmxvYWR0AA10ZXN0MEBlY2MuY29tdAANdGVzdDBAZWNjLmNvbXQAEzIwMTgtMDQtMjEgMTU6NTc6NTU=")
                .build().toString();
    }

    @RequestMapping(value = "transaction",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String transactions(String index) {
        List<TransactionList> transactionLists = new ArrayList<>();
        transactionLists.add(TransactionList.builder()
                .id("0")
                .type("FILE")
                .hash("rO0ABXNyADFjb20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5pbXBsLlRpY2tldFRyYW5zYWN0aW9uh56OvJWswAACAAdMAARj")
                .level("level_A")
                .timestamp(DateUtil.getDate())
                .owner("test0@ecc.com")
                .build());
        transactionLists.add(TransactionList.builder()
                .id("1")
                .type("FILE")
                .hash("rO0ABXNyADFjb20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5pbXBsLlRpY2tldFRyYW5zYWN0aW9uh56OvJWswAACAAdMAARj")
                .level("level_A")
                .timestamp(DateUtil.getDate())
                .owner("test0@ecc.com")
                .build());
        transactionLists.add(TransactionList.builder()
                .id("2")
                .type("TICKET")
                .hash("rO0ABXNyADFjb20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5pbXBsLlRpY2tldFRyYW5zYWN0aW9uh56OvJWswAACAAdMAARj")
                .level("level_A")
                .timestamp(DateUtil.getDate())
                .owner("test0@ecc.com")
                .build());
        transactionLists.add(TransactionList.builder()
                .id("3")
                .type("FILE")
                .hash("rO0ABXNyADFjb20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5pbXBsLlRpY2tldFRyYW5zYWN0aW9uh56OvJWswAACAAdMAARj")
                .level("level_A")
                .timestamp(DateUtil.getDate())
                .owner("test0@ecc.com")
                .build());
        transactionLists.add(TransactionList.builder()
                .id("4")
                .type("TICKET")
                .hash("rO0ABXNyADFjb20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5pbXBsLlRpY2tldFRyYW5zYWN0aW9uh56OvJWswAACAAdMAARj")
                .level("level_A")
                .timestamp(DateUtil.getDate())
                .owner("test0@ecc.com")
                .build());
        transactionLists.add(TransactionList.builder()
                .id("5")
                .type("FILE")
                .hash("rO0ABXNyADFjb20uZWNjLmRvbWFpbi50cmFuc2FjdGlvbi5pbXBsLlRpY2tldFRyYW5zYWN0aW9uh56OvJWswAACAAdMAARj")
                .level("level_A")
                .timestamp(DateUtil.getDate())
                .owner("test0@ecc.com")
                .build());
        return Content.builder()
                .status(200)
                .message("success")
                .data(transactionLists)
                .build().toString();
    }

    @RequestMapping(value = "error",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String error(String message) {
        return Content.builder()
                .status(400)
                .message("ERROR! 你遇到了错误！")
                .data(null)
                .build().toString();
    }
}
