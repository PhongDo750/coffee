package org.example.coffee.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.example.coffee.common.Common;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class CloudinaryHelper {
    public static Cloudinary cloudinary;

    static {
        cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        Common.CLOUDINARY_NAME, Common.CLOUDINARY_NAME_VALUE,
                        Common.CLOUDINARY_API_KEY, Common.CLOUDINARY_API_KEY_VALUE,
                        Common.CLOUDINARY_API_SECRET, Common.CLOUDINARY_API_SECRET_VALUE
                )
        );
        System.out.println("SUCCESS GENERATE INSTANCE FOR CLOUDINARY");
    }

    public static String uploadAndGetFileUrl(MultipartFile multipartFile){
        try {
//            File uploadedFile = convertMultiPartToFile(multipartFile);
            Map uploadResult = cloudinary.uploader().uploadLarge(multipartFile.getInputStream(), ObjectUtils.emptyMap());
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    private static File convertMultiPartToFile(MultipartFile file) throws IOException {
//        File convFile = new File(file.getOriginalFilename());
//        FileOutputStream fos = new FileOutputStream(convFile);
//        fos.write(file.getBytes());
//        fos.close();
//        return convFile;
//    }
}
