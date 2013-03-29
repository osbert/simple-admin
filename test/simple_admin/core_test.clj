(ns simple-admin.core-test
  (:use clojure.test
        simple-admin.core
        ring.mock.request
        compojure.core
        compojure.handler))

(defroutes test-app-routes
  (GET "/" [] "Hello world!"))

(defroutes test-admin-routes
  (GET "/private" [] "Hello admin!"))

(def test-app (site (routes test-app-routes (wrap-simple-admin test-admin-routes))))

(deftest test-simple-admin
  (testing "GET / should return 200"
    (let [response (test-app (request :get "/"))]
      (is (= (:status response) 200))))

  (testing "GET /private should return 302 to /admin/login"
    (let [response (test-app (request :get "/private"))]
      (is (= (:status response) 302))
      (is (= (-> response :headers (get "Location")) "http://localhost/admin/login"))))

  (testing "GET /admin/login"
    (let [response (test-app (request :get "/admin/login"))]
      (is (= (:status response) 200))))

  (testing "POST /admin/login"
    (let [response (test-app (request :post "/admin/login" {:username "admin" :password "default-admin-password"}))]
      (is (= (:status response) 303))
      (is (= (-> response :headers (get "Location")) "/"))))

  (testing "POST /admin/login incorrect creds"
    (let [response (test-app (request :post "/admin/login" {:username "admin" :password "wrong-password"}))]
      (is (= (:status response) 302))
      (is (= (-> response :headers (get "Location")) "http://localhost/admin/login?&login_failed=Y&username=admin")))))

(deftest test-custom-creds
  (with-redefs
    [environ.core/env (merge environ.core/env {:admin-password "custom-password" :admin-username "custom-user"})]
    (let [my-app (site (routes test-app-routes (wrap-simple-admin test-admin-routes)))]
      (testing "POST /admin/login"
        (let [response (my-app (request :post "/admin/login" {:username "custom-user" :password "custom-password"}))]
          (is (= (:status response) 303))
          (is (= (-> response :headers (get "Location")) "/"))))

      (testing "POST /admin/login default creds"
        (let [response (my-app (request :post "/admin/login" {:username "admin" :password "default-admin-password"}))]
          (is (= (:status response) 302))
          (is (= (-> response :headers (get "Location")) "http://localhost/admin/login?&login_failed=Y&username=admin")))))))

(deftest test-rebind
  (binding [*admin-prefix* "/booyah"]
    (let [my-app (site (routes test-app-routes (wrap-simple-admin test-admin-routes)))]
      (testing "GET / should return 200"
        (let [response (my-app (request :get "/"))]
          (is (= (:status response) 200))))

      (testing "GET /private should return 302 to /booyah/login"
        (let [response (my-app (request :get "/private"))]
          (is (= (:status response) 302))
          (is (= (-> response :headers (get "Location")) "http://localhost/booyah/login"))))

      (testing "GET /booyah/login"
        (let [response (my-app (request :get "/booyah/login"))]
          (is (= (:status response) 200)))))))

(deftest test-forced-https
  (with-redefs
    [environ.core/env (merge environ.core/env {:admin-force-https true})]
    (testing "GET /admin/login should redirect to https version"
      (let [response (test-app (request :get "/admin/login"))]
        (is (= (:status response) 302))
        (is (= (-> response :headers (get "Location")) "https://localhost/admin/login"))))

    (testing "GET https login should return 200"
      (let [response (test-app (request :get "https://localhost/admin/login"))]
        (is (= (:status response) 200))))))

