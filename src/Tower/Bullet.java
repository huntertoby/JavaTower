package Tower;

import enemy.Enemy;

import java.awt.*;

public class Bullet {
    private double x, y;
    private double speed = 10.0; // 子彈飛行速度
    private double damage;
    private Enemy target;
    private boolean used = false; // 撞擊後標記為已使用

    private int size = 8; // 子彈顯示大小(直徑)

    public Bullet(double startX, double startY, Enemy target, double damage) {
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.damage = damage;
    }

    public void update() {
        if (target == null || target.isDead()) {
            // 若目標已死亡或不存在，則子彈失去意義，可以標示為已使用
            used = true;
            return;
        }
        double targetCenterX = target.getX() + target.getWidth() / 2.0;
        double targetCenterY = target.getY() + target.getHeight() / 2.0;
        double dx = targetCenterX - x;
        double dy = targetCenterY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 14) {
            // 認定為命中（距離可自行調整）
            target.takeDamage(damage);
            used = true;
            return;
        }
        // 朝目標移動
        double ux = dx / dist;
        double uy = dy / dist;
        x += ux * speed;
        y += uy * speed;
    }

    public void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) (x - size / 2), (int) (y - size / 2), size, size);
    }

    public boolean isUsed() {
        return used;
    }
}
