package metromind;

import metromind.engine.*;

/** Self-contained test runner (no JUnit needed).  Run:  java -cp out metromind.Tests */
public final class Tests {
    static int pass = 0, fail = 0;

    static void check(String name, boolean ok) {
        if (ok) { pass++; System.out.println("  PASS  " + name); }
        else { fail++; System.out.println("  FAIL  " + name); }
    }

    static boolean same(int[] a, int[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (a[i] != b[i]) return false;
        return true;
    }

    static long seed = 12345;
    static int rnd(int bound) { seed = (seed * 6364136223846793005L + 1442695040888963407L); return (int) ((seed >>> 33) % bound); }
    static String randStr(int n, int alpha) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd(alpha)));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("== CO1  String algorithms ==");
        int[] f = StringAlgos.failure("abacabab");
        check("KMP failure('abacabab') = 0 0 1 0 1 2 3 2", same(f, new int[]{0, 0, 1, 0, 1, 2, 3, 2}));
        check("Z-function('aabxaab') = 7 1 0 0 3 1 0", same(StringAlgos.zArray("aabxaab"), new int[]{7, 1, 0, 0, 3, 1, 0}));
        check("KMP finds overlapping 'aa' in 'aaaa' (3 hits)", StringAlgos.kmp("aaaa", "aa").positions.size() == 3);
        check("Empty pattern -> no hits", StringAlgos.kmp("abc", "").positions.size() == 0);
        check("Pattern longer than text -> no hits", StringAlgos.rabinKarp("ab", "abc").positions.size() == 0);
        boolean all = true;
        for (int t = 0; t < 200; t++) {
            String text = randStr(60 + rnd(60), 2 + rnd(3));
            String pat = randStr(1 + rnd(5), 2 + rnd(3));
            int[] a = StringAlgos.naive(text, pat).positions.toArray();
            all &= same(a, StringAlgos.kmp(text, pat).positions.toArray());
            all &= same(a, StringAlgos.zSearch(text, pat).positions.toArray());
            all &= same(a, StringAlgos.rabinKarp(text, pat).positions.toArray());
            all &= same(a, StringAlgos.rabinKarpSingleSmall(text, pat).positions.toArray());
        }
        check("200 random cases: Naive == KMP == Z == Rabin-Karp (double & single)", all);
        check("smallestPeriod('abcabcabc') = 3", StringAlgos.smallestPeriod("abcabcabc") == 3);
        check("smallestPeriod('abcabd') = 6 (no repetition)", StringAlgos.smallestPeriod("abcabd") == 6);
        MatchResult adv = StringAlgos.naive("aaaaaaaaaaaaaaaaaaaa", "aaaab");
        MatchResult adv2 = StringAlgos.kmp("aaaaaaaaaaaaaaaaaaaa", "aaaab");
        check("KMP uses fewer comparisons than Naive on adversarial input", adv2.comparisons < adv.comparisons);
        check("Rabin-Karp single tiny mod produces spurious hits",
            StringAlgos.rabinKarpSingleSmall(randStr(5000, 26), "qzxv").spurious > 0);

        AhoCorasick ac = new AhoCorasick(new String[]{"he", "she", "his", "hers"});
        AhoCorasick.Scan sc = ac.search("ushers");
        check("Aho-Corasick 'ushers' finds she, he, hers (3 matches)", sc.end.size() == 3);

        System.out.println("== CO2  Suffix structures ==");
        SuffixArray sa = new SuffixArray("banana");
        check("SA('banana') = 5 3 1 0 4 2", same(sa.sa, new int[]{5, 3, 1, 0, 4, 2}));
        check("LCP('banana') = 0 1 3 0 0 2", same(sa.lcp, new int[]{0, 1, 3, 0, 0, 2}));
        check("SA search 'ana' -> 2 occurrences", sa.occurrences("ana").length == 2);
        check("SA search 'xyz' -> 0 occurrences", sa.occurrences("xyz").length == 0);
        check("Longest repeated substring of banana = 'ana' (len 3)", sa.longestRepeated()[0] == 3);
        check("Distinct substrings of banana = 15", sa.distinctSubstrings() == 15);
        SuffixAutomaton sam = new SuffixAutomaton("banana");
        check("Suffix automaton distinct substrings = 15", sam.distinctSubstrings() == 15);
        check("Suffix automaton count('ana') = 2", sam.count("ana") == 2);
        check("Suffix automaton contains('nan') / not 'nab'", sam.contains("nan") && !sam.contains("nab"));
        int[] lcs = SuffixArray.longestCommonSubstring("xabcdy", "zabcdw");
        check("LCS(SA+LCP) of xabcdy/zabcdw = 'abcd' (4)", lcs[0] == 4);
        check("LCS(automaton) agrees", new SuffixAutomaton("xabcdy").longestCommonSubstring("zabcdw")[0] == 4);
        boolean saAll = true;
        for (int t = 0; t < 100; t++) {
            String text = randStr(80, 3), pat = randStr(1 + rnd(4), 3);
            int[] o = new SuffixArray(text).occurrences(pat);
            // sort occurrences ascending (insertion sort) to compare with the naive scan
            for (int i = 1; i < o.length; i++) { int v = o[i], j = i - 1; while (j >= 0 && o[j] > v) { o[j + 1] = o[j]; j--; } o[j + 1] = v; }
            saAll &= same(o, StringAlgos.naive(text, pat).positions.toArray());
            saAll &= new SuffixAutomaton(text).count(pat) == o.length;
        }
        check("100 random cases: SuffixArray / Automaton agree with naive", saAll);

        System.out.println("== CO3  Advanced DP ==");
        check("Levenshtein(kitten, sitting) = 3", EditDistance.distance("kitten", "sitting", EditDistance.LEV) == 3);
        check("Two-row Levenshtein == full-table Levenshtein", EditDistance.levenshteinTwoRow("sunday", "saturday") == 3);
        check("Damerau(ab, ba) = 1 but Levenshtein = 2",
            EditDistance.distance("ab", "ba", EditDistance.DL) == 1 && EditDistance.distance("ab", "ba", EditDistance.LEV) == 2);
        check("Weighted: vowel swap cheaper than consonant swap",
            EditDistance.distance("Kukatpally", "Kukatpalli", EditDistance.WEIGHTED) < EditDistance.distance("Kukatpally", "Kukatpalmy", EditDistance.WEIGHTED));
        double[][] d = EditDistance.table("ameerpet", "amerpet", EditDistance.LEV);
        check("Edit script length consistent (ameerpet -> amerpet)", EditDistance.trace("ameerpet", "amerpet", EditDistance.LEV, d).length >= 8);
        MatrixChain mc = new MatrixChain(new int[]{30, 35, 15, 5, 10, 20, 25});
        check("Matrix chain (CLRS example) = 15125", mc.cost == 15125);
        check("Matrix chain beats left-to-right order", mc.cost < mc.leftToRightCost);
        OptimalBST ob = new OptimalBST(new String[]{"10", "12", "20"}, new long[]{34, 8, 50});
        check("Optimal BST (keys 10,12,20 / freq 34,8,50) = 142", ob.cost == 142);
        check("Knuth O(n^2) optimisation gives the same cost", ob.costKnuth == ob.cost);
        OptimalBST ob2 = new OptimalBST(Data.POPULAR_NAMES, Data.POPULAR_FREQ);
        check("OBST (10 stations): Knuth == cubic and <= balanced BST", ob2.costKnuth == ob2.cost && ob2.cost <= ob2.balancedCost);
        check("OBST Knuth does fewer operations", ob2.opsKnuth < ob2.opsCubic);
        Network net = new Network();
        int[] tg = {26, 0, 49};
        BitmaskRoute br = new BitmaskRoute(net.dist, 10, tg, false);
        long best = Long.MAX_VALUE;
        int[][] perms = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
        for (int[] p : perms) {
            long c = net.dist[10][tg[p[0]]] + net.dist[tg[p[0]]][tg[p[1]]] + net.dist[tg[p[1]]][tg[p[2]]];
            if (c < best) best = c;
        }
        check("Bitmask DP == brute force over 3! permutations", br.cost == best);
        check("Bitmask DP tour starts at the start station", br.order[0] == 10 && br.order.length == 4);
        Alignment g = Alignment.align(new String[]{"A", "B", "C", "D"}, new String[]{"A", "C", "D"}, false, 2, -1, -2);
        check("Needleman-Wunsch: A B C D vs A C D -> score 4", g.score == 4);
        Alignment l = Alignment.align(Data.ROUTE_STOPS[0], Data.ROUTE_STOPS[5], true, 2, -1, -2);
        check("Smith-Waterman finds the shared Red Line segment (8 stations -> score 16)", l.score == 16);

        System.out.println("\nResult: " + pass + " passed, " + fail + " failed");
        if (fail > 0) System.exit(1);
    }
}
