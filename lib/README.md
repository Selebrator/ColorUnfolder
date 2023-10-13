If you want to build prefixes of symbolic unfoldings, you need an SMT solver.
We currently support [cvc5](https://github.com/cvc5/cvc5) and [z3](https://github.com/Z3Prover/z3).
For now, you need both to compile `ColorUnfolder`,
but only one to execute it.
If you have a system-wide installation,
that should work for executing `ColorUnfolder`,
but for compiling it the necessary files must be present in this directory (`lib`).
You can use the `get-cvc5.sh` and `get-z3.sh` script in the project root directory
to automatically build an SMT solver locally.
If the script worked, no manual intervention is needed.
Verify the installation by checking that you have these files for cvc5:
```
lib
├── cvc5.jar
├── libcvc5jni.so
└── libcvc5.so.1
```
Or these files for z3:
```
lib
├── com.microsoft.z3.jar
├── libz3java.so
└── libz3.so
```
Now you can proceed with `./gradlew buildExecutableApp`.
