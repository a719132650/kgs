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
import javax.validation.constraints.Positive;
import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import com.kigooo.kgs.domain.kgDomain.KgApi;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@Validated
public interface KgApiService {
    KgResponseJson createApi(KgApi kgApi,List<String> commandOIDList,List<String> operateOIDList);
    KgResponseJson deleteApi(String apiOID);
    KgResponseJson updateApi(KgApi kgApi,List<String> commandOIDList,List<String> operateOIDList);
    KgResponseJson updateApiStatus(List<String> apiOIDList,int status);
    KgResponseJson getApiDetail(String apiOID);
    KgApi getApiByOID(@Valid @NotBlank String apiOID);
    List<String> getApiOIDsBySubMenuOIDs(@Valid @NotEmpty List<String> subMenuOIDList);
    List<String> getApiOIDsByMenuOIDs(@Valid @NotEmpty List<String> menuOIDList);
    List<String> getApiOIDsByCommandOIDs(@Valid @NotEmpty List<String> commandOIDList);
    List<String> getApiOIDsByOperateOIDs(@Valid @NotEmpty List<String> operateOIDList);
    List<String> getApiOIDsInRealByRoleOID(@Valid @NotBlank String roleOID,@Valid @Positive int status);
    KgResponseJson getApiList(int page,int pageSize,String condition,List<String> businessTypeList,int init);
	List<KgApi> getApiListByCommandOID(@Valid @NotBlank String commandOID);
    ResponseEntity exportApiList(String condition, List<String> businessTypeList);
}
