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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import com.kigooo.kgs.component.kgResponseException.KgResponseException;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.config.KgProperties;
import com.kigooo.kgs.dao.kgDao.KgMenuDao;
import com.kigooo.kgs.domain.kgDomain.*;
import com.kigooo.kgs.service.kgService.KgApiService;
import com.kigooo.kgs.service.kgService.KgRelService;
import com.kigooo.kgs.service.kgService.KgMenuService;
import com.kigooo.kgs.service.kgService.KgRoleService;
import com.kigooo.kgs.util.KgUtil;

@Service
@Validated
public class KgMenuImpl implements KgMenuService {

    @Autowired
    private KgMenuDao kgMenuDao;
    @Autowired
    private KgProperties kgProperties;
    @Autowired @Lazy
    private KgRoleService kgRoleService;
    @Autowired @Lazy
    private KgApiService kgApiService;
    @Autowired @Lazy
    private KgRelService kgRelService;
    @Autowired
    private Environment env;

    private KgResponseJson kgResponseJsonDefault = new KgResponseJson();

    private static final int step = 10; // sort步长

    // ## subMenu
    /**
     * 创建SubMenu
     * @param kgSubMenu SubMenu对象
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:16
     */
    @Override
    public KgResponseJson createSubMenu(KgSubMenu kgSubMenu){
        checkSubMenuLegality(kgSubMenu,null);
        kgSubMenu.setCreateAt(System.currentTimeMillis());
        kgSubMenu.setStatus(1);
        kgSubMenu.setSort(getMaxSortOfSubMenu()+step);
        kgMenuDao.createSubMenu(kgSubMenu);
        return kgResponseJsonDefault;
    }

