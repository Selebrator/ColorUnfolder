If you want to build prefixes of symbolic unfoldings, you need an SMT solver.
We currently support [cvc5](https://github.com/cvc5/cvc5) and [z3](https://github.com/Z3Prover/z3).
For now, you need both to compile `ColorUnfolder`,
but only one to execute it.
If you have a system-wide installation,
that should work for executing `ColorUnfolder`,
but for compiling it the necessary files must be present in this directory (`lib`).
You can use the `get-cvc5.sh` and `get-z3.sh` script in the project root directory
to automatically obtain either SMT solver locally.
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

# Troubleshooting
By default, these scripts download a prebuilt distribution of the SMT solvers.
If your system libraries are incompatible with the downloaded versions,
you can attempt to build the problematic library yourself.
Change the last line of the provided scripts `get-cvc5.sh` and `get-z3.sh`
from `download` to `build`. For an automated build of the library,
given that the required build tools are installed.
