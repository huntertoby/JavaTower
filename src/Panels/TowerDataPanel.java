package Panels;

import Tower.Tower;

import javax.swing.*;
import java.awt.*;
import Data.towerData;

import org.json.JSONObject;

public class TowerDataPanel extends JPanel {
    private JLabel towerDataLabel;

    public TowerDataPanel() {
        setLayout(new BorderLayout());
        setAlignmentX(Component.LEFT_ALIGNMENT);

        towerDataLabel = new JLabel("<html>選擇一個塔來查看數據</html>");
        towerDataLabel.setFont(new Font("微軟正黑體",Font.PLAIN,20));
        towerDataLabel.setVerticalAlignment(SwingConstants.TOP);

        add(towerDataLabel, BorderLayout.CENTER);
    }

    public void choseToBuild()
    {
        this.towerDataLabel.setText("<html>選擇一個類型來建造</html>");
    }


    public void updateTowerData(Tower tower ) {

        String type = "無";

        if (tower != null) {
            if (tower.getType()!= null){
                switch (tower.getType()) {
                    case SLOW -> type = "緩速";
                    case FREEZE -> type = "冰凍";
                    case BURN -> type = "燃燒";
                    case POISON -> type = "毒藥";
                }}

            String towerInfo;

            if (tower.getLevel() < tower.getMaxLevel()) {

                int nextLevel = tower.getLevel() + 1;
                JSONObject levelData = towerData.getTowerLevelData(tower.getTowerName(),nextLevel);

                String nextType = type; // 假設屬性不變，如果可能變化需進一步處理
                double nextRange = levelData.getDouble("range");
                double nextDamage = levelData.getDouble("damage");
                double nextFireRate = levelData.getDouble("fireRate");
                double nextCost = levelData.getInt("cost");

                towerInfo = "<html>塔名稱: " + tower.getTowerName() + ")<br>"
                        + "等級: " + tower.getLevel() +" -> "+ nextLevel + "<br>"
                        + "屬性: " + type + " -> "+ nextLevel + "<br>"
                        + "範圍: " + tower.getRange() + " -> "+ nextRange + "<br>"
                        + "傷害: " + tower.getDamage() + " -> "+ nextDamage + "<br>"
                        + "攻擊速度: " + tower.getFireRate() + " -> "+ nextFireRate + " 次/秒<br>"
                        + "價值: " + tower.getCostMoney() + " 元<br>"
                        + "升級價格: " + nextCost + " 元<html>";


            }else {
                towerInfo = "<html>塔名稱: " + tower.getTowerName() + ")<br>"
                        + "等級: " + tower.getLevel() +" (最高等級) "+ "<br>"
                        + "屬性: " + type + "<br>"
                        + "範圍: " + tower.getRange() + "<br>"
                        + "傷害: " + tower.getDamage() + "<br>"
                        + "傷害: " + tower.getDamage() + "<br>"
                        + "價值: " + tower.getCostMoney() + " 元<html>";
            }
            this.towerDataLabel.setText(towerInfo);
        } else if(TowerTypeButtonPanel.selectedTowerName != null)
        {
            JSONObject levelData = towerData.getTowerLevelData(TowerTypeButtonPanel.selectedTowerName,1);
            double Range = levelData.getDouble("range");
            double Damage = levelData.getDouble("damage");
            double FireRate = levelData.getDouble("fireRate");
            double cost = levelData.getInt("cost");

            type  = towerData.getTowerTypeString(TowerTypeButtonPanel.selectedTowerName);

            switch (type) {
                case "SLOW" -> type = "緩速";
                case "FREEZE" -> type = "冰凍";
                case "BURN" -> type = "燃燒";
                case "POISON" -> type = "毒藥";
            }

            String towerInfo =

                    "<html>塔名稱: "+ TowerTypeButtonPanel.selectedTowerName +"<br>"
                    + "等級: 1 "+ "<br>"
                    + "屬性: " + type + "<br>"
                    + "範圍: " + Range + "<br>"
                    + "傷害: " + Damage + "<br>"
                    + "攻擊速度: " + FireRate + "<br>"
                    + "價格: " + cost + "  元<html>";

            this.towerDataLabel.setText(towerInfo);

        }else {
            this.towerDataLabel.setText("<html>選擇一個塔格來查看數據</html>");
        }

    }

    public void warning(String message) {

    }
}
