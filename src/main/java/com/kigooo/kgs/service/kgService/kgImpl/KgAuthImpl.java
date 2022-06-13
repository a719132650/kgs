package com.kigooo.kgs.service.kgService.kgImpl;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import com.kigooo.kgs.domain.kgDomain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import com.kigooo.kgs.component.kgJWT.KgJWTUtil;
import com.kigooo.kgs.component.kgRedis.KgRedisUtil;
import com.kigooo.kgs.component.kgResponseException.KgResponseException;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.config.KgProperties;
import com.kigooo.kgs.dao.kgDao.KgAuthDao;
import com.kigooo.kgs.service.kgService.KgAuthService;
import com.kigooo.kgs.service.kgService.KgRoleService;
import com.kigooo.kgs.service.kgService.KgUserService;
import com.kigooo.kgs.util.KgDateUtil;
import com.kigooo.kgs.util.KgMD5Util;
import com.kigooo.kgs.util.KgUtil;

@Service
@Validated
public class KgAuthImpl implements KgAuthService {

    @Autowired
    private KgAuthDao kgAuthDao;
    @Autowired
    private KgProperties kgProperties;
    @Autowired
    private KgRedisUtil kgRedisUtil;	
    @Autowired
    private KgUserService kgUserService;
    @Autowired
    private KgRoleService kgRoleService;

    private KgResponseJson kgResponseJsonDefault = new KgResponseJson();


    /**
     * 请求地址是否在白名单中
     * @param api 接口url
     * @return boolean
     * @author
     * @date 2021/8/30 15:05
     */
    @Override
    public boolean isUnProtectedUrl(String api){
        return kgAuthDao.isInState(api,0)>0;
    }

    /**
     * 请求地址是否为公共接口
     * @param api 接口url
     * @return boolean
     * @author
     * @date 2021/8/30 15:06
     */
    @Override
    public boolean isPublicUrl(String api){
        return kgAuthDao.isInState(api,1)>0;
    }

    /**
     * 请求地址是否为需要权限的
     * @param api 接口url
     * @return boolean
     * @author
     * @date 2021/8/30 15:06
     */
    @Override
    public boolean isPrivateUrl(String api){
        return kgAuthDao.isInState(api,2)>0;
    }

    /**
     * 登陆
     * @param account 账户
     * @param password 密码
     * @param httpServletResponse http请求
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 15:07
     */
    @Override
    public KgResponseJson login(String account, String password, HttpServletResponse httpServletResponse){
        KgResponseJson kgResponseJson = new KgResponseJson();
        KgUser kgUser = kgUserService.getUserByAccount(account,1);
        String token;
        String userOID;
        if(KgUtil.isNotEmpty(kgUser) && KgMD5Util.getMD5(password).equals(kgUser.getuPassword())){
            kgResponseJson.putData("user",kgUser);
            userOID = kgUser.getOid();
            token = KgJWTUtil.createToken(userOID);
            httpServletResponse.setHeader(kgProperties.getTokenInHeader(),token);
            long saveTime = KgUtil.toLong(kgProperties.getTokenDefaultExpire());
            kgRedisUtil.set(kgProperties.getTokenPrefixInRedis()+userOID,token,saveTime);
            saveToken(token);
            updateLastLoginAt(userOID);
        }
        else{
            throw new KgResponseException(kgProperties.getCode10001(),kgProperties.getMsg10001());
        }
        return kgResponseJson;
    }
	
    /**
     * 注销登陆
     * @param httpServletRequest http请求
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 15:08
     */
    @Override
    public KgResponseJson logout(HttpServletRequest httpServletRequest){
        KgResponseJson kgResponseJson = new KgResponseJson();
        String token = httpServletRequest.getHeader(kgProperties.getTokenInHeader());
        String userOID = KgJWTUtil.getUserOIDByToken(token);
        if(KgUtil.isNotEmpty(userOID)){
            kgRedisUtil.del(kgProperties.getTokenPrefixInRedis()+userOID);
        }
        return kgResponseJson;
    }	

