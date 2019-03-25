package lamdba.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PeekCheck {
    private int a;
    private int b;

    public PeekCheck(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public PeekCheck(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public static void main(String[] args) {
        List<PeekCheck> peekCheckList = new ArrayList<>();
        for (int i= 0;i<10;i++){
            peekCheckList.add(new PeekCheck(i));
        }

        peekCheckList.stream().peek(peekCheck -> {peekCheck.setB(10);}).collect(Collectors.toList());

        peekCheckList.forEach(peekCheck -> {
            System.out.println("a:"+peekCheck.a+" b:"+peekCheck.getB());
        });

    }
}

