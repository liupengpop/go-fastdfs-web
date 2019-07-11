package com.perfree.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.perfree.entity.FileResult;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 获取文件列表工具类
 */
public class GetFileUtil {

    /**
     * 获取文件列表
     * @param serverAddress 服务地址
     * @param dir 要获取的目录(根目录为null)
     * @return List<FileResult>
     */
    public static List<FileResult> getDirOrFileList(String showUrl,String serverAddress,String dir){
        HashMap<String, Object> param = new HashMap<>(10);
        if(StrUtil.isNotBlank(dir)){
           param.put("dir",dir);
        }
        String result = HttpUtil.post(serverAddress + GoFastDfsApi.LIST_DIR, param);
        JSONObject parseObj = JSONUtil.parseObj(result);
        List<FileResult> files = new ArrayList<>();
        if(parseObj.getStr("message").equals("") && StrUtil.isNotBlank(parseObj.getStr("data"))) {
            JSONArray parseArray = JSONUtil.parseArray(parseObj.getStr("data"));
            for (int i = 0;i < parseArray.size();i++) {
                FileResult fileResult = new FileResult();
                JSONObject file = JSONUtil.parseObj(parseArray.getStr(i));
                if(file.getStr("name").equals("_big")){
                    continue;
                }
                fileResult.setMd5(file.getStr("md5"));
                fileResult.setPath(file.getStr("path"));
                fileResult.setName(file.getStr("name"));
                fileResult.setIs_dir(file.getBool("is_dir"));
                fileResult.setPeerAddr(showUrl);
                if(file.getBool("is_dir")){
                    fileResult.setSize("0");
                }else{
                    fileResult.setSize(FileSizeUtil.GetLength(Long.valueOf(file.getStr("size"))));
                }
                fileResult.setMTime(DateUtil.timeStamp2Date(file.getStr("mtime"),null));
                files.add(fileResult);
            }
        }
        return files;
    }
}
