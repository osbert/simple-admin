# simple-admin

A Clojure library built on top of cemerick/friend designed to protect
some routes from public access. A single admin user and password are
specified as environment variables. HTTPS can also enforced on these
routes, but is not by default.

Credentials/behavior can be customized through the environment
variables (or system properties, since weavejester/environ is used):

  * ADMIN_PASSWORD (defaults to "default-admin-password")
  * ADMIN_USERNAME (defaults to "admin")
  * ADMIN_FORCE_HTTPS (defaults to nil)

The following routes are added:

  * GET admin/login
  * POST admin/login (from cemirick/friend)
  * POST admin/logout

Future work to be done:

  * Allow custom login form view
  * Allow custom post-login/logout redirect

## Installation

Include the following dependency in your `project.clj` file:

```clojure
:dependencies [[osbert/simple-admin "0.1.0-SNAPSHOT"]]
```

## Usage

```clojure
(use 'compojure.core) ; For defroutes in example below
(use 'compojure.handler) ; For additional middleware required by friend
(use ['simple-admin.core :only ['wrap-simple-admin wrap-admin-only]])
```

Split your routes into two types, publicly facing and admin required:

```clojure
(defroutes app-routes
  (GET "/" [] "Hello world!"))

(defroutes admin-routes
  (GET "/private" [] "Hello admin!"))
```

`wrap-admin-only` will wrap admin-routes in a context and require
logging in as an admin using an interactive form before accessing
admin-routes.

`wrap-simple-admin` includes the public admin routes and adds friend
middleware. In this example, to then combine these two sets of routes
to create your entire application:

```clojure
(def app (-> (routes app-routes (wrap-admin-only admin-routes))
             wrap-simple-admin
             site))
```

Now, navigating to `/admin/private` should redirect to `/admin/login`.
Logging in should allow you to view `/admin/private`. To logout
afterwards, add something to your `/admin/private` views to send a
POST to `(simple-admin.core/logout-uri)`.

## License

Copyright © 2013 Osbert Feng

Distributed under the Eclipse Public License, the same as Clojure.
