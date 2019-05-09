import com.esotericsoftware.kryo.Kryo;

public interface KryoClassRegistrator {

    void register(Kryo kryo);

}