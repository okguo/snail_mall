package com.okguo.snailmall.thridparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class SnailmallThirdPartyApplicationTests {

	@Autowired
	private OSSClient ossClient;

	@Test
	void testFileUpload() throws FileNotFoundException {
		InputStream inputStream = new FileInputStream("D:\\tools\\1.jpg");
		ossClient.putObject("snailmall-public", "1.jpg", inputStream);
		System.out.println("上传成功");
	}

	@Test
	void contextLoads() {
	}

}
