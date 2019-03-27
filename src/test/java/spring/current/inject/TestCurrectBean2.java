package spring.current.inject;

public class TestCurrectBean2 {
    private TestCurrectBean testCurrectBean;

    public TestCurrectBean2(TestCurrectBean testCurrectBean) {
        this.testCurrectBean = testCurrectBean;
    }

    public TestCurrectBean getTestCurrectBean() {
        return testCurrectBean;
    }

    public void setTestCurrectBean(TestCurrectBean testCurrectBean) {
        this.testCurrectBean = testCurrectBean;
    }

    public String play2(){
        return "好厉害";
    }
}
