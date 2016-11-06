public class ByteClassLoader extends ClassLoader {
    public ByteClassLoader(ClassLoader loader) {
        super(loader);
    }

    public Class<?> defineClass(byte[] bytecode) {
        Class<?> clazz = defineClass(null, bytecode, 0, bytecode.length);
        resolveClass(clazz);
        return clazz;
    }
}
