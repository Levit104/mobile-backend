package itmo

enum class Config(s: String) {
    SECRET("secret"),
    ISSUER("http://0.0.0.0:8080/"),
   AUDIENCE("http://0.0.0.0:8080/hello"), 
    REALM("Access to 'hello'")
}