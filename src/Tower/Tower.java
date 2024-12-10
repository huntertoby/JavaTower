package Tower;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;


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

    // 屬性
    private double range;          // 攻擊範圍（像素）
    private double damage;         // 每次攻擊的傷害
    private double fireRate;       // 每秒攻擊次數
    private double fireCooldown;   // 攻擊冷卻時間

    private int level;             // 塔的等級
    private double upgradeCost;    // 升級所需金額
    private double sellValue;      // 賣出塔可獲得的金額

    private TargetType targetType; // 目標選擇類型
    private Image towerImage;      // 塔的圖像

    private double angle;

    // 常量
    private static final double DEFAULT_RANGE = 150.0;
    private static final double DEFAULT_DAMAGE = 25.0;
    private static final double DEFAULT_FIRE_RATE = 1.0; // 每秒1次
    private static final double DEFAULT_UPGRADE_COST = 100.0;
    private static final double DEFAULT_SELL_VALUE = 50.0;

    public enum TargetType {
        FIRST,    // 最先進入的敵人
        LAST,     // 最後進入的敵人
        STRONGEST, // 生命值最高的敵人
        CLOSEST   // 最近的敵人
    }

    public Tower(int tileX, int tileY, int tileWidth, int tileHeight, String imagePath) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.pixelX = tileX * tileWidth;
        this.pixelY = tileY * tileHeight;



        this.range = DEFAULT_RANGE;
        this.damage = DEFAULT_DAMAGE;
        this.fireRate = DEFAULT_FIRE_RATE;
        this.fireCooldown = 0.0;
        this.level = 1;
        this.upgradeCost = DEFAULT_UPGRADE_COST;
        this.sellValue = DEFAULT_SELL_VALUE;
        this.targetType = TargetType.FIRST;
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
    public boolean upgrade() {
        level++;
        range += 20.0;       // 增加攻擊範圍
        damage += 10.0;      // 增加傷害
        fireRate += 0.2;     // 增加攻擊速度
        upgradeCost *= 1.5;  // 增加升級成本
        sellValue += 25.0;   // 增加賣出價值
        return true;
    }

    /**
     * 賣出塔，返回賣出價值。
     *
     * @return 賣出價值
     */
    public double sell() {
        return sellValue;
    }

    /**
     * 渲染塔的圖像到畫面上。
     *
     * @param g 畫布的 Graphics 對象
     */

    public void render(Graphics g) {

        this.angle += 45;

        Graphics2D g2d = (Graphics2D) g.create();

        // 計算圖像的中心點
        double centerX = pixelX + this.tileWidth / 2.0;
        double centerY = pixelY + this.tileHeight / 2.0;


        // 創建一個 AffineTransform 對象
        AffineTransform at = new AffineTransform();

        // 平移到圖像的中心點
        at.translate(centerX, centerY);

        // 旋轉
        at.rotate(angle);

        System.out.println(this.towerImage.getWidth(null));
        System.out.println(this.towerImage.getHeight(null));



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

    public double getUpgradeCost() {
        return upgradeCost;
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
