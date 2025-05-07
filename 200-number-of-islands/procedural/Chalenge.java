public class Chalenge {

    public static void main(String[] args) {
/*        int[][] matrix = {
                {1, 1, 1, 1, 0},
                {1, 1, 0, 1, 0},
                {1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        int[][] matrix = {
                {1, 0, 0, 1, 0, 0},
                {1, 0, 0, 0, 1, 0},
                {0, 0, 1, 1, 0, 0},
                {1, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0},
                {1, 1, 1, 0, 0, 1}
        };*/
        int[][] matrix = {
                {1,1,0,0,0},
                {1,1,0,0,0},
                {0,0,1,0,0},
                {0,0,0,1,1}
        };

        IslandMapAnalyzer islandMapAnalyzer = new IslandMapAnalyzer(matrix);
        int numberOfIslands = islandMapAnalyzer.checkMap();

        System.out.println("Count of islands is:" + numberOfIslands);
    }
}