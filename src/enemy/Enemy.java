package enemy;

import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;

import src.TileMap;

public class Enemy {
    // 敵人的位置（以像素計）
    private double x;
    private double y;
    // 敵人的速度(像素/更新tick)
    private double speed = 10;

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

        // 假設每個tile大小為64x64
        // 若 tilemap 有方法取得 tile 寬高，那就用該方法
        int tilePixelWidth = 64;
        int tilePixelHeight = 64;

        // 在此定義一條簡易的路線範例（實務上可用A*或 BFS 動態取得）
        // 以下假設路線為一系列(x, y) 的 tile 座標，而非像素座標
        // 注意：這只是範例。實際上你需要從地圖中找出一條合法路線（非1、非148的tile）
        path = new ArrayList<>();
        // 假設路線為 (0,2) -> (1,2) -> (2,2) -> ... (此為隨機假設)
        // 請務必依你的地圖實際通行路線修改。
        path.add(new float[]{10.65F, 0f});

        path.add(new float[]{10.65F, 4.2f});
        path.add(new float[]{2.3F, 4.2f});
        path.add(new float[]{2.3F, 8f});
        path.add(new float[]{10.65F, 8f});
        path.add(new float[]{10.65F, 11.5f});


        // ... 可自行加入更多路線點

        // 將敵人初始位置設為路線的起點（tile中心）
        if(!path.isEmpty()) {
            float startTileX = path.get(0)[0];
            float startTileY = path.get(0)[1];
            this.x = startTileX * tilePixelWidth + tilePixelWidth / 2.0 - width/2.0;
            this.y = startTileY * tilePixelHeight + tilePixelHeight / 2.0 - height/2.0;
        }
    }

    /**
     * 更新敵人位置與移動
     */
    public void update() {

//        System.out.println(currentTargetIndex);

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

        System.out.println(dist);

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

    /**
     * 繪製敵人
     */
    public void render(Graphics g) {
        // 可根據需要換成敵人貼圖
        g.fillOval((int)x, (int)y, width, height);
    }

    /**
     * 可選：設定敵人路徑（若想在外部指定或動態尋路後再設定）
     */
    public void setPath(List<float[]> newPath) {
        this.path = newPath;
        this.currentTargetIndex = 0;
    }

    // 取得敵人位置(像素為單位)
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // 若需要檢查敵人是否走到終點，可新增方法：
    public boolean hasReachedEnd() {
        return currentTargetIndex >= path.size();
    }
}
