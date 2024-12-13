package Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlButtonsPanel extends JPanel {
    private JButton timeControlButton;
    private JButton buyUpgradeButton;
    private JButton sellButton;

    public ControlButtonsPanel(ActionListener timeControlListener,
                               ActionListener buyUpgradeListener,
                               ActionListener sellListener) {
        setLayout(new GridLayout(3, 1, 5, 5));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        timeControlButton = new JButton("開始");
        buyUpgradeButton = new JButton("購買/升級");
        sellButton = new JButton("賣出(8折");

        // 添加按鈕事件
        timeControlButton.addActionListener(timeControlListener);
        buyUpgradeButton.addActionListener(buyUpgradeListener);
        sellButton.addActionListener(sellListener);

        add(timeControlButton);
        add(buyUpgradeButton);
        add(sellButton);
    }

    public void setTimeControlButtonText(String text) {
        timeControlButton.setText(text);
    }

    public void setBuyUpgradeButtonText(String text) {
        buyUpgradeButton.setText(text);
    }
}
