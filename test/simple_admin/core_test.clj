(ns simple-admin.core-test
  (:use clojure.test
        simple-admin.core
        ring.mock.request
        compojure.core))

(defroutes test-app-routes
  (GET "/" [] "Hello world!"))

(defroutes test-admin-routes
  (GET "/private" [] "Hello admin!"))

(def test-app (routes test-app-routes (wrap-simple-admin test-admin-routes)))

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

  (testing "POST /admin/login"))

(deftest test-rebind
  (binding [*admin-prefix* "/booyah"]
    (let [my-app (routes test-app-routes (wrap-simple-admin test-admin-routes))]
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
