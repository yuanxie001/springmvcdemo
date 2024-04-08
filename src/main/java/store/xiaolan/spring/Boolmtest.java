package store.xiaolan.spring;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class Boolmtest {
    public static void main(String[] args) {

        BloomFilter<String> bloomFilter =
                BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 100L, 0.01);
        bloomFilter.put("123");
        boolean b = bloomFilter.mightContain("1234");
        System.out.println(b);

    }
}
