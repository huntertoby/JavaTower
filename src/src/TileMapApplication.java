package src;

import javax.swing.*;

public class TileMapApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 加載地圖和tileset
                TileMap tileMap = new TileMap(" asset/map/level1.tmj");
                MapPanel mapPanel = new MapPanel(tileMap, " asset/map/level1.png");

                // 創建窗口
                JFrame frame = new JFrame("塔防地圖");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new JScrollPane(mapPanel)); // 如果地圖較大，可以添加滾動條
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
