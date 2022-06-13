package com.kigooo.kgs.service.kgService.kgImpl;

/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kigooo.kgs.component.kgResponseException.KgResponseException;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.config.KgProperties;
import com.kigooo.kgs.dao.kgDao.KgRelDao;
import com.kigooo.kgs.domain.kgDomain.*;
import com.kigooo.kgs.service.kgService.*;
import com.kigooo.kgs.util.KgUtil;

@Service
public class KgRelImpl implements KgRelService {

    @Autowired
    private KgRelDao kgRelDao;
    @Autowired
    private KgProperties kgProperties;
    @Autowired @Lazy
    private KgUserService kgUserService;
    @Autowired @Lazy
    private KgRoleService kgRoleService;	
    @Autowired @Lazy
    private KgMenuService kgMenuService;
    @Autowired @Lazy
    private KgApiService kgApiService;
    @Autowired
    private Environment env;

    private KgResponseJson kgResponseJsonDefault = new KgResponseJson();

    //user2role
    /**
     * 建立用户与角色关联（一用户对多角色）
     * @param userOID 用户OID
     * @param roleOIDList 角色OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:02
     */
    @Override
    @Transactional
    public void createUser2Roles(String userOID,List<String> roleOIDList){
        if(!kgUserService.checkUserExist(userOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        for(String roleOID:roleOIDList){
            if(!kgRoleService.checkRoleExist(roleOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        List<String> roleOIDFilter = new ArrayList<>();
        for(String roleOID :roleOIDList){
            if(!checkUser2RoleExist(userOID,roleOID)){
                roleOIDFilter.add(roleOID);
            }
        }
        if(KgUtil.isNotEmpty(roleOIDFilter)){
            long createAt = System.currentTimeMillis();
            kgRelDao.createUser2Roles(userOID,roleOIDFilter,createAt);
        }
    }

    /**
     * 建立角色与用户关联（一角色对多用户）
     * @param roleOID 角色OID
     * @param userOIDList 用户OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:03
     */
    @Override
    @Transactional
    public KgResponseJson createUsers2Role(List<String> userOIDList,String roleOID){
        for(String userOID:userOIDList){
            if(!kgUserService.checkUserExist(userOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        if(!kgRoleService.checkRoleExist(roleOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        List<String> userOIDFiltered = new ArrayList<>();
        for(String userOID: userOIDList){
            if(!checkUser2RoleExist(userOID,roleOID)){
                userOIDFiltered.add(userOID);
            }
        }
        if(KgUtil.isNotEmpty(userOIDFiltered)){
            long createAt = System.currentTimeMillis();
            kgRelDao.createUsers2Role(userOIDFiltered,roleOID,createAt);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 批量删除用户与角色关联
     * @param ridList 关联表主键RID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/2 15:51
     */
    @Override
    public KgResponseJson deleteUser2Role(List<String> ridList){
        if(kgRelDao.checkUser2RoleDoByRids(ridList, Integer.parseInt(env.getProperty("kgs.developInit")))>0){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgRelDao.deleteUser2Role(ridList);
        return kgResponseJsonDefault;
    }

    @Override
    public void deleteUser2RoleByUserOID(String userOID){
        if(!kgUserService.checkUserExist(userOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgRelDao.deleteUser2RoleByUserOID(userOID);
    }

    @Override
    public void deleteUser2RoleByRoleOID(String roleOID){
        if(!kgRoleService.checkRoleExist(roleOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgRelDao.deleteUser2RoleByRoleOID(roleOID);
    }

    /**
     * 获取指定用户与角色关联信息
     * @param userOID 用户OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/2 15:46
     */
    @Override
    public KgResponseJson getUser2RoleByUserOIDForReturn(String userOID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        kgResponseJson.putListData(getUser2RoleByUserOID(userOID));
        return kgResponseJson;
    }

    /**
     * 获取指定用户与角色关联
     * @param userOID 用户OID
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRelationship>
     * @author
     * @date 2021/10/14 9:08
     */
    @Override
    public List<KgRelationship> getUser2RoleByUserOID(String userOID){
        return kgRelDao.getUser2RoleByUserOID(userOID);
    }

    /**
     * 获取指定角色与用户关联
     * @param roleOID 角色OID
     * @param page 页码
     * @param pageSize 每页条数
     * @param condition 检索条件
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/10/14 9:09
     */
    @Override
    public KgResponseJson getUser2RoleByRoleOIDForReturn(String roleOID,int page,int pageSize,String condition){
        KgResponseJson kgResponseJson = new KgResponseJson();
        PageHelper.startPage(page,pageSize);
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):(null);
        List<KgRelationship> relationshipList = kgRelDao.getUser2RoleByRoleOID(roleOID,condition);
        PageInfo<KgRelationship> pageInfo = new PageInfo<>(relationshipList);
        kgResponseJson.putPagingData(page,pageSize,pageInfo.getPages(), (int) pageInfo.getTotal());
        kgResponseJson.putListData(relationshipList);
        return kgResponseJson;
    }

    /**
     * 检查角色与用户关联是否存在
     * @param roleOID 角色OID
     * @param userOID 用户OID
     * @return boolean
     * @author
     * @date 2021/9/23 11:08
     */
    private boolean checkUser2RoleExist(@Valid @NotBlank String userOID,@Valid @NotBlank String roleOID){
        return (kgRelDao.checkUser2RoleExist(userOID,roleOID)>0);
    }

    //role2command
    /**
     * 建立角色与二级菜单关联（一角色对多菜单）
     * @param roleOID 角色OID
     * @param commandOIDList 二级菜单OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:04
     */
    @Override
    @Transactional
    public void createRole2Commands(String roleOID,List<String> commandOIDList){
        List<String> commandOIDFiltered = new ArrayList<>();
        for(String commandOID : commandOIDList){
            if (!checkRole2CommandExist(roleOID,commandOID)){
                commandOIDFiltered.add(commandOID);
            }
        }
        if(KgUtil.isNotEmpty(commandOIDFiltered)){
            kgRelDao.createRole2Commands(roleOID,commandOIDFiltered,System.currentTimeMillis());
        }
    }

    /**
     * 创建二级菜单与角色关联
     * @param commandOID 二级菜单OID
     * @param roleOIDList 角色OID集合
     * @return void
     * @author
     * @date 2021/9/15 13:41
     */
    @Override
    @Transactional
    public void createRoles2Command(List<String> roleOIDList,String commandOID){
        List<String> roleOIDListFiltered = new ArrayList<>();
        for(String roleOID: roleOIDList){
            if (!checkRole2CommandExist(roleOID,commandOID)){
                roleOIDListFiltered.add(roleOID);
            }
        }
        if(KgUtil.isNotEmpty(roleOIDListFiltered)){
            kgRelDao.createRoles2Command(roleOIDListFiltered,commandOID,System.currentTimeMillis());
        }
    }

    /**
     * 批量删除角色与二级菜单关联
     * @param ridList 关联表主键RID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/9 14:20
     */
    @Override
    @Transactional
    public void deleteRole2Command(List<String> ridList){
        kgRelDao.deleteRole2Command(ridList);
    }

    @Override
    public void deleteRole2CommandByRoleOID(String roleOID){
        kgRelDao.deleteRole2CommandByRoleOID(roleOID);
    }

    @Override
    public void deleteRole2CommandByCommandOID(String commandOID){
        kgRelDao.deleteRole2CommandByCommandOID(commandOID);
    }

    /**
     * 获取指定角色与二级菜单关联
     * @param roleOID 角色OID
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRelationship>
     * @author
     * @date 2021/10/14 9:10
     */
    @Override
    public List<KgRelationship> getRole2CommandByRoleOID(String roleOID){
        return kgRelDao.getRole2CommandByRoleOID(roleOID);
    }

    /**
     * 获取指定二级菜单与角色关联
     * @param commandOID 二级菜单OID
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRelationship>
     * @author
     * @date 2021/10/14 9:10
     */
    @Override
    public List<KgRelationship> getRole2CommandByCommandOID(String commandOID){
        return kgRelDao.getRole2CommandByCommandOID(commandOID);
    }

    /**
     * 检查角色与二级菜单关联是否存在
     * @param roleOID 角色OID
     * @param commandOID 二级菜单OID
     * @return boolean
     * @author
     * @date 2021/8/30 14:05
     */
    private boolean checkRole2CommandExist(@Valid @NotBlank String roleOID,@Valid @NotBlank String commandOID){
        return (kgRelDao.checkRole2CommandExist(roleOID,commandOID)>0);
    }

    //role2operate
    /**
     * 建立角色与操作关联
     * @param operateOIDList 操作OID
     * @param roleOID 角色OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:13
     */
    @Override
    @Transactional
    public void createRole2Operates(String roleOID,List<String> operateOIDList){
        kgRelDao.createRole2Operates(roleOID,operateOIDList,System.currentTimeMillis());
    }

    /**
     * 建立操作与角色关联
     * @param operateOID 操作OID
     * @param roleOIDList 角色OID集合
     * @return void
     * @author
     * @date 2021/9/15 16:12
     */
    @Override
    @Transactional
    public void createRoles2Operate(List<String> roleOIDList,String operateOID){
        kgRelDao.createRoles2Operate(roleOIDList,operateOID,System.currentTimeMillis());
    }

    /**
     * 批量删除角色与操作关联
     * @param ridList 关联表主键RID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/9 14:24
     */
    @Override
    public void deleteRole2Operate(List<String> ridList){
        kgRelDao.deleteRole2Operate(ridList);
    }

    @Override
    public void deleteRole2OperateByRoleOID(String roleOID){
        kgRelDao.deleteRole2OperateByRoleOID(roleOID);
    }

    /**
     * 获取指定角色与操作关联
     * @param roleOID 角色OID
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRelationship>
     * @author
     * @date 2021/10/14 9:11
     */
    @Override
    public List<KgRelationship> getRole2OperateByRoleOID(String roleOID){
        return kgRelDao.getRole2OperateByRoleOID(roleOID);
    }

    /**
     * 获取指定操作与角色关联
     * @param operateOID 操作OID
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRelationship>
     * @author
     * @date 2021/10/14 9:11
     */
    @Override
    public List<KgRelationship> getRole2OperateByOperateOID(String operateOID){
        return kgRelDao.getRole2OperateByOperateOID(operateOID);
    }

    //role2range
    /**
     * 建立角色与参数关联（一角色对多参数）
     * @param roleOID 角色OID
     * @param rangeOIDList 参数OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/5/13 14:02
     */
    @Override
    @Transactional
    public KgResponseJson createRole2Ranges(String roleOID,List<String> rangeOIDList){
        if(!kgRoleService.checkRoleExist(roleOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        List<String> rangeOIDFiltered = new ArrayList<>();
        for(String rangeOID: rangeOIDList){
            if(!checkRole2RangeExist(roleOID,rangeOID)){
                rangeOIDFiltered.add(rangeOID);
            }
        }
        if(KgUtil.isNotEmpty(rangeOIDFiltered)){
            long createAt = System.currentTimeMillis();
            kgRelDao.createRole2Ranges(roleOID,rangeOIDFiltered,createAt);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 批量删除角色与参数关联
     * @param ridList 关联表主键RID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/5/13 14:02
     */
    @Override
    public KgResponseJson deleteRole2Range(List<String> ridList){
        kgRelDao.deleteRole2Range(ridList);
        return kgResponseJsonDefault;
    }

    /**
     * 获取指定角色与参数关联信息
     * @param roleOID 角色OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/5/13 14:02
     */
    @Override
    public KgResponseJson getRole2RangeByRoleOID(String roleOID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        kgResponseJson.putListData(kgRelDao.getRole2RangeByRoleOID(roleOID));
        return kgResponseJson;
    }

    /**
     * 检查角色与参数关联是否存在
     * @param roleOID 角色OID
     * @param rangeOID 参数OID
     * @return boolean
     * @author
     * @date 2022/5/13 14:02
     */
    private boolean checkRole2RangeExist(@Valid @NotBlank String roleOID,@Valid @NotBlank String rangeOID){
        return (kgRelDao.checkRole2RangeExist(roleOID,rangeOID)>0);
    }

    //command2api
    /**
     * 创建二级菜单与接口关联
     * @param commandOID 二级菜单OID
     * @param apiOIDList 接口OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/3 10:03
     */
    @Override
    public void createCommand2Apis(String commandOID,List<String> apiOIDList){
        List<String> apiOIDFiltered = new ArrayList<>();
        for(String apiOID : apiOIDList){
            if(!checkCommand2ApiExist(commandOID, apiOID)){
                apiOIDFiltered.add(apiOID);
            }
        }
        if(KgUtil.isNotEmpty(apiOIDFiltered)){
            kgRelDao.createCommand2Apis(commandOID,apiOIDFiltered,System.currentTimeMillis());
        }
    }

    /**
     * 创建二级菜单与接口关联
     * @param commandOIDList 二级菜单OID集合
     * @param apiOID 接口OID
     * @return void
     * @author
     * @date 2021/12/20 10:01
     */
    public void createCommands2Api(List<String> commandOIDList,String apiOID){
        List<String> commandOIDFiltered = new ArrayList<>();
        for(String commandOID: commandOIDList){
            if(!checkCommand2ApiExist(commandOID,apiOID)){
                commandOIDFiltered.add(commandOID);
            }
        }
        if(KgUtil.isNotEmpty(commandOIDFiltered)){
            kgRelDao.createCommands2Api(apiOID,commandOIDFiltered,System.currentTimeMillis());
        }
    }

    /**
     * 删除二级菜单与接口关联
     * @param ridList 二级菜单与接口关联表主键RID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/16 10:47
     */
    @Override
    public void deleteCommand2Api(List<String> ridList){
        kgRelDao.deleteCommand2Api(ridList);
    }

    /**
     * 删除二级菜单与接口关联
     * @param ridList 二级菜单与接口关联表主键RID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/16 10:47
     */
    @Override
    public void deleteCommand2ApiByCommandOID(String commandOID){
        kgRelDao.deleteCommand2ApiByCommandOID(commandOID);
    }

    /**
     * 获取指定二级菜单与接口关联
     * @param commandOID 二级菜单OID集合
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRelationship>
     * @author
     * @date 2021/10/14 9:12
     */
    @Override
    public List<KgRelationship> getCommand2ApiByCommandOID(String commandOID){
        return kgRelDao.getCommand2ApiByCommandOID(commandOID);
    }

    /**
     * 获取指定接口与二级菜单的关联信息
     * @param apiOID 接口OID
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRelationship>
     * @author
     * @date 2021/9/8 13:12
     */
    @Override
    public List<KgRelationship> getCommand2ApiByApiOID(String apiOID){
        return kgRelDao.getCommand2ApiByApiOID(apiOID);
    }

    /**
     * 检查二级菜单与api关联是否存在
     * @param commandOID 二级菜单OID
     * @param apiOID apiOID
     * @return boolean
     * @author
     * @date 2021/9/3 10:03
     */
    private boolean checkCommand2ApiExist(@Valid @NotBlank String commandOID,@Valid @NotBlank String apiOID){
        return (kgRelDao.checkCommand2ApiExist(commandOID,apiOID)>0);
    }

    //role2api
    /**
     * 创建角色与接口关联
     * @param roleOID 角色OID
     * @param apiOIDList 接口OID集合
     * @return void
     * @author
     * @date 2021/9/23 15:34
     */
    private void createRole2Apis(@Valid @NotBlank String roleOID,@Valid @NotEmpty List<String> apiOIDList){
        List<String> apiOIDListFiltered = new ArrayList<>();
        for(String apiOID: apiOIDList){
            if(!checkRole2ApiExist(roleOID,apiOID)){
                apiOIDListFiltered.add(apiOID);
            }
        }
        if(KgUtil.isNotEmpty(apiOIDListFiltered)){
            kgRelDao.createRole2Apis(roleOID,apiOIDListFiltered,System.currentTimeMillis());
        }
    }

    /**
      * 创建角色与接口关联
      * @param roleOIDList 角色OID集合
      * @param apiOID 接口OID
      * @return void
      * @author
      * @date 2021/9/24 15:04
      */
    @Override
    public void createRoles2Api(List<String> roleOIDList,String apiOID){
        List<String> roleOIDListFiltered = new ArrayList<>();
        for(String roleOID: roleOIDList){
            if(!checkRole2ApiExist(roleOID,apiOID)){
                roleOIDListFiltered.add(roleOID);
            }
        }
        if(KgUtil.isNotEmpty(roleOIDListFiltered)){
            kgRelDao.createRoles2Api(roleOIDListFiltered,apiOID,System.currentTimeMillis());
        }
    }

    /**
      * 创建角色与接口关联
      * @param roleOIDList 角色OID集合
      * @param apiOIDList 接口OID集合
      * @return void
      * @author
      * @date 2021/9/23 15:34
     */
    @Override
    public void createRoles2Apis(List<String> roleOIDList,List<String> apiOIDList){
        roleOIDList = KgUtil.isNotEmpty(roleOIDList)?roleOIDList:new ArrayList<>();
        apiOIDList = KgUtil.isNotEmpty(apiOIDList)?apiOIDList:new ArrayList<>();
        List<String> roleOIDListFilter = new ArrayList<>();
        List<String> apiOIDListFilter = new ArrayList<>();
        for(String roleOID : roleOIDList){
            for(String apiOID : apiOIDList){
                if(!checkRole2ApiExist(roleOID,apiOID)){
                    roleOIDListFilter.add(roleOID);
                    apiOIDListFilter.add(apiOID);
                }
            }
        }
        if(roleOIDListFilter.size()>0 && apiOIDListFilter.size()>0){
            kgRelDao.createRoles2Apis(roleOIDListFilter,apiOIDListFilter,System.currentTimeMillis());
        }
    }

    /**
     * 批量删除角色与接口关联
    * @param ridList 关联表主键RID
    * @return void
    * @author
    * @date 2021/9/24 14:42
    */
    private void deleteRole2Api(@Valid @NotEmpty List<String> ridList){
        if(KgUtil.isNotEmpty(ridList)){
            kgRelDao.deleteRole2Api(ridList);
        }
    }

    @Override
    public void deleteRole2ApiByRoleOIDs(List<String> roleOIDList){
        kgRelDao.deleteRole2ApiByRoleOIDs(roleOIDList);
    }

    /**
     * 通过接口删除接口与角色关联
    * @param apiOIDList 接口OID集合
    * @return void
    * @author
    * @date 2021/10/5 9:17
    */
    @Override
    public void deleteRole2ApiByApiOIDs(List<String> apiOIDList){
        kgRelDao.deleteRole2ApiByApiOIDs(apiOIDList);
    }

    /**
     * 通过角色OID获取角色与接口关联RID
    * @param roleOIDList 角色OID集合
    * @return java.util.List<java.lang.String>
    * @author
    * @date 2021/10/5 9:17
    */
    private List<String> getRole2ApiRIDsByRoleOIDs(@Valid @NotEmpty List<String> roleOIDList){
        return kgRelDao.getRole2ApiRIDsByRoleOIDs(roleOIDList);
    }

    /**
     * 获取指定接口与角色关联RID集合
    * @param apiOIDList 接口OID集合
    * @return void
    * @author
    * @date 2021/9/24 14:46
    */
    private List<String> getRole2ApiRIDsByApiOIDs(@Valid @NotEmpty List<String> apiOIDList){
        return kgRelDao.getRole2ApiRIDsByApiOIDs(apiOIDList);
    }

    /**
     * 检查关联是否存在
    * @param apiOID 接口OID
    * @param roleOID 角色OID
    * @return boolean
    * @author
    * @date 2021/8/30 14:13
    */
    private boolean checkRole2ApiExist(@Valid @NotBlank String roleOID,@Valid @NotBlank String apiOID){
        return (kgRelDao.checkRole2ApiExist(roleOID,apiOID) >0) ;
    }

    //reset
    /**
     * 重置角色与接口关联
    * @return void
    * @author
    * @date 2021/8/30 14:15
    */
    @Override
    public void resetRole2Api(){
        kgRelDao.deleteRole2ApiAll();
        List<String> roleOIDs = kgRoleService.getRoleOIDs(1);
        for(String roleOID : roleOIDs){
            List<String> apiOIDList = kgApiService.getApiOIDsInRealByRoleOID(roleOID,1);
            if(KgUtil.isNotEmpty(apiOIDList)){
                kgRelDao.createRole2Apis(roleOID,apiOIDList,System.currentTimeMillis());
            }
        }
    }

    /**
     * 重整指定角色对应接口关联
    * @param roleOIDList 角色OID集合
    * @return void
    * @author
    * @date 2021/10/5 9:03
    */
    @Override
    public void resetRole2ApiByRoleOIDs(List<String> roleOIDList){
        List<String> role2ApiRIDList = getRole2ApiRIDsByRoleOIDs(roleOIDList);
        deleteRole2Api(role2ApiRIDList);
        for(String roleOID :roleOIDList){
            List<String> apiOIDList = kgApiService.getApiOIDsInRealByRoleOID(roleOID,1);
            createRole2Apis(roleOID,apiOIDList);
        }
    }

    /**
     * 重整指定二级菜单对应接口与角色关联
    * @param commandOIDList 二级菜单OID集合
    * @return void
    * @author
    * @date 2021/10/5 9:03
    */
    @Override
    public void resetRole2ApiByCommandOIDs(List<String> commandOIDList){
        List<String> apiOIDList = kgApiService.getApiOIDsByCommandOIDs(commandOIDList);
        if(KgUtil.isNotEmpty(apiOIDList)){
            resetRole2ApiByApiOIDs(apiOIDList);
        }
    }

    /**
     * 重整指定操作对应接口与角色关联
    * @param operateOIDList 接口OID集合
    * @return void
    * @author
    * @date 2021/10/5 9:03
    */
    @Override
    public void resetRole2ApiByOperatesOIDs(List<String> operateOIDList){
        List<String> apiOIDList = kgApiService.getApiOIDsByOperateOIDs(operateOIDList);
        if(KgUtil.isNotEmpty(apiOIDList)){
            resetRole2ApiByApiOIDs(apiOIDList);
        }
    }

    /**
     * 重整指定接口对应角色关联
    * @param apiOIDList 接口OID集合
    * @return void
    * @author
    * @date 2021/10/5 9:03
    */
    @Override
    public void resetRole2ApiByApiOIDs(List<String> apiOIDList){
        List<String> role2ApiRIDList = getRole2ApiRIDsByApiOIDs(apiOIDList);
        deleteRole2Api(role2ApiRIDList);
        for(String apiOID :apiOIDList){
        KgApi kgApi = kgApiService.getApiByOID(apiOID);
            if(kgApi.getStatus() == 1){
                List<String> roleOIDList = kgRoleService.getRoleOIDsInRealByApiOID(apiOID,1);
                if(KgUtil.isNotEmpty(roleOIDList)){
                    createRoles2Api(roleOIDList,apiOID);
                }
            }
        }
    }

    //other
    @Override
    public Map<String,List> getRelationshipDiff(List<KgRelationship> relationshipList, List<String> comparedOIDs, String comparedDirect){
        relationshipList = KgUtil.isNotEmpty(relationshipList)?relationshipList:new ArrayList<>();
        comparedOIDs = KgUtil.isNotEmpty(comparedOIDs)?comparedOIDs:new ArrayList<>();
        Map<String,List> map = new HashMap<>();
        List<KgRelationship> missRelationshipList = new ArrayList<>();
        Set<String> comparedOIDSet = new HashSet<>(comparedOIDs);
        List<String> relObjOIDs = new ArrayList<>();
        List<String> missRIDs = new ArrayList<>();
        List<String> missOIDs = new ArrayList<>();
        for(KgRelationship relationshipItem : relationshipList){
            if(comparedDirect.equals("to")){
                relObjOIDs.add(relationshipItem.getToOID());
                if(!comparedOIDSet.contains(relationshipItem.getToOID())){
                    missRIDs.add(relationshipItem.getRid());
                    missOIDs.add(relationshipItem.getToOID());
                    missRelationshipList.add(relationshipItem);
                }
            }
            else if(comparedDirect.equals("from")){
                relObjOIDs.add(relationshipItem.getFromOID());
                if(!comparedOIDSet.contains(relationshipItem.getFromOID())){
                    missRIDs.add(relationshipItem.getRid());
                    missOIDs.add(relationshipItem.getFromOID());
                    missRelationshipList.add(relationshipItem);
                }
            }
        }
        comparedOIDSet.removeAll(relObjOIDs);
        map.put("missRelationshipList", missRelationshipList); //未匹配到的REL集合
        map.put("missRIDs",new ArrayList<>(missRIDs)); // 未匹配到的RID集合
        map.put("missOIDs",new ArrayList<>(missOIDs)); // 未匹配到的OID集合
        map.put("missNewOIDs",new ArrayList<>(comparedOIDSet)); // 未匹配到的OID集合
        return map;
    }
}
