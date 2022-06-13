package com.kigooo.kgs.service.kgService.kgImpl;

/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

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
import com.kigooo.kgs.dao.kgDao.KgApiDao;
import com.kigooo.kgs.domain.kgDomain.*;
import com.kigooo.kgs.service.kgService.KgApiService;
import com.kigooo.kgs.service.kgService.KgRelService;
import com.kigooo.kgs.service.kgService.KgMenuService;
import com.kigooo.kgs.util.KgUtil;

@Service
@Validated
public class KgApiImpl implements KgApiService {

    @Autowired
    private KgApiDao kgApiDao;
    @Autowired
    private KgProperties kgProperties;
    @Autowired @Lazy
    private KgMenuService kgMenuService;	
    @Autowired @Lazy
    private KgRelService kgRelService;
    @Autowired
    private Environment env;

    private KgResponseJson kgResponseJsonDefault = new KgResponseJson();
    /**
     * 创建接口
     * @param kgApi 接口对象
     * @param commandOIDList 关联commandOID集合
     * @param operateOIDList 关联operateOID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 13:26
     */
    @Override
    @Transactional
    public KgResponseJson createApi(KgApi kgApi,List<String> commandOIDList,List<String> operateOIDList){
        //checkOperate
        for(String operateOID:operateOIDList){
            KgOperate kgOperate = kgMenuService.getOperateByOID(operateOID);
            if(KgUtil.isNotEmpty(kgOperate.getApiOid())){
                throw new KgResponseException(kgProperties.getCode10006(),kgProperties.getMsg10006());
            }
        }
        KgResponseJson kgResponseJson = new KgResponseJson();
        checkApiLegality(kgApi,null);
        kgApi.setCreateAt(System.currentTimeMillis());
        kgApi.setStatus(1);
        kgApiDao.createApi(kgApi);
        String apiOID = kgApi.getOid();
        if(KgUtil.isNotEmpty(commandOIDList)){
            kgRelService.createCommands2Api(commandOIDList,apiOID);
        }
        if(KgUtil.isNotEmpty(operateOIDList)){
            kgMenuService.updateOperatesWithApi(operateOIDList,apiOID);
        }
        //deal role2api
        if(KgUtil.isNotEmpty(commandOIDList) || KgUtil.isNotEmpty(operateOIDList)){
            kgRelService.resetRole2ApiByApiOIDs(Arrays.asList(apiOID));
        }
        kgResponseJson.putData("api",kgApi);
        return kgResponseJson;
    }

