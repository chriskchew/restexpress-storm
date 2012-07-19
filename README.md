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

...the handle() method handles each matching request and will be passed an instance of BodyType that has already been deserialized from JSON.  The method is responsible for validating and authenticating the HTTP request and converting it into one or more Storm emissions as a List&lt;Emission&gt;.  The Emission class is a data wrapper that contains the Object emission and the String streamId, both properties are sent to the Spout's SpoutOutputCollector.

The declareFields() method works exactly like the Spout's declareFields() method and should describe all the emissions that can possibly come from your handler.

The default HTTP response is an empty body with a 201 status code.  You can alter this slightly by throwing Exceptions in your RouteToStreamDefinitionHandler.handle() method.  I recommend throwing ["official" RestExpress exceptions](https://github.com/RestExpress/RestExpress/tree/master/src/java/com/strategicgains/restexpress/exception) because they are automagically mapped to HTTP status codes and can provide you a way to specify error messages in the response body. 

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

## Consistency Note

The request handler does not directly emit the request into Storm.  Instead it is dropped into a concurrent queue shared between the Spout and the RestExpress workers.  The Spout then polls this queue for emissions in the nextTuple() method.  The consequence of this is that there is no guarantee that a successful HTTP request makes it into Storm -- the server could crash with a few messages in the shared queue, and these messages would be lost.

I would love to solve this, but I do not know how to do it using Storm's Spout architecture.

One can circumvent this problem by using a traditional RestExpress installation that writes to persistent storage and then creating a Spout that pulls said storage.

## Ideas For Making It Better

1. Pass the request headers into the RouteToStreamDefinitionHandler.handle method for further request introspection.
2. Have an alternative RouteToStreamDefinitionHandler that receives the raw request without the automagic JSON deserialization.
3. Provide a way for RouteToStreamDefinitionHandler to specify the response body and code for successful requests.
4. Improve the guarantee that a successful HTTP always makes it into Storm.