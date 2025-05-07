public class Chalenge {

    public static void main(String[] args) {

        int[][] matrix = {
                {1, 1, 1, 1, 0},
                {1, 1, 0, 1, 0},
                {1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };

        int[][] matrix2 = {
                {1, 0, 0, 1, 0, 0},
                {1, 0, 0, 0, 1, 0},
                {0, 0, 1, 1, 0, 0},
                {1, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 0},
                {1, 1, 1, 0, 0, 1}
        };

        int[][] matrix3 = {
                {1,1,0,0,0},
                {1,1,0,0,0},
                {0,0,1,0,0},
                {0,0,0,1,1}
        };

        IslandMapAnalyzer islandMapAnalyzer = new IslandMapAnalyzer(matrix);
        int numberOfIslands = islandMapAnalyzer.checkMap();
        System.out.println("Count of islands is:" + numberOfIslands);

        IslandMapAnalyzer islandMapAnalyzer2 = new IslandMapAnalyzer(matrix2);
        int numberOfIslands2 = islandMapAnalyzer2.checkMap();
        System.out.println("Count of islands is:" + numberOfIslands2);

        IslandMapAnalyzer islandMapAnalyzer3 = new IslandMapAnalyzer(matrix3);
        int numberOfIslands3 = islandMapAnalyzer3.checkMap();
        System.out.println("Count of islands is:" + numberOfIslands3);
    }
}