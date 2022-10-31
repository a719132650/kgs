package com.kigooo.kgs.service.recipesService;

import com.kigooo.kgs.component.kgResponseJson.KgResponseJson;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface RecipesService {
    KgResponseJson uploadPic(MultipartFile[] fileList, HttpServletRequest httpServletRequest);
}
