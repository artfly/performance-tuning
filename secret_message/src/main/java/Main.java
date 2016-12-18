import javassist.*;

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
    private static final String SECURITY_CLASS_NAME = "SecurityMessenger";
    private static final String SECURITY_MESSAGE_FIELD = "SECURITY_MESSAGE";
    private static final String SECURITY_MESSAGE = "Hello world";

    private static final String NO_PATH_MSG = "Error : no path specified.";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(NO_PATH_MSG);
            System.exit(1);
        }

        File path = new File(args[0]);
        createClass(path);
        traversePath(path);
    }

    private static void createClass(File path) {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass(SECURITY_CLASS_NAME);
        try {
            CtClass stringClass = pool.get(String.class.getCanonicalName());
            CtField f = new CtField(stringClass, SECURITY_MESSAGE_FIELD, cc);
            f.setModifiers(Modifier.STATIC);
            f.setModifiers(Modifier.PRIVATE);
            cc.addField(f, CtField.Initializer.constant(SECURITY_MESSAGE));

            CtMethod m = CtNewMethod.getter(SECURITY_METHOD_NAME, f);
            cc.addMethod(m);
            cc.writeFile(path.getAbsolutePath());
        } catch (CannotCompileException | NotFoundException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void traversePath(File path) {
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
                    } catch (IOException | InvocationTargetException | IllegalAccessException |
                            NoSuchMethodException | InstantiationException | ClassFormatError | NoClassDefFoundError e) {
                        System.err.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
        return securityMethod;
    }
}
