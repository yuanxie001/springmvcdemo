package other;

public class InvokeTest {

    public void test1(){
        test(()->{return "lik";});
    }

    public void test(){
        In in = new InImpl();
        in.getName();
    }

    private synchronized void test(In in){
        in.getName();
    }
}
interface In{
    String getName();
}

class InImpl implements In{

    @Override
    public String getName() {
        return "like";
    }
}