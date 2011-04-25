package org.sanyanse.loader;


import java.lang.reflect.Array;
import java.util.*;

import org.sanyanse.common.Graph;
import org.sanyanse.common.GraphBuilder;
import org.sanyanse.common.GraphLoader;
import org.sanyanse.common.Util;


public class RandomGraphLoader implements GraphLoader
{
  double _connectionPercent;
  int _minNodes;
  int _maxNodes;
  long _seed;

  public RandomGraphLoader(int maxNodes, double connectionPercent) {
    this(maxNodes, maxNodes, connectionPercent, System.currentTimeMillis());
  }

  public RandomGraphLoader(int maxNodes, int minNodes, double colorablePercent, long seed) {
    _maxNodes = maxNodes;
    _minNodes = minNodes;
    if (_maxNodes < _minNodes) {
      throw new IllegalArgumentException(String.format("max nodes must be greater than %s", Integer.toString(_minNodes)));
    }

    _connectionPercent = colorablePercent;
    _seed = seed;
  }

  @Override
  public Graph load()
  {
    Random rnd = new Random(_seed);

    int nodeCnt = Math.max(rnd.nextInt(_maxNodes), _minNodes);

    int bucketSize = (int)(nodeCnt / 3);

    List<String> nodeOrder = new ArrayList<String>(nodeCnt);
    Map<String, Set<String>> buildMap = new HashMap<String, Set<String>>();

    List<Set<String>> buckets = new ArrayList<Set<String>>(3);
    buckets.add(new HashSet<String>(bucketSize));
    buckets.add(new HashSet<String>(bucketSize));
    buckets.add(new HashSet<String>(bucketSize));

    final Random r = new Random();

    for (int i = 1; i <= nodeCnt; i++)
    {
      String nodeId = Util.getNodeName(i);
      int b = r.nextInt(3);
      buckets.get(b).add(nodeId);
      buildMap.put(nodeId, new HashSet<String>());
    }

    double sum = 0.0;

    for (Set<String> bucket : buckets)
    {
      for (String nodeId : bucket)
      {
        nodeOrder.add(nodeId);

        Set<String> neighbors = buildMap.get(nodeId);

        for (int j = 1; j <= nodeCnt; j++)
        {
          String neighborId = Util.getNodeName(j);
          if (neighborId.equals(nodeId)) continue;
          if (bucket.contains(neighborId)) continue;
          double rx = rnd.nextDouble();
          if (rx > _connectionPercent) continue;
          neighbors.add(neighborId);
        }

        neighbors.addAll(neighbors);

        sum += neighbors.size() / (double )nodeCnt;
      }
    }

    // ensure we have unidirectional connections
    for (String nodeId : buildMap.keySet())
    {
      for (String neighborId : buildMap.get(nodeId))
      {
        buildMap.get(neighborId).add(nodeId);
      }
    }

    double realP = sum / (double )nodeCnt;
    System.out.println(String.format("actual distribution = %s", realP));

    Collections.sort(nodeOrder, new Comparator<String>()
    {
      @Override
      public int compare(String o, String o1)
      {
        return (new Double(0.5).compareTo(r.nextDouble()));
      }
    });

    GraphBuilder builder = new GraphBuilder(nodeCnt, realP);

    for (String nodeId : nodeOrder) {
      builder.addNode(nodeId, buildMap.get(nodeId));
    }

    Graph graph = builder.build();

    return graph;
  }
}
