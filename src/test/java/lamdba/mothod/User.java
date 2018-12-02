package lamdba.mothod;

public class User {
    private String name;
    private SexType sex;
    private Integer age;

    public enum SexType{
        MAN(1,"男"),WAMAN(0,"女");

        private int code;
        private String desc;

        SexType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(SexType sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public User() {
    }

    public User(String name, SexType sex, Integer age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
}
