/**
 * Analyzes a 2D map to identify and count disconnected land masses (islands).
 * Uses primitive arrays and manual adjacency logic instead of standard algorithms like DFS or BFS.
 * Adjacency is considered only for top and left neighbors, ensuring deterministic island formation.
 */

public class IslandMapAnalyzer {

    private final int[][] grid;
    private int[][] islandGroups;
    private int islandCount = 0;
    private final int MAX_COLS;

    /**
     * Constructs an analyzer instance for a given 2D binary grid.
     *
     * @param grid the map grid where 1 represents land and 0 represents water
     */
    public IslandMapAnalyzer(int[][] grid) {
        this.grid = grid;
        MAX_COLS = grid[0].length;
        islandGroups = new int[256][]; // Initial capacity; grows dynamically
    }

    /**
     * Iterates over the entire map to identify and group land cells into islands.
     * Returns the total number of distinct islands found.
     *
     * @return total island count
     */
    public int checkMap() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                boolean isLand = grid[row][col] == 1;
                if (isLand) {
                    int currentEncodedCoordinate = encodeCoordinates(row,col);
                    connectLandCell(currentEncodedCoordinate);
                }
            }
        }
        return islandCount;
    }

    /**
     * Attempts to associate a land cell with existing adjacent islands,
     * or creates a new island if no adjacent islands are found.
     */
    private void connectLandCell(int encodedCoordinate) {
        int[] adjacentIslands = findAdjacentIslands(encodedCoordinate);
        if (adjacentIslands.length > 0) {
            mergeIslandsAndLand(adjacentIslands, encodedCoordinate);
        } else {
            createNewIsland(encodedCoordinate);
        }
    }

    /**
     * Identifies indexes of islands that are adjacent (top or left) to a new land cell.
     * Scans all existing islands. At most two adjacent islands are expected due to input ordering.
     *
     * @param encodedCoordinate encoded coordinate of the new land cell
     * @return array of adjacent island indexes
     */
    private int[] findAdjacentIslands(int encodedCoordinate) {
        int[] adjacentIslands = new int[0];
        for (int islandIndex = 0; islandIndex < islandCount; islandIndex++) {
            for (int landIndex = 0; landIndex < islandGroups[islandIndex].length; landIndex++) {
                int encodedExistingLand = islandGroups[islandIndex][landIndex];
                boolean hasNeighbor = isAdjacentUpOrLeft(encodedExistingLand, encodedCoordinate);
                if (hasNeighbor) {
                    adjacentIslands = appendIslandIndex(adjacentIslands, islandIndex);
                    break;
                }
            }
        }
        return adjacentIslands;
    }

    /**
     * Determines if two encoded coordinates represent adjacent land cells.
     * Only top and left adjacency is considered (to avoid redundancy and recursion).
     *
     * @param encodedExistingLand existing land cell
     * @param encodedCoordinate new land cell
     * @return true if adjacent (up or left), false otherwise
     */
    private boolean isAdjacentUpOrLeft(int encodedExistingLand, int encodedCoordinate) {
        int[] newLandCoordinates = decodeCoordinates(encodedCoordinate);
        int row = newLandCoordinates[0];
        int col = newLandCoordinates[1];

        int[] existingLandCoordinates = decodeCoordinates(encodedExistingLand);
        int existingRow = existingLandCoordinates[0];
        int existingCol = existingLandCoordinates[1];

        return (row - 1 == existingRow && existingCol == col) ||
                (row == existingRow && existingCol == col -1);
    }

    /**
     * Appends an island index to the list of detected adjacent islands.
     * Creates a new array with increased capacity.
     *
     * @param adjacentIslands current array of island indexes
     * @param islandIndex new index to append
     * @return new array containing the appended index
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
     * Merges the new land cell into the appropriate island(s).
     * If two adjacent islands are found, they are merged together.
     *
     * @param connectedIslands indexes of adjacent islands
     * @param encodedCoordinate the new land cell to integrate
     */
    private void mergeIslandsAndLand(int[] connectedIslands, int encodedCoordinate) {
        int firstIslandIndex = connectedIslands[0];
        int[] firstIsland = islandGroups[firstIslandIndex];
        addLandToIsland(firstIsland,firstIslandIndex,encodedCoordinate);

        if (connectedIslands.length > 1) {
            int secondIslandIndex = connectedIslands[1];
            int[] secondIsland = islandGroups[secondIslandIndex];
            int[] mergedIsland = getMergedIsland(firstIsland,secondIsland);
            islandGroups[firstIslandIndex] = mergedIsland;
            removeSecondIslandFromArray(secondIslandIndex);
        }
    }

    /**
     * Concatenates two island arrays into one.
     * Used when two distinct islands are connected by a new land cell.
     *
     * @param firstIsland the first island
     * @param secondIsland the second island
     * @return merged island array
     */
    private int[] getMergedIsland(int[] firstIsland,int[] secondIsland) {
        int size = firstIsland.length + secondIsland.length;
        int[] mergedIsland = new int[size];

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
     * Removes the specified island from the array by shifting all subsequent islands left.
     * Updates the island count accordingly.
     *
     * @param indexToRemove index of island to remove
     */
    private void removeSecondIslandFromArray(int indexToRemove) {
        for (int i = indexToRemove; i < islandCount - 1; i++) {
            islandGroups[i] = islandGroups[i + 1];
        }
        islandCount--;
    }

    /**
     * Adds a land cell to the specified island group.
     * Replaces the island's array with a new one that includes the additional cell.
     *
     * @param island the original island
     * @param islandIndex index of the island in the main array
     * @param encodedCoordinate land cell to add
     */
    private void addLandToIsland(int[] island,int islandIndex, int encodedCoordinate) {
        int size = island.length;
        int[] expandedIsland = new int[size + 1];
        for (int i = 0; i < size; i++) {
            expandedIsland[i] = island[i];
        }
        expandedIsland[size] = encodedCoordinate;
        islandGroups[islandIndex] = expandedIsland;
    }

    /**
     * Creates a new island with a single land cell.
     * Adds it to the island group array and increments the island count.
     *
     * @param encodedCoordinate coordinate of the new land cell
     */
    private void createNewIsland(int encodedCoordinate) {
        ensureCapacityForIslands();
        int[] newIsland = new int[1];
        newIsland[0] = encodedCoordinate;
        islandGroups[islandCount++] = newIsland;
    }

    /**
     * Ensures that the islandGroups array has capacity for at least one more island.
     * Doubles its size if the capacity has been reached.
     */
    private void ensureCapacityForIslands() {
        if (islandCount == islandGroups.length) {
            int[][] resizedIslands = new int[islandGroups.length * 2][];
            for (int i = 0; i < islandCount; i++) {
                resizedIslands[i] = islandGroups[i];
            }
            islandGroups = resizedIslands;
        }
    }

    /**
     * Encodes a 2D coordinate into a single integer.
     * Used to simplify storage and comparison of coordinates.
     *
     * @param row the row index
     * @param col the column index
     * @return encoded coordinate
     */
    private int encodeCoordinates(int row, int col) {
        return row * MAX_COLS + col;
    }

    /**
     * Decodes an encoded coordinate back into a 2D [row, col] array.
     *
     * @param encodedCoordinate the encoded value
     * @return int array with [row, col]
     */
    private int[] decodeCoordinates(int encodedCoordinate) {
        int col = encodedCoordinate % MAX_COLS;
        int row = encodedCoordinate / MAX_COLS;
        return new int[]{row, col};
    }

}
