package gdd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class DataLoader {

    public static HashMap<Integer, SpawnDetails> loadSpawns(String filename) {
        HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] values = line.split(",");
                if (values.length >= 4) {
                    try {
                        int frame = Integer.parseInt(values[0].trim());
                        String type = values[1].trim();
                        int x = Integer.parseInt(values[2].trim());
                        int y = Integer.parseInt(values[3].trim());

                        spawnMap.put(frame, new SpawnDetails(type, x, y));
                    } catch (NumberFormatException e) {
                    }
                }
            }

        } catch (IOException e) {
            return new HashMap<>();
        }

        return spawnMap;
    }

    public static boolean validateSpawnDuration(HashMap<Integer, SpawnDetails> spawnMap, int targetFrames) {
        if (spawnMap.isEmpty()) {
            return false;
        }

        int maxFrame = spawnMap.keySet().stream().max(Integer::compare).orElse(0);
        return maxFrame >= targetFrames;
    }

    public static void printSpawnStats(HashMap<Integer, SpawnDetails> spawnMap) {
    }
}