package com.kigooo.kgs.service.kgService.kgImpl;

/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import com.kigooo.kgs.domain.kgDomain.KgMenu;
import com.kigooo.kgs.domain.kgDomain.KgSubMenu;
import com.kigooo.kgs.service.kgService.KgMenuService;
import com.kigooo.kgs.util.KgExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kigooo.kgs.component.kgResponseException.KgResponseException;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.config.KgProperties;
import com.kigooo.kgs.dao.kgDao.KgRoleDao;
import com.kigooo.kgs.domain.kgDomain.KgRelationship;
import com.kigooo.kgs.domain.kgDomain.KgRole;
import com.kigooo.kgs.service.kgService.KgApiService;
import com.kigooo.kgs.service.kgService.KgRelService;
import com.kigooo.kgs.service.kgService.KgRoleService;
import com.kigooo.kgs.util.KgUtil;

@Service
@Validated
public class KgRoleImpl implements KgRoleService {

    @Autowired
    private KgRoleDao kgRoleDao;
    @Autowired
    private KgProperties kgProperties;
    @Autowired @Lazy
    private KgApiService kgApiService;
    @Autowired @Lazy
    private KgRelService kgRelService;
    @Autowired @Lazy
    private KgMenuService kgMenuService;
    @Autowired
    private Environment env;

    private KgResponseJson kgResponseJsonDefault = new KgResponseJson();
    /**
     * 创建角色并建立关联
     * @param name 角色名
     * @param description 角色描述
     * @param commandOIDList 关联二级菜单OID集合
     * @param operateOIDList 关联操作OID集合
     * @param userOIDList 关联用户OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/10 11:16
     */
    @Override
    @Transactional
    public KgResponseJson createRole(String name,String description,List<String> commandOIDList,List<String> operateOIDList,List<String>userOIDList){
        KgRole kgRole = new KgRole(name,description);
        checkRoleLegality(kgRole,null);
        kgRoleDao.createRole(kgRole,System.currentTimeMillis());
        String roleOID = kgRole.getOid();
        if(KgUtil.isNotEmpty(commandOIDList)){
            kgRelService.createRole2Commands(roleOID,commandOIDList);
        }
        if(KgUtil.isNotEmpty(operateOIDList)){
            kgRelService.createRole2Operates(roleOID,operateOIDList);
        }
        if(KgUtil.isNotEmpty(userOIDList)){
            kgRelService.createUsers2Role(userOIDList,roleOID);
        }
        //deal role2api
        if(KgUtil.isNotEmpty(commandOIDList) || KgUtil.isNotEmpty(operateOIDList)){
            kgRelService.resetRole2ApiByRoleOIDs(Arrays.asList(roleOID));
        }
        return kgResponseJsonDefault;
    }

