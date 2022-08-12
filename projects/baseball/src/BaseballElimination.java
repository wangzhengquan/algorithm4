import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BaseballElimination {
  private class Team {
    int id;
    String name;
    int wins;
    int losses;
    int remains;
    int games[];

    Team(int N, int id) {
      this.games = new int[N];
      this.id = id;
    }

    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Team)) return false;
      Team team = (Team) o;
      return name.equals(team.name);
    }

    public int hashCode() {
      return Objects.hash(name);
    }
  }

  private Team[] teams;
  private Map<String, Team> map;
  private Map<String, Set<String>> certificateMap;
  private int N;

  // create a baseball division from given filename in format specified below
  public BaseballElimination(String filename) {
    if (filename == null) {
      throw new IllegalArgumentException("the argument to BaseballElimination() is null\n");
    }
    certificateMap = new TreeMap<>();
    In in = new In(filename);
    N = in.readInt();
    teams = new Team[N];
    map = new TreeMap<>();
    for (int i = 0; i < N; i++) {
      Team team = new Team(N, i);
      team.name = in.readString();
      team.wins = in.readInt();
      team.losses = in.readInt();
      team.remains = in.readInt();
      for (int j = 0; j < N; j++) {
        team.games[j] = in.readInt();
      }
      teams[i] = team;
      map.put(team.name, team);
    }

  }

  // number of teams
  public int numberOfTeams() {
    return N;
  }

  // all teams
  public Iterable<String> teams() {
    return map.keySet();
  }

  // number of wins for given team
  public int wins(String team) {
    validateTeam(team);
    return map.get(team).wins;
  }

  // number of losses for given team
  public int losses(String team) {
    validateTeam(team);
    return map.get(team).losses;
  }

  // number of remaining games for given team
  public int remaining(String team) {
    validateTeam(team);
    return map.get(team).remains;
  }

  // number of remaining games between team1 and team2
  public int against(String team1, String team2) {
    validateTeam(team1);
    validateTeam(team2);
    return map.get(team1).games[map.get(team2).id];
  }

  // is given team eliminated?
  public boolean isEliminated(String xname) {
    validateTeam(xname);
    return certificateOfElimination(xname) != null;
  }

  // subset R of teams that eliminates given team; null if not eliminated
  public Iterable<String> certificateOfElimination(String xname) {
    validateTeam(xname);
    Set<String> certificate = certificateMap.get(xname);
    if (certificate != null) {
      if (!certificate.isEmpty())
        return certificate;
      return null;
    } else {
      certificate = new TreeSet<>();
      certificateMap.put(xname, certificate);
    }
    Team xteam = map.get(xname);
    int xid = xteam.id;
    int n = N - 1;
    int V = n * (n - 1) / 2 + N;
    int s = V;
    int t = V + 1;


    Map<Integer, Integer[]> pairMap = new TreeMap<>();
    FlowNetwork H = new FlowNetwork(V + 2);
    int gamev = N;
    for (Team team : teams) {
      if (team.id == xid)
        continue;
      int moreWins = xteam.wins + xteam.remains - team.wins;
      if (moreWins < 0) {
        certificate.add(team.name);
        return certificate;
      }
      H.addEdge(new FlowEdge(team.id, t, moreWins));

      for (int opponent = team.id + 1; opponent < N; opponent++) {
        if (opponent == xid)
          continue;
        pairMap.put(gamev, new Integer[]{team.id, opponent});
        H.addEdge(new FlowEdge(s, gamev, team.games[opponent]));
        H.addEdge(new FlowEdge(gamev, team.id, Integer.MAX_VALUE));
        H.addEdge(new FlowEdge(gamev, opponent, Integer.MAX_VALUE));
        gamev++;
      }
    }
    // solve the maximum flow problem
//    StdOut.println("xid:" + xid + ", s:" + s + ", t:" + t + ", N:" + N);
//    StdOut.println("---network before----");
//    StdOut.println(H);
    FordFulkerson maxflow = new FordFulkerson(H, s, t);
//    StdOut.println("---network after----");
//    StdOut.println(H);

    for (Team team : teams) {
      if (maxflow.inCut(team.id))
        certificate.add(team.name);
    }

    if (certificate.isEmpty())
      return null;

    return certificate;
  }

  private void validateTeam(String team) {
    if (team == null || !map.containsKey(team)) {
      throw new IllegalArgumentException("invalid argument\n");
    }
  }


  private void showTeam(Team team) {
    StdOut.printf("Item:%d,%s,wins:%d,losses:%d,remains:%d,games:",
        team.id, team.name, team.wins, team.losses, team.remains);
    for (int g : team.games) {
      StdOut.print(g + " ");
    }
    StdOut.println();
  }


  public static void main(String[] args) {
    test(args);
//    test5();
  }

  private static void test4() {
    BaseballElimination division = new BaseballElimination("teams4.txt");
    division.certificateOfElimination("Philadelphia");
  }

  private static void test5() {
    BaseballElimination division = new BaseballElimination("teams5.txt");
    division.certificateOfElimination("Detroit");
  }

  private static void test(String[] args) {
    BaseballElimination division = new BaseballElimination(args[0]);
    for (String team : division.teams()) {
      if (division.isEliminated(team)) {
        StdOut.print(team + " is eliminated by the subset R = { ");
        for (String t : division.certificateOfElimination(team)) {
          StdOut.print(t + " ");
        }
        StdOut.println("}");
      } else {
        StdOut.println(team + " is not eliminated");
      }
    }
  }
}
