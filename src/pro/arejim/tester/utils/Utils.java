package pro.arejim.tester.utils;

import java.math.BigInteger;

public class Utils {

    /**
     * @param p - Степень в числе Мерсенна.
     * @return  - число Мерсенна.
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
        // Здесь должен быть метод Аткина-Морейна
        return N.isProbablePrime(128);      // Проверка числа встроеными методами JAVA
    }

    /**
     * @param p - Степень числа Мерсенна
     * @param number - Число Мерсенна
     * @return - true, если число простое. Иначе false.
     */
    public static boolean testM(BigInteger p, BigInteger number) {
        BigInteger S = new BigInteger("4");
        for (int i = 1; i < p.intValue() - 1; i++) {
            S = S.pow(2).subtract(new BigInteger("2")).divideAndRemainder(number)[1];    // (S^2 - 2) % M
        }
        return S.equals(BigInteger.ZERO);   // Возвращает true, если S == 0
    }
}
