package src;

import Tower.Bullet;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TileMap {
    private int[][] data;
    private int width;
    private int height;
    private List<int[]> spot = new ArrayList<>();

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

        layers = map.getJSONArray("layers");
        layer = layers.getJSONObject(1);
        dataArray = layer.getJSONArray("objects");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject spotData = dataArray.getJSONObject(i);
            spot.add(new int[]{spotData.getInt("x"), spotData.getInt("y")});
        }
    }
    public int getTileGID(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return -1;
        }
        return data[y][x];
    }

    public List<int[]> getSpot(){ return spot;}

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
