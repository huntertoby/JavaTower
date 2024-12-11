package Tower;


import enemy.Enemy;
import src.MapPanel;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.List;
import src.MapPanel;
import src.TowerDefenseGame;

/**
 * Tower 類別代表塔防遊戲中的一個塔。
 * 它包含塔的屬性，如位置、範圍、傷害、攻擊速度等，
 * 以及與敵人互動的方法，如攻擊和升級。
 */
public class Tower {
    // 坐標位置（基於格子地圖）
    private int tileX;
    private int tileY;

    private int tileWidth;
    private int tileHeight;

    // 像素位置（用於渲染）
    private int pixelX;
    private int pixelY;

    private String towerName;

    // 屬性
    private double range;          // 攻擊範圍（像素）
    private double damage;         // 每次攻擊的傷害
    private double fireRate;       // 每秒攻擊次數
    private double fireCooldown;   // 攻擊冷卻時間

    private int level;             // 塔的等級
    private int maxLevel;             // 塔的等級
    private double sellValue;      // 賣出塔可獲得的金額

    private TargetType targetType; // 目標選擇類型
    private Image towerImage;      // 塔的圖像

    private double angle;

    private Enemy currentTarget;



    public enum TargetType {
        FIRST,    // 最先進入的敵人
        LAST,     // 最後進入的敵人
        STRONGEST, // 生命值最高的敵人
        CLOSEST   // 最近的敵人
    }

    public Tower(int tileX, int tileY, String imagePath) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileWidth = MapPanel.tileWidth;
        this.tileHeight = MapPanel.tileHeight;
        this.pixelX = tileX * tileWidth;
        this.pixelY = tileY * tileHeight;
        this.level = 1;
        this.targetType = TargetType.CLOSEST;
        loadImage(imagePath);
    }

    /**
     * 載入塔的圖像。
     *
     * @param imagePath 圖像檔案的路徑
     */
    private void loadImage(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        this.towerImage = icon.getImage();
    }

    /**
     * 升級塔，提升其屬性。
     *
     * @return 升級是否成功（例如，玩家資金足夠）
     */

    /**
     * 賣出塔，返回賣出價值。
     *
     * @return 賣出價值
     */
    public double sell() {
        return sellValue;
    }

    public void update(double deltaTime, List<Enemy> enemies) {

        // 減少冷卻時間
        if (fireCooldown > 0) {
            fireCooldown -= deltaTime;
        }

        // 如果當前目標不在範圍內，尋找新目標
        if (currentTarget != null && !isInRange(currentTarget)) {
            currentTarget = acquireTarget(enemies);
        }

        // 如果沒有目標，嘗試獲取新的目標
        if (currentTarget == null) {
            currentTarget = acquireTarget(enemies);
        }



        // 如果有目標，旋轉並攻擊
        if (currentTarget != null) {

            // 如果沒有目標，嘗試獲取新的目標
            if (currentTarget.isDead()) {
                currentTarget = acquireTarget(enemies);
                return;
            }

            // 計算目標的中心點
            double targetCenterX = currentTarget.getX() + currentTarget.getWidth() / 2.0;
            double targetCenterY = currentTarget.getY() + currentTarget.getHeight() / 2.0;

            // 計算塔的中心點
            double towerCenterX = pixelX + this.tileWidth / 2.0;
            double towerCenterY = pixelY + this.tileHeight / 2.0;

            // 計算目標與塔之間的角度
            double dx = targetCenterX - towerCenterX;
            double dy = towerCenterY - targetCenterY;

            double angleRadians = Math.atan2(dx, dy);

            double angleDegrees = Math.toDegrees(angleRadians);

            // 確保角度在 0 到 360 度之間
            if(angleDegrees < 0){
                angleDegrees += 360;
            }

            angle = angleDegrees;

            // 如果冷卻時間已到，可以攻擊
            if (fireCooldown <= 0) {
                attack(currentTarget);
                fireCooldown = 1.0 / fireRate; // 重置冷卻時間
            }

        }




    }

    private void attack(Enemy target) {
        // 計算塔中心位置
        double towerCenterX = pixelX + this.tileWidth / 2.0;
        double towerCenterY = pixelY + this.tileHeight / 2.0;

        // 建立子彈物件
        Bullet bullet = new Bullet(towerCenterX, towerCenterY, target, damage);

        // 將子彈加入全域 bullets 清單
        TowerDefenseGame.bullets.add(bullet);
    }



    public boolean isInRange(Enemy enemy) {
        double dx = (pixelX + towerImage.getWidth(null) / 2.0) - (enemy.getX() + enemy.getWidth() / 2.0);
        double dy = (pixelY + towerImage.getHeight(null) / 2.0) - (enemy.getY() + enemy.getHeight() / 2.0);
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= range;
    }

    private Enemy acquireTarget(List<Enemy> enemies) {
        Enemy selected = null;
        double minDistance = Double.MAX_VALUE;
        double maxHealth = Double.MIN_VALUE;

        for (Enemy enemy : enemies) {
            if (!isInRange(enemy)) continue; // 超出範圍
            selected = enemy;
            return selected;
        }
        return null;
    }

    public void render(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();

        // 計算圖像的中心點
        double centerX = pixelX + this.tileWidth / 2.0;
        double centerY = pixelY + this.tileHeight / 2.0;


        // 創建一個 AffineTransform 對象
        AffineTransform at = new AffineTransform();

        // 平移到圖像的中心點
        at.translate(centerX, centerY);

        // 旋轉
        at.rotate(Math.toRadians(angle));

        // 縮放圖像到 tileWidth 和 tileHeight
        double scaleX = (double) this.tileWidth / this.towerImage.getWidth(null);
        double scaleY = (double) this.tileHeight / this.towerImage.getHeight(null);


        at.scale(scaleX, scaleY);

        // 平移圖像，使其中心對齊旋轉點
        at.translate(-towerImage.getWidth(null) / 2.0, -towerImage.getHeight(null) / 2.0);

        // 啟用抗鋸齒（可選）
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 繪製旋轉後的圖像
        g2d.drawImage(towerImage, at, null);

        // 釋放 Graphics2D 資源
        g2d.dispose();
    }


    // Getter 和 Setter 方法

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public double getRange() {
        return range;
    }

    public double getDamage() {
        return damage;
    }

    public double getFireRate() {
        return fireRate;
    }

    public int getLevel() {
        return level;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setFireRate(double fireRate) {
        this.fireRate = fireRate;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public double getSellValue() {
        return sellValue;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public Image getTowerImage() {
        return towerImage;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
