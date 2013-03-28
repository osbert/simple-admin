(ns simple-admin.core
  (:use compojure.core
        environ.core
        [hiccup.page :only [html5 include-css include-js]]
        [hiccup.core :only [html]]
        [hiccup.def :only [defelem]]
        [ring.util.anti-forgery :only [anti-forgery-field]]
        [ring.middleware.anti-forgery :only [wrap-anti-forgery]])
  (:require [cemerick.friend :as friend]
            [cemerick.friend
             [workflows :as workflows]
             [credentials :as creds]]
            [hiccup.form :as form]))

(def ^:dynamic *admin-prefix* "/admin")

(def ^:dynamic *login-route* "/login")
(def ^:dynamic *logout-route* "/logout")

(defn login-uri [] (clojure.string/join "" [*admin-prefix* *login-route*]))
(defn logout-uri [] (clojure.string/join "" [*admin-prefix* *logout-route*]))

;; NOTE: When running behind a proxy like on elastic beanstalk or
;; Heroku, re-bind this to friend/requires-scheme-with-proxy before
;; calling wrap-simple-admin, see associated friend documentation.
(def ^:dynamic *friend-scheme-middleware* friend/requires-scheme)

(def admins {"admin" {:username (or (env :admin-username) "admin")
                      :password (creds/hash-bcrypt (or (env :admin-password) "default-admin-password"))
                      :roles #{::admin}}})

(defn wrap-force-https
  ([handler] (wrap-force-https handler *friend-scheme-middleware*))
  ([handler friend-requires-scheme]
     (if (boolean (env :admin-force-https))
       (friend-requires-scheme handler :https)
       handler)))

(defelem login-form []
  (form/form-to [:post (login-uri)]
                (form/text-field {:placeholder "username"} "username")
                (form/password-field {:placeholder "password"} "password")
                (anti-forgery-field)
                (form/submit-button "Login")))

(defroutes public-admin-routes
  (GET *login-route* [] (html5 [:head [:title "Simple Admin Login"]] [:body (login-form)]))
  (friend/logout (POST *logout-route* request (ring.util.response/redirect "/"))))

(defn public-admin-handler []
  (context *admin-prefix* request
           (-> public-admin-routes
               wrap-anti-forgery
               wrap-force-https)))

(defn wrap-admin-only
  "Middleware that forces authorization as an admin."
  [handler]
  (-> handler
      (friend/wrap-authorize #{::admin})
      wrap-anti-forgery
      wrap-force-https))

(defn wrap-simple-admin
  "Middleware to only allow routes in admin-handler to be accessed by an admin."
  [admin-handler]
  (-> (routes (public-admin-handler)
              (wrap-admin-only admin-handler))
      (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn admins)
                            :workflows [(workflows/interactive-form :login-uri (login-uri))]
                            :login-uri (login-uri)
                            :default-landing-uri "/"})))