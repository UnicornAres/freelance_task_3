package pro.arejim.tester.utils;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;

import java.math.BigInteger;

public class Utils {

    /**
     * @param p - Степень в числе Мерсенна.
     * @return - число Мерсенна.
     */
    public static BigInteger calcM(BigInteger p) {
        return new BigInteger("2").pow(p.intValue()).subtract(BigInteger.ONE);  // 2^p -1
    }

    /**
     * @param p - число проверяемое на просту.
     * @return - true, если число больше или равно 3 и простое. Иначе false.
     */
    public static boolean isPrime(BigInteger p) {
        return p.intValue() >= 3 && p.isProbablePrime(1);   // Проверка числа встроеными методами JAVA
    }

    /**
     * @param N - число проверяемое на просту.
     * @return - true, если число простое. Иначе false.
     */
    public static boolean testA(BigInteger N) {
        return N.isProbablePrime(128);      // Проверка числа встроеными методами JAVA
    }

    /**
     * @param p      - Степень числа Мерсенна
     * @param number - Число Мерсенна
     * @return - true, если число простое. Иначе false.
     */
    public static boolean testM(BigInteger p, BigInteger number, DoubleProperty progress) {
        BigInteger S = new BigInteger("4");
        double percents;
        double oldPercents = 0;
        progress.set(0);
        double ceil = Math.ceil(p.intValue()) - 1;
        for (int i = 1; i < ceil; i++) {
            percents = (double) i / ceil;
            // Проценты выполнения вычислений
            if (percents > oldPercents + 0.01 && percents <= 1.0) {
                oldPercents = percents;
                double finalOldPercents = oldPercents;
                Platform.runLater(() -> progress.setValue(finalOldPercents));
            }
            S = S.pow(2).subtract(new BigInteger("2")).divideAndRemainder(number)[1];    // (S^2 - 2) % M
        }
        return S.equals(BigInteger.ZERO);   // Возвращает true, если S == 0
    }
}
