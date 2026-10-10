# MetroMind - Intelligent Public Transport Platform
**Advanced Algorithms project - Review-1 (Modules 1-3: CO1, CO2, CO3)**

A real-time web application. The **engine is pure Java** (every algorithm hand-built, **no `java.util.*`** anywhere in the code), served by a tiny built-in HTTP server to an **HTML + CSS + JavaScript** interface.

---
## 1. How to run (VS Code)

**Requirement:** JDK 11 or newer (`javac -version` must work). In VS Code install the *Extension Pack for Java* (VS Code will suggest it).

1. Unzip the project and open the **`metromind`** folder in VS Code (*File -> Open Folder*).
2. Press **F5** and choose **"MetroMind - Start server"** (or open `src/metromind/Main.java` and click *Run*).
3. Open **http://localhost:8080** in your browser.

Prefer the terminal? In the project root:

| System | Start the app | Run the tests |
|---|---|---|
| Windows | `run.bat` | `run-tests.bat` |
| macOS / Linux | `./run.sh` | `./run-tests.sh` |

Manual commands: `javac -d out $(find src -name "*.java")` then `java -cp out metromind.Main 8080`.
(Run from the project root - the server looks for the `web/` folder there. Use another port with `java -cp out metromind.Main 9000`.)

---
## 2. Problem statement
A metro operator manages stations, routes, passenger requests, vehicles, maintenance reports and a continuous stream of transport events. Most of this is **text and network data**. Staff and passengers must search it instantly, correct mistyped station names, scan alerts for incidents, compare route documents, and plan multi-stop trips. Naive scanning and brute force do not scale, so MetroMind applies advanced string, suffix-structure and dynamic-programming algorithms.

**Objectives**
1. Search station names, alerts and ticket codes in linear time (CO1).
2. Index route documents so substring/similarity queries are fast (CO2).
3. Correct station names, plan optimal multi-stop trips, order data operations and shape an access-frequency index with DP (CO3).
4. Show, with live counters and benchmarks, *why* each algorithm was chosen.

**Inputs -> Outputs**

| Input | Output |
|---|---|
| pattern / ticket code | match positions, snippets, comparison counts |
| keyword list + alert feed | highlighted alerts, severity (HIGH / WATCH / NORMAL) |
| route description | repeating unit and period |
| two route documents | longest common text, similarity matrix |
| mistyped station name | "Did you mean ...?", edit script, DP table |
| start + up to 12 destinations | cheapest visiting order, legs, map path |
| matrix dimensions / station frequencies | optimal multiplication order / optimal BST |

**Use cases:** passenger station search - control-room alert scanning - ticket verification - "did you mean" correction - multi-stop trip planning - detecting repeated / shared route descriptions - comparing route variants.

---
## 3. DSA mapping (Modules 1-3)

| Module / CO | Algorithm | MetroMind feature | File |
|---|---|---|---|
| M1 CO1 | Naive | baseline for comparison | `engine/StringAlgos.java` |
| M1 CO1 | **KMP** (failure function) | station-name & alert search | `StringAlgos.java` |
| M1 CO1 | **Z-function** | repeated route descriptions, period detection | `StringAlgos.java` |
| M1 CO1 | **Rabin-Karp** (rolling hash, double hash) | ticket & service-code search | `StringAlgos.java` |
| M1 CO1 | **Aho-Corasick** | multi-keyword alert scanner | `engine/AhoCorasick.java` |
| M2 CO2 | **Suffix Array** (prefix doubling, O(n log^2 n)) | index of all route documents | `engine/SuffixArray.java` |
| M2 CO2 | **LCP / Kasai** O(n) | common & repeated route text, distinct substrings | `SuffixArray.java` |
| M2 CO2 | **Suffix Automaton** | fast substring retrieval and counting | `engine/SuffixAutomaton.java` |
| M2 CO2 | SA-IS, Suffix Trees | conceptual / preview level (as per syllabus) | report / viva notes |
| M3 CO3 | **Levenshtein, Damerau-Levenshtein, weighted** (Wagner-Fischer) | station-name correction | `engine/EditDistance.java` |
| M3 CO3 | **Bitmask DP** (Held-Karp) | multi-stop trip planning | `engine/BitmaskRoute.java` |
| M3 CO3 | **Matrix-Chain** (interval DP) | ordering transport-data operations | `engine/MatrixChain.java` |
| M3 CO3 | **Optimal BST** (cubic + Knuth O(n^2)) | index of frequently accessed stations | `engine/OptimalBST.java` |
| M3 CO3 | **Needleman-Wunsch / Smith-Waterman** | route alignment | `engine/Alignment.java` |
| support | Floyd-Warshall | travel times between stations | `engine/Network.java` |

