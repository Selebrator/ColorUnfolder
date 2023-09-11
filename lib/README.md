For now, you have to bring your own cvc5 installation.
You have to provide these files:
```
lib
├── cvc5.jar
├── libcvc5jni.so
└── libcvc5.so.1
```
You can use the `get-cvc5.sh` script in the project root directory to automatically build cvc5 locally.
If the script worked, no manual intervention is needed and you can proceed with `gradle buildExecutableApp`.
