package generics;

import java.util.*;

class Frob{}
class Fnorkle{}
class Quark<Q>{}

public class LostInformation {

    public static void main(String[] args) {
        List<Frob> list = new ArrayList<>();
        Map<Frob,Fnorkle> map = new HashMap<>();

        Quark<Fnorkle> quark = new Quark<>();



        System.out.println(Arrays.toString(list.getClass().getTypeParameters()));
        System.out.println(Arrays.toString(map.getClass().getTypeParameters()));
        System.out.println(Arrays.toString(quark.getClass().getTypeParameters()));


    }

}
