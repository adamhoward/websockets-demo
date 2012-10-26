(ns websockets-demo.core
  (:import [org.webbitserver WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler]))

(def connections (atom #{}))

(defn send-to-subscribers [msg connections]
  (doseq [c connections]
    (println "sending" msg "to" c)
    (.send c msg)))

(defn send-loop []
  (while true
    (do
      (send-to-subscribers (str (rand)) @connections)
      (. Thread (sleep 1000)))))

(defn -main []
  (println "Starting server...")
  (doto (WebServers/createWebServer 9000)
    (.add "/ws"
          (proxy [WebSocketHandler] []
            (onOpen [c]
              (println "opened" c)
              (swap! connections conj c))
            (onClose [c]
              (println "closed" c)
              (swap! connections disj c))
            (onMessage [c j] (println c j))))
    (.add (StaticFileHandler. "."))
    (.start))
  (println "Server started.")
  (send-loop))
