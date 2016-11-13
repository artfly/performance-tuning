import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

public class Main {
    static Calculator createCalculator() throws Exception {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(Calculator.class);
        Class<?> calculatorClass = factory.createClass();
        MethodHandler mi = (self, thisMethod, proceed, args) -> {
            if (thisMethod.equals(Calculator.class.getMethod("sum", int.class, int.class))) {
                return (Integer) proceed.invoke(self, args) + 1;
            }
            return proceed.invoke(self, args);
        };
        Calculator calculator = (Calculator) calculatorClass.newInstance();
        ((Proxy) calculator).setHandler(mi);
        return calculator;
    }

    public static void main(String[] args) {
        try {
            Calculator calculator = createCalculator();
            System.out.println(String.format("2 + 2 = %d", calculator.sum(2, 2)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Calculator {
        public Calculator() {
        }

        public int sum(int x, int y) {
            return x + y;
        }
    }
}
