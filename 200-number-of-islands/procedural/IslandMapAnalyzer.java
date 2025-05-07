/**
 * Analyzes a 2D map to identify and count disconnected land masses (islands).
 * Does not use standard algorithms like DFS or BFS.
 * Islands are tracked using primitive arrays and adjacency logic (top and left neighbors only).
 */

public class IslandMapAnalyzer {

    private final int[][] grid;
    private int[][][] islandGroups;
    private int islandCount = 0;

    public IslandMapAnalyzer(int[][] grid) {
        this.grid = grid;
        islandGroups = new int[256][][]; // Initial capacity
    }

    /**
     * Iterates over the entire map to identify land cells (value == 1).
     * Each land cell is either merged into an existing island or starts a new one.
     * Returns the total number of identified islands.
     */
    public int checkMap() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                boolean isLand = grid[row][col] == 1;
                if (isLand) {
                    int[] currentLandCoordinates = new int[]{row, col};
                    connectLandCell(currentLandCoordinates);
                }
            }
        }
        return islandCount;
    }

    /**
     * Integrates a new land cell into existing islands if it's adjacent.
     * If no adjacent islands are found, a new island is created.
     */
    private void connectLandCell(int[] currentLandCoordinates) {
        int[] adjacentIslands = findAdjacentIslands(currentLandCoordinates);
        if (adjacentIslands.length > 0) {
            mergeIslandsAndLand(adjacentIslands, currentLandCoordinates);
        } else {
            createNewIsland(currentLandCoordinates);
        }
    }

    /**
     * Scans all current islands to check if the new land cell is adjacent
     * (above or to the left) to any of them.
     * Returns the indexes of at most two adjacent islands.
     */
    private int[] findAdjacentIslands(int[] newLandPosition) {
        int[] adjacentIslands = new int[0];
        for (int islandIndex = 0; islandIndex < islandCount; islandIndex++) {
            for (int landIndex = 0; landIndex < islandGroups[islandIndex].length; landIndex++) {
                int[] existingLandPosition = islandGroups[islandIndex][landIndex];
                boolean hasNeighbor = isAdjacentUpOrLeft(existingLandPosition, newLandPosition);
                if (hasNeighbor) {
                    adjacentIslands = appendIslandIndex(adjacentIslands, islandIndex);
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
     * Returns a new array containing the existing adjacent island indexes
     * plus the newly found one.
     */
    private int[] appendIslandIndex(int[] adjacentIslands, int islandIndex) {
        int size = adjacentIslands.length;
        int[] newIdentifiedIslands = new int[size + 1];
        for (int i = 0; i < size; i++) {
            newIdentifiedIslands[i] = adjacentIslands[i];
        }
        newIdentifiedIslands[size] = islandIndex;
        return newIdentifiedIslands;
    }

    /**
     * Merges the new land cell into the connected island(s).
     * If connected to two islands, merges both into one.
     * If connected to a single island, the land is simply appended to it.
     */
    private void mergeIslandsAndLand(int[] connectedIslands, int[] currentLandCoordinates) {
        int firstIslandIndex = connectedIslands[0];
        int[][] firstIsland = islandGroups[firstIslandIndex];
        addLandToIsland(firstIsland,firstIslandIndex,currentLandCoordinates);

        if (connectedIslands.length > 1) {
            int secondIslandIndex = connectedIslands[1];
            int[][] secondIsland = islandGroups[secondIslandIndex];
            int[][] mergedIsland = getMergedIsland(firstIsland,secondIsland);
            islandGroups[firstIslandIndex] = mergedIsland;
            removeSecondIslandFromArray(secondIslandIndex);
        }
    }

    /**
     * Returns a new island array by concatenating the land cells of two islands.
     * Used when the new land bridges two distinct islands.
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
     * Removes the second island involved in a merge by shifting
     * all subsequent islands left in the array.
     * Decreases the total island count.
     */
    private void removeSecondIslandFromArray(int indexToRemove) {
        for (int i = indexToRemove; i < islandCount - 1; i++) {
            islandGroups[i] = islandGroups[i + 1];
        }
        islandCount--;
    }

    /**
     * Expands the target island to include a new land cell.
     * Updates the islands array at the specified index.
     */
    private void addLandToIsland(int[][] island,int islandIndex, int[] currentLandCoordinates) {
        int size = island.length;
        int[][] expandedIsland = new int[size + 1][];
        for (int i = 0; i < size; i++) {
            expandedIsland[i] = island[i];
        }
        expandedIsland[size] = currentLandCoordinates;
        islandGroups[islandIndex] = expandedIsland;
    }

    /**
     * Creates a new island consisting of a single land cell
     * and adds it to the islands array.
     */
    private void createNewIsland(int[] currentLandCoordinates) {
        ensureCapacityForIslands();
        int[][] newIsland = new int[1][];
        newIsland[0] = currentLandCoordinates;
        islandGroups[islandCount++] = newIsland;
    }

    /**
     * Ensures the islands array has sufficient capacity.
     * Doubles its size if the current capacity is reached.
     */
    private void ensureCapacityForIslands() {
        if (islandCount == islandGroups.length) {
            int[][][] resizedIslands = new int[islandGroups.length * 2][][];
            for (int i = 0; i < islandCount; i++) {
                resizedIslands[i] = islandGroups[i];
            }
            islandGroups = resizedIslands;
        }
    }

}
