package pro.arejim.tester.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Primes {

    private final int[] DISCRIMINANTS = {-3, -4, -7, -8, -11, -12, -16, -19, -27, -28, -43, -67, -163};
    private BigInteger N;

    public BigInteger getN() {
        return N;
    }

    public void setN(BigInteger N) {
        this.N = N;
    }

    // Алгоритм Лежандра, необходим для проверки всех дискриминантов, и вовода подходящих для N
    // Возращает -1, 0, 1
    private int calculateLegendre(BigInteger D, BigInteger N) {
        if (D.compareTo(N) == 0 || D.compareTo(N) == 1 || D.compareTo(BigInteger.ZERO) == -1) {
            return calculateLegendre(D.remainder(N), N);
        } else if (D.compareTo(BigInteger.ZERO) == 0 || D.compareTo(BigInteger.ONE) == 0) {
            return Integer.parseInt(D.toString());
        } else if (D.compareTo(BigInteger.valueOf(2)) == 0) {
            if (N.remainder(BigInteger.valueOf(8)).compareTo(BigInteger.ONE) == 0 ||
                    N.remainder(BigInteger.valueOf(8)).compareTo(BigInteger.valueOf(7)) == 0) {
                return 1;
            } else {
                return -1;
            }
        } else if (D.compareTo(N.subtract(BigInteger.ONE)) == 0) {
            if (N.remainder(BigInteger.valueOf(4)).compareTo(BigInteger.ONE) == 0) {
                return 1;
            } else {
                return -1;
            }
        }
        if (!prime(D)) {
            ArrayList<BigInteger> factors = factorize(D);
            int count = 1;
            for (BigInteger factor : factors) {
                count *= calculateLegendre(factor, N);
            }
            return count;
        } else {
            if (N.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2)).remainder(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0 ||
                    D.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2)).remainder(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0) {
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
        for (int i : checkDiscriminants()) {
            BigInteger d = new BigInteger(String.valueOf(i)).negate();
            BigInteger m = N;
            BigInteger r = sqrt(m.subtract(d), m);

            if (N.compareTo(BigInteger.valueOf(3)) == 0) {
                return true;
            }
            if (r.compareTo(BigInteger.ZERO) != 0) {
                if (((r.multiply(r)).mod(m)).compareTo(m.subtract(d)) == 0) {
                    BigInteger f = m;
                    BigInteger x = r;

                    while ((x.multiply(x)).compareTo(m) >= 0) {
                        BigInteger temp = f;
                        f = x;
                        x = temp.mod(f);
                    }
                    BigInteger z = (m.subtract(x.multiply(x))).divide(d);
                    BigInteger y = sqrt(z);
                    if ((y.multiply(y).compareTo(z) == 0)) {
                        return true;
                    }
                }
            } else {
                return calculateGoldwaser();
            }
        }
        return false;
    }

    private boolean calculateGoldwaser() {
        if (N.compareTo(BigInteger.ZERO) == 0 || N.compareTo(BigInteger.ONE) == 0) {
            return false;
        }
        if (N.compareTo(BigInteger.valueOf(2)) == 0) {
            return true;
        }
        if (N.remainder(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0) {
            return false;
        }
        BigInteger s = N.subtract(BigInteger.ONE);
        while (s.remainder(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0) {
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
            while (exp.compareTo(N.subtract(BigInteger.ONE)) != 0 && mod.compareTo(BigInteger.ONE) != 0 && mod.compareTo(N.subtract(BigInteger.ONE)) != 0) {
                mod = mod.multiply(mod).mod(N);
                exp = exp.multiply(BigInteger.valueOf(2));
            }
            if (mod.compareTo(N.subtract(BigInteger.ONE)) != 0 && exp.remainder(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean prime(BigInteger D) {
        for (int i = 2; D.compareTo(BigInteger.valueOf(i)) == 1; i++) {
            if (D.remainder(BigInteger.valueOf(i)).compareTo(BigInteger.ZERO) == 0) {
                return false;
            }
        }
        return true;
    }

    private static BigInteger mod(BigInteger a, BigInteger b, BigInteger p) {
        BigInteger r = BigInteger.ONE;
        while (b.compareTo(BigInteger.ZERO) == 1) {
            if ((b.and(BigInteger.ONE).compareTo(BigInteger.ONE)) == 0)
                r = r.multiply(a).remainder(p);
            b = b.shiftRight(1);
            a = a.multiply(a).remainder(p);
        }
        return r;
    }

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

    private BigInteger sqrt(BigInteger D, BigInteger N) {
        BigInteger x;
        D = D.remainder(N);
        if ((D.modPow(N.subtract(BigInteger.ONE), N)).compareTo(BigInteger.ONE) != 0) {
            return BigInteger.ZERO;
        }
        if ((D.modPow(N.subtract(BigInteger.ONE), N)).compareTo(BigInteger.ONE) == 0) {
            if ((D.modPow((N.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2)), N).compareTo(N.subtract(BigInteger.ONE)) == 0)) {
                return BigInteger.ZERO;
            }
            if ((D.modPow((N.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2)), N)).compareTo(BigInteger.ONE) == 0) {
                if ((N.remainder(BigInteger.valueOf(4))).compareTo(BigInteger.valueOf(3)) == 0) {
                    x = D.modPow((N.add(BigInteger.ONE)).divide(BigInteger.valueOf(4)), N);
                    return x;
                }
                if ((N.remainder(BigInteger.valueOf(8))).compareTo(BigInteger.valueOf(5)) == 0) {
                    x = D.modPow((N.add(BigInteger.valueOf(3))).divide(BigInteger.valueOf(8)), N);
                    BigInteger c = (x.multiply(x)).remainder(N);
                    if (((c.remainder(N)).compareTo(N.subtract(D))) == 0) {
                        x = (x.multiply((BigInteger.valueOf(2)).modPow((N.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(4)), N))).remainder(N);
                    }
                    return x;
                }
                if ((N.remainder(BigInteger.valueOf(8))).compareTo(BigInteger.valueOf(1)) == 0) {
                    BigInteger d = BigInteger.valueOf(2);
                    BigInteger t = N.subtract(BigInteger.ONE);
                    int s = 0;
                    int i;
                    BigInteger m;
                    while ((t.remainder(BigInteger.valueOf(2))).compareTo(BigInteger.ZERO) == 0) {
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

    private ArrayList<BigInteger> factorize(BigInteger D) {
        ArrayList<BigInteger> factors = new ArrayList<>();
        int P = 2;
        while (true) {
            while (D.remainder(BigInteger.valueOf(P)).compareTo(BigInteger.ZERO) == 0 && D.compareTo(BigInteger.ZERO) == 1) {
                factors.add(BigInteger.valueOf(P));
                D = D.divide(BigInteger.valueOf(P));
            }
            P += 1;
            if (D.divide(BigInteger.valueOf(P)).compareTo(BigInteger.valueOf(P)) == -1) {
                break;
            }
        }
        if (D.compareTo(BigInteger.valueOf(1)) == 1) {
            factors.add(D);
        }
        return factors;
    }

    public boolean isPrime(String number) {
        try {
            setN(new BigInteger(number));
        } catch (NumberFormatException e) {
            System.exit(-1);
        }
        return calculateCornacchia();
    }
}