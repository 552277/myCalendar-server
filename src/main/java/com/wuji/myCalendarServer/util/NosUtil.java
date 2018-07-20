package com.wuji.myCalendarServer.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * nos 常用接口封装
 *
 * @author zhongweichang
 */
public class NosUtil {

    private static String bucketName;
    private static String accessKey;
    private static String secretKey;
    private static String hostName;
    private static NosClient nosClient;

    private static final int SUCCESS_RETURN_CODE = 0;

    private static int SMALL_FILE_MAX_SIZE = 1024 * 1024 * 20; // 50M

    public static Logger logger = LoggerFactory.getLogger(NosUtil.class);

    static {
        ResourceBundle rb = ResourceBundle.getBundle("nos");
        bucketName = rb.getString("bucketName");
        accessKey = rb.getString("accessKey");
        secretKey = rb.getString("secretKey");
        hostName = rb.getString("hostName");
        NosUtil.init(accessKey, secretKey);
    }

    public static void init(String accessKey, String secretKey) {
        nosClient = new NosClient(new BasicCredentials(accessKey, secretKey));

        if (!nosClient.doesBucketExist(bucketName)) {
            nosClient.createBucket(bucketName);
        }
    }

    /**
     * 上传一个小文件(最大支持100M)
     * 
     * @param objectName 文件的唯一标识符,建议用UUID
     * @param file 需要上传的文件
     */
    public static void uploadSmallObject(String objectName, File file) {
        Preconditions.checkNotNull(objectName, "objectName can not be null!");
        Preconditions.checkNotNull(file, "file can not be null!");
        PutObjectResult result = nosClient.putObject(new PutObjectRequest(bucketName, objectName, file));
        if (result.getCallbackRetCode() != SUCCESS_RETURN_CODE) {
            //添加输出日志
            throw new ServiceException(result.getCallbackRetMessage());
        }
    }

    /**
     * 上传一个小文件(最大支持100M)
     * 
     * @param file 需要上传的文件
     * @return 文件在nos中的唯一标识符
     */
    public static String uploadSmallObject(File file) {
        Preconditions.checkNotNull(file, "file can not be null!");
        String objectName = UUID.randomUUID().toString();
        uploadSmallObject(objectName, file);
        return objectName;
    }

    /**
     * 上传一个大文件
     * 
     * @param objectName 文件的唯一标识符
     * @param file 需要上传的文件
     * @throws IOException
     * @throws ClientException
     * @throws com.netease.cloud.ServiceException
     */
    public static void uploadHugeObject(String objectName, File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // 初始化一个分块
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
                objectName);
        InitiateMultipartUploadResult result = nosClient.initiateMultipartUpload(initiateMultipartUploadRequest);
        String uploadId = result.getUploadId();
        int buffSize = 5 * 1024 * 1024;
        byte[] buff = new byte[buffSize];
        int readLen = -1;
        int index = 1;
        try {
            while ((readLen = is.read(buff, 0, buffSize)) != -1) {
                // 每次从流中读取5M数据，并作为分块上传
                InputStream partStream = new ByteArrayInputStream(buff);
                nosClient.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
                        .withInputStream(partStream).withKey(objectName).withPartSize(readLen).withPartNumber(index));
                index++;
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new ServiceException(e.getMessage());
            }
        }

        // 列出所有上传的分块
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, objectName, uploadId);
        PartListing parts = nosClient.listParts(listPartsRequest);
        List<PartETag> partETags = Lists.newArrayList();
        for (PartSummary par : parts.getParts()) {
            partETags.add(new PartETag(par.getPartNumber(), par.getETag()));
        }

        // 完成分块上传
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName,
                objectName, uploadId, partETags);
        CompleteMultipartUploadResult cmuResult = nosClient.completeMultipartUpload(completeMultipartUploadRequest);
        if (cmuResult.getCallbackRetCode() != SUCCESS_RETURN_CODE) {
            logger.info(String.format("upload result : return code %d, return msg %s", cmuResult.getCallbackRetCode(),
                    cmuResult.getCallbackRetMessage()));
            throw new ServiceException(cmuResult.getCallbackRetMessage());
        }
    }

    /**
     * 上传一个大文件
     * 
     * @param file 需要上传的文件
     * @return 文件在nos中的唯一标识符
     * @throws IOException
     */
    public static String uploadHugeObject(File file) throws IOException {
        Preconditions.checkNotNull(file, "file can not be null!");
        String objectName = UUID.randomUUID().toString();
        uploadHugeObject(objectName, file);
        return objectName;
    }

    /**
     * nos object是否存在
     * 
     * @param objectName
     * @return 是否存在
     */
    public static boolean doesObjectExist(String objectName) {
        return nosClient.doesObjectExist(bucketName, objectName);
    }

    /**
     * 封装文件大小上传不同的视频
     *
     * @param file
     * @return
     */
    public static String uploadObject(File file) {
        try {
            return file.length() < SMALL_FILE_MAX_SIZE ? uploadSmallObject(file) : uploadHugeObject(file);
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            return null;
        }
    }

    /**
     * 获取对象的md5
     *
     * @param objectName
     * @return
     */
    public static String getObjectMd5(String objectName) {
        ObjectMetadata om = nosClient.getObjectMetadata(bucketName, objectName);
        return om.getContentMD5();
    }

    /**
     * 删除一个object
     * 
     * @param obejctName object name in nos
     */
    // public static void deleteObject(String objectName) {
    // nosClient.deleteObject(bucketName, objectName);
    // }

    /**
     * 获取公有桶URL
     * 
     * @param objectName object name in nos
     * @return url 地址
     */
    public static String getPublicObjectURL(String objectName) {
        if (Strings.isNullOrEmpty(objectName)) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.startsWith(hostName, "http")) {
            sb.append("https://");
        }
        sb.append(hostName).append("/").append(objectName).append("?x=y");
        return sb.toString();
    }

    /**
     * 拷贝一个obj
     * 
     * @param resObj
     * @param destObj
     */
    public static void copyObject(String resObj, String destObj) {
        nosClient.copyObject(bucketName, resObj, bucketName, destObj);
    }

    public static String getNosObjByNosUrl(String url) {
        if (Strings.isNullOrEmpty(url))
            return null;
        url = url.split("\\?")[0];
        if (Strings.isNullOrEmpty(url))
            return null;

        String[] names = url.split("/");
        return names[names.length - 1];
    }

    /**
     * 获取公有桶Http URL
     *
     * @param objectName object name in nos
     * @return url 地址
     */
    public static String getPublicHttpURL(String objectName) {
        if (Strings.isNullOrEmpty(objectName)) {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(hostName).append("/").append(objectName);
        return sb.toString();
    }

    public static void main(String[] args) {
        // String obj1 = NosUtil.uploadSmallObject(new File("C:/Users/Public/Pictures/Sample Pictures/test.JPG"));
        // System.out.println(obj1);
        System.out.println(NosUtil.getPublicObjectURL("74f60041-18d1-4bab-bd96-a00856af2d49"));
        // String obj2 = NosUtil.uploadSmallObject(new File("/Users/wwlong/Desktop/aaaaa/异形banner@3x.png"));
        System.out.println(NosUtil.doesObjectExist("74f60041-18d1-4bab-bd96-a00856af2d49"));
    }
}
