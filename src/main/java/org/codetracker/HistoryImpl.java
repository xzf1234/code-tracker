package org.codetracker;

import com.google.common.graph.EndpointPair;
import org.codetracker.api.CodeElement;
import org.codetracker.api.Edge;
import org.codetracker.api.Graph;
import org.codetracker.api.History;
import org.codetracker.change.Change;

import java.util.*;

public class HistoryImpl<N extends CodeElement> implements History<N> {
  private final Graph<N, Edge> graph;
  private final HistoryReportImpl historyReport;
  private final List<HistoryInfo<N>> historyInfoList;

  public HistoryImpl(Graph<N, Edge> graph, HistoryReportImpl historyReport) {
    this.graph = graph;
    this.historyReport = historyReport;
    this.historyInfoList = processHistory(graph);
  }

  private static <T extends CodeElement> List<HistoryInfo<T>> processHistory(Graph<T, Edge> graph) {
    List<HistoryInfo<T>> historyInfoList = new ArrayList<>();
    if (graph == null) return historyInfoList;

    Set<EndpointPair<T>> edges = graph.getEdges();

    for (EndpointPair<T> edge : edges) {
      Edge edgeValue = graph.getEdgeValue(edge).get();
      if (Change.Type.NO_CHANGE.equals(edgeValue.getType())) continue;
      HistoryInfoImpl<T> historyInfoImpl =
          new HistoryInfoImpl<>(
              edge.source(),
              edge.target(),
              edgeValue.getChangeList(),
              edge.target().getVersion().getId(),
              edge.target().getVersion().getTime(),
              edge.target().getVersion().getAuthorName());
      historyInfoList.add(historyInfoImpl);
    }
    Collections.sort(historyInfoList);
    return historyInfoList;
  }

  @Override
  public Graph<N, Edge> getGraph() {
    return graph;
  }

  @Override
  public HistoryReport getHistoryReport() {
    return historyReport;
  }

  @Override
  public List<HistoryInfo<N>> getHistoryInfoList() {
    return historyInfoList;
  }

  public static class HistoryInfoImpl<C extends CodeElement> implements HistoryInfo<C> {
    private final C elementBefore;
    private final C elementAfter;
    private final Set<Change> changeList = new HashSet<>();
    private final String commitId;
    private final long commitTime;
    private final String committerName;

    /**
     * @param elementBefore Element Before
     * @param elementAfter Element After
     * @param changeList Change List
     * @param commitId Commit ID
     * @param commitTime Commit Time
     */
    public HistoryInfoImpl(
        C elementBefore,
        C elementAfter,
        Set<Change> changeList,
        String commitId,
        long commitTime,
        String committerName) {
      this.elementBefore = elementBefore;
      this.elementAfter = elementAfter;
      this.changeList.addAll(changeList);
      this.commitId = commitId;
      this.commitTime = commitTime;
      this.committerName = committerName;
    }

    @Override
    public C getElementBefore() {
      return elementBefore;
    }

    @Override
    public C getElementAfter() {
      return elementAfter;
    }

    @Override
    public Set<Change> getChangeList() {
      return changeList;
    }

    @Override
    public String getCommitId() {
      return commitId;
    }

    @Override
    public long getCommitTime() {
      return commitTime;
    }

    @Override
    public String getCommitterName() {
      return committerName;
    }

    @Override
    public int compareTo(HistoryInfo<C> toCompare) {
      return Long.compare(this.commitTime, toCompare.getCommitTime());
    }
  }

  public static class HistoryReportImpl implements HistoryReport {

    private int analysedCommits = 0;
    private int gitLogCommandCalls = 0;
    private int step2 = 0;
    private int step3 = 0;
    private int step4 = 0;
    private int step5 = 0;

    public int getAnalysedCommits() {
      return analysedCommits;
    }

    public void analysedCommitsPlusPlus() {
      analysedCommits++;
    }

    public void gitLogCommandCallsPlusPlus() {
      gitLogCommandCalls++;
    }

    public int getGitLogCommandCalls() {
      return gitLogCommandCalls;
    }

    public int getStep2() {
      return step2;
    }

    public int getStep3() {
      return step3;
    }

    public int getStep4() {
      return step4;
    }

    public int getStep5() {
      return step5;
    }

    public void step2PlusPlus() {
      step2++;
    }

    public void step3PlusPlus() {
      step3++;
    }

    public void step4PlusPlus() {
      step4++;
    }

    public void step5PlusPlus() {
      step5++;
    }
  }
}
