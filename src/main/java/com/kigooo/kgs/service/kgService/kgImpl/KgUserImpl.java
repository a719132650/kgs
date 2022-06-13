package com.kigooo.kgs.service.kgService.kgImpl;

/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.*;
import com.kigooo.kgs.component.kgRedis.KgRedisUtil;
import com.kigooo.kgs.service.kgService.KgRoleService;
import com.kigooo.kgs.util.KgExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kigooo.kgs.component.kgResponseException.KgResponseException;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.config.KgProperties;
import com.kigooo.kgs.dao.kgDao.KgUserDao;
import com.kigooo.kgs.domain.kgDomain.KgRelationship;
import com.kigooo.kgs.domain.kgDomain.KgUser;
import com.kigooo.kgs.service.kgService.KgRelService;
import com.kigooo.kgs.service.kgService.KgUserService;
import com.kigooo.kgs.util.KgMD5Util;
import com.kigooo.kgs.util.KgUtil;

@Service
public class KgUserImpl implements KgUserService {

    @Autowired
    private KgUserDao kgUserDao;
    @Autowired
    private KgProperties kgProperties;	
    @Autowired @Lazy
    private KgRelService kgRelService;
    @Autowired @Lazy
    private KgRoleService kgRoleService;
    @Autowired
    private KgRedisUtil kgRedisUtil;
    @Autowired
    private Environment env;

    private KgResponseJson kgResponseJsonDefault = new KgResponseJson();

    /**
     * 创建用户（包括授权）
     * @param kgUser 用户信息
     * @param roleOIDList 授权角色OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/6 13:31
     */
    @Override
    @Transactional
    public KgResponseJson createUser(KgUser kgUser,List<String> roleOIDList){
        KgResponseJson kgResponseJson = new KgResponseJson();
        kgUser.setuPassword(KgMD5Util.getMD5(kgUser.getuPassword()));
        kgUser.setCreateAt(System.currentTimeMillis());
        kgUser.setStatus(1);
        kgUser.setHeadUrl("/system/default.png");
        checkUserLegality(kgUser,null);
        kgUserDao.createUser(kgUser);
        kgResponseJson.putData("user",kgUser);
        if(KgUtil.isNotEmpty(roleOIDList)){
            kgRelService.createUser2Roles(kgUser.getOid(),roleOIDList);
        }
        return kgResponseJson;
    }

