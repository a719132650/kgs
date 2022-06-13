package com.kigooo.kgs.service.kgService;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.domain.kgDomain.KgCommand;
import com.kigooo.kgs.domain.kgDomain.KgMenu;
import com.kigooo.kgs.domain.kgDomain.KgOperate;
import com.kigooo.kgs.domain.kgDomain.KgSubMenu;
import org.springframework.validation.annotation.Validated;

@Validated
public interface KgMenuService {

    //subMenu
    KgResponseJson createSubMenu(KgSubMenu kgSubMenu);
    KgResponseJson deleteSubMenu(String subMenuOID);
    KgResponseJson updateSubMenu(KgSubMenu kgSubMenu);
    KgResponseJson updateSubMenuStatus(List<String> subMenuOIDList,int status);
	KgResponseJson updateSubMenuSort(String subMenuOID,int sort);
    List<KgSubMenu> getSubMenuListByMenuOIDs(@Valid @NotEmpty List<String> menuOIDList);
	
    //menu
    KgResponseJson createMenu(KgMenu kgMenu);
    KgResponseJson deleteMenu(String menuOID);
    KgResponseJson updateMenu(KgMenu kgMenu);
    KgResponseJson updateMenuStatus(List<String> menuOIDList,int status);
    KgResponseJson updateMenuSort(KgMenu kgMenu);
    List<KgMenu> getMenuListByCommandOIDs(@Valid @NotEmpty List<String> commandOIDList);

    //command
    KgResponseJson createCommand(KgCommand kgCommand, List<String> roleOIDList,List<String> apiOIDList);
    KgResponseJson deleteCommand(String commandOID);
	KgResponseJson updateCommand(KgCommand kgCommand, List<String>roleOIDList, List<String>apiOIDList);
    KgResponseJson updateCommandStatus(List<String> commandOIDList,int status);
    KgResponseJson updateCommandSort(KgCommand kgCommand);
    KgResponseJson getCommandDetail(String commandOID);
	int getCommandCountByApiOID(@Valid @NotBlank String apiOID,@Valid @NotEmpty List<Integer> status);
    List<KgCommand> getCommandListByOperateOIDs(@Valid @NotEmpty List<String> operateOIDList);
	
    //operate
    KgResponseJson createOperate(KgOperate kgOperate,List<String> roleOIDList);
    KgResponseJson deleteOperate(String operateOID);
	KgResponseJson updateOperate(KgOperate kgOperate,List<String> roleOIDList);
    void updateOperatesWithApi(@Valid @NotEmpty List<String> operateOIDList,@Valid @NotBlank String apiOID);
	void updateOperatesEmptyApi(@Valid @NotEmpty List<String> operateOIDList);
    KgResponseJson updateOperateStatus(List<String> operateOIDList,int status);
    KgResponseJson getOperateDetail(String operateOID);
	KgOperate getOperateByOID(String operateOID);
    List<String> getOperateOIDsByApiOIDs(@Valid @NotEmpty List<String> apiOIDList);
    int getOperateCountByApiOID(@Valid @NotBlank String apiOID,@Valid @NotEmpty List<Integer> statusList);
	List<KgOperate> getOperateListByApiOID(String apiOID);
	
    //tree
    KgResponseJson getSubMenuTreeAll(int init);
}
