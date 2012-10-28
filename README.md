# websockets-demo

A WebSockets **and EventSource** demo in Clojure

## Try it
Clone the repo and run `lein run` (assuming you have [Leiningen 2](http://github.com/technomancy/leiningen) installed.)

For Websockets: http://localhost:9000/websockets-client.html  
For EventSource: http://localhost:9000/eventsource-client.html

You should see a graph that gets updated once per second.

## Dependencies
* [Webbit](https://github.com/webbit/webbit)
* [Smoothie Charts](https://github.com/joewalnes/smoothie/)
