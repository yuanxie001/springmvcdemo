package other;

public class ClasTest {
    public static void main(String[] args) {
        int i;
        for (i=0;i<100;i++){

        }
    }

    int aligin2grain(int i,int grain){
        return (i+grain-1)& ~(grain-1);
    }

    void whileDouble(){
        double i = 0.0;
        while (i<100.1){
            i++;
        }
    }

    int less(double d){
        if (d < 100.0){
            return 1;
        }else {
            return -1;
        }
    }
}
