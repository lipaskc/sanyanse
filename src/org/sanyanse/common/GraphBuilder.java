package org.sanyanse.common;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class GraphBuilder
{
  public final int NodeCount;
  public final double EdgeProbability;
  public final Vertex[] Vertices;
  public final Map<String, NodeInfo> NodeMap;
  private int _index = 0;

  public GraphBuilder(int nodeCnt)
  {
    this(nodeCnt, -1);
  }

  class NodeInfo
  {
    public Vertex Vertex;
    public Set<String> EdgeSet;
    public int Index;
    public NodeInfo(Vertex node, Set<String> edgeSet, int index)
    {
      Vertex = node;
      EdgeSet = edgeSet;
      Index = index;
    }
  }

  public GraphBuilder(int nodeCnt, double p)
  {
    NodeCount = nodeCnt;
    EdgeProbability = p;
    Vertices = new Vertex[nodeCnt];
    NodeMap = new HashMap<String, NodeInfo>(nodeCnt);
  }

  public Vertex addNode(String nodeId, String[] edges)
  {
    return addNode(nodeId, new HashSet<String>(Arrays.asList(edges)));
  }

  public Vertex addNode(String nodeId, Set<String> edges, int color)
  {
    Vertex node = addNode(nodeId, edges);
    node.Color = color;
    return node;
  }

  public Vertex addNode(String nodeId, Set<String> edges)
  {
    Vertex node = new Vertex(nodeId);
    Vertices[_index] = node;
    NodeMap.put(node.Id, new NodeInfo(node, edges, _index));
    _index++;
    return node;
  }

  public Graph build()
  {
    for (int i = 0; i < NodeCount; i++)
    {
      final NodeInfo info = NodeMap.get(Vertices[i].Id);
      info.Vertex.Edges = new int[info.EdgeSet.size()];

      // TODO: we may wan to sort the edges by degree
      int j = 0;
      for (String id : info.EdgeSet)
      {
        info.Vertex.Edges[j++] = NodeMap.get(id).Index;
      }

//      Arrays.sort(info.Vertex.Edges);
    }

    Graph graph = new Graph(NodeCount, EdgeProbability, Vertices);

    return graph;
  }
}