    /**
     * 交换token
     * @param httpServletRequest http请求
     * @param httpServletResponse http响应
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 15:08
     */
    @Override
    public KgResponseJson changeToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        KgResponseJson kgResponseJson = new KgResponseJson();
        String newToken;
        String token = httpServletRequest.getHeader(kgProperties.getTokenInHeader());
        String userOID = KgJWTUtil.getUserOIDByToken(token);
        if(KgUtil.isNotEmpty(userOID)){
            newToken = KgJWTUtil.createToken(userOID);
            httpServletResponse.setHeader(kgProperties.getTokenInHeader(),newToken);
            long saveTime = KgUtil.toLong(kgProperties.getTokenDefaultExpire());
            kgRedisUtil.set(kgProperties.getTokenPrefixInRedis()+userOID,newToken,saveTime);
            saveToken(newToken);
        }else{
            throw new KgResponseException(kgProperties.getCode10002(),kgProperties.getMsg10002());
        }
        return kgResponseJson;
    }

    /**
     * 保存token
     * @param token token
     * @return void
     * @author
     * @date 2021/8/30 15:09
     */
    private void saveToken(@Valid @NotBlank String token) {
        String createAt = KgUtil.toString(KgDateUtil.utcToTimestamp(KgJWTUtil.getIssuedAt(token)));
        String expiresAt = KgUtil.toString(KgDateUtil.utcToTimestamp(KgJWTUtil.getExpiration(token)));
        String userOID = KgJWTUtil.getUserOIDByToken(token);
        kgAuthDao.saveToken(token,expiresAt,createAt,userOID);
    }

    /**
     * 更新用户最后一次登陆时间
     * @param userOID 用户OID
     * @return void
     * @author
     * @date 2021/8/30 15:09
     */
    private void updateLastLoginAt(@Valid @NotBlank String userOID){
        kgAuthDao.updateLastLoginAt(userOID,System.currentTimeMillis());
    }

    /**
     * 维护个人信息
     * @param kgUser 用户信息
     * @param httpServletRequest http请求
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/11/5 14:02
     */
    @Override
    public KgResponseJson updateUserMy(KgUser kgUser,HttpServletRequest httpServletRequest){
        String userOID = KgUtil.getUserOIDByHttpRequest(httpServletRequest);
        if(!kgUserService.checkUserExist(userOID, 0)){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgUser.setOid(userOID);
        kgUser.setUpdateAt(System.currentTimeMillis());
        kgAuthDao.updateUserMy(kgUser);
        return kgResponseJsonDefault;
    }

    /**
     * 个人修改密码
     * @param httpServletRequest http请求
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/12/24 15:15
     */
    @Override
    public KgResponseJson updateUserPasswordMy(HttpServletRequest httpServletRequest,String oldPassword,String newPassword){
        String userOID = KgUtil.getUserOIDByHttpRequest(httpServletRequest);
        if(!kgUserService.checkUserExist(userOID, 0)){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        KgUser kgUser = kgAuthDao.getUserByOIDMy(userOID);
        oldPassword = KgMD5Util.getMD5(oldPassword);
        newPassword = KgMD5Util.getMD5(newPassword);
        if(kgUser.getuPassword().equals(oldPassword)){
            kgAuthDao.updateUserPasswordMy(userOID,newPassword);
        }else{
            throw new KgResponseException(kgProperties.getCode10004(),"The old password is wrong!");
        }
        return kgResponseJsonDefault;
    }

    /**
     * 用户头像上传
     * @param file 图片文件
     * @param httpServletRequest http请求
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/10/9 15:59
     */
    @Override
    public KgResponseJson updateUserHeadUrlMy(MultipartFile[] file, HttpServletRequest httpServletRequest) {
        KgResponseJson kgResponseJson = new KgResponseJson();
        String userOID = KgUtil.getUserOIDByHttpRequest(httpServletRequest);
        if(!kgUserService.checkUserExist(userOID, 0)){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        String savePath = kgProperties.getUserHeaderPath();
        List<Map<String,String>> nameList = KgUtil.uploadFiles(file,savePath,"HEAD","2");
        for(Map<String,String> map : nameList){
            String visitPath = map.get("visitPath");
            kgAuthDao.updateUserHeadUrlMy(userOID,visitPath);
            map.put("visitPath",visitPath);
        }
        kgResponseJson.putListData(nameList);
        return kgResponseJson;
    }

    /**
     * 获取用户详细信息
     * @param httpServletRequest http请求
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/11/5 11:12
     */
    @Override
    public KgResponseJson getUserDetailMy(HttpServletRequest httpServletRequest){
        KgResponseJson kgResponseJson = new KgResponseJson();
        String userOID = KgUtil.getUserOIDByHttpRequest(httpServletRequest);
        KgUser kgUser = kgAuthDao.getUserByOIDMy(userOID);
        kgResponseJson.putData("user",kgUser);
        List<KgRole> roleList = kgRoleService.getRoleListByUserOID(userOID);
        kgResponseJson.putData("roleList",roleList);
        return kgResponseJson;
    }

    /**
     * 获取用户拥有访问权限的菜单信息（不包括操作）
     * @param httpServletRequest http请求
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/10/7 13:59
     */
    @Override
    public KgResponseJson getSubMenuTreeMy(HttpServletRequest httpServletRequest){
        KgResponseJson kgResponseJson = new KgResponseJson();
        String userOID = KgUtil.getUserOIDByHttpRequest(httpServletRequest);
        if(KgUtil.isNotEmpty(userOID)){
            List<KgSubMenu> subMenuList = kgAuthDao.getSubMenuTreeMy(userOID);
            List<KgSubMenu> subMenuListAdjust = new ArrayList<>();
            for(KgSubMenu kgSubMenu:subMenuList){
                List<KgMenu> kgMenuList = new ArrayList<>();
                for(KgMenu kgMenu: kgSubMenu.getChildren()){
                    if(KgUtil.isNotEmpty(kgMenu.getChildren())){
                        kgMenuList.add(kgMenu);
                    }
                }
                kgSubMenu.setChildren(kgMenuList);
                if(KgUtil.isNotEmpty(kgSubMenu.getChildren())){
                    subMenuListAdjust.add(kgSubMenu);
                }
            }
            kgResponseJson.putListData(subMenuListAdjust);
        }
        return kgResponseJson;
    }

    @Override
    public KgResponseJson getOperateByCommandOIDMy(String commandOID, HttpServletRequest httpServletRequest){
        KgResponseJson kgResponseJson = new KgResponseJson();
        String userOID = KgUtil.getUserOIDByHttpRequest(httpServletRequest);
        List<KgOperate> operateList = kgAuthDao.getOperateByCommandOIDMy(commandOID,userOID);
        kgResponseJson.putListData(operateList);
        return kgResponseJson;
    }

    @Override
    public KgResponseJson getDataIdsByRangeIdMy(String rangeId, HttpServletRequest httpServletRequest){
        KgResponseJson kgResponseJson = new KgResponseJson();
        String userOID = KgUtil.getUserOIDByHttpRequest(httpServletRequest);
        List<KgRole> roleList = kgRoleService.getRoleListByUserOID(userOID);
        List<String> roleOIDList = new ArrayList<>();
        for(KgRole item : roleList){
            roleOIDList.add(item.getOid());
        }
        List<String> dataIdList = kgAuthDao.getDataIdsByRangeIdMy(rangeId, roleOIDList);
        kgResponseJson.putListData(dataIdList);
        return kgResponseJson;
    }
}
