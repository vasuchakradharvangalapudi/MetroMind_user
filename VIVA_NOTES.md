# Viva preparation - MetroMind (Modules 1-3)
Be ready to open the code and trace these. Answers are short on purpose - explain in your own words.

## CO1 - String algorithms
**Q1. Why is the naive search O(nm) and when is it worst?**  Every shift may compare up to m characters. Worst on repetitive text, e.g. text `aaaa...a`, pattern `aaaab` (see Benchmark -> Adversarial).

**Q2. Explain the KMP failure function.**  `f[i]` = length of the longest proper prefix of `p[0..i]` that is also its suffix. On a mismatch we jump `k = f[k-1]` instead of restarting, so the text pointer never moves back -> O(n + m). (`StringAlgos.failure`, `kmp`)

**Q3. Why is KMP linear?**  `k` increases at most once per text character and every fallback decreases it, so total fallbacks <= n.

**Q4. How does the Z-function find a pattern?**  Build `P + '$' + T`; a position with `z[i] = |P|` is a match. `z` is computed in O(n) with the window `[l, r)` that reuses earlier values. Period detection: smallest `p` dividing `n` with `z[p] = n - p`.

**Q5. How does Rabin-Karp work and what is a spurious hit?**  Polynomial rolling hash: remove the outgoing char, shift, add the incoming char in O(1). Equal hashes are verified char-by-char; a hash match that is not a real match is a spurious hit. Single hash with mod 101 shows many; double hashing (two large mods) makes them practically zero. Average O(n + m), worst O(nm).

**Q6. Why Aho-Corasick for alerts instead of KMP per keyword?**  KMP costs O(k * n) for k keywords; Aho-Corasick builds one trie with failure links (+ dictionary links for suffix matches) and scans the text once: O(n + total pattern length + matches).

## CO2 - Suffix structures
**Q7. What is a suffix array and how did you build it?**  Sorted order of all suffixes. Prefix doubling: sort by first 2^k characters using ranks from the previous round, my own merge sort -> O(n log^2 n). `SuffixArray.build`.

**Q8. What does Kasai's algorithm do and why is it O(n)?**  Computes `lcp[i]` = common prefix of neighbouring suffixes. Visiting suffixes in text order, `h` drops by at most 1 per step, so total work is O(n).

**Q9. How do you search with a suffix array?**  Two binary searches (lower/upper bound) over the sorted suffixes comparing against the pattern: O(m log n). Number of occurrences = `hi - lo`.

**Q10. Uses of the LCP array in your project?**  Longest repeated substring = max LCP; distinct substrings = n(n+1)/2 - sum(LCP); longest common substring of two documents via `A#B` (adjacent suffixes from different documents).

**Q11. Suffix array vs suffix automaton vs suffix tree?**  SA: small memory, O(m log n) queries. Automaton: O(n) states, O(m) queries, counts occurrences. Suffix tree: O(m) queries but heavy memory (preview level). SA-IS builds SA in O(n) (concept level).

## CO3 - Advanced DP
**Q12. Recurrence of edit distance.**  `d[i][j] = min(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + (a_i != b_j))`; base `d[i][0]=i`, `d[0][j]=j`. Damerau adds `d[i-2][j-2]+1` for an adjacent swap. Weighted version makes vowel swaps (0.4) and sound-alike pairs (0.5) cheaper.

**Q13. Space optimisation?**  Row i needs only row i-1 -> two rows, O(min(n,m)) space (`levenshteinTwoRow`), but then no traceback.

**Q14. Why bitmask DP for the trip planner?**  Visiting k destinations in the best order is TSP-like. `dp[mask][i]` = cheapest way to have visited the set `mask`, ending at `i`; O(2^k k^2) vs k! for brute force. Practical only for small k (that is why we cap at 12). Distances come from Floyd-Warshall.

**Q15. Matrix-chain: recurrence and why DP?**  `m[i][j] = min over k of m[i][k] + m[k+1][j] + p[i]*p[k+1]*p[j+1]`. Parenthesisation changes the cost massively (see 15,125 vs 40,500). Overlapping subproblems -> O(n^3).

**Q16. Optimal BST - what is optimised and what is Knuth's improvement?**  Minimise sum(freq * depth). `e[i][j] = min over root r of e[i][r-1] + e[r+1][j] + w(i,j)`. Knuth: `root[i][j-1] <= root[i][j] <= root[i+1][j]` shrinks the root search -> O(n^2). The app shows both step counts.

**Q17. Needleman-Wunsch vs Smith-Waterman?**  NW is global (fills first row/column with gap penalties, answer at the corner). SW is local (scores clamp at 0, answer is the max cell) so it finds the best shared segment.

**Q18. When is DP the wrong tool?**  No overlapping subproblems, greedy suffices, or the state space is exponential without compression.

## Project-level
**Q19. Why is `java.util` not used?**  Course constraint: the engine is hand-built (`IntList`, own sorts, own JSON writer). The server only uses `java.io`, `java.nio`, `java.net`, `com.sun.net.httpserver`.

**Q20. How did you validate correctness?**  38 tests, including 200 randomised cross-checks of all matchers against the naive search and 100 randomised SA/automaton checks (`Tests.java`).
