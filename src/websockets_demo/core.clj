(ns websockets-demo.core
  (:import [org.webbitserver WebServers WebSocketHandler EventSourceHandler EventSourceMessage]
           [org.webbitserver.handler StaticFileHandler]))

(def ws-connections (atom #{}))
(def es-connections (atom #{}))

;;; Send a message to each connection
(defn send-to-subscribers [msg connections]
  (doseq [c connections]
    (println "sending" msg "to" c)
    (.send c msg)))

;;; Every second, send a random number between 0 and 1 to all connections
(defn send-loop []
  (while true
    (do
      (send-to-subscribers (str (rand)) @ws-connections)
      (send-to-subscribers (EventSourceMessage. (str (rand))) @es-connections)
      (. Thread (sleep 1000)))))

;;; Start the server and the send loop
(defn -main []
  (println "Starting server...")
  (doto (WebServers/createWebServer 9000)
    ;; Mount WebSocketHandler at /ws
    (.add "/ws"
          (proxy [WebSocketHandler] []
            (onOpen [c]
              (println "opened" c)
              (swap! ws-connections conj c))
            (onClose [c]
              (println "closed" c)
              (swap! ws-connections disj c))
            (onMessage [c j] (println c j))))
    ;; Mount EventSourceHandler at /es
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
