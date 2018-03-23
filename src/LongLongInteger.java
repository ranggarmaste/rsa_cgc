/**
 * LongLongInteger
 *
 * @author ranggarmaste
 * @since Mar 22, 2018.
 */

public class LongLongInteger {
    private String value;

    public LongLongInteger(String value) {
        this.value = value;
    }

    public int compareTo(LongLongInteger l) {
        String s1 = this.value;
        String s2 = l.value;
        int diff = s1.length() - s2.length();
        if (diff > 0) {
            s2 = padZeroFront(s2, diff);
        } else if (diff < 0) {
            s1 = padZeroFront(s1, -diff);
        }
        return s1.compareTo(s2);
    }

    public LongLongInteger minus(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;
        char[] s1Arr = s1.toCharArray();
        char[] s2Arr = s2.toCharArray();
        String ans = "";

        if (s1.charAt(0) != '-') {
            if (s2.charAt(0) != '-') {
                // Pad zeroes to the front of either string with a shorter length
                int diff = s1Arr.length - s2Arr.length;
                if (diff > 0) {
                    s2 = padZeroFront(s2, diff);
                    s2Arr = s2.toCharArray();
                } else if (diff < 0) {
                    s1 = padZeroFront(s1, -diff);
                    s1Arr = s1.toCharArray();
                }

                if (s1.compareTo(s2) < 0) {
                    char[] temp = s1Arr;
                    s1Arr = s2Arr;
                    s2Arr = temp;
                }

                for (int i = s1Arr.length - 1; i >= 0; i--) {
                    if (s1Arr[i] < s2Arr[i]) {
                        int min = ((int) s1Arr[i]) + 10 - ((int) s2Arr[i]);
                        s1Arr[i-1]--;
                        ans = Integer.toString(min) + ans;
                    } else {
                        int min = ((int) s1Arr[i]) - ((int) s2Arr[i]);
                        ans = Integer.toString(min) + ans;
                    }
                }

                ans = trimZeroes(ans);
                if (s1.compareTo(s2) < 0) {
                    ans = "-" + ans;
                }
                return fromString(ans);
            } else { // s2 is negative
                return plus(fromString(s2.substring(1)));
            }
        } else { // s1 is negative
            if (s2.charAt(0) != '-') {
                return negate(fromString(s1.substring(1)).plus(l));
            } else {
                return fromString(s2.substring(1)).minus(fromString(s1.substring(1)));
            }
        }
    }

    public LongLongInteger plus(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;
        char[] s1Arr = s1.toCharArray();
        char[] s2Arr = s2.toCharArray();
        String ans = "";
        int carry = 0;

        // Pad zeroes to the front of either string with a shorter length
        if (s1.charAt(0) != '-') {
            if (s2.charAt(0) != '-') {
                // Pad zeroes to the front of either string with a shorter length
                int diff = s1Arr.length - s2Arr.length;
                if (diff > 0) {
                    s2 = padZeroFront(s2, diff);
                    s2Arr = s2.toCharArray();
                } else if (diff < 0) {
                    s1 = padZeroFront(s1, -diff);
                    s1Arr = s1.toCharArray();
                }

                for (int i = s1Arr.length - 1; i >= 0; i--) {
                    int sum = ((int) s1Arr[i]) + ((int) s2Arr[i]) + carry - (2 * 48);
                    ans = Integer.toString(sum % 10) + ans;
                    carry = sum / 10;
                }
                if (carry == 1) {
                    ans = "1" + ans;
                }
                return fromString(ans);
            } else { // s2 is negative
                return minus(fromString(s2.substring(1)));
            }
        } else { // s1 is negative
            if (s2.charAt(0) != '-') {
                return l.minus(fromString(s1.substring(1)));
            } else { // s2 is negative
                return negate(fromString(s1.substring(1)).plus(fromString(s2.substring(1))));
            }
        }
    }

    public LongLongInteger negate(LongLongInteger l) {
        if (l.value.charAt(0) == '-') {
            return fromString(l.value.substring(1));
        }
        return fromString("-" + l.value);
    }

