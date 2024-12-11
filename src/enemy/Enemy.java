package enemy;

import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;

import src.TileMap;

public class Enemy {

    private double health;
    // 敵人的位置（以像素計）
    private double x;
    private double y;
    // 敵人的速度(像素/更新tick)
    private double speed = 1;

    // 地圖資訊
    private TileMap tileMap;
    private int tileWidth;
    private int tileHeight;

    // 路徑：存放敵人要行走的路徑點，每個點為(tileX, tileY)
    private List<float[]> path;
    private int currentTargetIndex = 0; // 目前目標路徑點索引

    // 敵人尺寸（示意用，用來繪製在畫面上的大小）
    private int width = 32;
    private int height = 32;

    public Enemy(TileMap tileMap) {


        this.tileMap = tileMap;
        this.tileWidth = tileMap.getWidth();
        this.tileHeight = tileMap.getHeight();
        this.health = 50;
        int tilePixelWidth = 64;
        int tilePixelHeight = 64;

        path = new ArrayList<>();
        path.add(new float[]{10.65F, 0f});
        path.add(new float[]{10.65F, 4.2f});
        path.add(new float[]{2.3F, 4.2f});
        path.add(new float[]{2.3F, 8f});
        path.add(new float[]{10.65F, 8f});
        path.add(new float[]{10.65F, 11.5f});

        if(!path.isEmpty()) {
            float startTileX = path.get(0)[0];
            float startTileY = path.get(0)[1];
            this.x = startTileX * tilePixelWidth + tilePixelWidth / 2.0 - width/2.0;
            this.y = startTileY * tilePixelHeight + tilePixelHeight / 2.0 - height/2.0;
        }
    }

    public void update() {

        // 若路線已到最後一個目標點，就不再移動
        if (currentTargetIndex >= path.size()) {
            return;
        }
        int tilePixelWidth = 64;
        int tilePixelHeight = 64;

        // 取得目前目標點的像素位置（該tile的中心）
        float targetTileX = path.get(currentTargetIndex)[0];
        float targetTileY = path.get(currentTargetIndex)[1];

        double targetX = targetTileX * tilePixelWidth + tilePixelWidth/2.0 - width/2.0;
        double targetY = targetTileY * tilePixelHeight + tilePixelHeight/2.0 - height/2.0;

        // 計算移動方向
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist <= 8) {
            // 已抵達目標點，切換至下一個路徑點
            currentTargetIndex++;
        } else {
            // 根據距離計算單位方向並前進
            double ux = dx / dist;
            double uy = dy / dist;

            x += ux * speed;
            y += uy * speed;
        }


    }

    public boolean isDead() {
        return health <= 0;
    }


    public void render(Graphics g) {
        // 可根據需要換成敵人貼圖
        g.fillOval((int)x, (int)y, width, height);

    }

    public void setPath(List<float[]> newPath) {
        this.path = newPath;
        this.currentTargetIndex = 0;
    }

    public void takeDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
        }
    }

    // 取得敵人位置(像素為單位)
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setHealth(int health) { this.health = health; }
    public double getHealth() { return health; }




    // 若需要檢查敵人是否走到終點，可新增方法：
    public boolean hasReachedEnd() {
        return currentTargetIndex >= path.size();
    }


}
