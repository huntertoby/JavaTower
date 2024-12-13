package enemy;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import static enemy.StatusType.*;

public class Enemy {

    private double maxHealth;
    private double health;
    // 敵人的位置（以像素計）
    private double x;
    private double y;

    private int reward;

    // 基礎速度(像素/更新tick)
    private double baseSpeed = 0.1;
    private double currentSpeed;

    // 路徑：存放敵人要行走的路徑點，每個點為(tileX, tileY)
    private List<int[]> path;
    private int currentTargetIndex; // 目前目標路徑點索引

    // 敵人尺寸（示意用，用來繪製在畫面上的大小）
    private int width = 32;
    private int height = 32;

    // 狀態效果管理
    private List<StatusEffect> activeStatusEffects;
    private boolean isFrozen = false;
    private List<Double> speedModifiers; // 用於累積多個減速效果

    // 新增：形狀與顏色（可由 JSON 或預設邏輯來設定）
    private String shape = "circle"; // "circle", "square", "triangle"
    private Color color = Color.RED; // 預設紅色，如需每種形狀不同顏色可改

    public Enemy(int health) {
        this.health = health;
        this.maxHealth = health;
        this.path = new ArrayList<>();
        this.activeStatusEffects = new ArrayList<>();
        this.speedModifiers = new ArrayList<>();
        this.currentSpeed = baseSpeed;
    }

