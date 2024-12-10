package src;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TileMap {
    private int[][] data;
    private int width;
    private int height;

    public TileMap(String tmjFilePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(tmjFilePath)), "UTF-8");
        JSONObject map = new JSONObject(content);
        this.width = map.getInt("width");
        this.height = map.getInt("height");

        JSONArray layers = map.getJSONArray("layers");
        JSONObject layer = layers.getJSONObject(0); // 假設只有一個圖層
        JSONArray dataArray = layer.getJSONArray("data");

        data = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y][x] = dataArray.getInt(y * width + x);
            }
        }


    }

    public int getTileGID(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return -1;
        }
        return data[y][x];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
