package org.sanyanse.writer;


import org.sanyanse.common.Vertex;
import org.sanyanse.common.ColoringResult;
import org.sanyanse.common.ColoringResultWriter;
import org.sanyanse.common.Graph;


public class StdoutResultWriter implements ColoringResultWriter
{
  public void write(ColoringResult result, Graph origGraph) {

    System.out.println(String.format("%s", Boolean.toString(result.IsColored)));

    if (result.IsColored) {
      for (Vertex node : result.Graph.Vertices) {
        System.out.println(String.format("%s:%s", node.Id, node.Color));
      }
    }
  }

  public static StdoutResultWriter create() {
    return new StdoutResultWriter();
  }
}
