package spring.current.inject;

public class TestCurrectBean {
    private TestCurrectBean2 testCurrectBean;

    public TestCurrectBean(TestCurrectBean2 testCurrectBean) {
        this.testCurrectBean = testCurrectBean;
    }

    public TestCurrectBean2 getTestCurrectBean() {
        return testCurrectBean;
    }

    public void setTestCurrectBean(TestCurrectBean2 testCurrectBean) {
        this.testCurrectBean = testCurrectBean;
    }

    public String play(){
        return "调用成功";
    }
}
