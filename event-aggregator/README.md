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
 
