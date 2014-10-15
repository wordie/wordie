# wordie

an extension for Google Chrome that allows you to lookup words from multiple data sources.

This is the version developed during ClojureCup 2014.

## Usage

To compile the extension, execute:

```bash
cd wordie-extension
lein cljsbuild once prod
```

To compile a jar-file for the server, execute:

```bash
cd wordie-server
lein uberjar
```

See wordie-server/README.md for more details.

## License

Copyright © 2014 Vesa Marttila & Pavel Prokopenko

Distributed under the Eclipse Public License either version 1.0.