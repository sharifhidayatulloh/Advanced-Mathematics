/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

/**
 *
 * @author mhidayatulloh
 */
public class AdvancedMath5and6 {

    // ---- Konstanta, mengikuti konstanta pada slide ----
    static final double EPSILON1 = 0.00001;    // batas lebar selang akhir lelaran
    static final double EPSILON2 = 0.00000001; // bilangan yang sangat kecil, mendekati nol
    static final double EPSILON  = 0.000001;   // galat hampiran akar
    static final int    NMAKS    = 100;        // jumlah lelaran maksimum (penanganan divergen)

    static final String GARIS =
        "--------------------------------------------------------------------------------------------------------------";

    // f(x) = 4x^3 - 15x^2 + 17x - 6
    static double f(double x) {
        return 4 * x * x * x - 15 * x * x + 17 * x - 6;
    }

    // f'(x) = 12x^2 - 30x + 17  (turunan pertama, untuk Newton-Raphson)
    static double df(double x) {
        return 12 * x * x - 30 * x + 17;
    }

    // Prosedur lelaran titik-tetap, dari 4x^3 - 15x^2 + 17x - 6 = 0 :
    //    17x = 6 + 15x^2 - 4x^3   ->   x = g(x) = (15x^2 - 4x^3 + 6) / 17
    // |g'(x)| = |(30x - 12x^2)/17| = 0.705882 < 1 di sekitar akar x = 2 -> konvergen
    static double g(double x) {
        return (15 * x * x - 4 * x * x * x + 6) / 17.0;
    }

    // Wadah hasil tiap metode: hampiran akar + jumlah lelaran
    static class Hasil {
        double akar;
        int lelaran;
        Hasil(double akar, int lelaran) { this.akar = akar; this.lelaran = lelaran; }
    }

    public static void main(String[] args) {
        double a  = 1.5;   // batas bawah selang
        double b  = 3.0;   // batas atas selang
        double x0 = 3.0;   // tebakan awal (sesuai soal)
        double x1 = 2.5;   // tebakan kedua untuk metode secant

        System.out.println("==============================================================================================================");
        System.out.println("   TUGAS NO. 2 : AKAR PERSAMAAN f(x) = 4x^3 - 15x^2 + 17x - 6 = 0");
        System.out.println("   Bentuk faktor : f(x) = (x - 1)(4x - 3)(x - 2)   |   Akar sejati : 0.75 , 1.00 , 2.00");
        System.out.println("==============================================================================================================");
        System.out.printf("   Selang awal  : [%.1f, %.1f]  ->  f(%.1f) = %.6f , f(%.1f) = %.6f , f(a)*f(b) = %.6f < 0%n",
                a, b, a, f(a), b, f(b), f(a) * f(b));
        System.out.printf("   Tebakan awal : x0 = %.1f   (x1 = %.1f dipakai sebagai tebakan kedua metode secant)%n", x0, x1);

        // ---------------- METODE TERTUTUP ----------------
        Hasil rf = regulaFalsi(a, b);
        Hasil bd = bagidua(a, b);
        Hasil tt = iterasiTitikTetap(x0);

        // ---------------- METODE TERBUKA -----------------
        Hasil sc = secant(x0, x1);
        Hasil nr = newtonRaphson(x0);

        // ---------------- RINGKASAN ----------------------
        System.out.println();
        System.out.println("==============================================================================================================");
        System.out.println("   RINGKASAN : JUMLAH PERULANGAN (LELARAN) TIAP METODE");
        System.out.println("==============================================================================================================");
        System.out.printf("   %-4s %-30s %-10s %-16s %-8s%n", "No", "Metode", "Jenis", "Hampiran Akar", "Iterasi");
        System.out.println("   " + GARIS);
        System.out.printf("   %-4d %-30s %-10s %-16.6f %-8d%n", 1, "Regula Falsi",              "Tertutup", rf.akar, rf.lelaran);
        System.out.printf("   %-4d %-30s %-10s %-16.6f %-8d%n", 2, "Bolzano Method",  "Tertutup", bd.akar, bd.lelaran);
        System.out.printf("   %-4d %-30s %-10s %-16.6f %-8d%n", 3, "Fixed Iteration",       "Tertutup", tt.akar, tt.lelaran);
        System.out.printf("   %-4d %-30s %-10s %-16.6f %-8d%n", 4, "Secant",                    "Terbuka",  sc.akar, sc.lelaran);
        System.out.printf("   %-4d %-30s %-10s %-16.6f %-8d%n", 5, "Newton-Raphson",            "Terbuka",  nr.akar, nr.lelaran);
        System.out.println("   " + GARIS);
        System.out.println("   Kesimpulan: semua metode konvergen ke hampiran akar x = 2.000000 (akar sejati di sekitar x0 = 3).");
    }

