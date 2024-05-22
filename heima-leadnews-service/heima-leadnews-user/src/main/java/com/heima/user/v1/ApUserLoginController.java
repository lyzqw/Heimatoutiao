package com.heima.user.v1;


import com.heima.model.user.dtos.LoginDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //这个注解表明这是一个RESTful风格的控制器类，它会将方法的返回值直接序列化为HTTP响应体，并返回给客户端。
@RequestMapping("/api/v1/login")
//@Api(value = "app端用户登录", tags = "app端用户登录")
public class ApUserLoginController {

    //这个注解用于自动装配ApUserService类型的bean，它告诉Spring容器在运行时自动将一个ApUserService的实例注入到这个属性中。
    @Autowired
    private ApUserService apUserService;

    @PostMapping("/login_auth")
//    @ApiOperation("用户登录")
    public ResponseResult login(@RequestBody LoginDto dto) {
        return apUserService.login(dto);
    }
}
