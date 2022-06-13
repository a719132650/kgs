package com.kigooo.kgs.service.kgService;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.domain.kgDomain.KgUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Validated
public interface KgAuthService {
    boolean isUnProtectedUrl(@Valid @NotBlank String url);
    boolean isPublicUrl(@Valid @NotBlank String url);
    boolean isPrivateUrl(@Valid @NotBlank String url);
    KgResponseJson login(String account, String password, HttpServletResponse httpServletResponse);
    KgResponseJson logout(HttpServletRequest httpServletRequest);
    KgResponseJson changeToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    KgResponseJson updateUserMy(KgUser kgUser,HttpServletRequest httpServletRequest);
    KgResponseJson updateUserPasswordMy(HttpServletRequest httpServletRequest,String oldPassword,String newPassword);
    KgResponseJson updateUserHeadUrlMy(MultipartFile[] fileList, HttpServletRequest httpServletRequest);
    KgResponseJson getUserDetailMy(HttpServletRequest httpServletRequest);
    KgResponseJson getSubMenuTreeMy(HttpServletRequest httpServletRequest);
    KgResponseJson getOperateByCommandOIDMy(String commandOID, HttpServletRequest httpServletRequest);
    KgResponseJson getDataIdsByRangeIdMy(String rangeId, HttpServletRequest httpServletRequest);
}
