(defproject osbert/simple-admin "0.1.2"
  :description "Single user admin with password specified by the environment."
  :url "http://github.com/osbert/simple-admin"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [com.cemerick/friend "0.1.3" :exclusions [org.clojure/core.incubator]]
                 [compojure "1.1.5" :exclusions [ring/ring-core]]
                 [hiccup "1.0.2"]
                 [environ "0.4.0"]
                 [ring-anti-forgery "0.2.1"]]
  :profiles {:dev {:dependencies [[ring-mock "0.1.3"]]}})
