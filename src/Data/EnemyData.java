package Data;

import enemy.Enemy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 負責載入 enemies.json 並提供 createEnemy(...) 方法
 * 依據敵人名稱（type）回傳對應的 Enemy 物件。
 */
public class EnemyData {
    private JSONArray enemyArray;

    public EnemyData(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        JSONObject root = new JSONObject(content);
        this.enemyArray = root.getJSONArray("enemies");
    }

    public Enemy createEnemy(String enemyType) {
        // 從 enemyArray 找到對應的 enemyType
        for (int i = 0; i < enemyArray.length(); i++) {
            JSONObject eData = enemyArray.getJSONObject(i);
            if (eData.getString("name").equals(enemyType)) {
                // 解析各種屬性
                double health    = eData.getDouble("health");
                double baseSpeed = eData.getDouble("baseSpeed");

                Enemy enemy = new Enemy((int)health);
                enemy.setBaseSpeed(baseSpeed);
                enemy.setShape(eData.getString("shape"));
                enemy.setColor(Color.decode(eData.getString("color")));
                enemy.setReward(eData.getInt("reward"));

                System.out.println(enemy.getReward());



                return enemy;
            }
        }
        System.out.println("找不到指定敵人類型: " + enemyType + "，回傳一個預設敵人。");
        return new Enemy(100); // fallback
    }
}
