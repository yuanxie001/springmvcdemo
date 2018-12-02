package lamdba.stream;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * 切分处理测试类
 */

public class WordCounterSpliterator implements Spliterator<Character> {


    private final String string;
    private int currentChar = 0;
    public WordCounterSpliterator(String string) {
        this.string = string;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        //处理当前字符串
        action.accept(string.charAt(currentChar++));
        //如果还有字符串要处理返回true
        return currentChar < string.length();
    }

    /**
     * 主要是这个方法对流进行拆分
     *
     * @return
     */
    @Override
    public Spliterator<Character> trySplit() {

        int currentSize = string.length() - currentChar;
        if (currentSize < 10) {
            //返回null表示字符串足够小，则返回true
            return null;
        }
        //将试探拆分的位置设定为要解析字符串的中间
        for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
            //让拆分位置前进到下一个空格
            if (Character.isWhitespace(string.charAt(splitPos))) {
                //创建一个WordCounterSpliterator从开始到开始拆分的位置
                Spliterator<Character> spliterator =new WordCounterSpliterator(string.substring(currentChar, splitPos));
                //将这个WordCounterSpliterator起始位置设置成要拆分的位置
                currentChar = splitPos;
                return spliterator;
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public long estimateSize() {
        return string.length() - currentChar;
    }

    /**
     * 返回特征值
     * @return
     */
    @Override
    public int characteristics() {
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }


}
