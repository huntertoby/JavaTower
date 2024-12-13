package Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 負責讀取 waves.json，提供 getWaveInfo(waveIndex) 取得該波要生成的敵人及數量。
 */
public class WaveData {
    private JSONArray waveArray;

    public WaveData(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        JSONObject root = new JSONObject(content);
        this.waveArray = root.getJSONArray("waves");
    }

    /**
     * @param waveIndex 第幾波(1-based)
     * @return 該波要生成的敵人列表(JSONArray)，包含敵人類型與count
     */
    public JSONArray getWaveInfo(int waveIndex) {
        for (int i = 0; i < waveArray.length(); i++) {
            JSONObject waveObj = waveArray.getJSONObject(i);
            if (waveObj.getInt("wave") == waveIndex) {
                // 回傳 enemies 陣列
                return waveObj.getJSONArray("enemies");
            }
        }
        return null; // 找不到該波設定
    }
}
