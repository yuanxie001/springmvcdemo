package other;

import java.util.Map;

public class InvokeTest {



    public void test1() {
        test(() -> {
            return "lik";
        });
    }

    public void test() {
        In in = new InImpl();
        in.getName();
    }

    private void test(In in) {
        in.getName();
    }

    private String expection(int length) {
        try {
            int[] ints = new int[15];
            ints[20] = 12;
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("finish");
        }
        return "ok";
    }
}

interface In {
    String t ="123";
    String getName();
}

class InImpl implements In {
    public static String s = "365";
    @Override
    public String getName() {
        return "like";
    }
}

abstract class MyMap implements Map<String,Object>{

}