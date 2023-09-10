ColorUnfolder
=============
⚠️ **Under construction** ⚠️

This program implements the generalized Esparza/Römer/Vogler unfolding algorithm for high-level (colored) Petri nets.
That is, an algorithm to find a complete finite prefix of the maximal branching process.

Other authors also use the term "unfolding" differently,
to refer to the low-level net expressed through the high-level net.
We call that low-level net the expansion of the high-level net
and also implement an algorithm to calculate the expansion.

Features
--------

- [x] User interface
- [x] Low-level mode
- [x] Net parser
- [x] Unfolding algorithm
- [x] Unfolding/prefix renderer
- [x] Internal structure renderer
- [x] Unfolding up to bounded depth
- [x] Complete finite prefix with cut-offs
- [x] Reachability of target transitions
- [x] High-level to low-level expansion algorithm
- [ ] Benchmarking tool

Related Tools
-------------

- [Mole](http://www.lsv.fr/~schwoon/tools/mole/)
  is a well known implementation of the Esparza/Römer/Vogler unfolding algorithm for low-level Petri nets.

- We use [cvc5](https://github.com/cvc5/cvc5) to decide the predicates.

Related Publications
--------------------

Nick Würdemann, Thomas Chatain, and Stefan Haar.
“Taking Complete Finite Prefixes to High Level, Symbolically”.
In: PETRI NETS ’23. LNCS 13929. 2023, pp. 123–144.
doi: [10.1007/978-3-031-33620-1_7](https://www.doi.org/10.1007/978-3-031-33620-1_7).
[Open Access](https://hal.science/hal-04029490v1)

Thomas Chatain and Claude Jard.
“Symbolic Diagnosis of Partially Observable Concurrent Systems”.
In: FORTE ’04. LNCS 3235. 2004, pp. 326–342.
doi: [10.1007/978-3-540-30232-2_21](https://www.doi.org/10.1007/978-3-540-30232-2_21).

Javier Esparza, Stefan Römer, and Walter Vogler.
“An Improvement of McMillan’s Unfolding Algorithm”.
In: Formal Methods in System Design 20.3 (2002), pp. 285–310.
doi: [10.1023/A:1014746130920](https://www.doi.org/10.1023/A:1014746130920).

Building
--------

```sh
./get-cvc5.sh                 # build cvc5 with java bindings. Takes a long time ~10 minutes
./gradlew buildExecutableApp  # build color-unfolder
./color-unfolder --help       # start using it
```