    /* ==========================================================================
     *  METODE TERTUTUP 1 : REGULA FALSI  -  Program 3.3
     *  c = b - f(b)(b - a) / (f(b) - f(a))          ... (P.3.2)
     *  Kondisi berhenti (kriteria slide hal. 13):
     *    (1) lebar selang  |a - b| < epsilon1
     *    (2) nilai fungsi  |f(c)| < epsilon2
     *    (3) galat hampiran akar  |c - c_lama| < epsilon
     *  Kriteria (3) penting di sini karena ujung selang b diam (stagnan),
     *  sehingga lebar selang tidak pernah mencapai epsilon1.
     * ========================================================================== */
    static Hasil regulaFalsi(double a, double b) {
        System.out.println();
        System.out.println(GARIS);
        System.out.println("METODE TERTUTUP 1 : REGULA FALSI");
        System.out.printf("Selang awal [%.2f, %.2f], epsilon1 = %.5f, epsilon2 = %.8f, epsilon = %.6f%n",
                a, b, EPSILON1, EPSILON2, EPSILON);
        System.out.println(GARIS);
        System.out.printf("%3s %12s %12s %12s %12s %12s %12s %-13s %10s%n",
                "r", "a", "c", "b", "f(a)", "f(c)", "f(b)", "Selang baru", "Lebarnya");
        System.out.println(GARIS);

        int r = 0;
        double c = a, cLama = a;
        do {
            double fa = f(a), fb = f(b);
            if (fa == fb) {                            // pengaman pembagian nol
                System.out.println("f(b) - f(a) = 0, lelaran dihentikan!");
                break;
            }
            cLama = c;
            c  = b - fb * (b - a) / (fb - fa);         // rumus (P.3.2)
            double fc = f(c);
            double aLama = a, bLama = b;
            String selangBaru;
            if (Math.abs(fc) < EPSILON2) {             // f(c) = 0 -> c adalah akar
                a = c;  b = c;  selangBaru = "[c, c]";
            } else if (fa * fc < 0) {                  // selang baru [a, b] = [a, c]
                b = c;  selangBaru = "[a, c]";
            } else {                                   // selang baru [a, b] = [c, b]
                a = c;  selangBaru = "[c, b]";
            }
            System.out.printf("%3d %12.6f %12.6f %12.6f %12.6f %12.6f %12.6f %-13s %10.6f%n",
                    r, aLama, c, bLama, fa, fc, fb, selangBaru, Math.abs(b - a));
            r++;
        } while (Math.abs(a - b) >= EPSILON1            // kriteria (1)
              && Math.abs(c - cLama) >= EPSILON         // kriteria (3)
              && r < NMAKS);                            // pengaman divergen

        System.out.println(GARIS);
        System.out.printf("Hampiran akar  : c = %.6f%n", c);
        System.out.printf("Jumlah lelaran : %d kali%n", r);
        return new Hasil(c, r);
    }

    /* ==========================================================================
     *  METODE TERTUTUP 2 : BOLZANO METHOD  -  Program 3.2
     *  c = (a + b) / 2 , selang baru dipilih yang memuat akar.
     * ========================================================================== */
    static Hasil bagidua(double a, double b) {
        System.out.println();
        System.out.println(GARIS);
        System.out.println("METODE TERTUTUP 2 : BOLZANO METHOD");
        System.out.printf("Selang awal [%.2f, %.2f], epsilon1 = %.5f, epsilon2 = %.8f%n", a, b, EPSILON1, EPSILON2);
        System.out.println(GARIS);
        System.out.printf("%3s %12s %12s %12s %12s %12s %12s %-13s %10s%n",
                "r", "a", "c", "b", "f(a)", "f(c)", "f(b)", "Selang baru", "Lebarnya");
        System.out.println(GARIS);

        int r = 0;
        double c = a;
        do {
            c  = (a + b) / 2;                          // titik tengah [a, b]
            double fa = f(a), fc = f(c), fb = f(b);
            double aLama = a, bLama = b;
            String selangBaru;
            if (fa * fc < 0) {                         // selang baru [a, b] = [a, c]
                b = c;  selangBaru = "[a, c]";
            } else {                                   // selang baru [a, b] = [c, b]
                a = c;  selangBaru = "[c, b]";
            }
            System.out.printf("%3d %12.6f %12.6f %12.6f %12.6f %12.6f %12.6f %-13s %10.6f%n",
                    r, aLama, c, bLama, fa, fc, fb, selangBaru, Math.abs(b - a));
            r++;
        } while (Math.abs(a - b) >= EPSILON1 && Math.abs(f(c)) >= EPSILON2 && r < NMAKS);

        System.out.println(GARIS);
        System.out.printf("Hampiran akar  : c = %.6f%n", c);
        System.out.printf("Jumlah lelaran : %d kali%n", r);
        return new Hasil(c, r);
    }