    /**
     * 每個 frame 的更新：包含移動與判斷狀態效果
     */
    public void update() {
        // 更新狀態效果
        updateStatusEffects(0.0166);

        if (isFrozen) {
            // 被冰凍時暫停移動
            return;
        }
        if (currentTargetIndex >= path.size()) {
            // 已經走到終點
            return;
        }

        // 取得目前目標點(像素)
        double targetX = path.get(currentTargetIndex)[0];
        double targetY = path.get(currentTargetIndex)[1];

        // 移動
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= 8) {
            // 已抵達該路徑點，切換至下一點
            currentTargetIndex++;
        } else {
            double ux = dx / dist;
            double uy = dy / dist;
            x += ux * currentSpeed;
            y += uy * currentSpeed;
        }
    }

    /**
     * 狀態效果的維護
     */
    private void updateStatusEffects(double deltaTime) {
        isFrozen = false;
        speedModifiers.clear();

        // 遍歷所有狀態效果
        List<StatusEffect> toRemove = new ArrayList<>();
        for (StatusEffect effect : activeStatusEffects) {
            effect.applyEffect(this);
            // 減少效果剩餘時間
            effect.setDuration(effect.getDuration() - deltaTime);
            if (effect.getDuration() <= 0) {
                toRemove.add(effect);
                effect.removeEffect(this);
            }
        }
        activeStatusEffects.removeAll(toRemove);

        // 計算減速/凍結
        for (StatusEffect effect : activeStatusEffects) {
            if (effect.getType() == SLOW) {
                speedModifiers.add(effect.getMagnitude()); // 通常為負數
            }
            if (effect.getType() == FREEZE) {
                isFrozen = true;
            }
        }

        // 更新當前速度
        currentSpeed = baseSpeed;
        for (double modifier : speedModifiers) {
            currentSpeed += modifier; // 疊加所有速度修正
        }
        if (currentSpeed < 0) {
            currentSpeed = 0;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean isEnd() {
        return currentTargetIndex >= path.size();
    }

    /**
     * 敵人繪製：依照 shape 做對應形狀的渲染
     */
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        float renderX = (float) (x - width / 2);
        float renderY = (float) (y - height / 2);

        // 設置顏色
        g2d.setColor(color);

        switch (shape.toLowerCase()) {
            case "circle":
                // 以 ellipse 畫出圓形
                Ellipse2D.Float ellipse = new Ellipse2D.Float(renderX, renderY, width, height);
                g2d.fill(ellipse);
                break;
            case "square":
                // 以 fillRect 畫出方形
                g2d.fillRect((int) renderX, (int) renderY, width, height);
                break;
            case "triangle":
                // 畫一個大約等邊三角形
                Polygon triangle = new Polygon();
                triangle.addPoint((int) (renderX + width / 2), (int) renderY);           // 上頂
                triangle.addPoint((int) renderX, (int) (renderY + height));             // 左下
                triangle.addPoint((int) (renderX + width), (int) (renderY + height));    // 右下
                g2d.fillPolygon(triangle);
                break;
            case "diamond":
                // 畫一個菱形
                Polygon diamond = new Polygon();
                diamond.addPoint((int) (renderX + width / 2), (int) renderY);           // 上頂
                diamond.addPoint((int) renderX, (int) (renderY + height / 2));          // 左頂
                diamond.addPoint((int) (renderX + width / 2), (int) (renderY + height)); // 下頂
                diamond.addPoint((int) (renderX + width), (int) (renderY + height / 2)); // 右頂
                g2d.fillPolygon(diamond);
                break;
            case "pentagon":
                Polygon pentagon = new Polygon();
                pentagon.addPoint((int) (renderX + width / 2), (int) renderY);                      // 頂部
                pentagon.addPoint((int) (renderX), (int) (renderY + height / 3));                   // 左上
                pentagon.addPoint((int) (renderX + width / 4), (int) (renderY + height));          // 左下
                pentagon.addPoint((int) (renderX + width - width / 4), (int) (renderY + height));  // 右下
                pentagon.addPoint((int) (renderX + width), (int) (renderY + height / 3));          // 右上
                g2d.fillPolygon(pentagon);
                break;
            case "hexagon":
                Polygon hexagon = new Polygon();
                hexagon.addPoint((int) (renderX + width / 2), (int) renderY);                      // 上頂
                hexagon.addPoint((int) (renderX), (int) (renderY + height / 3));                   // 左上
                hexagon.addPoint((int) (renderX), (int) (renderY + 2 * height / 3));               // 左下
                hexagon.addPoint((int) (renderX + width / 2), (int) (renderY + height));           // 下頂
                hexagon.addPoint((int) (renderX + width), (int) (renderY + 2 * height / 3));       // 右下
                hexagon.addPoint((int) (renderX + width), (int) (renderY + height / 3));           // 右上
                g2d.fillPolygon(hexagon);
                break;
            case "star":
                Polygon star = new Polygon();

                // 圓心位置和半徑設定
                double centerX = renderX + width / 2.0;
                double centerY = renderY + height / 2.0;
                double outerRadius = width / 2.0; // 外圈半徑
                double innerRadius = outerRadius / 2.5; // 內圈半徑 (比例可以調整)

                // 五角星的點數量 (外圈和內圈交替，共10個點)
                int points = 10;

                for (int i = 0; i < points; i++) {
                    // 計算角度 (交替使用外圈和內圈)
                    double angle = Math.toRadians(-90 + (360.0 / points) * i);
                    double radius = (i % 2 == 0) ? outerRadius : innerRadius;

                    // 計算點的座標
                    int x = (int) (centerX + Math.cos(angle) * radius);
                    int y = (int) (centerY + Math.sin(angle) * radius);

                    // 添加點到五角星
                    star.addPoint(x, y);
                }

                // 繪製五角星
                g2d.fillPolygon(star);
                break;


            case "ellipse":
                g2d.fill(new Ellipse2D.Float(renderX, renderY, width * 2, height));
                break;

            case "heart":
                Path2D.Double heart = new Path2D.Double();
                heart.moveTo(renderX + width / 2, renderY + height);                          // 底部
                heart.curveTo(renderX, renderY + height / 2, renderX, renderY, renderX + width / 2, renderY); // 左曲線
                heart.curveTo(renderX + width, renderY, renderX + width, renderY + height / 2, renderX + width / 2, renderY + height); // 右曲線
                g2d.fill(heart);
                break;

            case "trapezoid":
                Polygon trapezoid = new Polygon();
                trapezoid.addPoint((int) (renderX + width / 4), (int) renderY);                 // 上左
                trapezoid.addPoint((int) (renderX + 3 * width / 4), (int) renderY);             // 上右
                trapezoid.addPoint((int) (renderX + width), (int) (renderY + height));           // 下右
                trapezoid.addPoint((int) renderX, (int) (renderY + height));                    // 下左
                g2d.fillPolygon(trapezoid);
                break;


            default:
                // 若無指定，就當作 circle
                Ellipse2D.Float defaultCircle = new Ellipse2D.Float(renderX, renderY, width, height);
                g2d.fill(defaultCircle);
        }


        // 繪製血量條
        drawHealthBar(g2d);

        // 繪製狀態圖示
        renderStatusEffects(g2d);

        g2d.dispose();
    }

    /**
     * 顯示血量條
     */
    private void drawHealthBar(Graphics2D g2d) {
        int barY = (int) (y - height / 2 - 10);
        int barX = (int) (x - width / 2);

        // 血量底
        g2d.setColor(Color.GRAY);
        g2d.fillRect(barX, barY, width, 5);

        // 血量比例
        g2d.setColor(Color.GREEN);
        int healthBarWidth = (int) (width * (health / maxHealth));
        g2d.fillRect(barX, barY, healthBarWidth, 5);
    }

    /**
     * 繪製狀態圖示（如SLOW, FREEZE等）
     */
    private void renderStatusEffects(Graphics2D g2d) {
        int barHeight = 10;
        int offsetY = 20;
        int effectCount = activeStatusEffects.size();
        int iconSize = barHeight;
        int padding = 2;

        // 狀態底色
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect((int) (x - width / 2), (int) (y - height / 2 - offsetY), width, barHeight);

        // 每個狀態畫一個小圓點，顏色視不同效果而異
        for (int i = 0; i < effectCount && i < 5; i++) {
            StatusEffect effect = activeStatusEffects.get(i);
            switch (effect.getType()) {
                case SLOW:
                    g2d.setColor(Color.CYAN);
                    break;
                case FREEZE:
                    g2d.setColor(Color.BLUE);
                    break;
                case BURN:
                    g2d.setColor(Color.ORANGE);
                    break;
                case POISON:
                    g2d.setColor(Color.MAGENTA);
                    break;
                default:
                    g2d.setColor(Color.WHITE);
            }
            int dotX = (int) (x - width / 2 + i * (iconSize + padding));
            int dotY = (int) (y - height / 2 - offsetY);

            g2d.fillRect(dotX, dotY, iconSize, iconSize);
        }
    }

    // ------------------------------------------
    // 以下為 Getter / Setter 區
    // ------------------------------------------
    public void setPath(List<int[]> newPath) {
        this.path = newPath;
        this.x = newPath.get(0)[0];
        this.y = newPath.get(0)[1];
        this.currentTargetIndex = 0;
    }

    public void takeDamage(double damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public void applyStatusEffect(StatusEffect effect) {
        if (effect.getType() == SLOW
                || effect.getType() == FREEZE
                || effect.getType() == BURN
                || effect.getType() == POISON) {
            activeStatusEffects.add(effect);
        }
    }

    public void setBaseSpeed(double speed) {
        this.baseSpeed = speed;
        this.currentSpeed = speed;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    /**
     * 可用 switch 或直接從 JSON 解析出 color (如 hex #XXXXXX)
     * 若只是固定形狀對應顏色，也可在 createEnemy() 那邊做對應。
     */
    public void setColor(Color color) {
        this.color = color;
    }

    public boolean hasReachedEnd() {
        return currentTargetIndex >= path.size();
    }

    // 取座標
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getReward(){
        return this.reward;
    }


    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public void addSpeedModifier(double modifier) {
        currentSpeed += modifier;
    }

    public void removeSpeedModifier(double modifier) {
        currentSpeed -= modifier;
    }
}