Modules 4-6 (network flow, NP-completeness, randomised/parallel) are *not* part of Review-1 and are intentionally not included.

---
## 4. Complexity summary

| Algorithm | Time | Space |
|---|---|---|
| Naive | O(nm) | O(1) |
| KMP | O(n + m) | O(m) |
| Z-function | O(n + m) | O(n + m) |
| Rabin-Karp | O(n + m) avg, O(nm) worst | O(1) |
| Aho-Corasick | O(n + sum\|P\| + matches) | O(sum\|P\| * sigma) |
| Suffix array (doubling) | build O(n log^2 n), query O(m log n) | O(n) |
| Kasai LCP | O(n) | O(n) |
| Suffix automaton | build O(n), query O(m) | O(n * sigma) |
| Edit distance (Wagner-Fischer) | O(nm) | O(nm), O(min(n,m)) with two rows |
| Needleman-Wunsch / Smith-Waterman | O(nm) | O(nm) |
| Matrix-chain | O(n^3) | O(n^2) |
| Optimal BST | O(n^3), Knuth O(n^2) | O(n^2) |
| Bitmask DP | O(2^k * k^2) | O(2^k * k) (brute force is O(k!)) |

The **Complexity & Benchmark** page of the app shows these live (comparison counts, timings on up to 2 million characters, adversarial inputs).

---
## 5. Project layout
```
metromind/
  README.md  VIVA_NOTES.md
  run.bat  run.sh  run-tests.bat  run-tests.sh
  .vscode/                  launch + settings for VS Code
  src/metromind/
    Main.java               HTTP server (static files + /api/*)
    Tests.java              38 test cases (no JUnit needed)
    engine/                 <- all algorithms, hand-built, no java.util
      IntList, MatchResult, StringAlgos, AhoCorasick,
      SuffixArray, SuffixAutomaton,
      EditDistance, MatrixChain, OptimalBST, BitmaskRoute, Alignment,
      Network, Data
    web/                    API layer (Api, Jw JSON writer, Params, Util)
  web/                      index.html, css/style.css, js/app.js
```

## 6. Testing
`run-tests` prints 38 PASS lines, for example: KMP failure/Z arrays on known strings, randomised cross-checks (Naive == KMP == Z == Rabin-Karp, SA/automaton vs naive), `banana` suffix array & LCP, edit-distance cases (kitten/sitting = 3), matrix-chain CLRS example = 15125, optimal-BST example = 142, bitmask DP vs brute force, and Smith-Waterman finding the shared 8-station segment.

## 7. Notes & honest limitations
* The network and documents are a **synthetic dataset inspired by the Hyderabad metro corridors**; replace `engine/Data.java` to use real data.
* Suffix array uses prefix doubling (practical O(n log^2 n)); SA-IS (O(n)) and suffix trees are explained at concept level as the syllabus specifies.
* "Damerau-Levenshtein" is the *optimal string alignment* variant (adjacent transpositions).
* The engine uses only `java.lang`; the server layer uses `com.sun.net.httpserver`, `java.io`, `java.nio`, `java.net` - never `java.util`.

## Website-style dashboard
The home page is designed like a real transit operations website. It includes a live clock, network status, simulated departure board, service feed, passenger tools and one-click algorithm demos. The live-looking dashboard is intentionally labelled as a simulated demo feed because the academic dataset is synthetic and the project does not connect to a live metro API.
