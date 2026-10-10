# MetroMind — Presentation Quick Guide

## Start
1. Open the `metromind` folder in VS Code.
2. Make sure `java -version` and `javac -version` work.
3. Double-click **START_HERE.bat** (or run `run.bat`).
4. Browser opens at `http://localhost:8080`.

## Best 3-minute demo
### 1) Dashboard — 30 sec
First show the dashboard like a real transit website.
- Point to **Network operational**, the live clock, average wait, active trains and rider count.
- Show the **Next Departures** board and **Control Room / Service Feed**.
- Click **Open Trip Planner** or use the 3 demo cards.

Say: “MetroMind is an intelligent metro analytics and passenger-support platform. The dashboard gives a real-time-style view, while the Java engine runs the actual algorithms behind search, correction, similarity and route planning.”

**Important:** the dashboard numbers and alerts are a simulated live demo feed built on the project's synthetic metro dataset. They are not a connection to a real metro control system.

### 2) Station & Alert Search — 60 sec (CO1)
- Click **Station & Alert Search**.
- Pattern: `ameerpet`
- Algorithm: **KMP**
- Search in: **Station names**
- Click Search.
- Point to **failure function**, matches, comparisons and time.
- Then choose **Naive** and search again.

Say: “KMP reuses the prefix information, so it avoids unnecessary comparisons. The page also runs all algorithms on the same query so we can compare the work done.”

### 3) Document Similarity — 50 sec (CO2)
- Click **Document Similarity**.
- Leave `every station` and click **Look up**.
- Show occurrences and suffix-array rows.
- Optionally enter `banana` in the small-text box and click **Build**.

Say: “The suffix array indexes the corpus. LCP/Kasai tells us how much neighboring suffixes share, which supports common and repeated text analysis.”

### 4) Station Name Correction — 40 sec (CO3)
- Click **Station Name Correction**.
- Enter `Ameerpat`.
- Click **Correct**.
- Show `Ameerpet`, edit distance and DP table.

Say: “Wagner-Fischer dynamic programming finds the minimum edit cost, so a mistyped station can be corrected to the closest valid name.”

### Bonus if asked
**Trip Planner:** select 4–6 destinations → Find best order. Explain Bitmask DP vs k! brute force.

**Alert Scanner:** leave the default keywords → Scan alerts. Explain Aho-Corasick finds multiple keywords in one pass.

**Complexity & Benchmark:** use this only if the faculty asks about time complexity or performance.

## Important honest point
The dataset is synthetic metro data inspired by Hyderabad Metro corridors. It is used for algorithm demonstration; it is not a live metro feed.
