# simple-admin

A Clojure library designed to protect routes from public access. A
single admin user and password are specified as environment variables.
HTTPS can also enforced on these routes.

Credentials/behavior can be customized through the environment
variables (or system properties, since weavejester/environ is used):

  ADMIN_PASSWORD (defaults to "default-admin-password")
  ADMIN_USERNAME (defaults to "admin")
  ADMIN_FORCE_HTTPS (defaults to nil)
  ADMIN_ROUTE_PREFIX (defaults to "/admin")

## Usage

FIXME

## License

Copyright © 2013 Osbert Feng

Distributed under the Eclipse Public License, the same as Clojure.
