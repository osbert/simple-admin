(defproject simple-admin "0.1.0-SNAPSHOT"
  :description "Single user admin with password specified by the environment."
  :url "http://github.com/osbert/simple-admin"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [com.cemerick/friend "0.1.3" :exclusions [org.clojure/core.incubator]]
                 [environ "0.4.0"]])
