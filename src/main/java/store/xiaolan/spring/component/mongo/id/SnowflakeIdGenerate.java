package store.xiaolan.spring.component.mongo.id;


public class SnowflakeIdGenerate extends AbstractIdGenerate<Long> {

    public SnowflakeIdGenerate(final long machineCode) {
        super(machineCode);
    }

    public Long generate() {
        return super.generateLong();
    }
}

