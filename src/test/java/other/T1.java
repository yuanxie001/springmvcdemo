package other;

/**
 * 测试代码，用于检测实例化和类的初始化谁先谁后的问题。
 */
public class T1 {
    /**
     * 如果这两个换了位置，所得到的结果会是什么？
     *
     * t1在前面，输出结果是2
     * t1在后面，输出结果是3
     */
    private static T1 t1 = new T1();
    private static int count = 2;

    public T1(){
        count++;
    }

    public static void main(String[] args) {
        System.out.println(T1.count);
    }

}
