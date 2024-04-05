package store.xiaolan.spring;


import com.alibaba.nacos.shaded.com.google.common.base.Charsets;
import com.alibaba.nacos.shaded.com.google.common.hash.BloomFilter;
import com.alibaba.nacos.shaded.com.google.common.hash.Funnels;

import java.nio.charset.Charset;

public class Boolmtest {
    public static void main(String[] args) {

        BloomFilter<String> bloomFilter =
                BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 100L, 0.01);
        bloomFilter.put("123");
        boolean b = bloomFilter.mightContain("1234");
        System.out.println(b);

    }
}
