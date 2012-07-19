restexpress-storm
=================

An HTTP Restful input Spout for Storm using RestExpress.

This project is not currently deployed in production anywhere and should be treated accordingly.  That said, RestExpress is used heavily in production and is generally known to be stable.  Considering there is not much code in the project I do not expect any problems.

## Building

Build using ant:

    ant release

...and look inside the generated dist directory.

## Using

Using the RestExpress Storm spout is a very easy two-step process.

### 1. Implement RouteToStreamDefinitionHandler

You first need to implement one or more handlers that RestExpress will delegate to you whenever a request for a matching url comes in.  The RouteToStreamDefinitionHandler looks like:

    public interface RouteToStreamDefinitionHandler<BodyType> extends Serializable {
        public List<Emission> handle(BodyType body);
        public void declareFields(OutputFieldsDeclarer declarer);
    }

...the handle() method handles each matching request.  The declareFields() method works exactly like the Spout's declareFields() method.

### 2. Wire Into Your Topology

After creating your handler(s), it is necessary to wire up the Spout:

In this example, the ActivityHandler translates POST requests to /activity.json into Activity objects:

    int PARALLELISM = 1;
    TopologyBuilder builder = new TopologyBuilder();
            
    ArrayList<RouteToStreamDefinition<?>> routeToStreamDefinitions = new ArrayList<RouteToStreamDefinition<?>>();
    routeToStreamDefinitions.add(
            new RouteToStreamDefinition<Activity>(
                    "/activity.{format}",
                    Activity.class,
                    new ActivityHandler(Utils.DEFAULT_STREAM_ID),
                    HttpMethod.POST.toString()
            )
    );
    
    RestExpressSpoutConfig restConfig = new RestExpressSpoutConfig(
            1, //Request worker count
            1, //Request executor thread
            50, //Milliseconds for the Spout to delay inside nextTuple() when no messages are present
            8080 //The port the REST server should listen on
    );
    
    //From rest endpoint
    builder.setSpout("activities", new RestExpressSpout(restConfig, routeToStreamDefinitions), PARALLELISM);

    ...