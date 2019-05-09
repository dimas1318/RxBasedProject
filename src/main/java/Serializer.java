import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.invoke.SerializedLambda;

public class Serializer {

    public  <T> T kryoSerDeser(T r) {
        Kryo kryo = new Kryo();
        kryo.register(Object[].class);
        kryo.register(Class.class);
        kryo.register(SerializedLambda.class);
        kryo.register(ClosureSerializer.Closure.class, new ClosureSerializer());
        kryo.register(Main.class);
        kryo.register(Continuation.class);
        kryo.register(CustContinuation.class);
        kryo.register(ContinuationScope.class);
        kryo.register(int[].class);
        kryo.register(Unsafe.class);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, r);
        }

        try (Input input = new Input((new ByteArrayInputStream(baos.toByteArray())))) {
            return (T) kryo.readClassAndObject(input);
        }
    }
}