    public LongLongInteger multiply(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;

        // Pad zeroes to the front of either string with a shorter length
        int diff = s1.length() - s2.length();
        if (diff > 0) {
            s2 = padZeroFront(s2, diff);
        } else if (diff < 0) {
            s1 = padZeroFront(s1, -diff);
        }

        // Recursion base: length of each string is only 1
        if (s1.length() == 1) {
            return fromString(Integer.toString(Integer.parseInt(s1) * Integer.parseInt(s2)));
        } else {
            // Recursion
            // Pad another zero if the length is odd
            if (s1.length() % 2 == 1) {
                s1 = padZeroFront(s1, 1);
                s2 = padZeroFront(s2, 1);
            }

            /*  1. Division stage
                Divides the string into four of the following:
                a = s1 div 10^s
                b = s1 mod 10^s
                c = s2 div 10^s
                d = s2 mod 10^s
                where s = length/2
             */
            int s = s1.length() / 2;
            String a = s1.substring(0, s);
            String b = s1.substring(s);
            String c = s2.substring(0, s);
            String d = s2.substring(s);

            /*  2. Conquer stage
                Recursive calls as the following:
                p = a * c
                q = b * d
                r = (a + b) * (c + d)
             */
            LongLongInteger p = fromString(a).multiply(fromString(c));
            LongLongInteger q = fromString(b).multiply(fromString(d));
            LongLongInteger r = fromString(a).plus(fromString(b)).multiply(fromString(c).plus(fromString(d)));

            /*  3. Combine stage
                Combining p, q, and r to create the final results with the following formula:
                ans = p * 10^2s + (r - p - q) * 10^s + q
             */
            String res1 = padZeroBack(p.value, 2 * s); // p * 10^2s
            String res2 = padZeroBack(r.minus(p.plus(q)).value, s); // (r - p - q) * 10^s
            String ans = fromString(res1).plus(fromString(res2).plus(q)).value;

            return fromString(trimZeroes(ans));
        }
    }

    public LongLongInteger divide(LongLongInteger l) {
        String s1 = value;
        String s2 = l.value;

        int pos = 0;
        String curr = "0";
        String ans = "0";
        while (pos < s1.length()) {
            while (pos < s1.length() && curr.length() < s2.length()) {
                curr += s1.charAt(pos);
                pos++;
            }
            while (curr.compareTo(s2) < 0) {
                if (pos >= s1.length()) {
                    break;
                }
                curr += s1.charAt(pos);
                pos++;
            }
            int count = 0;
            String agg = s2;
            while (fromString(s1).compareTo(fromString(agg)) >= 0) {
                agg = fromString(agg).plus(fromString(s2)).value;
                count++;
            }
            ans += Integer.toString(count);
            curr = fromString(curr).minus(fromString(s2).multiply(fromString(Integer.toString(count)))).value;
        }
        return fromString(trimZeroes(ans));
    }

    public LongLongInteger power(LongLongInteger exponent) {
        if (exponent.compareTo(fromString("0")) == 0) return fromString("1");

        LongLongInteger d = power(exponent.divide(fromString("2")));
        if (exponent.modulo(fromString("2")).compareTo(fromString("0")) == 0) {
            return d.multiply(d);
        }
        return multiply(d).multiply(d);
    }

    public LongLongInteger modulo(LongLongInteger l) {
        return minus(l.multiply(divide(l)));
    }

    public LongLongInteger moduloExponent(LongLongInteger exponent, LongLongInteger mod) {
        LongLongInteger res = fromString("1");
        LongLongInteger base = modulo(mod);

        while (exponent.compareTo(fromString("0")) > 0)
        {
            System.out.println("OO");
            if ((exponent.value.charAt(exponent.value.length()-1)) == '1')
                res = res.multiply(base).modulo(mod);
            System.out.println("BB");
            String exponentBits = exponent.toBits();
            System.out.println("CC");
            exponent = LongLongInteger.fromBits("0" + exponentBits.substring(0, exponentBits.length()-1));
            base = base.multiply(base).modulo(mod);
        }
        System.out.println("DONE");
        return res;
    }

    public String toBits() {
        if (value == "0") return "0";

        LongLongInteger init = fromString(value);
        StringBuilder builder = new StringBuilder();
        while (init.compareTo(fromString("1")) >= 0) {
            System.out.println(init);
            builder.append(init.modulo(fromString("2")).value);
            init = init.divide(fromString("2"));
        }
        return builder.reverse().toString();
    }

    public static LongLongInteger fromBits(String bits) {
        LongLongInteger ans = fromString("0");
        for (int i = bits.length()-1; i >= 0; i--) {
            int count = bits.length()-i-1;
            if (bits.charAt(i) != '0') {
                ans = ans.plus(fromString("2").power(fromString(Integer.toString(count))));
            }
        }
        return ans;
    }

    public static LongLongInteger fromString(String value) {
        return new LongLongInteger(value);
    }

    @Override
    public String toString() {
        return value;
    }

    private String padZeroFront (String s, int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            builder.append("0");
        }
        builder.append(s);
        return builder.toString();
    }

    private String padZeroBack (String s, int n) {
        StringBuilder builder = new StringBuilder(s);
        for (int i = 0; i < n; i++) {
            builder.append("0");
        }
        return builder.toString();
    }

    private String trimZeroes(String s) {
        int i = 0;
        while (i < s.length() - 1 && s.charAt(i) == '0') {
            i++;
        }
        return s.substring(i);
    }
}
