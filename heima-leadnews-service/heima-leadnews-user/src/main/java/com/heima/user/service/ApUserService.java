package com.heima.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.common.dtos.ResponseResult;

public interface ApUserService extends IService<ApUser> {
    public ResponseResult login(LoginDto dto);
}
