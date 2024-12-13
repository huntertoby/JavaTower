package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import Data.*;
import Panels.*;
import Tower.*;
import enemy.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class TowerDefenseGame extends JPanel implements ActionListener, MouseListener {
    private TileMap tileMap;
    private MapPanel mapPanel;
    public static List<Tower> towers = new ArrayList<>();
    public static List<Enemy> enemies = new ArrayList<>();
    public static List<Bullet> bullets = new ArrayList<>();

    private Timer timer;

    private towerData data;            // 舊的塔資料管理
    private EnemyData enemyData;  // 新增：敵人資料讀取
    private WaveData waveData;    // 新增：波數資料讀取

    // 遊戲狀態
    private boolean isRunning = false;
    private int playerHealth = 100;
    private double playerMoney = 500.0;
    private int currentWave = 1;

    // UI 組件
    private PlayerInfoPanel playerInfoPanel;
    private TowerDataPanel towerDataPanel;
    private ControlButtonsPanel controlButtonsPanel;
    private TowerTypeButtonPanel towerTypeButtonPanel;

    // 選中的塔
    public static Tower selectedTower = null;
    private boolean selectedTowerSpot;
    private int selectedTileX = -1;
    private int selectedTileY = -1;

    // 生成相關屬性
    private Queue<String> spawnQueue;    // 新增：此波待生成的敵人「種類」隊列
    private double spawnInterval = 1.0;  // 每隔幾秒生成一隻敵人
    private double spawnTimer = 0.0;

    public TowerDefenseGame(){
        setLayout(new BorderLayout());

        try {
            tileMap = new TileMap("asset/map/level1.tmj");
            mapPanel = new MapPanel(tileMap, "asset/map/level1.png");
            data = new towerData();          // 原本的塔資料讀取

            // 新增：讀取 enemies.json 與 waves.json
            enemyData = new EnemyData("asset/enemy/enemies.json");    // 自行放置在你的專案路徑
            waveData  = new WaveData("asset/enemy/waves.json");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化地圖面板
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        add(scrollPane, BorderLayout.CENTER);

        mapPanel.addMouseListener(this);

        // 初始化側邊欄組件
        playerInfoPanel = new PlayerInfoPanel(playerHealth, playerMoney, currentWave);
        towerDataPanel = new TowerDataPanel();
        controlButtonsPanel = new ControlButtonsPanel(
                e -> toggleGameState(),
                e -> handleBuyUpgrade(),
                e -> handleSell()
        );
        towerTypeButtonPanel = new TowerTypeButtonPanel(data.getTowers(),this,towerDataPanel);

        // 組合側邊欄
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(300, 0)); // 固定寬度
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sidebar.add(playerInfoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(towerDataPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(towerTypeButtonPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(controlButtonsPanel);

        add(sidebar, BorderLayout.EAST);

        // 初始化計時器
        timer = new Timer(16, this); // 約60 FPS
        timer.stop();
        repaint();

        startWave();
    }

    /**
     * 切換遊戲的運行狀態（開始/暫停）
     */
    private void toggleGameState() {
        this.isRunning = !this.isRunning;

        if (!isRunning) {
            controlButtonsPanel.setTimeControlButtonText("開始");
            timer.stop();
        } else {
            controlButtonsPanel.setTimeControlButtonText("暫停");
            // 若這波尚未生成所有敵人，但暫停過後又繼續，直接 timer.start() 即可
            // 若這波完全生成完畢，才進入下一波
            if (spawnQueue.isEmpty()) {
                currentWave++;
                startWave();
            }
            timer.start();
        }
    }

    /**
     * 讀取波數資料，填充 spawnQueue
     */
    private void startWave() {
        // 從 waveData 取得當前波資訊
        JSONArray waveInfo = waveData.getWaveInfo(currentWave);
        if (waveInfo == null) {
            System.out.println("沒有更多波數，遊戲結束或可自行處理。");
            return;
        }

        spawnTimer = 0.0;
        spawnInterval = 1.0;  // 假設每秒出一隻
        spawnQueue = new ArrayDeque<>();

        // 把這波要生成的所有敵人「種類」，加入 queue
        for (int i = 0; i < waveInfo.length(); i++) {
            JSONObject enemyObj = waveInfo.getJSONObject(i);
            String type = enemyObj.getString("type");   // 如 "普通兵"
            int count  = enemyObj.getInt("count");      // 要生成幾隻
            for (int c = 0; c < count; c++) {
                spawnQueue.offer(type);
            }
        }

        System.out.println("開始第 " + currentWave + " 波敵人！ 總生成數量：" + spawnQueue.size());
    }

    private void handleBuyUpgrade() {
        if (towerTypeButtonPanel.getSelectedTowerName() == null) {
            JOptionPane.showMessageDialog(null, "沒有選擇砲塔類型", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        if (selectedTower == null && towerTypeButtonPanel.getSelectedTowerName() == null) return;

        if (selectedTower == null && selectedTowerSpot && !checkTowers(selectedTileX, selectedTileY)) {
            int costMoney = towerData.getTowerCost(towerTypeButtonPanel.getSelectedTowerName(), 1);

            if (costMoney > playerMoney){
                JOptionPane.showMessageDialog(null, "金額不足", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Tower tower = data.createTower(towerTypeButtonPanel.getSelectedTowerName(),1,selectedTileX,selectedTileY);
            tower.setCostMoney(costMoney);
            towers.add(tower);
            selectedTower = tower;
            controlButtonsPanel.setBuyUpgradeButtonText("升級");
            towerDataPanel.updateTowerData(selectedTower);
            playerMoney -= costMoney;

        } else if (selectedTower != null) {
            if (selectedTower.getMaxLevel() == selectedTower.getLevel()) {
                JOptionPane.showMessageDialog(null, "已經是最高等", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (towerData.getTowerCost(selectedTower.getTowerName(),selectedTower.getLevel() + 1) > playerMoney){
                JOptionPane.showMessageDialog(null, "金額不足", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int costMoney = towerData.getTowerCost(selectedTower.getTowerName(),selectedTower.getLevel()+1);

            Tower upgradedTower = data.createTower(
                    selectedTower.getTowerName(),
                    selectedTower.getLevel() + 1,
                    selectedTileX,
                    selectedTileY
            );

            upgradedTower.setCostMoney(selectedTower.getCostMoney() + costMoney);
            playerMoney -= costMoney;
            towers.remove(selectedTower);

            towers.add(upgradedTower);
            selectedTower = upgradedTower;
            towerDataPanel.updateTowerData(selectedTower);
        }
        repaint();
        playerInfoPanel.updateInfo(playerHealth,playerMoney,currentWave);
    }

    private void handleSell() {
        if (selectedTower == null) {
            JOptionPane.showMessageDialog(null, "請選擇有效的防禦塔", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        playerMoney += selectedTower.getCostMoney() * 0.8;
        towers.remove(selectedTower);
        towerDataPanel.updateTowerData(null);
        selectedTower = null;
        playerInfoPanel.updateInfo(playerHealth,playerMoney,currentWave);
        repaint();
    }

    private void updateSidebar() {
        playerInfoPanel.updateInfo(playerHealth, playerMoney, currentWave);
        towerDataPanel.updateTowerData(selectedTower);
    }

    public boolean checkTowers(int selectedTileX, int selectedTileY) {
        for (Tower tower : towers) {
            if (tower.getTileX() == selectedTileX && tower.getTileY() == selectedTileY) {
                towerDataPanel.updateTowerData(tower);
                System.out.println("這裡有塔");
                selectedTower = tower;
                return true;
            }
        }
        selectedTower = null;
        return false;
    }

    public void repaintGame() {
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.selectedTileX = e.getX() / MapPanel.tileWidth;
        this.selectedTileY = e.getY() / MapPanel.tileHeight;

        if (mapPanel.clickTower(selectedTileX, selectedTileY)) {
            selectedTowerSpot = true;
            if (checkTowers(selectedTileX, selectedTileY)){
                controlButtonsPanel.setBuyUpgradeButtonText("升級");
            } else {
                towerDataPanel.choseToBuild();
                controlButtonsPanel.setBuyUpgradeButtonText("購買");
            }
        } else {
            selectedTower = null;
            selectedTowerSpot = false;
            towerDataPanel.updateTowerData(null);
            System.out.println(selectedTower);
        }
        repaint();
    }

    // 其他MouseListener方法 - 留空即可
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args){
        JFrame frame = new JFrame("塔防遊戲");
        TowerDefenseGame game = new TowerDefenseGame();
        frame.add(game);
        frame.setSize(1280, 1010);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        double deltaTime = 0.016;

        // 更新塔
        for (Tower tower: towers) {
            tower.update(0.01666,enemies);
        }

        // 更新子彈
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            if (bullet.isUsed()) {
                bullets.remove(i);
            }
        }

        // 更新敵人
        for (Enemy enemy: enemies) {
            enemy.update();

            if (enemy.getHealth()<=0) System.out.println(enemy.getHealth());

            if (enemy.isEnd()) {
                playerHealth -= 10;
                playerInfoPanel.updateInfo(playerHealth,playerMoney,currentWave);
            }
            if (enemy.isDead()) {
                playerMoney+=enemy.getReward();

                System.out.println(enemy.isDead());

                playerInfoPanel.updateInfo(playerHealth,playerMoney,currentWave);
            }
        }

        // 移除死亡或到終點的敵人
        enemies.removeIf(Enemy::isDead);
        enemies.removeIf(Enemy::isEnd);

        // 生成敵人（透過 spawnQueue）
        if (isRunning) {
            spawnTimer += deltaTime;
            if (!spawnQueue.isEmpty() && spawnTimer >= spawnInterval) {
                String enemyType = spawnQueue.poll(); // 從隊列取出一個敵人類型
                // 依照enemyType從enemyData建立 Enemy 物件
                Enemy eObj = enemyData.createEnemy(enemyType);
                // 設置路徑
                eObj.setPath(tileMap.getSpot());
                enemies.add(eObj);
                spawnTimer = 0.0;
                System.out.println("生成一隻 " + enemyType + "，剩餘：" + spawnQueue.size());
            }
        }

        // 如果此波 spawnQueue 空了，且場上已無敵人，代表此波結束
        if (enemies.isEmpty() && spawnQueue.isEmpty() && isRunning) {
            System.out.println("本波敵人清除完畢，波次結束");
            timer.stop();
            isRunning = false;
            controlButtonsPanel.setTimeControlButtonText("開始");
            playerInfoPanel.updateInfo(playerHealth,playerMoney,currentWave);
            bullets.clear();
        }

        repaint();
    }
}
