package other;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

public class MethodHandlesTest {
    public static void main(String[] args) {
        List<Class<?>> list = new ArrayList<>();
        list.add(String.class);
        list.add(int.class);
        MethodType methodType = MethodType.methodType(String.class, list);
        try {

//            MethodHandle test = MethodHandles.lookup().bind(new PrivatePojo(), "test", methodType);
//
//            Object list1 = test.invokeWithArguments("list", 3);
//            System.out.println(list1);

            MethodHandle methodHandle2= MethodHandles.lookup().findVirtual(PrivatePojo.class,"test",methodType);

            Object str2 = methodHandle2.invokeExact(new PrivatePojo(), "str2", 2);

            System.out.println(str2);
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    static class Fater{
        public String get(){
            return  interGet();
        }

        private String interGet(){
            return "fater inter get";
        }
    }

    static class Son extends Fater{
        public String interGet(){
            return "son inter get";
        }
    }

}
