package com.kigooo.kgs.controller.kgController;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.domain.kgDomain.KgUser;
import com.kigooo.kgs.service.kgService.KgAuthService;

@Controller
@RequestMapping("/auth")
@Validated
public class KgAuthController {
    @Resource
    private KgAuthService kgAuthService;

    // auth220001
    @RequestMapping("/login")
    @ResponseBody
    public KgResponseJson login(@Valid @NotBlank(message = "用户名不能为空") String account,@Valid @NotBlank(message = "密码不能为空") String password, HttpServletResponse httpServletResponse){
        return kgAuthService.login(account,password,httpServletResponse);
    }

    // auth220002
    @RequestMapping("/logout")
    @ResponseBody
    public KgResponseJson logout(HttpServletRequest httpServletRequest){
        return kgAuthService.logout(httpServletRequest);
    }

    // auth220003
    @RequestMapping("/changeToken")
    @ResponseBody
    public KgResponseJson changeToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return kgAuthService.changeToken(httpServletRequest,httpServletResponse);
    }

    // auth220004
    @RequestMapping("/updateUserMy")
    @ResponseBody
    public KgResponseJson updateUserMy(String uName,String phone,String sex,HttpServletRequest httpServletRequest){
        KgUser kgUser = new KgUser().setuName(uName).setPhone(phone).setSex(sex);
        return kgAuthService.updateUserMy(kgUser,httpServletRequest);
    }

    // auth220005
    @RequestMapping("/updateUserPasswordMy")
    @ResponseBody
    public KgResponseJson updateUserPasswordMy(HttpServletRequest httpServletRequest,@Valid @NotBlank @Size(min = 6,max = 20) String oldPassword,@Valid @NotBlank @Size(min = 6,max = 20) String newPassword){
        return kgAuthService.updateUserPasswordMy(httpServletRequest,oldPassword,newPassword);
    }

    // auth220006
    @RequestMapping("/updateUserHeadUrlMy")
    @ResponseBody
    public KgResponseJson updateUserHeadUrlMy(@Valid @NotEmpty @RequestParam("fileList") MultipartFile[] fileList, HttpServletRequest httpServletRequest) {
        return kgAuthService.updateUserHeadUrlMy(fileList,httpServletRequest);
    }

    // auth220007
    @RequestMapping("/getUserDetailMy")
    @ResponseBody
    public KgResponseJson getUserDetailMy(HttpServletRequest httpServletRequest){
        return kgAuthService.getUserDetailMy(httpServletRequest);
    }

    // auth220008
    @RequestMapping("/getSubMenuTreeMy")
    @ResponseBody
    public KgResponseJson getSubMenuTreeMy(HttpServletRequest httpServletRequest){
        return kgAuthService.getSubMenuTreeMy(httpServletRequest);
    }

    // auth220009
    @RequestMapping("/getOperateByCommandOIDMy")
    @ResponseBody
    public KgResponseJson getOperateByCommandOIDMy(String commandOID, HttpServletRequest httpServletRequest){
        return kgAuthService.getOperateByCommandOIDMy(commandOID,httpServletRequest);
    }

    // auth220010
    @RequestMapping("/getDataIdsByRangeIdMy")
    @ResponseBody
    public KgResponseJson getDataIdsByRangeIdMy(String rangeId, HttpServletRequest httpServletRequest){
        return kgAuthService.getDataIdsByRangeIdMy(rangeId,httpServletRequest);
    }
}