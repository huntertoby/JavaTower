package src;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import Tower.Tower;

public class data {

    private JSONArray towerArray;

    public data () throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("asset/tower/tower.tmj")), "UTF-8");
        JSONObject map = new JSONObject(content);

        towerArray = map.getJSONArray("towers");
    }

    public JSONArray getTowers() { return towerArray; }

    public Tower createTower(String name, int level, int tileX, int tileY) {
        for (int i = 0; i < towerArray.length(); i++) {
            JSONObject towerData = towerArray.getJSONObject(i);

            if (towerData.getString("name").equalsIgnoreCase(name)) {
                JSONObject levelData = towerData.getJSONObject("levels").getJSONObject(String.valueOf(level));

                Tower tower = new Tower(
                        tileX,
                        tileY,
                        towerData.getString("imagePath")
                );

                // 設置屬性
                tower.setRange(levelData.getDouble("range"));
                tower.setDamage(levelData.getDouble("damage"));
                tower.setFireRate(levelData.getDouble("fireRate"));

                return tower;
            }
        }
        throw new IllegalArgumentException("無法找到名稱為 " + name + " 的塔或等級無效");
    }
}
