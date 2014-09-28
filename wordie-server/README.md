wordie server
=============

## Building

To compile you need to have two additional files:

    /resources/wordie_server/merriam_webster/keys.clj
    /resources/wordie_server/yandex/keys.clj

An example of `yandex/keys.clj`:

    (ns wordie-server.yandex.keys)

    (def api
      "YOUR KEY")

And an example of `merriam_webster/keys.clj`:

    (ns wordie-server.merriam-webster.keys)

    (def dictionary
      "YOUR KEY")

    (def thesaurus
      "YOUR KEY")

After you have these two files building is as simple as running: `lein uberjar`

## Running

Once you have the JAR compiled just run `java -jar target/wordie.jar`. After which you should have a Jetty server running in localhost on port 3000.
