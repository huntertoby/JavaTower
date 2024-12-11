package Panels;

import Tower.Tower;

import javax.swing.*;
import java.awt.*;

public class TowerDataPanel extends JPanel {
    private JLabel towerDataLabel;

    public TowerDataPanel() {
        setLayout(new BorderLayout());
        setAlignmentX(Component.LEFT_ALIGNMENT);

        towerDataLabel = new JLabel("<html>選擇一個塔來查看數據</html>");
        towerDataLabel.setVerticalAlignment(SwingConstants.TOP);

        add(towerDataLabel, BorderLayout.CENTER);
    }

    public void choseToBuild()
    {
        this.towerDataLabel.setText("<html>選擇一個類型來建造</html>");
    }


    public void updateTowerData(Tower tower) {
        if (tower != null) {
            String towerInfo = "<html>塔位置: (" + tower.getTileX() + ", " + tower.getTileY() + ")<br>"
                    + "等級: " + tower.getLevel() + "<br>"
                    + "範圍: " + tower.getRange() + "<br>"
                    + "傷害: " + tower.getDamage() + "<br>"
                    + "攻擊速度: " + tower.getFireRate() + " 次/秒</html>";
            this.towerDataLabel.setText(towerInfo);
        } else {
            this.towerDataLabel.setText("<html>選擇一個塔格來查看數據</html>");
        }
    }

    public void warning(String message) {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
