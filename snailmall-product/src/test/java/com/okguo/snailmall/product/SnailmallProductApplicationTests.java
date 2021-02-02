package com.okguo.snailmall.product;

import com.okguo.snailmall.product.entity.BrandEntity;
import com.okguo.snailmall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SnailmallProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Autowired
	RedissonClient redisson;

	@Test
	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		brandEntity.setDescript("aaa");
		brandEntity.setName("华为");
		brandService.save(brandEntity);
	}

	@Test
	public void testRedisson(){
		System.out.println(redisson);
	}

}
