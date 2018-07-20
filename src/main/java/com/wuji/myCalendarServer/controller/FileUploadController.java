package com.wuji.myCalendarServer.controller;

import com.google.common.collect.Lists;
import com.netease.cloud.ClientException;
import com.wuji.myCalendarServer.dto.ResponseResult;
import com.wuji.myCalendarServer.util.NosUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author zhongweichang
 * @date 2018/7/13 上午9:59
 */
@RestController
@RequestMapping("/api/file")
public class FileUploadController extends BaseController {

    /**
     * 上传文件
     *
     * @param response
     * @param request
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseResult upload(HttpServletResponse response, HttpServletRequest request,
                                 @RequestParam(value = "file", required = false) MultipartFile file) {
        ResultDelegate delegate = new ResultDelegate() {

            @Override
            public Object getResultObject() throws Exception {
                if (file == null) {
                    throw new ClientException("文件不能为空");
                }
                return handleUploadFile(file);
            }
        };
        return getResponseResult(request, delegate);
    }

    public String handleUploadFile(MultipartFile multipartFile) throws IOException {
        String savePath = "temp/";
        File file = new File(savePath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
        String fileName = multipartFile.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        List<String> extensions = listExtensionOfPicture();
        if (!extensions.contains(extension)) {
            throw new ClientException("禁止上传以" + extension + "为后缀的文件");
        }
        if(1==1) {
            return "";
        }
        String filePath = file.getAbsolutePath() + "/" + fileName;
        File targetFile = new File(filePath);
        multipartFile.transferTo(targetFile);
        String nosFileName = NosUtil.uploadSmallObject(new File(filePath));
        File tempFile = new File(filePath);
        tempFile.delete();
        return nosFileName;
    }

    public List<String> listExtensionOfPicture() {
        List<String> extensions = Lists.newArrayList();
        extensions.add(".png");
        extensions.add(".jpg");
        extensions.add(".jpeg");
        return extensions;
    }
}