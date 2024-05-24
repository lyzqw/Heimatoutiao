package com.heima.wemedia.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/channel")
//http://localhost:8802/wemedia/MEDIA/admin/api/v1/channel/channels
public class WmChannelController {

    @Autowired
    private WmChannelService service;

    @GetMapping("/channels")
    public ResponseResult findAll(){
        return service.findAll();
    }

}
