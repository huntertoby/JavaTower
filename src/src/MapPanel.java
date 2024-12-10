package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

import Tower.Tower;
import enemy.Enemy;

public class MapPanel extends JPanel {
    private TileMap tileMap;
    private BufferedImage mapImage;
    private BufferedImage selectedTowerImage;
    private int tileWidth = 60;
    private int tileHeight = 60;
    private int selectedTileX = -1;
    private int selectedTileY = -1;
    private List<Tower> towers;
    private List<Enemy> enemies;

    public MapPanel(TileMap tileMap, String mapImagePath) throws IOException {
        this.tileMap = tileMap;
        this.mapImage = ImageIO.read(new File(mapImagePath));
        this.selectedTowerImage = ImageIO.read(new File("asset/image/selected_tower.png"));

        this.setPreferredSize(new Dimension(tileMap.getWidth() * tileWidth, tileMap.getHeight() * tileHeight));
    }

    public boolean clickTower(int _selectedTileX, int _selectedTileY) {

        selectedTileX = -1;
        selectedTileY = -1;
        int gid = tileMap.getTileGID(_selectedTileX, _selectedTileY);
        if (gid == 148) {
            selectedTileX = _selectedTileX;
            selectedTileY = _selectedTileY;
            repaint();
            return true;
        }
        repaint();
        return false;

    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 繪製整個地圖圖像
        g.drawImage(mapImage, 0, 0, tileMap.getWidth() * tileWidth, tileMap.getHeight() * tileHeight, null);

        // 如果有選中的塔，繪製選中的塔圖片
        if (selectedTileX != -1 && selectedTileY != -1) {


            int drawX = selectedTileX * tileWidth;
            int drawY = selectedTileY * tileHeight;
            g.drawImage(selectedTowerImage, drawX, drawY, tileWidth, tileHeight, null);
        }
        if (this.towers != null) {
            for (Tower tower : this.towers) {
                tower.render(g);
            }
        }
        if (this.enemies != null) {
            for (Enemy enemy : this.enemies) {
                enemy.render(g);
            }
        }


    }

    // Getter 方法
    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTowers(List<Tower> towers) {
        this.towers = towers;
    }

    public List<Tower> getTowers() { return towers; }

    public void setEnemies(List<Enemy> enemies) { this.enemies = enemies; }
}
