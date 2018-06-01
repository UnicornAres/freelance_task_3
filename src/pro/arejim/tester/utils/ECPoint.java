package pro.arejim.tester.utils;

import java.math.BigInteger;

public class ECPoint {
    private BigInteger x;
    private BigInteger y;
    private BigInteger a;
    private BigInteger b;
    private BigInteger FieldChar;

    public ECPoint(ECPoint p) {
        this.x = p.getX();
        this.y = p.getY();
        this.a = p.getA();
        this.b = p.getB();
        this.FieldChar = p.getFieldChar();
    }

    public ECPoint() {
        this.x = new BigInteger("");
        this.y = new BigInteger("");
        this.a = new BigInteger("");
        this.b = new BigInteger("");
        this.FieldChar = new BigInteger("");
    }

    public ECPoint(Object x, Object y, Object a, Object b, Object fc) {
        this.x = new BigInteger(String.valueOf(x));
        this.y = new BigInteger(String.valueOf(y));
        this.a = new BigInteger(String.valueOf(a));
        this.b = new BigInteger(String.valueOf(b));
        this.FieldChar = new BigInteger(String.valueOf(fc));
    }

    public static ECPoint add(ECPoint p1, ECPoint p2) {
        ECPoint p3 = new ECPoint();
        p3.setA(p1.getA());
        p3.setB(p1.getB());
        p3.setFieldChar(p1.getFieldChar());

        BigInteger dy = p2.getY().subtract(p1.getY());
        BigInteger dx = p2.getX().subtract(p1.getX());

        if (dx.compareTo(BigInteger.ZERO) < 0)
            dx = dx.add(p1.getFieldChar());
        if (dy.compareTo(BigInteger.ZERO) < 0)
            dy = dy.add(p1.getFieldChar());

        BigInteger m = (dy.multiply(dx.modInverse(p1.getFieldChar()))).divideAndRemainder(p1.getFieldChar())[1];
        if (m.compareTo(BigInteger.ZERO) < 0)
            m = m.add(p1.getFieldChar());
        p3.setX((m.pow(2).subtract(p1.getX()).subtract(p2.getX())).divideAndRemainder(p1.getFieldChar())[1]);
        p3.setY((m.multiply(p1.getX().subtract(p3.getX())).subtract(p1.getY())).divideAndRemainder(p1.getFieldChar())[1]);
        if (p3.getX().compareTo(BigInteger.ZERO) < 0)
            p3.setX(p3.getX().add(p1.getFieldChar()));
        if (p3.getY().compareTo(BigInteger.ZERO) < 0)
            p3.setY(p3.getY().add(p1.getFieldChar()));
        return p3;
    }

    //сложение точки P c собой же
    public static ECPoint Double(ECPoint p) {
        ECPoint p2 = new ECPoint();
        p2.setA(p.getA());
        p2.setB(p.getB());
        p2.setFieldChar(p.getFieldChar());

        BigInteger dy = new BigInteger("3").multiply(p.getX().pow(2).multiply(p.getA()));
        BigInteger dx = new BigInteger("2").multiply(p.getY());

        if (dx.compareTo(BigInteger.ZERO) < 0)
            dx = dx.add(p.getFieldChar());
        if (dy.compareTo(BigInteger.ZERO) < 0)
            dy = dy.add(p.getFieldChar());

        BigInteger m = (dy.multiply(dx.modInverse(p.getFieldChar()))).divideAndRemainder(p.getFieldChar())[1];
        p2.setX((m.pow(2).subtract(p.getX()).subtract(p.getX())).divideAndRemainder(p.getFieldChar())[1]);
        p2.setY(m.multiply(p.getX().subtract(p2.getX())).subtract(p.getY()).divideAndRemainder(p.getFieldChar())[1]);
        if (p2.getX().compareTo(BigInteger.ZERO) < 0)
            p2.setX(p2.getX().add(p.getFieldChar()));
        if (p2.getY().compareTo(BigInteger.ZERO) < 0)
            p2.setX(p2.getX().add(p.getFieldChar()));

        return p2;
    }

    //умножение точки на число x, по сути своей представляет x сложений точки самой с собой
    public static ECPoint multiply(BigInteger x, ECPoint p) {
        ECPoint temp = p;
        BigInteger TWO = new BigInteger("2");
        x = x.subtract(BigInteger.ONE);
        while (x.compareTo(BigInteger.ZERO) != 0) {
            if ((x.divideAndRemainder(TWO)[1]).compareTo(BigInteger.ZERO) != 0) {
                if (temp.getX().equals(p.getX()) || temp.getY().equals(p.getY()))
                    temp = Double(temp);
                else
                    temp = add(temp, p);
                x = x.subtract(BigInteger.ONE);
            }
            x = x.divide(TWO);
            p = Double(p);
        }
        return temp;
    }

    public static BigInteger sqrt(final BigInteger number)
    {
        if(number.signum() == -1)
            throw new ArithmeticException("We can only calculate the square root of positive numbers.");
        return newtonIteration(number, BigInteger.ONE);
    }

    private static BigInteger newtonIteration(BigInteger n, BigInteger x0)
    {
        final BigInteger x1 = n.divide(x0).add(x0).shiftRight(1);
        return x0.equals(x1)||x0.equals(x1.subtract(BigInteger.ONE)) ? x0 : newtonIteration(n, x1);
    }

    public BigInteger getFieldChar() {
        return FieldChar;
    }

    public void setFieldChar(BigInteger fieldChar) {
        FieldChar = fieldChar;
    }

    public BigInteger getB() {
        return b;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }
}
