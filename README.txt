Parjac is a java compiler, it is supposed to be fast, it is supposed
to use modern tools and thus be easily hackable.

Some things:
*) Development is done using java 8, no support for earlier versions.
*) Compilation is done on directories, not files (internally it is a
   fileset that can be any number of files).
*) Every step should be internally time logged so that it is easy to
   inspect performance.
*) The lexer is hand written.
*) The parser is basically a earley parser, but uses a cache for
   the predicted states. Inspiration from the marpa project.
*) Tests are done with testng.
*) Uses asm for bytecode handling.

Building:
"gradle build"

Running:
"gradle run -Pargs='-i /some/source/dir -d /some/output/dir'"

Gradle is icky when it comes to handling command line arguments.

Alternative building and running:
"./compile.sh" and "./run.sh -i /some/source/dir -d /some/output/dir",
but note that this requires that the dependencies are found in the
libs folder.
