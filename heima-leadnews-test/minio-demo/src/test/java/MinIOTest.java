import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import io.minio.*;
import io.minio.errors.MinioException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void test() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("/Users/liuyuzhe/workspace/day1/heima-leadnews1/list.html");
        String path = fileStorageService.uploadHtmlFile("", "list2.html", fileInputStream);
        System.out.println("path: " + path);
    }


//        可以上传
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("https://play.min.io")
                            .credentials("Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")
                            .build();

            // Make 'asiatrip' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("asiatrip").build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("asiatrip").build());
            } else {
                System.out.println("Bucket 'asiatrip' already exists.");
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
            ObjectWriteResponse response = minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("asiatrip")
                            .object("plugins/js/axios.min.js")//文件名
                            .contentType("text/js")//文件类型
                            .filename("/Users/liuyuzhe/workspace/day1/heima-leadnews1/index.css")
                            .build());

            System.out.println(" successfully  ");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.getMessage());
        }
    }

//    public static void main(String[] args)
//            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
//        try {
//            // Create a minioClient with the MinIO server playground, its access key and secret key.
//            MinioClient minioClient =
//                    MinioClient.builder()
//                            .endpoint("http://localhost:9000")
//                            .credentials("jTgyWwL859gsVXPobjSM", "av2oUXTxgNuIMctgMnFkwbYq6aL7Wl5pjCo61quj")
//                            .build();
//
//            // Make 'asiatrip' bucket if not exist.
//            boolean found =
//                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("leadnews").build());
//            if (!found) {
//                // Make a new bucket called 'asiatrip'.
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket("leadnews").build());
//            } else {
//                System.out.println("Bucket 'leadnews' already exists.");
//            }
//
//            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
//            // 'asiatrip'.
//            minioClient.uploadObject(
//                    UploadObjectArgs.builder()
//                            .bucket("leadnews")
//                            .object("leadnews.zip")
//                            .filename("/Users/liuyuzhe/workspace/day1/heima-leadnews1/list.html")
//                            .build());
//            System.out.println(
//                    "'/home/user/Photos/asiaphotos.zip' is successfully uploaded as "
//                            + "object 'asiaphotos-2015.zip' to bucket 'asiatrip'.");
//        } catch (MinioException e) {
//            System.out.println("Error occurred: " + e);
//            System.out.println("HTTP trace: " + e.getMessage());
//        }
//    }
}
