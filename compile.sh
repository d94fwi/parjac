mkdir -p bin

javac \
 -classpath libs/asm-5.0.4.jar:libs/asm-tree-5.0.4.jar \
 -d bin \
 -sourcepath src \
 -Xdiags:verbose \
 -Xlint:unchecked \
 -Xmaxerrs 1000 \
 $(find src/ -name \*.java)
