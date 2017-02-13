package com.bangduoduo.common.servlet;


import com.bangduoduo.common.RichTextInitListener;
import com.bangduoduo.common.util.Utils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * Created by cpeng2 on 3/3/2015.
 */
@WebServlet(name="upload",urlPatterns = "/ke/upload")
public class FileUploadCommonServlet extends HttpServlet{

    private String location;
    //最大文件大小
    private final long maxSize = 1024 * 1024 * 10;
    private String fileWebPath;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    public static final Map<String,String> extMap = new HashMap<String,String>();
    static {
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("flash", "swf,flv");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
    }
    public void init(ServletConfig config) throws ServletException {
        Object globalHost = config.getServletContext().getAttribute(RichTextInitListener.GLOBAL_WEB_HOST);
        Object globallocation = config.getServletContext().getAttribute(RichTextInitListener.GLOBAL_LOCATION);

        if(globalHost == null || globallocation == null) {
            throw new RuntimeException("找不到富文本配置内容.");
        }
        fileWebPath = globalHost.toString();
        location = globallocation.toString();
        Utils.checkPath(location);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        final String type = req.getParameter("dir");
        String date = Utils.formatDate("yyyy-MM-dd");
        String extFolder = type + File.separator + date;
        String path = location + File.separator + extFolder;
        File file = new File(path);
        PrintWriter writer = resp.getWriter();
        if(!file.exists()) {
            file.mkdirs();
        }
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");
            List items = upload.parseRequest(req);
            Iterator itr = items.iterator();
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                String fileName = item.getName();
                long fileSize = item.getSize();
                if (!item.isFormField()) {
                    //检查文件大小
                    String errorMsg = checkFileItem(item,type);
                    if(StringUtils.isNotBlank(errorMsg)) {
                        writer.write(getError("SYSTEM_ERROR", errorMsg));
                        writer.flush();
                        return;
                    }
                    String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
                    try {
                        File uploadedFile = new File(path, newFileName);
                        item.write(uploadedFile);
                    } catch (Exception e) {
                        writer.write(getError("UPLOAD_FAIL", "上传文件失败"));
                        writer.flush();
                        return;
                    }

                    writer.write(successMessage(fileWebPath + type + "/" + date + "/" + newFileName));
                    writer.flush();
                }
            }
        }catch (Exception e) {
                writer.write(getError("SYSTEM_ERROR","系统错误!"));
                writer.flush();
         } finally {
            writer.close();
        }

    }

    private String successMessage(String url) {
        JSONObject obj = new JSONObject();
        obj.put("error", 0);
        obj.put("url", url);
        return obj.toJSONString();
    }

    private String getError(String code,String message) {
        JSONObject obj = new JSONObject();
        obj.put("error",code);
        obj.put("message",message);
        return obj.toString();
    }

    private String checkUpload(HttpServletRequest request,String savePath){
        if(!ServletFileUpload.isMultipartContent(request)){
            return "请选择文件.";
        }
        //检查目录
        File uploadDir = new File(savePath);
        if(!uploadDir.isDirectory()){
            return "上传目录不存在.";
        }
        //检查目录写权限
        if(!uploadDir.canWrite()){
            return "上传目录没有写权限.";
        }

        String dirName = request.getParameter("dir");
        if (dirName == null) {
            dirName = "image";
        }
        if(!extMap.containsKey(dirName)){
            return "目录名不正确.";
        }
        return null;
    }

    private String checkFileItem(FileItem item, String type) {
            //检查文件大小
            if (item.getSize() > maxSize) {
                return "上传文件大小超过限制.";
            }
            //检查扩展名
            String fileName = item.getName();
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!Arrays.<String>asList(extMap.get(type).split(",")).contains(fileExt)) {
                return "上传文件扩展名是不允许的扩展名.只允许" + extMap.get(type) + "格式.";
            }
            return null;
    }
}
