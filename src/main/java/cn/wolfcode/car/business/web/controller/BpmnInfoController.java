package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.serivce.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.config.SystemConfig;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.file.FileUploadUtils;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 服务项控制器
 */
@Controller
@RequestMapping("business/bpmnInfo")
public class BpmnInfoController {
    //模板前缀
    private static final String prefix = "business/bpmnInfo/";

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("bpmnInfo:view")
    @RequestMapping("/listPage")
    public String listPage() {
        return prefix + "list";
    }

    @RequiresPermissions("bpmnInfo:deployPage")
    @RequestMapping("/deployPage")
    public String deployPage() {
        // 控制打开一个模态框进行添加操作
        return prefix + "deployPage";
    }


    @RequiresPermissions("bpmnInfo:add")
    @RequestMapping("/addPage")
    public String addPage() {
        return prefix + "add";
    }


    @RequiresPermissions("bpmnInfo:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model) {
        model.addAttribute("bpmnInfo", bpmnInfoService.get(id));
        return prefix + "edit";
    }


    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("bpmnInfo:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo) {
        System.out.println(qo);
        return bpmnInfoService.query(qo);
    }


    //新增
    @RequiresPermissions("bpmnInfo:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BpmnInfo bpmnInfo) {
        bpmnInfoService.save(bpmnInfo);
        return AjaxResult.success();
    }


    //  文件上传逻辑
    @RequiresPermissions("bpmnInfo:/upload")
    @RequestMapping("/upload")
    @ResponseBody
    public AjaxResult upload(MultipartFile file) throws IOException {

        // 获取文件名称 并对文件进行判空
        if (file != null && file.getSize() > 0) {
            // 获取文件的名称 获取后缀名判断是否是bpmn文件
            // 将文件写到路劲中
            String filename = file.getOriginalFilename();
            String ext = FilenameUtils.getExtension(filename);
            // 忽略大小写进行判断
            if (!"bpmn".equalsIgnoreCase(ext)) {
                throw new BusinessException("请传输bpmn文件内容");
            }
            // 将文将写到 D:/upload 该路径中
            String upload = FileUploadUtils.upload(SystemConfig.getUploadPath(), file);

            return AjaxResult.success("文件上传成功", upload);
        } else {
            return AjaxResult.success("不允许传输空的文件");
        }
    }


    @RequiresPermissions("bpmnInfo:deploy")
    @RequestMapping("/deploy")
    @ResponseBody
    public AjaxResult deploy(String path, String bpmnType, String info) throws FileNotFoundException {
        // 发生Ajax请求响应数据回去
        bpmnInfoService.deploy(path, bpmnType, info);
        return AjaxResult.success();
    }

    // /readResource?deploymentId="+row.deploymentId+"&type=xml";
    @RequestMapping("/readResource")
    @ResponseBody
    public void readResource(Long deploymentId, String type, HttpServletResponse response) throws IOException {
        // 根据前端传输过来的类型参数响应对应的内容给前端做展示 png 或者是xml
        InputStream inputStream = bpmnInfoService.readResource(deploymentId,type);
        // 转为对应的二进制文件给到前端
        IOUtils.copy(inputStream,response.getOutputStream());
    }




    //编辑
    @RequiresPermissions("bpmnInfo:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(BpmnInfo bpmnInfo) {
        bpmnInfoService.update(bpmnInfo);
        return AjaxResult.success();
    }

     //流程定义的删除操作
    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResult delete(Long id) {
        bpmnInfoService.delete(id);
        return AjaxResult.success();
    }



    //删除
    @RequiresPermissions("bpmnInfo:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        bpmnInfoService.deleteBatch(ids);
        return AjaxResult.success();
    }


}
