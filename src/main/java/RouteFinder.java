package main.java;

import java.util.*;

public interface RouteFinder {
    char Exit = 'X';
    char Start = '@';
    char Wall = '#';
    char Path = '+';

    class Node {
        int x,y,g,h,f;
        Node parent;
        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;

            if (!(obj instanceof Node))
                return false;

            Node n = (Node) obj;
            return this.x == n.x && this.y == n.y;
        }
    }

    static char[][] findRoute(char[][] map){
        //Init variables
        int lenX = map.length;
        int lenY = map[0].length;
        ArrayList<Node> openList = new ArrayList<>();
        ArrayList<Node> closedList = new ArrayList<>();

        Node startNode = new Node(0,0);
        Node exitNode = new Node(0,0);
        //GetStartPosition
        for (var i=0;i<lenX;i++){
            for (var j = 0; j < lenY; j++) {
                if (map[i][j] == Start) { startNode = new Node(i,j);}
                if (map[i][j] == Exit){ exitNode = new Node(i,j);}
            }
        }

        //Main Algorithm
        startNode.g = 0;
        startNode.h = CalcEvrCost(startNode,exitNode);
        startNode.f = startNode.g + startNode.h;

        openList.add(startNode);
        while (!openList.isEmpty()){
            int minCost = 777777;
            Node currentNode = null;
            for (Node node: openList) {
                if (node.f<minCost){
                    minCost = node.f;
                    currentNode = node;
                }
            }

            //if we are find the path
            if (currentNode.equals(exitNode)){
               List<Node> path = GetPath(currentNode);
               return GetSolvedMaze(path, map);
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            for (var sX=-1;sX<2;sX++){
                for (var sY=-1;sY<2;sY++){
                    //skip out of range
                    if (currentNode.x + sX < 0 || currentNode.x + sX > lenX-1 ||
                            currentNode.y +sY <0 || currentNode.y+sY > lenY-1) continue;
                    //allow move (0;-1)(0;1)(-1;0)(1;0)
                    //skip diag move (-1;-1) (-1;1) (1;1) (1;-1)
                    if (sX*sX == sY*sY) continue;
                    //skip wall
                    if (map[currentNode.x + sX][currentNode.y+sY] == Wall ) continue;
                    //main
                    int tentativeScore = currentNode.g + 1;
                    Node n = new Node(currentNode.x + sX, currentNode.y + sY);
                    if (closedList.contains(n) && tentativeScore <= n.g)
                        continue;
                    if (!openList.contains(n) || tentativeScore <= n.g) {
                        n.parent = currentNode;
                        n.g = tentativeScore;
                        n.f = tentativeScore + CalcEvrCost(n, exitNode);
                        if (!openList.contains(n))
                            openList.add(n);
                    }
                }
            }
        }
        return null;
    }

    private static int CalcEvrCost(Node node1, Node node2){
        return Math.abs(node1.x-node2.x) + Math.abs(node1.y-node2.y);
    }

    private static List<Node> GetPath(Node node){
        ArrayList<Node> path = new ArrayList<>();
        while(node != null){
            path.add(node);
            node = node.parent;
        }
        return path;
    }

    private static char[][] GetSolvedMaze(List<Node> path, char[][] map){
        for(var len = 1;len<path.size()-1;len++){
            map[path.get(len).x][path.get(len).y] = Path;
        }
        return map;
    }
}