    /**
     * 删除SubMenu
     * @param subMenuOID SubMenuOID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:17
     */
    @Override
    @Transactional
    public KgResponseJson deleteSubMenu(String subMenuOID){
        if(!checkSubMenuExist(subMenuOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        if(getMenuCountBySubMenuOID(subMenuOID)>0){
            throw new KgResponseException(kgProperties.getCode10004(),"Can not delete this menu cause exist children!");
        }
        kgMenuDao.deleteSubMenu(subMenuOID,System.currentTimeMillis());
        resetSubMenuSort();
        return kgResponseJsonDefault;
    }

    /**
     * 修改SubMenu
     * @param kgSubMenu SubMenu对象
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:16
     */
    @Override
    public KgResponseJson updateSubMenu(KgSubMenu kgSubMenu){
        if(!checkSubMenuExist(kgSubMenu.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        checkSubMenuLegality(kgSubMenu,Arrays.asList(kgSubMenu.getOid()));
        kgSubMenu.setUpdateAt(System.currentTimeMillis());
        kgMenuDao.updateSubMenu(kgSubMenu);
        return kgResponseJsonDefault;
    }

    /**
     * 修改SubMenu状态
     * @param subMenuOIDList SubMenuOID集合
     * @param status 状态
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:17
     */
    @Override
    public KgResponseJson updateSubMenuStatus(List<String> subMenuOIDList,int status){
        for(String subMenuOID : subMenuOIDList){
            if(!checkSubMenuExist(subMenuOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        kgMenuDao.updateSubMenuStatus(subMenuOIDList,status);
        //deal role2api
        List<String> apiOIDList = KgUtil.isNotEmpty(subMenuOIDList)?kgApiService.getApiOIDsBySubMenuOIDs(subMenuOIDList):new ArrayList<>();
        if(KgUtil.isNotEmpty(apiOIDList)){
            kgRelService.resetRole2ApiByApiOIDs(apiOIDList);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 修改SubMenu序号
     * @param subMenuOID SubMenuOID
     * @param sort 序号
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/10/14 9:25
     */
    @Override
    @Transactional
    public KgResponseJson updateSubMenuSort(String subMenuOID,int sort){
        if(!checkSubMenuExist(subMenuOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        KgSubMenu kgSubMenu = getSubMenuByOID(subMenuOID);
        int oldSort = kgSubMenu.getSort();
        int newSort = sort*10;
        newSort = (newSort > oldSort)?(newSort+1):(newSort-1);
        kgMenuDao.updateSubMenuSort(subMenuOID,newSort);
        resetSubMenuSort();
        return kgResponseJsonDefault;
    }

    /**
     * 根据OID获取SubMenu
     * @param subMenuOID subMenu OID
     * @return com.kigooo.kgs.domain.kgDomain.KgSubMenu
     * @author
     * @date 2021/10/14 9:26
     */
    private KgSubMenu getSubMenuByOID(@Valid @NotBlank String subMenuOID){
        return kgMenuDao.getSubMenuByOID(subMenuOID);
    }

    /**
     * 获取SubMenu序号最大值
     * @return int
     * @author
     * @date 2021/10/14 9:16
     */
    private int getMaxSortOfSubMenu(){
        String sortStr = kgMenuDao.getMaxSortOfSubMenu();
        return KgUtil.isEmpty(sortStr)?0:KgUtil.toInt(sortStr);
    }

    /**
     * 根据MenuOID获取SubMenuList
     * @param menuOIDList menu OID集合
     * @return com.kigooo.kgs.domain.kgDomain.KgSubMenu
     * @author
     * @date 2021/12/14 20:29
     */
    @Override
    public List<KgSubMenu> getSubMenuListByMenuOIDs(List<String> menuOIDList){
        return kgMenuDao.getSubMenuListByMenuOIDs(menuOIDList);
    }

    /**
     * 重整SubMenu顺序
     * @return void
     * @author
     * @date 2022/1/17 15:57
     */
    private void resetSubMenuSort(){
        kgMenuDao.resetSubMenuSort(kgMenuDao.getSubMenuOIDs());
    }

    /**
     * 检查SubMenu合法性（name重复/sort冲突）
     * @param kgSubMenu SubMenu信息
     * @param exceptSubMenuOIDList 排除SubMenuOID集合
     * @return void
     * @author
     * @date 2021/8/30 16:01
     */
    private void checkSubMenuLegality(KgSubMenu kgSubMenu,List<String> exceptSubMenuOIDList){
        if(KgUtil.isNotEmpty(exceptSubMenuOIDList)){
            if(kgMenuDao.checkSubMenuNameExistExcept(kgSubMenu.getName(),exceptSubMenuOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of sub menu is already exists");
            }
        }else{
            if(kgMenuDao.checkSubMenuNameExist(kgSubMenu.getName())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of sub menu is already exists");
            }
        }
    }

    private Boolean checkSubMenuExist(String oid,int init){
        KgSubMenu kgSubMenu = kgMenuDao.getSubMenuByOID(oid);
        if(KgUtil.isNotEmpty(kgSubMenu) && kgSubMenu.getInitFlag()<=init){
            return true;
        }else{
            return false;
        }
    }

    // ## menu
    /**
     * 创建一级菜单
     * @param kgMenu 一级菜单对象
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:21
     */
    @Override
    public KgResponseJson createMenu(KgMenu kgMenu){
        if(!checkSubMenuExist(kgMenu.getSubMenuOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        checkMenuLegality(kgMenu,null);
        kgMenu.setStatus(1);
        kgMenu.setCreateAt(System.currentTimeMillis());
        int sort = getMaxSortOfMenuBySubMenuOID(kgMenu.getSubMenuOid());
        kgMenu.setSort(sort+step);
        kgMenuDao.createMenu(kgMenu);
        return kgResponseJsonDefault;
    }

    /**
     * 删除一级菜单
     * @param menuOID 一级菜单OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:22
     */
    @Override
    @Transactional
    public KgResponseJson deleteMenu(String menuOID){
        if(!checkMenuExist(menuOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        if(getCommandCountByMenuOID(menuOID)>0){
            throw new KgResponseException(kgProperties.getCode10004(),"Can not delete this menu cause exist children!");
        }
        KgMenu kgMenu = getMenuByOID(menuOID);
        kgMenuDao.deleteMenu(menuOID,System.currentTimeMillis());
        resetMenuSortBySubMenuOID(kgMenu.getSubMenuOid());
        return kgResponseJsonDefault;
    }

    /**
     * 修改一级菜单
     * @param kgMenu 一级菜单对象
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:21
     */
    @Override
    public KgResponseJson updateMenu(KgMenu kgMenu){
        if(!checkMenuExist(kgMenu.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        checkMenuLegality(kgMenu,Arrays.asList(kgMenu.getOid()));
        kgMenu.setUpdateAt(System.currentTimeMillis());
        kgMenuDao.updateMenu(kgMenu);
        return kgResponseJsonDefault;
    }

    /**
     * 修改一级菜单状态
     * @param menuOIDList 一级菜单OID集合
     * @param status 状态
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:23
     */
    @Override
    public KgResponseJson updateMenuStatus(List<String> menuOIDList,int status){
        for(String menuOID : menuOIDList){
            if(!checkMenuExist(menuOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        kgMenuDao.updateMenuStatus(menuOIDList,status);
        //deal role2api
        List<String> apiOIDList = KgUtil.isNotEmpty(menuOIDList)?kgApiService.getApiOIDsByMenuOIDs(menuOIDList):new ArrayList<>();
        if(KgUtil.isNotEmpty(apiOIDList)){
            kgRelService.resetRole2ApiByApiOIDs(apiOIDList);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 调整一级菜单所属SubMenu及顺序
     * @param kgMenu 调整后一级菜单信息
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/17 10:30
     */
    @Override
    @Transactional
    public KgResponseJson updateMenuSort(KgMenu kgMenu){
        if(!checkMenuExist(kgMenu.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        KgMenu oldKgMenu = getMenuByOID(kgMenu.getOid());
        if(kgMenu.getSubMenuOid().equals(oldKgMenu.getSubMenuOid())){
            int oldSort = oldKgMenu.getSort();
            int newSort = kgMenu.getSort()*step;
            newSort = (newSort > oldSort)?(newSort+1):(newSort-1);
            kgMenuDao.updateMenuSort(kgMenu.getOid(),newSort);
            resetMenuSortBySubMenuOID(kgMenu.getSubMenuOid());
        }else{
            String oldSubMenuOID = oldKgMenu.getSubMenuOid();
            int newSort = kgMenu.getSort()*step-1;
            kgMenuDao.updateMenuSort(kgMenu.getOid(),newSort);
            kgMenuDao.updateMenuParentOID(kgMenu.getOid(),kgMenu.getSubMenuOid());
            resetMenuSortBySubMenuOID(kgMenu.getSubMenuOid());
            resetMenuSortBySubMenuOID(oldSubMenuOID);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 根据OID获取一级菜单
     * @param menuOID 一级菜单OID
     * @return com.kigooo.kgs.domain.kgDomain.KgMenu
     * @author
     * @date 2021/10/14 9:20
     */
    private KgMenu getMenuByOID(@Valid @NotBlank String menuOID){
        return kgMenuDao.getMenuByOID(menuOID);
    }

    /**
     * 获取指定SubMenu下一级菜单序号最大值
     * @param subMenuOID subMenuOID
     * @return int
     * @author
     * @date 2021/10/14 9:20
     */
    private int getMaxSortOfMenuBySubMenuOID(@Valid @NotBlank String subMenuOID){
        String sortStr =  kgMenuDao.getMaxSortOfMenuBySubMenuOID(subMenuOID);
        return KgUtil.isEmpty(sortStr)?0:KgUtil.toInt(sortStr);
    }

    /**
     * 获取SubMenu下一级菜单数量
     * @param menuOID SubMenu OID
     * @return int
     * @author
     * @date 2021/9/15 13:18
     */
    private int getMenuCountBySubMenuOID(@Valid @NotBlank String menuOID){
        return kgMenuDao.getMenuCountBySubMenuOID(menuOID);
    }

    /**
     * 根据Command OID获取MenuList
     * @param commandOIDList command OID集合
     * @return com.kigooo.kgs.domain.kgDomain.KgSubMenu
     * @author
     * @date 2021/12/14 20:18
     */
    @Override
    public List<KgMenu> getMenuListByCommandOIDs(List<String> commandOIDList){
        return kgMenuDao.getMenuListByCommandOIDs(commandOIDList);
    }

    /**
     * 重整指定SubMenu下Menu顺序
     * @param subMenuOID SubMenuOID
     * @return void
     * @author
     * @date 2022/1/17 16:14
     */
    private void resetMenuSortBySubMenuOID(@Valid @NotBlank String subMenuOID){
        kgMenuDao.resetMenuSort(kgMenuDao.getMenuOIDsBySubMenuOID(subMenuOID));
    }

    /**
     * 检查一级菜单合法性（name重复）
     * @param kgMenu 一级菜单对象
     * @param exceptMenuOIDList 排除MenuOID集合
     * @return void
     * @author
     * @date 2021/8/30 14:27
     */
    private void checkMenuLegality(KgMenu kgMenu,List<String> exceptMenuOIDList){
        if(KgUtil.isNotEmpty(exceptMenuOIDList)){
            if(kgMenuDao.checkMenuNameExistExcept(kgMenu.getName(), kgMenu.getSubMenuOid(),exceptMenuOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of menu is already exists");
            }
        }else{
            if(kgMenuDao.checkMenuNameExist(kgMenu.getName(), kgMenu.getSubMenuOid())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of menu is already exists");
            }
        }
    }

    private Boolean checkMenuExist(String oid,int init){
        KgMenu kgMenu = kgMenuDao.getMenuByOID(oid);
        if(KgUtil.isNotEmpty(kgMenu) && kgMenu.getInitFlag()<=init){
            return true;
        }else{
            return false;
        }
    }

    // ## command
    /**
     * 创建二级菜单并建立与角色关联
     * @param kgCommand 二级菜单信息
     * @param roleOIDList 关联角色OID集合
     * @param apiOIDList 关联接口OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/15 13:40
     */
    @Override
    @Transactional
    public KgResponseJson createCommand(KgCommand kgCommand, List<String> roleOIDList,List<String> apiOIDList){
        if(!checkMenuExist(kgCommand.getMenuOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        checkCommandLegality(kgCommand,null);
        kgCommand.setStatus(1);
        kgCommand.setCreateAt(System.currentTimeMillis());
        kgCommand.setSort(getMaxSortOfCommandByMenuOID(kgCommand.getMenuOid())+step);
        kgMenuDao.createCommand(kgCommand);
        if(KgUtil.isNotEmpty(roleOIDList)){
            kgRelService.createRoles2Command(roleOIDList,kgCommand.getOid());
        }
        if(KgUtil.isNotEmpty(apiOIDList)){
            kgRelService.createCommand2Apis(kgCommand.getOid(),apiOIDList);
        }
        //deal role2api
        if(KgUtil.isNotEmpty(roleOIDList) && KgUtil.isNotEmpty(apiOIDList)){
            kgRelService.createRoles2Apis(roleOIDList, apiOIDList);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 删除二级菜单
     * @param commandOID 二级菜单OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:29
     */
    @Override
    @Transactional
    public KgResponseJson deleteCommand(String commandOID){
        if(!checkCommandExist(commandOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        if(getOperateCountByCommandOID(commandOID)>0){
            throw new KgResponseException(kgProperties.getCode10004(),"Can not delete this menu cause exist children!");
        }
        KgCommand kgCommand = kgMenuDao.getCommandByOID(commandOID);
        Set<String> apiOIDSet = new HashSet<>(kgApiService.getApiOIDsByCommandOIDs(Arrays.asList(commandOID)));
        Set<String> operateApiOIDSet = new HashSet<>(kgMenuDao.getOperateApiOIDsByCommandOIDs(Arrays.asList(commandOID)));
        apiOIDSet.addAll(operateApiOIDSet);
        kgMenuDao.deleteCommand(commandOID,System.currentTimeMillis());
        resetCommandSortByMenuOID(kgCommand.getMenuOid());
        kgRelService.deleteRole2CommandByCommandOID(commandOID);
        kgRelService.deleteCommand2ApiByCommandOID(commandOID);
        //deal role2api
        if(KgUtil.isNotEmpty(apiOIDSet)){
            kgRelService.resetRole2ApiByApiOIDs(new ArrayList<>(apiOIDSet));
        }
        return kgResponseJsonDefault;
    }

    /**
     * 修改二级菜单信息同时修改二级菜单与角色关联
     * @param kgCommand 二级菜单信息
     * @param roleOIDList 关联角色OID集合
     * @param apiOIDList 关联接口OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/16 10:39
     */
    @Override
    @Transactional
    public KgResponseJson updateCommand(KgCommand kgCommand, List<String>roleOIDList, List<String>apiOIDList){
        if(!checkCommandExist(kgCommand.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        checkCommandLegality(kgCommand,Arrays.asList(kgCommand.getOid()));
        kgCommand.setUpdateAt(System.currentTimeMillis());
        kgMenuDao.updateCommand(kgCommand);
        //api
        List<KgRelationship> command2ApiList = kgRelService.getCommand2ApiByCommandOID(kgCommand.getOid());
        Map<String,List> apiMap = kgRelService.getRelationshipDiff(command2ApiList,apiOIDList,"to");
        if(KgUtil.isNotEmpty(apiMap.get("missRIDs"))){
            kgRelService.deleteCommand2Api(apiMap.get("missRIDs"));
        }
        if(KgUtil.isNotEmpty(apiMap.get("missNewOIDs"))){
            kgRelService.createCommand2Apis(kgCommand.getOid(),apiMap.get("missNewOIDs"));
        }
        //role
        List<KgRelationship> role2CommandList = kgRelService.getRole2CommandByCommandOID(kgCommand.getOid());
        Map<String,List> roleMap = kgRelService.getRelationshipDiff(role2CommandList,roleOIDList,"to");
        if(KgUtil.isNotEmpty(roleMap.get("missRIDs"))){
            kgRelService.deleteRole2Command(roleMap.get("missRIDs"));
        }
        if(KgUtil.isNotEmpty(roleMap.get("missNewOIDs"))){
            kgRelService.createRoles2Command(roleMap.get("missNewOIDs"),kgCommand.getOid());
        }
        //deal role2api
        kgRelService.resetRole2ApiByCommandOIDs(Arrays.asList(kgCommand.getOid()));
        return kgResponseJsonDefault;
    }

    /**
     * 修改二级菜单状态
     * @param commandOIDList 二级菜单OID集合
     * @param status 状态
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:29
     */
    @Override
    public KgResponseJson updateCommandStatus(List<String> commandOIDList,int status){
        for(String commandOID : commandOIDList){
            if(!checkCommandExist(commandOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        if(KgUtil.isNotEmpty(commandOIDList)){
            kgMenuDao.updateCommandStatus(commandOIDList,status);
            //deal role2api
            kgRelService.resetRole2ApiByCommandOIDs(commandOIDList);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 调整二级菜单所属一级菜单及顺序
     * @param kgCommand 二级菜单信息
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/17 10:37
     */
    @Override
    @Transactional
    public KgResponseJson updateCommandSort(KgCommand kgCommand){
        if(!checkCommandExist(kgCommand.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        KgCommand oldKgCommand = kgMenuDao.getCommandByOID(kgCommand.getOid());
        if(kgCommand.getMenuOid().equals(oldKgCommand.getMenuOid())){
            int oldSort = oldKgCommand.getSort();
            int newSort = kgCommand.getSort()*step;
            newSort = (newSort > oldSort)?(newSort+1):(newSort-1);
            kgMenuDao.updateCommandSort(kgCommand.getOid(),newSort);
            resetCommandSortByMenuOID(kgCommand.getMenuOid());
        }else{
            int newSort = kgCommand.getSort()*step-1;
            kgMenuDao.updateCommandSort(kgCommand.getOid(),newSort);
            kgMenuDao.updateCommandParentOID(kgCommand.getOid(),kgCommand.getMenuOid());
            resetCommandSortByMenuOID(kgCommand.getMenuOid());
            resetCommandSortByMenuOID(oldKgCommand.getMenuOid());
        }
        return kgResponseJsonDefault;
    }

    /**
     * 获取command详细信息
     * @param commandOID commandOID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/12/22 13:57
     */
    @Override
    public KgResponseJson getCommandDetail(String commandOID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        KgCommand kgCommand = kgMenuDao.getCommandByOID(commandOID);
        kgResponseJson.putData("command",kgCommand);
        List<KgApi> apiList = kgApiService.getApiListByCommandOID(commandOID);
        kgResponseJson.putData("apiList",apiList);
        List<KgRole> roleList = kgRoleService.getRoleListByCommandOIDs(Arrays.asList(commandOID));
        kgResponseJson.putData("roleList",roleList);
        return kgResponseJson;
    }

    /**
     * 获取指定一级菜单下二级菜单最大序号
     * @param menuOID 一级菜单OID
     * @return int
     * @author
     * @date 2021/10/14 9:22
     */
    private int getMaxSortOfCommandByMenuOID(@Valid @NotBlank String menuOID){
        String sortStr = kgMenuDao.getMaxSortOfCommandByMenuOID(menuOID);
        return KgUtil.isEmpty(sortStr)?0:KgUtil.toInt(sortStr);
    }

    /**
     * 获取一级菜单下二级菜单数量
     * @param menuOID 一级菜单OID
     * @return int
     * @author
     * @date 2021/9/15 13:18
     */
    private int getCommandCountByMenuOID(@Valid @NotBlank String menuOID){
        return kgMenuDao.getCommandCountByMenuOID(menuOID);
    }

    /**
     * 接口与二级菜单关联数量
     * @param apiOID 接口OID
     * @param statusList 状态集合
     * @return int
     * @author
     * @date 2021/10/8 8:31
     */
    @Override
    public int getCommandCountByApiOID(String apiOID, List<Integer> statusList){
        return kgMenuDao.getCommandCountByApiOID(apiOID, statusList);
    }

    /**
     * 根据操作获取commandList
     * @param operateOIDList 操作OID集合
     * @return java.util.List<com.kigooo.kgs.domain.kgDomain.KgCommand>
     * @author
     * @date 2021/12/22 13:50
     */
    @Override
    public List<KgCommand> getCommandListByOperateOIDs(List<String> operateOIDList){
        return kgMenuDao.getCommandListByOperateOIDs(operateOIDList);
    }

    /**
     * 重整Menu下Command顺序
     * @param menuOID menuOID
     * @return void
     * @author
     * @date 2022/1/18 9:08
     */
    private void resetCommandSortByMenuOID(@Valid @NotBlank String menuOID){
        List<String> commandOIDList = kgMenuDao.getCommandOIDsByMenuOID(menuOID);
        if(KgUtil.isNotEmpty(commandOIDList)){
            kgMenuDao.resetCommandSort(commandOIDList);
        }
    }

    /**
     * 检查二级菜单合法性（name重复/url重复）
     * @param kgCommand 二级菜单对象
     * @param exceptMenuOIDList 排除二级菜单OID集合
     * @return void
     * @author
     * @date 2021/8/30 14:31
     */
    private void checkCommandLegality(KgCommand kgCommand,List<String> exceptMenuOIDList){
        if(KgUtil.isEmpty(exceptMenuOIDList)){
            if(kgMenuDao.checkCommandNameExist(kgCommand.getName(), kgCommand.getMenuOid())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of menu is already exists");
            }
            if(kgMenuDao.checkCommandUrlExist(kgCommand.getUrl(), kgCommand.getMenuOid())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The url of menu is already exists");
            }
        }else{
            if(kgMenuDao.checkCommandNameExistExcept(kgCommand.getName(), kgCommand.getMenuOid(),exceptMenuOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of menu is already exists");
            }
            if(kgMenuDao.checkCommandUrlExistExcept(kgCommand.getUrl(), kgCommand.getMenuOid(),exceptMenuOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The url of menu is already exists");
            }
        }
    }

    private Boolean checkCommandExist(String oid,int init){
        KgCommand kgCommand = kgMenuDao.getCommandByOID(oid);
        if(KgUtil.isNotEmpty(kgCommand) && kgCommand.getInitFlag()<=init){
            return true;
        }else{
            return false;
        }
    }

    // #operate
    /**
     * 创建操作并建立对应接口与角色关联
     * @param kgOperate 操作信息
     * @param roleOIDList 角色OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/15 13:53
     */
    @Override
    public KgResponseJson createOperate(KgOperate kgOperate,List<String> roleOIDList){
        if(!checkCommandExist(kgOperate.getCommandOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        checkOperateLegality(kgOperate,null);
        kgOperate.setCreateAt(System.currentTimeMillis());
        if(KgUtil.isEmpty(kgOperate.getApiOid())){
            kgOperate.setApiOid(null);
        }
        kgMenuDao.createOperate(kgOperate);
        if(KgUtil.isNotEmpty(roleOIDList)){
            kgRelService.createRoles2Operate(roleOIDList,kgOperate.getOid());
        }
        //deal role2api
        if(KgUtil.isNotEmpty(roleOIDList) && KgUtil.isNotEmpty(kgOperate.getApiOid())){
            kgRelService.createRoles2Api(roleOIDList,kgOperate.getApiOid());
        }
        return kgResponseJsonDefault;
    }

    /**
     * 删除操作
     * @param operateOID 操作OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:39
     */
    @Override
    public KgResponseJson deleteOperate(String operateOID){
        if(!checkOperateExist(operateOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        KgOperate kgOperate = getOperateByOID(operateOID);
        kgMenuDao.deleteOperate(operateOID,System.currentTimeMillis());
        //deal role2api
        if(KgUtil.isNotEmpty(kgOperate.getApiOid())){
            kgRelService.resetRole2ApiByApiOIDs(Arrays.asList(kgOperate.getApiOid()));
        }
        return kgResponseJsonDefault;
    }

    /**
     * 修改操作信息并修改操作与角色关联
     * @param kgOperate 操作信息
     * @param roleOIDList 关联角色OID集合
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/16 11:09
     */
    @Override
    public KgResponseJson updateOperate(KgOperate kgOperate,List<String> roleOIDList){
        if(!checkOperateExist(kgOperate.getOid(), Integer.parseInt(env.getProperty("kgs.developInit")))){
            throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
        }
        checkOperateLegality(kgOperate,Arrays.asList(kgOperate.getOid()));
        kgOperate.setUpdateAt(System.currentTimeMillis());
        KgOperate oldKgOperate = getOperateByOID(kgOperate.getOid());
        if(KgUtil.isEmpty(oldKgOperate.getApiOid())){
            oldKgOperate.setApiOid(null);
        }
        if(KgUtil.isEmpty(kgOperate.getApiOid())){
            oldKgOperate.setApiOid(null);
        }
        kgMenuDao.updateOperate(kgOperate);
        //role
        List<KgRelationship> role2OperateList = kgRelService.getRole2OperateByOperateOID(kgOperate.getOid());
        Map<String,List> apiMap = kgRelService.getRelationshipDiff(role2OperateList,roleOIDList,"to");
        if(KgUtil.isNotEmpty(apiMap.get("missRIDs"))){
            kgRelService.deleteRole2Operate(apiMap.get("missRIDs"));
        }
        if(KgUtil.isNotEmpty(apiMap.get("missNewOIDs"))){
            kgRelService.createRoles2Operate(apiMap.get("missNewOIDs"),kgOperate.getOid());
        }
        
        if(!(KgUtil.isEmpty(kgOperate.getApiOid())&&KgUtil.isEmpty(oldKgOperate.getApiOid()))){
            Set<String> apiOIDSet = new HashSet<>();
            if(KgUtil.isNotEmpty(kgOperate.getApiOid())){
                apiOIDSet.add(kgOperate.getApiOid());
            }
            if(KgUtil.isNotEmpty(oldKgOperate.getApiOid())){
                apiOIDSet.add(oldKgOperate.getApiOid());
            }
            //deal role2api
            if(KgUtil.isNotEmpty(apiOIDSet)){
                kgRelService.resetRole2ApiByApiOIDs(new ArrayList<>(apiOIDSet));
            }
        }
        return kgResponseJsonDefault;
    }

    /**
     * 清空操作对应接口
     * @param operateOIDList 操作OID集合
     * @return void
     * @author
     * @date 2021/12/22 13:41
     */
    @Override
    public void updateOperatesEmptyApi(List<String> operateOIDList){
        for(String operateOID : operateOIDList){
            if(!checkOperateExist(operateOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        kgMenuDao.updateOperatesEmptyApi(operateOIDList);
    }

    /**
     * 修改操作对应接口
     * @param operateOIDList 操作OID集合
     * @param apiOID 接口OID
     * @return void
     * @author
     * @date 2021/12/22 13:38
     */
    @Override
    public void updateOperatesWithApi(List<String> operateOIDList,String apiOID){
        for(String operateOID : operateOIDList){
            if(!checkOperateExist(operateOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        for(String operateOID : operateOIDList){
            kgMenuDao.updateOperateApi(operateOID,apiOID);
        }
    }

    /**
     * 修改操作状态
     * @param operateOIDList 操作OID集合
     * @param status 状态
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/8/30 14:38
     */
    @Override
    public KgResponseJson updateOperateStatus(List<String> operateOIDList,int status){
        for(String operateOID : operateOIDList){
            if(!checkOperateExist(operateOID, Integer.parseInt(env.getProperty("kgs.developInit")))){
                throw new KgResponseException(kgProperties.getCode10006(), kgProperties.getMsg10006());
            }
        }
        kgMenuDao.updateOperateStatus(operateOIDList,status);
        //deal role2api
        List<String> apiOIDList = kgApiService.getApiOIDsByOperateOIDs(operateOIDList);
        if(KgUtil.isNotEmpty(apiOIDList)){
            kgRelService.resetRole2ApiByApiOIDs(apiOIDList);
        }
        return kgResponseJsonDefault;
    }

    /**
     * 获取操作详细信息
     * @param operateOID 操作OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/12/22 13:54
     */
    @Override
    public KgResponseJson getOperateDetail(String operateOID){
        KgResponseJson kgResponseJson = new KgResponseJson();
        KgOperate kgOperate = kgMenuDao.getOperateByOID(operateOID);
        kgResponseJson.putData("operate",kgOperate);
        KgApi kgApi = (KgUtil.isNotEmpty(kgOperate.getApiOid()))?kgApiService.getApiByOID(kgOperate.getApiOid()):null;
        kgResponseJson.putData("api",kgApi);
        List<KgRole> roleList = kgRoleService.getRoleListByOperateOIDs(Arrays.asList(operateOID));
        kgResponseJson.putData("roleList",roleList);
        return kgResponseJson;
    }

    /**
     * 通过OID获取操作信息
     * @param operateOID 操作OID
     * @return com.kigooo.kgs.domain.kgDomain.KgOperate
     * @author
     * @date 2021/8/30 14:38
     */
    @Override
    public KgOperate getOperateByOID(String operateOID){
        return kgMenuDao.getOperateByOID(operateOID);
    }

    /**
     * 获取二级菜单下操作
     * @param apiOIDList 二级菜单OID集合
     * @return java.util.List<java.lang.String> 操作OID集合
     * @author
     * @date 2021/8/30 14:45
     */
    public List<String> getOperateOIDsByApiOIDs(List<String> apiOIDList){
        return kgMenuDao.getOperateOIDsByApiOIDs(apiOIDList);
    }

    /**
     * 获取二级菜单下操作数量
     * @param commandOID 二级菜单OID
     * @return int
     * @author
     * @date 2021/9/15 13:12
     */
    private int getOperateCountByCommandOID(@Valid @NotBlank String commandOID){
        return kgMenuDao.getOperateCountByCommandOID(commandOID);
    }

    /**
     * 接口与操作关联数量
     * @param apiOID 接口OID
     * @param statusList 状态集合
     * @return int
     * @author
     * @date 2021/10/8 8:31
     */
    @Override
    public int getOperateCountByApiOID(String apiOID,List<Integer> statusList){
        return kgMenuDao.getOperateCountByApiOID(apiOID,statusList);
    }

    /**
     * 根据接口获取对应操作
     * @param apiOID 接口OID
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/9/3 11:11
     */
    @Override
    public List<KgOperate> getOperateListByApiOID(String apiOID){
        return kgMenuDao.getOperateListByApiOID(apiOID);
    }

    /**
     * 检查操作信息合法性（name重复/api重复）
     * @param kgOperate 操作对象
     * @param exceptOperateOIDList 排除操作OID集合
     * @return void
     * @author
     * @date 2021/8/30 14:39
     */
    private void checkOperateLegality(KgOperate kgOperate,List<String> exceptOperateOIDList){
        if(kgOperate.getApiOid()==null){
            throw new KgResponseException(kgProperties.getCode10004(),"The api of operate can't be empty!");
        }
        if(exceptOperateOIDList!=null && exceptOperateOIDList.size()>0){
            if(kgMenuDao.checkOperateNameExistExcept(kgOperate.getName(),kgOperate.getCommandOid(),exceptOperateOIDList)>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of operate is already exists!");
            }
        }else{
            if(kgMenuDao.checkOperateNameExist(kgOperate.getName(),kgOperate.getCommandOid())>0){
                throw new KgResponseException(kgProperties.getCode10004(),"The name of operate is already exists!");
            }
        }
    }

    private Boolean checkOperateExist(String oid,int init){
        KgOperate kgOperate = kgMenuDao.getOperateByOID(oid);
        if(KgUtil.isNotEmpty(kgOperate) && kgOperate.getInitFlag()<=init){
            return true;
        }else{
            return false;
        }
    }

    // #tree
    /**
     * 获取全部菜单信息（包括操作）
     * @return com.kigooo.kgs.component.kgResponseJson.KgResponseJson
     * @author
     * @date 2021/10/7 13:59
     */
    @Override
    public KgResponseJson getSubMenuTreeAll(int init){
        KgResponseJson kgResponseJson = new KgResponseJson();
        List<KgSubMenu> subMenuList = kgMenuDao.getSubMenuTreeAll(init);
        kgResponseJson.putListData(subMenuList);
        return kgResponseJson;
    }
}
