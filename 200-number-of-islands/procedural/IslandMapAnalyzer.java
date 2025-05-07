public class IslandMapAnalyzer {

    private final int[][] map;
    private int[][][] islands = new int[0][][];

    public IslandMapAnalyzer(int[][] map) {
        this.map = map;
    }

    /**
     * Iterates through the entire map grid to identify land cells (value == 1).
     * For each land cell, it tries to associate it with existing islands or
     * create a new one if necessary.
     * Returns the total number of islands found in the map.
     */
    public int checkMap() {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                boolean isLand = map[row][col] == 1;
                if (isLand) {
                    int[] newLandPosition = new int[]{row, col};
                    integrateLandIntoIslands(newLandPosition);
                }
            }
        }
        return islands.length;
    }

    /**
     * Determines whether the new land position connects to any existing islands.
     * If it does, merges it accordingly. Otherwise, creates a new isolated island.
     */
    private void integrateLandIntoIslands(int[] newLandPosition) {
        int[] connectedIslands = getAdjacentIslands(newLandPosition);
        if (connectedIslands.length > 0) {
            mergeIslandsAndLand(connectedIslands, newLandPosition);
        } else {
            createNewIsland(newLandPosition);
        }
    }

    /**
     * Checks if the new land position is adjacent (above or to the left)
     * to any existing island. Since only these two directions are considered,
     * at most two adjacent islands can be found.
     * Returns the indexes of the adjacent islands, if any.
     */
    private int[] getAdjacentIslands(int[] newLandPosition) {
        int[] adjacentIslands = new int[0];
        for (int islandIndex = 0; islandIndex < islands.length; islandIndex++) {
            for (int landIndex = 0; landIndex < islands[islandIndex].length; landIndex++) {
                int[] existingLandPosition = islands[islandIndex][landIndex];
                boolean hasNeighbor = isAdjacentUpOrLeft(existingLandPosition, newLandPosition);
                if (hasNeighbor) {
                    adjacentIslands = addToNeighborIslands(adjacentIslands, islandIndex);
                    break;
                }
            }
        }
        return adjacentIslands;
    }

    /**
     * Returns true if the new land cell is directly above or to the left
     * of the current land cell.
     * This is the only adjacency rule used, which guarantees that each new
     * land cell can be adjacent to at most two existing islands.
     */
    private boolean isAdjacentUpOrLeft(int[] existingLandPosition, int[] newLandPosition) {
        int currentRow = existingLandPosition[0];
        int currentCol = existingLandPosition[1];
        int landRow = newLandPosition[0];
        int landCol = newLandPosition[1];

        return (landRow - 1 == currentRow && landCol == currentCol) ||
                (landRow == currentRow && landCol - 1 == currentCol);
    }

    /**
     * Appends a new island index to the list of adjacent islands.
     * This is used during adjacency detection when a neighboring
     * island is found.
     */
    private int[] addToNeighborIslands(int[] adjacentIslands, int islandIndex) {
        int size = adjacentIslands.length;
        int[] newIdentifiedIslands = new int[size + 1];
        for (int i = 0; i < size; i++) {
            newIdentifiedIslands[i] = adjacentIslands[i];
        }
        newIdentifiedIslands[size] = islandIndex;
        return newIdentifiedIslands;
    }

    /**
     * Merges the new land position into the adjacent island(s).
     * Since only top and left neighbors are considered, at most
     * two adjacent islands can exist.
     *
     * If two islands are adjacent, they are merged along with the new land.
     * If only one island is adjacent, the new land is added to it.
     */
    private void mergeIslandsAndLand(int[] connectedIslands, int[] newLandPosition) {
        int[][] firstIsland = islands[connectedIslands[0]];
        int[][] mergedIsland;
        if (connectedIslands.length > 1) {
            int[][] secondIsland = islands[connectedIslands[1]];
            mergedIsland = getMergedIsland(firstIsland,secondIsland);
            mergedIsland = addLandToIsland(mergedIsland,newLandPosition);
            realocateMergedIslandOnMap(mergedIsland,connectedIslands[0],connectedIslands[1]);
        } else {
            mergedIsland = addLandToIsland(firstIsland,newLandPosition);
            realocateMergedIslandOnMap(mergedIsland,connectedIslands[0]);
        }
    }

    /**
     * Concatenates the land positions of two islands into a single new island array.
     * This is used when a new land cell connects two previously separate islands.
     */
    private int[][] getMergedIsland(int[][] firstIsland,int[][] secondIsland) {
        int size = firstIsland.length + secondIsland.length;
        int[][] mergedIsland = new int[size][];
        for (int i = 0; i < firstIsland.length ; i++) {
            mergedIsland[i] = firstIsland[i];
        }
        int nextIndex = firstIsland.length;
        for (int i = 0; i < secondIsland.length ; i++) {
            mergedIsland[nextIndex + i] = secondIsland[i];
        }
        return mergedIsland;
    }

    /**
     * Replaces a specific island in the islands array with a newly merged version.
     * This is used when only one island is involved in the merge.
     */
    private void realocateMergedIslandOnMap(int[][] mergedIsland, int index) {
        int[][][] newIslands = new int[islands.length][][];
        for (int i = 0; i < islands.length; i++) {
            if (i == index) {
                newIslands[i] = mergedIsland;
            } else {
                newIslands[i] = islands[i];
            }
        }
        islands = newIslands;
    }

    /**
     * Replaces one island in the islands array with a merged version,
     * and removes the second island involved in the merge.
     * Used when two islands are connected by the new land cell.
     */
    private void realocateMergedIslandOnMap(int[][] mergedIsland, int index, int indexToRemove) {
        int newSize = islands.length-1;
        int[][][] newIslands = new int[newSize][][];
        for (int i = 0; i < newSize; i++) {
            if (i >= indexToRemove) {
                newIslands[i] = islands[i + 1];
            } else if (i == index)  {
                newIslands[i] = mergedIsland;
            } else {
                newIslands[i] = islands[i];
            }
        }
        islands = newIslands;
    }

    /**
     * Returns a new island array with the new land cell appended
     * to the existing island.
     */
    private int[][] addLandToIsland(int[][] island, int[] newLandPosition) {
        int size = island.length;
        int[][] mergedIsland = new int[size + 1][];
        for (int i = 0; i < size; i++) {
            mergedIsland[i] = island[i];
        }
        mergedIsland[size] = newLandPosition;
        return mergedIsland;
    }

    /**
     * Creates a new island composed of a single land cell
     * and appends it to the list of islands.
     */
    private void createNewIsland(int[] newLandPosition) {
        int[][] newIslandUnit = new int[1][];
        newIslandUnit[0] = newLandPosition;
        int size = islands.length;

        int[][][] newIsland = new int[size + 1][][];
        for (int i = 0; i < size; i++) {
            newIsland[i] = islands[i];
        }

        newIsland[size] = newIslandUnit;
        islands = newIsland;
    }

}