    /**
     * 删除用户
     * @param userOID 对象OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 15:11
     */
    @Override
    @Transactional
    public KgResponseJson deleteUser(String userOID){
        if(!checkUserExist(userOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgRelService.deleteUser2RoleByUserOID(userOID);
        kgUserDao.deleteUser(userOID,System.currentTimeMillis());
        return kgResponseJsonDefault;
    }

    /**
     * 修改对象
     * @param kgUser 用户对象
     * @param roleOIDList 关联角色OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 15:11
     */
    @Override
    @Transactional
    public KgResponseJson updateUser(KgUser kgUser,List<String> roleOIDList){
        if(!checkUserExist(kgUser.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        checkUserLegality(kgUser,Arrays.asList(kgUser.getOid()));
        kgUser.setUpdateAt(System.currentTimeMillis());
        kgUserDao.updateUser(kgUser);
        List<KgRelationship> user2RoleList = kgRelService.getUser2RoleByUserOID(kgUser.getOid());
        Map<String,List> roleMap = kgRelService.getRelationshipDiff(user2RoleList,roleOIDList,"to");
        List<String> user2roleRIDList = roleMap.get("missRIDs");
        if(KgUtil.isNotEmpty(user2roleRIDList)){
            kgRelService.deleteUser2Role(user2roleRIDList);
        }
        List<String> roleOIDListNew = roleMap.get("missNewOIDs");
        if(KgUtil.isNotEmpty(roleOIDListNew)){
            kgRelService.createUser2Roles(kgUser.getOid(),roleOIDListNew);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 修改用户状态
     * @param userOID 用户OID
     * @param status 状态
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 15:12
     */
    @Override
    public KgResponseJson updateUserStatus(String userOID,int status){
        if(!checkUserExist(userOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgUserDao.updateUserStatus(userOID,status);
        if(status==0){
            kgRedisUtil.del(kgProperties.getTokenPrefixInRedis()+userOID);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 修改密码
     * @param userOID 用户OID
     * @param password 密码
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 15:12
     */
    @Override
    public KgResponseJson updateUserPassword(String userOID,String password){
        if(!checkUserExist(userOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        password = KgMD5Util.getMD5(password);
        kgUserDao.updateUserPassword(userOID,password);
        return kgResponseJsonDefault;
    }

    /**
     * 根据用户OID获取用户信息
     * @param userOID 用户OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 15:13
     */
    @Override
    public KgResponseJson getUserDetail(String userOID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        kgResponseJson.putData("user",kgUserDao.getUserByOID(userOID));
        return kgResponseJson;
    }

    /**
     * 根据账号获取用户信息
     * @param account 账号
     * @return com.kigooo.kgs.domain.kgDomain.KgUser
     * @author
     * @date 2021/8/30 15:10
     */
    @Override
    public KgUser getUserByAccount(String account,int status){
        return kgUserDao.getUserByAccount(account,status);
    }

    /**
     * 分页获取用户信息
     * @param page 页码
     * @param pageSize 每页条数
     * @param condition 检索条件
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/1 13:09
     */
    @Override
    public KgResponseJson getUserList(int page,int pageSize,String condition,int init){
        KgResponseJson kgResponseJson = new KgResponseJson();
        PageHelper.startPage(page,pageSize);
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):null;
        List<KgUser> userList = kgUserDao.getUserList(condition,init);
        PageInfo<KgUser> pageInfo = new PageInfo<>(userList);
        kgResponseJson.putListData(userList);
        kgResponseJson.putPagingData(page,pageSize,pageInfo.getPages(),(int)pageInfo.getTotal());
        return kgResponseJson;
    }

    /**
     * 获取指定角色未关联用户列表
     * @param page 页码
     * @param pageSize 每页条数
     * @param roleOID 角色OID
     * @param condition 检索条件
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/5/12 13:09
     */
    @Override
    public KgResponseJson getUserListNotByRoleOID(int page,int pageSize,String roleOID,String condition,int init){
        KgResponseJson kgResponseJson = new KgResponseJson();
        PageHelper.startPage(page,pageSize);
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):null;
        List<KgUser> userList = kgUserDao.getUserListNotByRoleOID(roleOID,condition,init);
        PageInfo<KgUser> pageInfo = new PageInfo<>(userList);
        kgResponseJson.putListData(userList);
        kgResponseJson.putPagingData(page,pageSize,pageInfo.getPages(),(int)pageInfo.getTotal());
        return kgResponseJson;
    }

    /**
     * 检查用户信息合法性
     * @param kgUser 用户信息
     * @param exceptRoleOIDList 排除用户OID集合
     * @return void
     * @author
     * @date 2021/9/1 13:09
     */
    private void checkUserLegality(KgUser kgUser,List<String> exceptRoleOIDList){
        if(KgUtil.isNotEmpty(exceptRoleOIDList)){
            if(kgUserDao.checkUserAccountExistExcept(kgUser.getuAccount(),exceptRoleOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The account of user is already exists!");
            }
            if(kgUserDao.checkUserNameExistExcept(kgUser.getuName(),exceptRoleOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of user is already exists!");
            }
            if(KgUtil.isNotEmpty(kgUser.getPhone()) && kgUserDao.checkUserPhoneExistExcept(kgUser.getPhone(),exceptRoleOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The phone of user is already exists!");
            }
        }else{
            if(kgUserDao.checkUserAccountExist(kgUser.getuAccount())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The account of user is already exists!");
            }
            if(kgUserDao.checkUserNameExist(kgUser.getuName())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of user is already exists!");
            }
            if(KgUtil.isNotEmpty(kgUser.getPhone()) && kgUserDao.checkUserPhoneExist(kgUser.getPhone())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The phone of user is already exists!");
            }
        }
    }

    @Override
    public Boolean checkUserExist(String oid,int init){
        KgUser kgUser = kgUserDao.getUserByOID(oid);
        if(KgUtil.isNotEmpty(kgUser) && kgUser.getInitFlag()<=init){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public ResponseEntity exportUserList(String condition){
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):null;
        List<KgUser> userList = kgUserDao.getUserList(condition,0);
        String [] [] attributes = {{"uAccount","uAccount"},{"uPassword","Password"},{"sex","sex"},{"status","status"},{"initFlag","initFlag"}};
        return KgExcelUtil.exportToExcelWithDomainList(attributes,userList);
    }
}
