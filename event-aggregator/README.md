# Event aggregator
Event aggregator periodically processes the event service database,
transforms and summarised information, and writes data
to Big Query tables.

The output data is then consumed by dashboards or loaded into
data analysis software such as R.

## Accessing Spark UI
During entire runtime, Spark exposes a UI where one can check what jobs are being run,
how big various datasets are, and so on.

In order to access the Spark UI, you need to make port 4040 of the pod available:
```
kubectl port-forward POD_ID 4040:4040
```

Then head to [localhost:4040](http://localhost:4040) and enjoy the Spark UI.
 
## Running the app locally
One of the secrets must be available as a file on the host machine rather than
an environment variable.

First, get that file and store it on your machine:

```
bo show credential production event-aggregator-google-service-account-key event-aggregator-google-service-account-key.json > /tmp/event-aggregator-google-service-account-key.json
```

Next, get all the other event aggregator secrets and paste in IntelliJ configuration:

```
bo show env-vars production event-aggregator | pbcopy
```

Finally, make sure that `BIG_QUERY_SERVICE_ACCOUNT_KEY_PATH` matches the location where you have saved
the key in the very beginning, e.g.:

```
BIG_QUERY_SERVICE_ACCOUNT_KEY_PATH=/tmp/event-aggregator-google-service-account-key.json
```
