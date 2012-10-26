(ns websockets-demo.core
  (:import [org.webbitserver WebServers WebSocketHandler EventSourceHandler EventSourceMessage]
           [org.webbitserver.handler StaticFileHandler]))

(def ws-connections (atom #{}))
(def es-connections (atom #{}))

(defn send-to-subscribers [msg connections]
  (doseq [c connections]
    (println "sending" msg "to" c)
    (.send c msg)))

(defn send-loop []
  (while true
    (do
      (send-to-subscribers (str (rand)) @ws-connections)
      (send-to-subscribers (EventSourceMessage. (str (rand))) @es-connections)
      (. Thread (sleep 1000)))))

(defn -main []
  (println "Starting server...")
  (doto (WebServers/createWebServer 9000)
    (.add "/ws"
          (proxy [WebSocketHandler] []
            (onOpen [c]
              (println "opened" c)
              (swap! ws-connections conj c))
            (onClose [c]
              (println "closed" c)
              (swap! ws-connections disj c))
            (onMessage [c j] (println c j))))
    (.add (StaticFileHandler. "."))
    (.start))
  (doto (WebServers/createWebServer 9001)
    (.add "/es"
          (proxy [EventSourceHandler] []
            (onOpen [c]
              (println "opened" c)
              (swap! es-connections conj c))
            (onClose [c]
              (println "closed" c)
              (swap! es-connections disj c))))
    (.add (StaticFileHandler. "."))
    (.start))
  (println "Server started.")
  (send-loop))
