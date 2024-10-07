ColorUnfolder
=============

This program implements the generalized Esparza/Römer/Vogler unfolding algorithm for high-level (colored) Petri nets.
That is, an algorithm to find a finite complete prefix of the symbolic unfolding.

Other authors also use the term "unfolding" differently,
to refer to the low-level net expressed through the high-level net.
We call that low-level net the expansion of the high-level net
and also implement an algorithm to calculate the expansion.

We also implement a just-in-time expansion algorithm
for building the low-level unfolding of the expansion of a high-level net
without building the expansion.

Features
--------

- [x] High-level net to symbolic unfolding
- [x] High-level net to low-level unfolding
- [x] High-level net to low-level expansion net
- [x] Low-level net to low-level unfolding
- [x] Finite complete prefix of unfolding using cut-offs
- [x] Unfolding up to bounded depth
- [x] Reachability of target transitions
- [x] Command-line user interface
- [x] Net parser (`hllola` format)
- [x] Unfolding/prefix renderer
- [x] Internal structure renderer
- [ ] Tuples

Non-goals
---------

- Non-safe nets. We require the input net to be safe (1-bounded).
  That is, in every reachable marking there is at most one token on a place.
  Some authors call a high-level net 1-bounded if its expansion is 1-bounded.
  In the high-level net that is equivalent to
  every reachable marking having at most one token of every color on a place.
  Our requirement is stricter.

Related Tools
-------------

- [Mole](http://www.lsv.fr/~schwoon/tools/mole/)
  is a well known implementation of the Esparza/Römer/Vogler unfolding algorithm for low-level Petri nets.

- We use [cvc5](https://github.com/cvc5/cvc5) or [Z3](https://github.com/Z3Prover/z3) to decide the predicates.

Related Publications
--------------------

- Nick Würdemann, Thomas Chatain, Stefan Haar, and Lukas Panneke.
  “Taking Complete Finite Prefixes to High Level, Symbolically”.
  In: Fundamenta Informaticae, vol. 192, iss. 3-4, pp. 313-361.
  doi: [10.3233/FI-242196](https://www.doi.org/10.3233/FI-242196).
  [Open Access](https://arxiv.org/abs/2311.11443).\
  Journal article about the theory and this tool.

- Nick Würdemann, Thomas Chatain, and Stefan Haar.
  “Taking Complete Finite Prefixes to High Level, Symbolically”.
  In: PETRI NETS ’23. LNCS 13929. 2023, pp. 123–144.
  doi: [10.1007/978-3-031-33620-1_7](https://www.doi.org/10.1007/978-3-031-33620-1_7).
  [Open Access](https://hal.science/hal-04029490v1).\
  Introducing the algorithm for building
  complete finite prefixes of symbolic unfoldings for high-level Petri nets.
  This is our primary source.

- Thomas Chatain and Claude Jard.
  “Symbolic Diagnosis of Partially Observable Concurrent Systems”.
  In: FORTE ’04. LNCS 3235. 2004, pp. 326–342.
  doi: [10.1007/978-3-540-30232-2_21](https://www.doi.org/10.1007/978-3-540-30232-2_21).\
  Introducing symbolic unfoldings.

- Javier Esparza, Stefan Römer, and Walter Vogler.
  “An Improvement of McMillan’s Unfolding Algorithm”.
  In: Formal Methods in System Design 20.3 (2002), pp. 285–310.
  doi: [10.1023/A:1014746130920](https://www.doi.org/10.1023/A:1014746130920).\
  Low-level unfolding algorithm that the symbolic approach is based on.

- Victor Khomenko and Maciej Koutny.
  “Branching Processes of High-Level Petri Nets”.
  In: TACAS ’03. LNCS 2619. 2003, pp. 458–472.
  doi: [10.1007/3-540-36577-X_34](https://www.doi.org/10.1007/3-540-36577-X_34).\
  Introducing an efficient algorithm to build
  complete finite prefixes of low-level unfoldings for high-level Petri nets.

Building on Linux
--------

```sh
./get-cvc5.sh                 # install cvc5 with java bindings
./get-z3.sh                   # install z3 with java bindings
./gradlew buildExecutableApp  # build color-unfolder
./color-unfolder --help       # start using it
```

Bring your own net
-------------------

There are two ways to have `ColorUnfolder` read in your net:

1. **Give a file path or pipe a file into the program.**
   The format supported by `ColorUnfolder` is similar to `hllola`,
   the high-level format of LoLA,
   a well known low-level Petri net analysis tool.
   The documentation of this format is found in the archive
   provided at [LoLA's webpage](https://theo.informatik.uni-rostock.de/theo-forschung/tools/lola/).
   However, they seem to not be using the `hllola` format
   and instead rely on some other tool translating a high-level net into their low-level format.
2. **Write your net as code.**
   Since we care most about parametrized nets for our research,
   we generate all nets programmatically.
   The code for our nets is found in `de.lukaspanneke.masterthesis.examples.Examples`.
   To make the program aware of your net, add it to `de.lukaspanneke.masterthesis.ui.Main#getBuiltinNet`.
