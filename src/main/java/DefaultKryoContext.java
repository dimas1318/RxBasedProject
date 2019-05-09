import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.ByteArrayOutputStream;

public class DefaultKryoContext implements KryoContext {

    private static final int DEFAULT_BUFFER = 1024 * 100;

    private KryoPool pool;

    public static KryoContext newKryoContextFactory(KryoClassRegistrator registrator) {
        return new DefaultKryoContext(registrator);
    }

    private DefaultKryoContext(KryoClassRegistrator registrator) {
        KryoFactory factory = new KryoFactoryImpl(registrator);

        pool = new KryoPool.Builder(factory).softReferences().build();
    }

    private static class KryoFactoryImpl implements KryoFactory {
        private KryoClassRegistrator registrator;

        public KryoFactoryImpl(KryoClassRegistrator registrator) {
            this.registrator = registrator;
        }

        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            registrator.register(kryo);

            return kryo;
        }
    }


    @Override
    public byte[] serialze(Object obj) {
        return serialze(obj, DEFAULT_BUFFER);
    }

    @Override
    public byte[] serialze(Object obj, int bufferSize) {

        Output output = new Output(new ByteArrayOutputStream(), bufferSize);

        Kryo kryo = pool.borrow();

        kryo.writeClassAndObject(output, obj);
        byte[] serialized = output.toBytes();

        pool.release(kryo);

        return serialized;
    }

    @Override
    public Object deserialze(Class clazz, byte[] serialized) {

        Kryo kryo = pool.borrow();

        Input input = new Input(serialized);
        Object obj = kryo.readClassAndObject(input);

        pool.release(kryo);

        return obj;
    }

}