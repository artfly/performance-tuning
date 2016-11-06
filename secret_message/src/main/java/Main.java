import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {
    private static final String SECURITY_METHOD_NAME = "getSecurityMessage";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Error : no path specified.");
            System.exit(1);
        }

        File path = new File(args[0]);
        ByteClassLoader byteLoader = new ByteClassLoader(Thread.currentThread().getContextClassLoader());
        try (Stream<Path> paths = Files.walk(Paths.get(path.getAbsolutePath()))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        Class<?> clz = byteLoader.defineClass(Files.readAllBytes(filePath));
                        Method securityMethod = getSecurityMethod(clz);
                        if (securityMethod != null) {
                            System.out.println(securityMethod.invoke(clz.getConstructor().newInstance(), (Object[]) null));
                        }
                    } catch (IOException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Method getSecurityMethod(Class<?> clz) {
        Method securityMethod = null;
        try {
            securityMethod = clz.getMethod(SECURITY_METHOD_NAME, (Class<?>[]) null);
        } catch (NoSuchMethodException e) {
        }
        return securityMethod;
    }
}