    /**
     * 删除接口
     * @param apiOID 接口OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 13:27
     */
    @Override
    @Transactional
    public KgResponseJson deleteApi(String apiOID){
        if(!checkApiExist(apiOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        List<Integer> statusList = new ArrayList<>(Arrays.asList(0,1));
        if(kgMenuService.getCommandCountByApiOID(apiOID,statusList)>0 || kgMenuService.getOperateCountByApiOID(apiOID,statusList)>0){
            throw new KgResponseException(kgProperties.getCode10004(),"Can't delete this api cause it was related to command or operate!");
        }
        kgApiDao.deleteApi(apiOID,System.currentTimeMillis());
        return kgResponseJsonDefault;
    }

    /**
     * 修改接口信息（不包括状态）
     * @param kgApi 接口对象
     * @param commandOIDList 关联commandOID集合
     * @param operateOIDList 关联operateOID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2022/1/13 14:56
     */
    @Override
    @Transactional
    public KgResponseJson updateApi(KgApi kgApi,List<String> commandOIDList,List<String> operateOIDList){
        if(!checkApiExist(kgApi.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        String apiOID = kgApi.getOid();
        checkApiLegality(kgApi,Arrays.asList(apiOID));
        kgApi.setUpdateAt(System.currentTimeMillis());
        kgApiDao.updateApi(kgApi);
        //command
        List<KgRelationship> command2ApiList = kgRelService.getCommand2ApiByApiOID(apiOID);
        Map<String,List> commandMap = kgRelService.getRelationshipDiff(command2ApiList,commandOIDList,"from");
        if(KgUtil.isNotEmpty(commandMap.get("missRIDs"))){
            kgRelService.deleteCommand2Api(commandMap.get("missRIDs"));
        }
        if(KgUtil.isNotEmpty(commandMap.get("missNewOIDs"))){
            kgRelService.createCommands2Api(commandMap.get("missNewOIDs"),apiOID);
        }
        //operate
        List<String> oldOperateOIDList = kgMenuService.getOperateOIDsByApiOIDs(Arrays.asList(apiOID));
        if(KgUtil.isNotEmpty(oldOperateOIDList)){
            kgMenuService.updateOperatesEmptyApi(oldOperateOIDList);
        }
        if(KgUtil.isNotEmpty(operateOIDList)){
            //chechOperate
            for(String operateOID:operateOIDList){
                KgOperate kgOperate = kgMenuService.getOperateByOID(operateOID);
                if(KgUtil.isNotEmpty(kgOperate.getApiOid())){
                    throw new KgResponseException(kgProperties.getCode10006(),kgProperties.getMsg10006());
                }
            }
            kgMenuService.updateOperatesWithApi(operateOIDList,apiOID);
        }
        kgRelService.resetRole2ApiByApiOIDs(Arrays.asList(apiOID));
        return kgResponseJsonDefault;
    }

    /**
     * 批量修改接口状态
     * @param apiOIDList 接口OIDj集合
     * @param status 状态
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 13:27
     */
    @Override
    public KgResponseJson updateApiStatus(List<String> apiOIDList,int status){
        for(String apiOID : apiOIDList){
            if(!checkApiExist(apiOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        kgApiDao.updateApiStatus(apiOIDList,status);
        if(status == 0){
            kgRelService.deleteRole2ApiByApiOIDs(apiOIDList);
        }else if(status == 1){
            kgRelService.resetRole2ApiByApiOIDs(apiOIDList);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 获取接口详细信息及关联信息
     * @param apiOID 接口OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/10/5 8:40
     */
    @Override
    public KgResponseJson getApiDetail(String apiOID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        KgApi kgApi = kgApiDao.getApiByOID(apiOID);
        kgResponseJson.putData("api",kgApi);
        //command
        Map<String,List<?>> relatedCommandMap = new HashMap<>();
        List<KgRelationship> command2ApiList = kgRelService.getCommand2ApiByApiOID(apiOID);
        List<String> commandOIDList = new ArrayList<>();
        for(KgRelationship kgRelationship : command2ApiList){
            commandOIDList.add(kgRelationship.getFromOID());
        }
        List<KgMenu> menuList = KgUtil.isNotEmpty(commandOIDList)?kgMenuService.getMenuListByCommandOIDs(commandOIDList):new ArrayList<>();
        List<String> menuOIDList = new ArrayList<>();
        for(KgMenu kgMenu : menuList){
            menuOIDList.add(kgMenu.getOid());
        }
        List<KgSubMenu> subMenuList = KgUtil.isNotEmpty(menuOIDList)?kgMenuService.getSubMenuListByMenuOIDs(menuOIDList):new ArrayList<>();
        relatedCommandMap.put("commandList",command2ApiList);
        relatedCommandMap.put("menuList",menuList);
        relatedCommandMap.put("subMenuList",subMenuList);
        kgResponseJson.putData("commandMap",relatedCommandMap);
        //operate
        Map<String,List<?>> relatedOperateMap = new HashMap<>();
        List<KgOperate> operateList = kgMenuService.getOperateListByApiOID(apiOID);
        List<String> operateOIDList = new ArrayList<>();
        for(KgOperate kgOperate : operateList){
            operateOIDList.add(kgOperate.getOid());
        }
        List<KgCommand> commandList = KgUtil.isNotEmpty(operateOIDList)?kgMenuService.getCommandListByOperateOIDs(operateOIDList):new ArrayList<>();
        List<String> commandOIDList2 = new ArrayList<>();
        for(KgCommand kgCommand : commandList){
            commandOIDList2.add(kgCommand.getOid());
        }
        List<KgMenu> menuList2 = KgUtil.isNotEmpty(commandOIDList2)?kgMenuService.getMenuListByCommandOIDs(commandOIDList2):new ArrayList<>();
        List<String> menuOIDList2 = new ArrayList<>();
        for(KgMenu kgMenu : menuList2){
            menuOIDList2.add(kgMenu.getOid());
        }
        List<KgSubMenu> subMenuList2 = KgUtil.isNotEmpty(menuOIDList2)?kgMenuService.getSubMenuListByMenuOIDs(menuOIDList2):new ArrayList<>();
        relatedOperateMap.put("operateList",operateList);
        relatedOperateMap.put("commandList",commandList);
        relatedOperateMap.put("menuList",menuList2);
        relatedOperateMap.put("subMenuList",subMenuList2);
        kgResponseJson.putData("operateMap",relatedOperateMap);
        return kgResponseJson;
    }

    /**
     * 获取接口对象
     * @param apiOID 接口OID
     * @return com.kigooo.kgs.domain.kgDomain.KgApi
     * @author
     * @date 2021/12/22 13:55
     */
    @Override
    public KgApi getApiByOID(String apiOID){
        return kgApiDao.getApiByOID(apiOID);
    }

    @Override
    public List<String> getApiOIDsBySubMenuOIDs(List<String> subMenuOIDList){
        Set<String> apiOIDSet = new HashSet<>();
        apiOIDSet.addAll(kgApiDao.getApiOIDsInCommandBySubMenuOIDs(subMenuOIDList));
        apiOIDSet.addAll(kgApiDao.getApiOIDsInOperateBySubMenuOIDs(subMenuOIDList));
        return new ArrayList<>(apiOIDSet);
    }

    @Override
    public List<String> getApiOIDsByMenuOIDs(List<String> menuOIDList){
        Set<String> apiOIDSet = new HashSet<>();
        apiOIDSet.addAll(kgApiDao.getApiOIDsInCommandByMenuOIDs(menuOIDList));
        apiOIDSet.addAll(kgApiDao.getApiOIDsInOperateByMenuOIDs(menuOIDList));
        return new ArrayList<>(apiOIDSet);
    }

    /**
     * 获取二级菜单下所有ApiOID集合
     * @param commandOIDList 二级菜单OID集合
     * @return java.util.List<java.lang.String>
     * @author
     * @date 2021/10/5 8:38
     */
    @Override
    public List<String> getApiOIDsByCommandOIDs(List<String> commandOIDList){
        return KgUtil.isNotEmpty(commandOIDList)?kgApiDao.getApiOIDsByCommandOIDs(commandOIDList):new ArrayList<>();
    }

    /**
     * 获取操作对应接口信息
     * @param operateOIDList 操作OID集合
     * @return java.util.List<java.lang.String>
     * @author
     * @date 2021/10/5 8:41
     */
    @Override
    public List<String> getApiOIDsByOperateOIDs(List<String> operateOIDList){
        return (KgUtil.isNotEmpty(operateOIDList))?kgApiDao.getApiOIDsByOperateOIDs(operateOIDList):new ArrayList<>();
    }

    /**
     * 获取角色关联二级菜单下接口OID集合
     * @param roleOID 角色主键OID
     * @return java.util.List<java.lang.String>
     * @author
     * @date 2021/8/30 13:51
     */
    private List<String> getApiOIDsInCommandByRoleOID(@Valid @NotBlank String roleOID,@Valid @Positive int status){
        return kgApiDao.getApiOIDsInCommandByRoleOID(roleOID,status);
    }

    /**
     * 获取角色关联二级菜单所有接口OID集合
     * @param roleOID 角色主键OID
     * @return java.util.List<java.lang.String>
     * @author
     * @date 2021/8/30 13:53
     */
    private List<String> getApiOIDsInOperateByRoleOID(@Valid @NotBlank String roleOID,@Valid @Positive int status){
        return kgApiDao.getApiOIDsInOperateByRoleOID(roleOID,status);
    }

    /**
     * 获取角色关联接口OID集合
     * @param roleOID 角色OID
     * @return java.util.List<java.lang.String>
     * @author
     * @date 2021/8/30 13:49
     */
    @Override
    public List<String> getApiOIDsInRealByRoleOID(String roleOID,int status){
        Set<String> apiOIDSet = new HashSet<>();
        apiOIDSet.addAll(getApiOIDsInCommandByRoleOID(roleOID,status));
        apiOIDSet.addAll(getApiOIDsInOperateByRoleOID(roleOID,status));
        return new ArrayList<>(apiOIDSet);
    }

    /**
     * 根据条件检索api
     * @param page 页码
     * @param pageSize 每页记录数
     * @param condition 检索条件
     * @param businessTypeList api接口所属业务类型
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/3 9:48
     */
    @Override
    public KgResponseJson getApiList(int page,int pageSize,String condition,List<String> businessTypeList,int init){
        businessTypeList = KgUtil.isNotEmpty(businessTypeList)?businessTypeList:null;
        KgResponseJson kgResponseJson = new KgResponseJson();
        PageHelper.startPage(page,pageSize);
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):(null);
        List<KgApi> apiList = kgApiDao.getApiList(condition,businessTypeList,init);
        kgResponseJson.putListData(apiList);
        PageInfo<KgApi> pageInfo = new PageInfo<>(apiList);
        kgResponseJson.putPagingData(page,pageSize,pageInfo.getPages(), (int) pageInfo.getTotal());
        return kgResponseJson;
    }

    /**
     * 获取二级菜单下接口集合
     * @param commandOID 二级菜单OID
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgApi>
     * @author
     * @date 2021/12/22 13:57
     */
    @Override
    public List<KgApi> getApiListByCommandOID(String commandOID){
        return kgApiDao.getApiListByCommandOID(commandOID);
    }

    /**
     * 检查接口信息合法性
     * @param kgApi 接口信息
     * @param exceptApiOIDList 排除接口OID集合
     * @return void
     * @author
     * @date 2021/9/23 9:33
     */
    private void checkApiLegality(KgApi kgApi,List<String> exceptApiOIDList){
        if(KgUtil.isEmpty(exceptApiOIDList)){
            if(kgApiDao.checkAPIExist(kgApi.getApi())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The api is already exists");
            }
        }else{
            if(kgApiDao.checkAPIExistExcept(kgApi.getApi(),exceptApiOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The api is already exists");
            }
        }
        if(kgApi.getAuthType()!=0 && kgApi.getAuthType()!=1 && kgApi.getAuthType()!=2){
            throw new KgResponseException(kgProperties.getCode10004(),"Auth Type is illegal");
        }
    }

    private Boolean checkApiExist(String oid,int init){
        KgApi kgApi = kgApiDao.getApiByOID(oid);
        if(KgUtil.isNotEmpty(kgApi) && kgApi.getInitFlag()<=init){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 导出api
     * @param condition
     * @param businessTypeList 
     * @return org.springframework.http.ResponseEntity
     * @author
     * @date 2022/3/21 9:49
     */
    @Override
    public ResponseEntity exportApiList(String condition, List<String> businessTypeList){
        businessTypeList = KgUtil.isNotEmpty(businessTypeList)?businessTypeList:null;
        condition = (KgUtil.isNotEmpty(condition))?("%"+condition+"%"):(null);
        List<KgApi> apiList = kgApiDao.getApiList(condition,businessTypeList,2);
        String [] [] attributes = {{"api","api"},{"description","description"},{"businessType","businessType"},{"authType","authType"},{"status","status"},{"initFlag","initFlag"}};
        return KgExcelUtil.exportToExcelWithDomainList(attributes,apiList);
    }
}
