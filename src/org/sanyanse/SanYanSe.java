package org.sanyanse;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.sanyanse.colorer.BasicBacktrackColorer;
import org.sanyanse.colorer.FoldingColorer;
import org.sanyanse.colorer.MultiColorer;
import org.sanyanse.common.ColoringResult;
import org.sanyanse.common.GraphColorer;
import org.sanyanse.common.GraphLoader;
import org.sanyanse.common.GraphSpec;
import org.sanyanse.loader.IIDFileLoader;
import org.sanyanse.loader.PetersenLoader;
import org.sanyanse.loader.RandomGraphLoader;
import org.sanyanse.writer.StdoutGraphSpecWriter;
import org.sanyanse.writer.StdoutResultWriter;


public class SanYanSe
{
  static class SimpleThreadFactory implements ThreadFactory, Thread.UncaughtExceptionHandler
  {
    public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setUncaughtExceptionHandler(this);
      return t;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable)
    {
      System.out.print(throwable.toString());
    }
  }

  public static void main(String[] args)
  {
    GraphLoader loader;

    String graphName = args.length > 0 ? args[0] : "memory";

    //= LinkedInFileLoader.create(args[0]);
//    loader = new RandomGraphLoader(32, 32, 1.00, 0);
//    loader = IIDFileLoader.create("/home/brian/src/IID/250/4.00/graph_2835");
    loader = new PetersenLoader();
    GraphSpec graphSpec = loader.load();

    System.out.println("graph spec");
    StdoutGraphSpecWriter.create().write(graphSpec);
    System.out.println();

    List<GraphColorer> colorers = new ArrayList<GraphColorer>();
//    colorers.add(new BasicBacktrackColorer(graphSpec));
    colorers.add(new FoldingColorer(graphSpec));

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new SimpleThreadFactory());
    MultiColorer mc = MultiColorer.create(executor, colorers);

    try
    {
      ColoringResult result = mc.call();
      if (result == null)
      {
        result = ColoringResult.createNotColorableResult();
      }

      String outfileName = String.format("%s_%s_out", "sanyanse", graphName);
      StdoutResultWriter.create().write(result);
    }
    catch (Exception e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    executor.shutdown();
  }
}
