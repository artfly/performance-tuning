package ru.nsu.fit.javaperf.sartakov.agent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class TransactionAgent {
    public static void premain(String agentArgument, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassTransformer());
    }

    private static class ClassTransformer implements ClassFileTransformer {
        private static final String PACKAGE = "ru/nsu/fit/javaperf";
        private int count;

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            byte[] bytecode = classfileBuffer;

            if (className.contains(PACKAGE)) {
                CtClass ctClass;
                try {
                    ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));
                } catch (IOException e) {
                    return classfileBuffer;
                }

                if (ctClass == null || ctClass.isFrozen()) {
                    return classfileBuffer;
                }
                for (CtMethod method : ctClass.getDeclaredMethods()) {
                    try {
                        method.insertBefore("{ System.out.println" +
                                "(\"Method " + method.getName() +
                                " started at \" + System.nanoTime()); }");
                        method.insertAfter("{ System.out.println" +
                                "(\"Method " + method.getName() +
                                " ended at \" + System.nanoTime()); }");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    bytecode = ctClass.toBytecode();
                } catch (CannotCompileException | IOException e) {
                    return classfileBuffer;
                }
            }
            count++;
            System.out.println(String.format("Added %d class", count));
            return bytecode;
        }
    }
}
