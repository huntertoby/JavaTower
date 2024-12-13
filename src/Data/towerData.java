package Data;

import enemy.StatusType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import Tower.Tower;

public class towerData {

    static public JSONArray towerArray;


    public towerData() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("asset/tower/tower.tmj")), "UTF-8");
        JSONObject towers = new JSONObject(content);
        towerArray = towers.getJSONArray("towers");
    }

    public JSONArray getTowers() { return towerArray; }

    public Tower createTower(String name, int level, int tileX, int tileY) {
        for (int i = 0; i < towerArray.length(); i++) {
            JSONObject towerData = towerArray.getJSONObject(i);

            if (towerData.getString("name").equalsIgnoreCase(name)) {

                JSONObject levelData = towerData.getJSONObject("levels").getJSONObject(String.valueOf(level));

                StatusType type;
                switch (towerData.getString("type")){
                    case "POISON":
                        type = StatusType.POISON;
                        break;
                    case "BURN":
                        type = StatusType.BURN;
                        break;
                    case "FREEZE":
                        type = StatusType.FREEZE;
                        break;
                    default:
                        type = null;
                }

                Tower tower = new Tower(
                        tileX,
                        tileY,
                        towerData.getString("imagePath"),
                        Color.decode(towerData.getString("BulletColor")),
                        type,
                        name
                );

                // 設置屬性
                tower.setMaxLevel(towerData.getInt("MaxLevel"));
                tower.setLevel(level);
                tower.setRange(levelData.getDouble("range"));
                tower.setDamage(levelData.getDouble("damage"));
                tower.setFireRate(levelData.getDouble("fireRate"));
                tower.setCostMoney(levelData.getInt("cost"));

                return tower;
            }
        }
        throw new IllegalArgumentException("無法找到名稱為 " + name + " 的塔或等級無效");
    }

    public static int getRange( String towerName ,int level ) {
        for (int i = 0; i < towerArray.length(); i++) {

            JSONObject towerData = towerArray.getJSONObject(i);
            if (towerData.getString("name").equalsIgnoreCase(towerName)) {
                JSONObject levelData = towerData.getJSONObject("levels").getJSONObject(String.valueOf(level));
                return levelData.getInt("range");
            }
        }
        return -1;
    }

    public static JSONObject getTowerLevelData( String towerName ,int level ) {
        for (int i = 0; i < towerArray.length(); i++) {
            JSONObject towerData = towerArray.getJSONObject(i);
            if (towerData.getString("name").equalsIgnoreCase(towerName)) {
                return  towerData.getJSONObject("levels").getJSONObject(String.valueOf(level));
            }

        }
        return null;
    }

    public static int getTowerCost( String towerName ,int level ) {
        for (int i = 0; i < towerArray.length(); i++) {
            JSONObject towerData = towerArray.getJSONObject(i);
            if (towerData.getString("name").equalsIgnoreCase(towerName)) {
                JSONObject levelData =  towerData.getJSONObject("levels").getJSONObject(String.valueOf(level));
                return levelData.getInt("cost");
            }
        }
        return -1;
    }

    public static String getTowerTypeString( String towerName) {
        for (int i = 0; i < towerArray.length(); i++) {
            JSONObject towerData = towerArray.getJSONObject(i);
            if (towerData.getString("name").equalsIgnoreCase(towerName)) {
                return towerData.getString("type");
            }
        }
        return null;
    }
}
