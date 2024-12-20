package Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import Tower.TowerType;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import Tower.Tower;
import src.MapPanel;
import src.TowerDefenseGame;

public class TowerTypeButtonPanel extends JPanel {

    static public String selectedTowerName;

    private TowerDataPanel towerDataPanel;

    public TowerTypeButtonPanel(JSONArray towers, TowerDefenseGame game,TowerDataPanel towerDataPanel) {

        setLayout(new GridLayout(3, 3, 5, 5)); // 3x3 grid with gaps
        setAlignmentX(Component.LEFT_ALIGNMENT);

        setBorder(BorderFactory.createTitledBorder("選擇砲台類型"));

        for (int i = 0; i < towers.length(); i++) {
            JSONObject tower = towers.getJSONObject(i);
            // 創建按鈕
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(60, 60));

            // 設置圖標（假設 imagePath 指向有效的圖片路徑）
            String imagePath = tower.getString("imagePath");
            ImageIcon icon = new ImageIcon(imagePath);
            button.setIcon(icon);

            // 設置提示文字（塔的名稱）
            String towerName = tower.getString("name");
            button.setToolTipText(towerName);

            // 添加按鈕的事件監聽器
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedTowerName = towerName;
                    setBorder(BorderFactory.createTitledBorder("選擇砲台類型: " + selectedTowerName));
                    towerDataPanel.updateTowerData(null);
                    game.repaint();
                }
            });

            // 將按鈕添加到面板
            add(button);
        }
    }

    public String getSelectedTowerName() {
        return selectedTowerName;

    }

}
