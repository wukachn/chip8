package io.github.wukachn;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Display extends JFrame implements KeyListener {

  private static final int DISPLAY_WIDTH = 64;
  private static final int DISPLAY_HEIGHT = 32;
  private static final int SCALE = 16;

  private final boolean[][] pixels = new boolean[DISPLAY_WIDTH][DISPLAY_HEIGHT];
  private final Keypad keypad;

  public Display(Keypad keypad) {
    this.keypad = keypad;
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawPixels(g);
      }
    };
    this.getContentPane().add(panel);
    this.setSize(DISPLAY_WIDTH * SCALE, (DISPLAY_HEIGHT * SCALE) + 28);
    this.setVisible(true);
    this.addKeyListener(this);
  }

  private void drawPixels(Graphics g) {
    for (int x = 0; x < DISPLAY_WIDTH; x++) {
      for (int y = 0; y < DISPLAY_HEIGHT; y++) {
        if (!pixels[x][y]) {
          g.setColor(Color.BLACK);
        } else {
          g.setColor(Color.WHITE);
        }
        g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
      }
    }
  }

  public boolean paint(int x, int y, byte[] spriteBytes) {
    boolean enableFlagRegister = false;
    for (int yLine = 0; yLine < spriteBytes.length; yLine++) {
      byte spriteByte = spriteBytes[yLine];
      for (int xLine = 0; xLine < 8; xLine++) {
        boolean isSet = (spriteByte & (0x80 >> xLine)) != 0;
        if (isSet) {
          if (pixels[x + xLine][y + yLine]) {
            pixels[x + xLine][y + yLine] = false;
            enableFlagRegister = true;
          } else {
            pixels[x + xLine][y + yLine] = true;
          }
        }
      }
    }
    repaint();
    return enableFlagRegister;
  }

  public void clear() {
    for (int i = 0; i < pixels.length; i++) {
      for (int j = 0; j < pixels[0].length; j++) {
        pixels[i][j] = false;
      }
    }
    repaint();
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    keypad.keyPressed(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    keypad.keyReleased(e);
  }
}