    /**
     * 删除角色
     * @param roleOID 角色OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:57
     */
    @Override
    @Transactional
    public KgResponseJson deleteRole(String roleOID){
        if(!checkRoleExist(roleOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgRoleDao.deleteRole(roleOID,System.currentTimeMillis());
        kgRelService.deleteRole2OperateByRoleOID(roleOID);
        kgRelService.deleteRole2CommandByRoleOID(roleOID);
        kgRelService.deleteUser2RoleByRoleOID(roleOID);
        kgRelService.deleteRole2ApiByRoleOIDs(Arrays.asList(roleOID));
        return kgResponseJsonDefault;
    }

    /**
     * 修改角色信息
     * @param kgRole 角色信息
     * @param commandOIDList 关联二级菜单OID集合
     * @param operateOIDList 关联操作OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/10/9 15:21
     */
    @Override
    @Transactional
    public KgResponseJson updateRole(KgRole kgRole,List<String> commandOIDList,List<String> operateOIDList){
        if(!checkRoleExist(kgRole.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        String roleOID = kgRole.getOid();
        checkRoleLegality(kgRole,Arrays.asList(roleOID));
        kgRoleDao.updateRole(kgRole,System.currentTimeMillis());
        //command
        Set<String> commandSet = new HashSet<>();
        List<KgRelationship> role2CommandList = kgRelService.getRole2CommandByRoleOID(roleOID);
        Map<String,List> commandMap = kgRelService.getRelationshipDiff(role2CommandList,commandOIDList,"to");
        if(KgUtil.isNotEmpty(commandMap.get("missRIDs"))){
            kgRelService.deleteRole2Command(commandMap.get("missRIDs"));
            commandSet.addAll(commandMap.get("missOIDs"));
        }
        if(KgUtil.isNotEmpty(commandMap.get("missNewOIDs"))){
            kgRelService.createRole2Commands(roleOID,commandMap.get("missNewOIDs"));
            commandSet.addAll(commandMap.get("missNewOIDs"));
        }
        //deal role2api
        if(KgUtil.isNotEmpty(commandSet)){
            kgRelService.resetRole2ApiByCommandOIDs(new ArrayList<>(commandSet));
        }
        //operate
        Set<String> operateSet = new HashSet<>();
        List<KgRelationship> relatedOperateList = kgRelService.getRole2OperateByRoleOID(roleOID);
        Map<String,List> operateMap = kgRelService.getRelationshipDiff(relatedOperateList,operateOIDList,"to");
        if(KgUtil.isNotEmpty(operateMap.get("missRIDs"))){
            kgRelService.deleteRole2Operate(operateMap.get("missRIDs"));
            operateSet.addAll(operateMap.get("missOIDs"));
        }
        if(KgUtil.isNotEmpty(operateMap.get("missNewOIDs"))){
            kgRelService.createRole2Operates(roleOID,operateMap.get("missNewOIDs"));
            operateSet.addAll(operateMap.get("missNewOIDs"));
        }
        //deal role2api
        if(KgUtil.isNotEmpty(operateSet)){
            kgRelService.resetRole2ApiByOperatesOIDs(new ArrayList<>(operateSet));
        }
        return kgResponseJsonDefault;
    }

    /**
     * 修改角色状态
     * @param roleOID 角色OID
     * @param status 状态
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:58
     */
    @Override
    public KgResponseJson updateRoleStatus(String roleOID,int status){
        if(!checkRoleExist(roleOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        kgRoleDao.updateRoleStatus(roleOID,status);
        if(status == 0){
            kgRelService.deleteRole2ApiByRoleOIDs(Arrays.asList(roleOID));
        }else if(status==1){
            kgRelService.resetRole2ApiByRoleOIDs(Arrays.asList(roleOID));
        }
        return kgResponseJsonDefault;
    }

    /**
     * 获取角色详细信息
     * @param roleOID 角色OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/9 8:38
     */
    public KgResponseJson getRoleDetail(String roleOID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        KgRole role = kgRoleDao.getRoleByOID(roleOID);
        kgResponseJson.putData("role",role);
        List<KgRelationship> role2CommandList = kgRelService.getRole2CommandByRoleOID(roleOID);
        kgResponseJson.putData("commandList",role2CommandList);
        List<String> commandOIDList = new ArrayList<>();
        for(KgRelationship item : role2CommandList){
            commandOIDList.add(item.getToOID());
        }
        List<KgMenu> menuList = KgUtil.isNotEmpty(commandOIDList)?kgMenuService.getMenuListByCommandOIDs(commandOIDList):new ArrayList<>();
        kgResponseJson.putData("menuList",menuList);
        List<String> menuOIDList = new ArrayList<>();
        for(KgMenu kgMenu : menuList){
            menuOIDList.add(kgMenu.getOid());
        }
        List<KgSubMenu> subMenuList =KgUtil.isNotEmpty(menuOIDList)?kgMenuService.getSubMenuListByMenuOIDs(menuOIDList):new ArrayList<>();
        kgResponseJson.putData("subMenuList",subMenuList);
        List<KgRelationship> role2OperateList = kgRelService.getRole2OperateByRoleOID(roleOID);
        kgResponseJson.putData("operateList",role2OperateList);
        return kgResponseJson;
    }

    /**
     *  获取所有角色OID集合
     * @return java.util.List<java.lang.String>
     * @author
     * @date 2021/8/30 14:59
     */
    @Override
    public List<String> getRoleOIDs(int status){
        return kgRoleDao.getRoleOIDs(status);
    }

    /**
     * 根据接口获取菜单关联角色
     * @param apiOID 接口OID
     * @return java.util.List<java.lang.String>
     * @author
     * @date 2021/8/30 15:01
     */
    private List<String> getRoleOIDsInCommandByApiOID(@Valid @NotBlank String apiOID,@Valid @Positive int status){
        return kgRoleDao.getRoleOIDsInCommandByApiOID(apiOID,status);
    }

    /**
     * 根据接口获取操作关联角色
     * @param apiOID 接口OID
     * @return java.util.List<java.lang.String>
     * @author
     * @date 2021/8/30 15:01
     */
    private List<String> getRoleOIDsInOperateByApiOID(@Valid @NotBlank String apiOID,@Valid @Positive int status){
        return kgRoleDao.getRoleOIDsInOperateByApiOID(apiOID,status);
    }

    @Override
    public List<String> getRoleOIDsInRealByApiOID(String apiOID,int status){
        Set<String> roleOIDSet = new HashSet<>();
        roleOIDSet.addAll(getRoleOIDsInCommandByApiOID(apiOID,status));
        roleOIDSet.addAll(getRoleOIDsInOperateByApiOID(apiOID,status));
        return new ArrayList<>(roleOIDSet);
    }

    /**
     * 根据条件检索角色信息
     * @param page 页码
     * @param pageSize 每页记录数量
     * @param condition 关键字
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/10 11:03
     */
    @Override
    public KgResponseJson getRoleList(int page,int pageSize,String condition,int init){
        KgResponseJson kgResponseJson = new KgResponseJson();
        PageHelper.startPage(page,pageSize);
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):(null);
        List<KgRole> roleList = kgRoleDao.getRoleList(condition,init);
        PageInfo<KgRole> pageInfo = new PageInfo<>(roleList);
        kgResponseJson.putListData(roleList);
        kgResponseJson.putPagingData(page,pageSize,pageInfo.getPages(),(int)pageInfo.getTotal());
        return kgResponseJson;
    }

    /**
     * 获取用户关联角色信息
     * @param userOID 角色OID
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRole>
     * @author
     * @date 2021/9/23 11:00
     */
    @Override
    public List<KgRole> getRoleListByUserOID(String userOID) {
        return kgRoleDao.getRoleListByUserOID(userOID);
    }

    /**
     * 获取command关联角色集合
     * @param commandOIDList commandOID集合
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRole>
     * @author
     * @date 2021/12/22 13:56
     */
    @Override
    public List<KgRole> getRoleListByCommandOIDs(List<String> commandOIDList){
        return kgRoleDao.getRoleListByCommandOIDs(commandOIDList);
    }

    /**
     * 获取操作关联角色集合
     * @param operateOIDList 操作OID集合
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRole>
     * @author
     * @date 2021/12/22 13:56
     */
    @Override
    public List<KgRole> getRoleListByOperateOIDs(List<String> operateOIDList){
        return kgRoleDao.getRoleListByOperateOIDs(operateOIDList);
    }

    /**
     * 获取接口关联角色
     * @param api 接口请求路径
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgRole>
     * @author
     * @date 2021/9/23 11:00
     */
    @Override
    public List<KgRole> getRoleListByApi(String api){
        return kgRoleDao.getRoleListByApi(api);
    }

    /**
     * 检查角色信息合法性
     * @param kgRole 角色信息
     * @param exceptRoleOIDList 排除接口OID集合
     * @return void
     * @author
     * @date 2021/10/5 9:09
     */
    private void checkRoleLegality(KgRole kgRole, List<String> exceptRoleOIDList){
        if(KgUtil.isEmpty(exceptRoleOIDList)){
            if(kgRoleDao.checkRoleNameExist(kgRole.getName())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of role is already exists!");
            }
        }else{
            if(kgRoleDao.checkRoleNameExistExcept(kgRole.getName(),exceptRoleOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of role is already exists!");
            }
        }
    }

    @Override
    public Boolean checkRoleExist(String oid,int init){
        KgRole kgRole = kgRoleDao.getRoleByOID(oid);
        if(KgUtil.isNotEmpty(kgRole) && kgRole.getInitFlag()<=init){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public ResponseEntity exportRoleList(String condition){
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):(null);
        List<KgRole> roleList = kgRoleDao.getRoleList(condition,0);
        String [] [] attributes = {{"name","name"},{"description","description"},{"status","status"},{"initFlag","initFlag"}};
        return KgExcelUtil.exportToExcelWithDomainList(attributes,roleList);
    }
}