    /* ==========================================================================
     *  METODE TERTUTUP 3 : FIXED ITERATION  -  Program 3.5/3.6
     *  x(r+1) = g(x(r)) dengan g(x) = (15x^2 - 4x^3 + 6) / 17
     *  Berhenti bila |x(r+1) - x(r)| < epsilon  (atau divergen bila r > Nmaks)
     * ========================================================================== */
    static Hasil iterasiTitikTetap(double x) {
        System.out.println();
        System.out.println(GARIS);
        System.out.println("METODE TERTUTUP 3 : FIXED ITERATION");
        System.out.println("Prosedur lelaran : x(r+1) = g(x(r)) = (15x(r)^2 - 4x(r)^3 + 6) / 17");
        System.out.printf("Tebakan awal x0 = %.1f , epsilon = %.6f , Nmaks = %d%n", x, EPSILON, NMAKS);
        System.out.println(GARIS);
        System.out.printf("%3s %16s %18s%n", "r", "x_r", "|x_r+1 - x_r|");
        System.out.println(GARIS);

        System.out.printf("%3d %16.6f %18s%n", 0, x, "-");
        int r = 0;
        double xSebelumnya;
        do {
            xSebelumnya = x;
            x = g(x);
            r++;
            System.out.printf("%3d %16.6f %18.6f%n", r, x, Math.abs(x - xSebelumnya));
        } while (Math.abs(x - xSebelumnya) >= EPSILON && r < NMAKS);

        System.out.println(GARIS);
        if (r >= NMAKS) {
            System.out.println("Divergen!");
            return new Hasil(x, r);
        }
        System.out.printf("Hampiran akar  : x = %.6f   (konvergen)%n", x);
        System.out.printf("Jumlah lelaran : %d kali%n", r);
        return new Hasil(x, r);
    }

    /* ==========================================================================
     *  METODE TERBUKA 1 : SECANT
     *  x(r+1) = x(r) - f(x(r)) * (x(r) - x(r-1)) / (f(x(r)) - f(x(r-1)))
     * ========================================================================== */
    static Hasil secant(double x0, double x1) {
        System.out.println();
        System.out.println(GARIS);
        System.out.println("METODE TERBUKA 1 : SECANT");
        System.out.printf("Tebakan awal x0 = %.1f , x1 = %.1f , epsilon = %.6f%n", x0, x1, EPSILON);
        System.out.println(GARIS);
        System.out.printf("%3s %12s %12s %12s %12s %12s %16s%n",
                "r", "x(r-1)", "x(r)", "f(x(r-1))", "f(x(r))", "x(r+1)", "|x(r+1) - x(r)|");
        System.out.println(GARIS);

        int r = 1;
        double x2 = x1, d = 0;
        do {
            double f0 = f(x0), f1 = f(x1);
            if (f1 == f0) {                            // pengaman pembagian nol
                System.out.println("f(x(r)) - f(x(r-1)) = 0, lelaran dihentikan!");
                break;
            }
            x2 = x1 - f1 * (x1 - x0) / (f1 - f0);
            d = Math.abs(x2 - x1);
            System.out.printf("%3d %12.6f %12.6f %12.6f %12.6f %12.6f %16.6f%n",
                    r, x0, x1, f0, f1, x2, d);
            x0 = x1;
            x1 = x2;
            r++;
        } while (d >= EPSILON && r < NMAKS);

        System.out.println(GARIS);
        if (r >= NMAKS) {
            System.out.println("Divergen!");
            return new Hasil(x2, r - 1);
        }
        System.out.printf("Hampiran akar  : x = %.6f   (konvergen)%n", x2);
        System.out.printf("Jumlah lelaran : %d kali%n", r - 1);
        return new Hasil(x2, r - 1);
    }

    /* ==========================================================================
     *  METODE TERBUKA 2 : NEWTON-RAPHSON
     *  x(r+1) = x(r) - f(x(r)) / f'(x(r))
     * ========================================================================== */
    static Hasil newtonRaphson(double x) {
        System.out.println();
        System.out.println(GARIS);
        System.out.println("METODE TERBUKA 2 : NEWTON-RAPHSON");
        System.out.printf("Tebakan awal x0 = %.1f , epsilon = %.6f%n", x, EPSILON);
        System.out.println(GARIS);
        System.out.printf("%3s %12s %12s %12s %12s %16s%n",
                "r", "x_r", "f(x_r)", "f'(x_r)", "x_r+1", "|x_r+1 - x_r|");
        System.out.println(GARIS);

        int r = 0;
        double xBaru = x, d = 0;
        do {
            double fx = f(x), dfx = df(x);
            if (dfx == 0) {                            // pengaman pembagian nol
                System.out.println("f'(x) = 0, lelaran dihentikan!");
                break;
            }
            xBaru = x - fx / dfx;
            d = Math.abs(xBaru - x);
            System.out.printf("%3d %12.6f %12.6f %12.6f %12.6f %16.6f%n",
                    r, x, fx, dfx, xBaru, d);
            x = xBaru;
            r++;
        } while (d >= EPSILON && r < NMAKS);

        System.out.println(GARIS);
        if (r >= NMAKS) {
            System.out.println("Divergen!");
            return new Hasil(xBaru, r);
        }
        System.out.printf("Hampiran akar  : x = %.6f   (konvergen)%n", xBaru);
        System.out.printf("Jumlah lelaran : %d kali%n", r);
        return new Hasil(xBaru, r);
    }
}
