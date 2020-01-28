# Code generator

This is the code generator for Java port of [@phensley/cldr](https://github.com/phensley/cldr-engine) TypeScript library.

It exists to save a lot of time hand-converting types and schema from the TypeScript library. It also enables the Java
library to reuse the resource packs from the TypeScript library as-is.

It also builds several of the options types used in the library, like `DateFormatOptions`, generating several helper methods to make they easy to use and maintain.
