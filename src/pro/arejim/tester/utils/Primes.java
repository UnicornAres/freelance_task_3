package pro.arejim.tester.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Primes {

    // Все возможные дискриминанты уравнения a^2+|D|*b^2=4N
    private final int[] DISCRIMINANTS = {-3, -4, -7, -8, -11, -12, -16, -19, -27, -28, -43, -67, -163};
    // Проверяемое число
    private BigInteger N;

    private static BigInteger mod(BigInteger a, BigInteger b, BigInteger p) {
        BigInteger r = BigInteger.ONE;
        while (b.compareTo(BigInteger.ZERO) > 0) {
            if (b.and(BigInteger.ONE).equals(BigInteger.ONE))
                r = r.multiply(a).remainder(p);
            b = b.shiftRight(1);
            a = a.multiply(a).remainder(p);
        }
        return r;
    }

    // Алгоритм Лежандра, необходим для проверки всех дискриминантов, и вовода подходящих для N
    // Пусть D - целое число, и N - простое число, не равное 2
    // Символ Лежандра (D/N) определяется следующим образом:
    //   (D/N) = 0, если D/P
    //   (D/N) = 1, если существует целое x^2=D(mod N)
    //   (D/N) = -1, условие противоположное (D/N) = 1
    private int calculateLegendre(BigInteger D, BigInteger N) { // Возращает -1, 0, 1
        if (D.equals(N) || D.compareTo(N) > 0 || D.compareTo(BigInteger.ZERO) < 0) {
            return calculateLegendre(D.remainder(N), N);
        } else if (D.intValue() == 0 || D.intValue() == 1) {
            return D.intValue();
        } else if (D.intValue() == 2) {
            if (N.remainder(BigInteger.valueOf(8)).equals(BigInteger.ONE) ||
                    N.remainder(BigInteger.valueOf(8)).equals(BigInteger.valueOf(7))) {
                return 1;
            } else {
                return -1;
            }
        } else if (D.equals(N.subtract(BigInteger.ONE))) {
            if (N.remainder(BigInteger.valueOf(4)).equals(BigInteger.ZERO)) {
                return 1;
            } else {
                return -1;
            }
        }
        if (!prime(D)) { // Является ли D простым в общем случае
            ArrayList<BigInteger> factors = factorize(D);
            int count = 1;
            for (BigInteger factor : factors) {
                count *= calculateLegendre(factor, N);
            }
            return count;
        } else {
            if (N.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2)).remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO) ||
                    D.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2)).remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
                return calculateLegendre(N, D);
            } else {
                return (-1) * calculateLegendre(N, D);
            }
        }
    }

    // Возращает все подходящие дискриминанты для N
    private ArrayList<Integer> checkDiscriminants() {
        ArrayList<Integer> discriminants = new ArrayList<>();
        for (int i : DISCRIMINANTS) {
            if (calculateLegendre(BigInteger.valueOf(i * (-1)), N) == -1) {
                discriminants.add(i);
            }
        }
        return discriminants;
    }

    private boolean calculateCornacchia() {
        if (checkDiscriminants().isEmpty()) {
            return false;
        }
        // Проверяем каждый дискримант на нахождение корней a и b ( a^2+|D|*b^2=4N )
        for (int i : checkDiscriminants()) {
            BigInteger d = new BigInteger(String.valueOf(i)).negate();
            BigInteger m = N;
            BigInteger r = sqrt(m.subtract(d), m);

            if (N.equals(BigInteger.valueOf(2)) || N.equals(BigInteger.valueOf(3))) {
                return true;
            }
            if (r.intValue() == 0) {
                if (((r.multiply(r)).mod(m)).equals(m.subtract(d))) {
                    BigInteger f = m;
                    BigInteger x = r;

                    while ((x.multiply(x)).compareTo(m) >= 0) {
                        BigInteger temp = f;
                        f = x;
                        x = temp.mod(f);
                    }
                    BigInteger z = (m.subtract(x.multiply(x))).divide(d);
                    BigInteger y = sqrt(z);
                    if (y.multiply(y).equals(z)) {
                        return true;
                    }
                }
            }
            // В противном случае, запускаем алгоритм Шуфа на дополнительную проверку
            else {
                return calculateGoldwasser();
            }
        }
        return false;
    }

    private boolean calculateGoldwasser() {
        if (N.equals(BigInteger.ZERO) || N.equals(BigInteger.ONE) || // Если равно 0, 1, 2 или парное число
                N.equals(BigInteger.valueOf(2)) ||
                N.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            return false;
        }
        BigInteger s = N.subtract(BigInteger.ONE);
        while (s.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            s = s.divide(BigInteger.valueOf(2));
        }
        Random rand = new Random();
        for (int I = 0; I < 50; I++) {
            long odd = rand.nextLong();
            if (odd < 0) odd = -odd;
            BigInteger u = N.mod(BigInteger.valueOf(odd));
            BigInteger a = u.remainder(N.subtract(BigInteger.ONE)).add(BigInteger.ONE);
            BigInteger exp = s;
            BigInteger mod = mod(a, exp, N);
            while (!exp.equals(N.subtract(BigInteger.ONE)) && !mod.equals(BigInteger.ONE) && !mod.equals(N.subtract(BigInteger.ONE))) {
                mod = mod.multiply(mod).mod(N);
                exp = exp.multiply(BigInteger.valueOf(2));
            }
            if (mod.compareTo(N.subtract(BigInteger.ONE)) != 0 && exp.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
                return false;
            }
        }
        return true;
    }

    // Провеяет, является ли число D простым
    private boolean prime(BigInteger D) {
        for (int i = 2; D.compareTo(BigInteger.valueOf(i)) > 0; i++) {
            if (D.remainder(BigInteger.valueOf(i)).equals(BigInteger.ZERO)) {
                return false;
            }
        }
        return true;
    }

    // Вычисление корня
    private BigInteger sqrt(BigInteger X) {
        BigInteger P;
        BigInteger mul = BigInteger.valueOf(2);
        while ((mul.multiply(mul)).compareTo(X) <= 0) {
            mul = mul.multiply(BigInteger.valueOf(2));
        }
        mul = mul.divide(BigInteger.valueOf(2));
        P = mul;
        while (mul.compareTo(BigInteger.ONE) >= 0) {
            mul = mul.divide(BigInteger.valueOf(2));
            if (((P.add(mul)).multiply(P.add(mul))).compareTo(X) <= 0)
                P = P.add(mul);
        }
        return P;
    }

    // Вычисление корня D по N
    // Если это не возможно, возращает 0
    private BigInteger sqrt(BigInteger D, BigInteger N) {
        BigInteger x;
        D = D.remainder(N);
        // D^N-1) mod N > 0 или D^(N-1) mod N > 0
        if (!(D.modPow(N.subtract(BigInteger.ONE), N)).equals(BigInteger.ONE)) {
            return BigInteger.ZERO;
        }
        if ((D.modPow(N.subtract(BigInteger.ONE), N)).equals(BigInteger.ONE)) {
            if ((D.modPow((N.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2)), N).equals(N.subtract(BigInteger.ONE)))) {
                return BigInteger.ZERO;
            }
            if ((D.modPow((N.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2)), N)).equals(BigInteger.ONE)) {
                if ((N.remainder(BigInteger.valueOf(4))).equals(BigInteger.valueOf(3))) {
                    x = D.modPow((N.add(BigInteger.ONE)).divide(BigInteger.valueOf(4)), N);
                    return x;
                }
                if ((N.remainder(BigInteger.valueOf(8))).equals(BigInteger.valueOf(5))) {
                    x = D.modPow((N.add(BigInteger.valueOf(3))).divide(BigInteger.valueOf(8)), N);
                    BigInteger c = (x.multiply(x)).remainder(N);
                    if (((c.remainder(N)).compareTo(N.subtract(D))) == 0) {
                        x = (x.multiply((BigInteger.valueOf(2)).modPow((N.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(4)), N))).remainder(N);
                    }
                    return x;
                }
                if ((N.remainder(BigInteger.valueOf(8))).equals(BigInteger.ONE)) {
                    BigInteger d = BigInteger.valueOf(2);
                    BigInteger t = N.subtract(BigInteger.ONE);
                    int s = 0;
                    int i;
                    BigInteger m;
                    while ((t.remainder(BigInteger.valueOf(2))).equals(BigInteger.ZERO)) {
                        s++;
                        t = t.divide(BigInteger.valueOf(2));
                    }
                    BigInteger k = D.modPow(t, N);
                    BigInteger n = d.modPow(t, N);
                    m = BigInteger.ZERO;
                    for (i = 0; i < s; i++) {
                        if (((k.multiply(n.modPow(m, N))).modPow((BigInteger.valueOf(2)).pow(s - 1 - i), N)).compareTo(N.subtract(BigInteger.ONE)) == 0) {
                            m = m.add((BigInteger.valueOf(2)).pow(i));
                        }
                    }
                    x = ((D.modPow((t.add(BigInteger.ONE)).divide(BigInteger.valueOf(2)), N)).multiply(n.modPow(m.divide(BigInteger.valueOf(2)), N))).remainder(N);
                    return x;
                }
            }
        }
        return BigInteger.ZERO;
    }

    // Декомпозиция D, приведение D к простым множителям
    private ArrayList<BigInteger> factorize(BigInteger D) {
        ArrayList<BigInteger> factors = new ArrayList<>();
        int P = 2;
        while (true) {
            while (D.remainder(BigInteger.valueOf(P)).equals(BigInteger.ZERO) && D.compareTo(BigInteger.ZERO) > 0) {
                factors.add(BigInteger.valueOf(P));
                D = D.divide(BigInteger.valueOf(P));
            }
            P += 1;
            if (D.divide(BigInteger.valueOf(P)).compareTo(BigInteger.valueOf(P)) < 0) {
                break;
            }
        }
        if (D.compareTo(BigInteger.valueOf(1)) > 0) {
            factors.add(D);
        }
        return factors;
    }

    // Основной метод
    // На вход подается строка (проверял до 500 символов)
    // Вывод: True - число простое, False - составное
    public boolean isPrime(BigInteger number) {
        this.N = number;
        return calculateCornacchia();
    }
}