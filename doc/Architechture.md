Architechtural Flow:
Client-->Load Balancer-->Multiple Instances of Rate Limiter app-->Redis Storage-->Decision.
Client :Sends request through a browser, Mobile application and such places.
Load Balancer: Distributes the traffic to the different instances.
Multiple instances: Multiple instances of our app having the same code logic and working independently with the shared state of instances through redis storage.
Decision :If token is available, sends the request else send HTTP 429 error to the client.
